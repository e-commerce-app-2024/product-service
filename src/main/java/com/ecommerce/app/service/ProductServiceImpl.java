package com.ecommerce.app.service;

import com.ecommerce.app.dto.*;
import com.ecommerce.app.exception.CategoryNotFoundException;
import com.ecommerce.app.exception.ProductExistsException;
import com.ecommerce.app.exception.ProductNotFoundException;
import com.ecommerce.app.exception.ProductPurchaseException;
import com.ecommerce.app.mapper.ProductMapper;
import com.ecommerce.app.model.CategoryEntity;
import com.ecommerce.app.model.ProductEntity;
import com.ecommerce.app.payload.PageResponse;
import com.ecommerce.app.repo.CategoryRepo;
import com.ecommerce.app.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void deleteProduct(Long id) {
        getById(id);
        productRepo.deleteById(id);
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
    public ProductResponse addProduct(ProductRequest productRequest) {
        var product = productMapper.toProduct(productRequest);
        var category = getCategoryById(productRequest.categoryId());
        boolean exists = productRepo.existsByNameAndCategoryId(productRequest.name(), category.getId());
        if (exists) {
            throw new ProductExistsException(productRequest.name());
        }
        product.setCategory(category);
        productRepo.save(product);
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
        return productMapper.toProductResponse(product);
    }

    @Override
    public synchronized List<ProductPurchaseResponse> purchaseProduct(List<ProductPurchaseRequest> productPurchaseRequests) {
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
        return productPurchaseResponseList;
    }

    private ProductPurchaseResponse prepareProductPurchaseResponse(ProductEntity product, Long quantity) {
        ProductPurchaseResponse purchaseResponse = productMapper.toProductPurchaseResponse(product);
        return ProductPurchaseResponse.builder()
                .id(purchaseResponse.id())
                .name(purchaseResponse.name())
                .description(purchaseResponse.description())
                .price(purchaseResponse.price())
                .totalPrice(purchaseResponse.price().multiply(new BigDecimal(quantity))).build();
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
}