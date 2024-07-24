package com.ecommerce.app.repo;


import com.ecommerce.app.model.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepo extends BaseRepo<ProductEntity> {

    List<ProductEntity> findByCategoryId(Long id);

    List<ProductEntity> findByIdInOrderById(List<Long> ids);

    boolean existsByNameAndCategoryId(String name, Long id);

    boolean existsByNameAndCategoryIdAndIdNot(String name, Long catId, Long id);
}
