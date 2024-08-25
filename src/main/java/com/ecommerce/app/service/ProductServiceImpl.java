package com.ecommerce.app.service;

import com.ecommerce.app.cache.CacheManagerService;
import com.ecommerce.app.dto.*;
import com.ecommerce.app.exception.CategoryNotFoundException;
import com.ecommerce.app.exception.ProductExistsException;
import com.ecommerce.app.exception.ProductNotFoundException;
import com.ecommerce.app.exception.ProductPurchaseException;
import com.ecommerce.app.mapper.ProductMapper;
import com.ecommerce.app.model.CategoryEntity;
import com.ecommerce.app.model.ProductEntity;
import com.ecommerce.app.model.ProductView;
import com.ecommerce.app.model.UserActionEntity;
import com.ecommerce.app.payload.PageResponse;
import com.ecommerce.app.repo.CategoryRepo;
import com.ecommerce.app.repo.ProductRepo;
import com.ecommerce.app.repo.ProductViewRepo;
import com.ecommerce.app.repo.UserActionRepo;
import com.ecommerce.app.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductViewRepo productViewRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;
    private final UserActionRepo userActionRepo;
    private final JwtTokenUtil jwtTokenUtil;
    private final CacheManagerService cacheManagerService;

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<ProductInfoResponse> getProductsInfo(List<Long> ids) {
        List<ProductEntity> products = productRepo.findByIdInOrderById(ids);
        return productMapper.toProductInfoResponse(products);
    }

    @Override
    public List<ProductResponse> getRandomProducts(int size) {
        return productMapper.toProductResponse(productRepo.getRandomProducts(size));
    }

    @Override
    public void deleteProduct(Long id) {
        getById(id);
        productRepo.deleteById(id);
        refreshProductView();
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(ProductFilterRequest request) {
        Sort sort = Sort.by(request.sort() != null ? request.sort() : Sort.Direction.DESC, request.sortBy() != null ? request.sortBy() : "createdAt");
        PageRequest pageRequest = PageRequest.of(request.index().intValue(), request.size().intValue(), sort);
        Page<ProductEntity> all = request.categoryId() != null ? productRepo.findByCategoryId(request.categoryId(), pageRequest) : productRepo.findAll(pageRequest);
        return new PageResponse<>
                (productMapper.toProductResponse(all.getContent()),
                        all.isLast(),
                        all.getNumber(),
                        all.getSize(),
                        all.getTotalElements(),
                        all.getTotalPages());
    }

    @Override
    public PageResponse<ProductViewResponse> getAllProducts(ProductFilterRequest request, boolean refreshView) {
        if (refreshView) refreshProductView();
        Sort sort = Sort.by(request.sort() != null ? request.sort() : Sort.Direction.DESC, request.sortBy() != null ? request.sortBy() : "createdAt");
        PageRequest pageRequest = PageRequest.of(request.index().intValue(), request.size().intValue(), sort);
        Page<ProductView> all = request.categoryId() != null ? productViewRepo.findByCategoryId(request.categoryId(), pageRequest) : productViewRepo.findAll(pageRequest);
        return new PageResponse<>
                (productMapper.fromProductView(all.getContent()),
                        all.isLast(),
                        all.getNumber(),
                        all.getSize(),
                        all.getTotalElements(),
                        all.getTotalPages());
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        var product = productMapper.toProduct(productRequest);
        var category = getCategoryById(productRequest.categoryId());
        boolean exists = productRepo.existsByNameAndCategoryId(productRequest.name(), category.getId());
        if (exists) {
            throw new ProductExistsException(productRequest.name());
        }
        product.setCategory(category);
        productRepo.save(product);
        refreshProductView();
        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        var product = getById(id);
        productMapper.updateEntityFromRequest(productRequest, product);
        var category = getCategoryById(productRequest.categoryId());
        boolean exists = productRepo.existsByNameAndCategoryIdAndIdNot(productRequest.name(), category.getId(), id);
        if (exists) {
            throw new ProductExistsException(productRequest.name());
        }
        product.setCategory(category);
        productRepo.save(product);
        refreshProductView();
        doAfterUpdate(id);
        return productMapper.toProductResponse(product);
    }

    private void doAfterUpdate(Long id) {
        cacheManagerService.clearCacheByName("productsInfo");
    }

    @Override
    @Transactional
    public synchronized PurchaseResponse purchaseProduct(CreatePurchaseRequest createPurchaseRequest) {
        List<ProductPurchaseRequest> productPurchaseRequests = createPurchaseRequest.purchaseList();
        List<Long> productIdList = productPurchaseRequests.stream().map(ProductPurchaseRequest::productId).toList();
        List<ProductEntity> updatedProducts = new ArrayList<>();
        List<ProductEntity> products = productRepo.findByIdInOrderById(productIdList);
        if (products.size() != productIdList.size()) {
            throw new ProductPurchaseException("one or more products does not exist");
        }

        List<ProductPurchaseResponse> productPurchaseResponseList = new ArrayList<>();
        productPurchaseRequests.stream().forEach(request -> {
            var product = findProduct(request.productId(), products);
            if (product.getQuantity() < request.quantity()) {
                throw new ProductPurchaseException(String.format("insufficient stock quantity for product with id %s ", request.productId()));
            }
            var newQuantity = product.getQuantity() - request.quantity();
            product.setQuantity(newQuantity);
            updatedProducts.add(product);
            productPurchaseResponseList.add(prepareProductPurchaseResponse(product, request.quantity()));
        });
        productRepo.saveAll(updatedProducts);
        refreshProductView();
        return PurchaseResponse.builder()
                .products(productPurchaseResponseList)
                .requestId(UUID.randomUUID().toString())
                .token(JwtTokenUtil.generateToken(createPurchaseRequest.userName(), UUID.randomUUID().toString()))
                .build();
    }

    @Override
    @Transactional
    public void rollbackPurchase(String requestId) {
        try {
            Optional<UserActionEntity> userAction = userActionRepo.findByRequestIdAndSuccess(requestId, true);
            if (userAction.isPresent()) {
                UserActionEntity userActionEntity = userAction.get();
                CreatePurchaseRequest createPurchaseRequest = parseRequestBody(userActionEntity.getRequestBody());
                log.info("rollback product purchase for list <{}>", userActionEntity.getRequestBody());
                List<ProductPurchaseRequest> products = createPurchaseRequest.purchaseList();
                for (ProductPurchaseRequest requestProduct : products) {
                    Optional<ProductEntity> productEntity = productRepo.findById(requestProduct.productId());
                    if (productEntity.isPresent()) {
                        ProductEntity originalProduct = productEntity.get();
                        Long quantity = originalProduct.getQuantity();
                        quantity += requestProduct.quantity();
                        originalProduct.setQuantity(quantity);
                        productRepo.save(originalProduct);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("rollback purchase for REQUEST ID: {} failed", requestId);
        }
    }

    @Override
    @Transactional
    public void rollbackPurchase(PurchaseResponse purchaseResponse) {
        boolean tokenValid = jwtTokenUtil.isTokenValid(purchaseResponse.token(), purchaseResponse.userName());
        if (tokenValid) {
            try {
                for (ProductPurchaseResponse requestProduct : purchaseResponse.products()) {
                    Optional<ProductEntity> productEntity = productRepo.findById(requestProduct.id());
                    if (productEntity.isPresent()) {
                        ProductEntity originalProduct = productEntity.get();
                        Long quantity = originalProduct.getQuantity();
                        quantity += requestProduct.quantity();
                        originalProduct.setQuantity(quantity);
                        productRepo.save(originalProduct);
                    }
                }
            } catch (Exception ex) {
                log.error("rollback products failed", ex);
            }
        }
    }

    @SneakyThrows
    private CreatePurchaseRequest parseRequestBody(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(AUTO_CLOSE_SOURCE, true);
        return mapper.readValue(requestBody, CreatePurchaseRequest.class);
    }

    private ProductPurchaseResponse prepareProductPurchaseResponse(ProductEntity product, Long quantity) {
        ProductPurchaseResponse purchaseResponse = productMapper.toProductPurchaseResponse(product);
        return ProductPurchaseResponse.builder()
                .id(purchaseResponse.id())
                .name(purchaseResponse.name())
                .description(purchaseResponse.description())
                .price(purchaseResponse.price())
                .quantity(quantity)
                .totalPrice(purchaseResponse.price().multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP))
                .build();
    }

    private ProductEntity findProduct(Long id, List<ProductEntity> products) {
        Optional<ProductEntity> product = products.stream().filter(productReq -> productReq.getId().equals(id)).findFirst();
        return product.orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductEntity getById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public CategoryEntity getCategoryById(Long id) {
        return categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private void refreshProductView() {
        try {
            productViewRepo.refreshProductView();
        } catch (Exception ex) {
            log.error("refreshProductView failed ", ex);
        }
    }
}
