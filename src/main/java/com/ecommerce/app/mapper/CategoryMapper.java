package com.ecommerce.app.mapper;

import com.ecommerce.app.model.CategoryEntity;
import com.ecommerce.app.dto.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    CategoryEntity toCategory(Category record);

    Category toCategoryResponse(CategoryEntity Category);

    List<Category> toCategoryResponse(List<CategoryEntity> categories);

    void updateEntityFromRequest(Category CategoryRequest, @MappingTarget CategoryEntity Category);
}
