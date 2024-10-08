package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.*;
import com.ecommerce.app.model.ProductEntity;
import com.ecommerce.app.model.ProductView;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    ProductEntity toProduct(ProductRequest productRequest);

    ProductResponse toProductResponse(ProductEntity product);

    List<ProductInfoResponse> toProductInfoResponse(List<ProductEntity> products);

    List<ProductResponse> toProductResponse(List<ProductEntity> products);

    List<ProductViewResponse> fromProductView(List<ProductView> products);

    ProductPurchaseResponse toProductPurchaseResponse(ProductEntity product);

    List<ProductPurchaseResponse> toProductPurchaseResponse(List<ProductEntity> products);

    void updateEntityFromRequest(ProductRequest productRequest, @MappingTarget ProductEntity product);

}
