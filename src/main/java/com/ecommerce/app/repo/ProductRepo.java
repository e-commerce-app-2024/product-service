package com.ecommerce.app.repo;


import com.ecommerce.app.model.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepo extends BaseRepo<ProductEntity> {

    List<ProductEntity> findByCategoryId(Long id);

    List<ProductEntity> findByIdInOrderById(List<Long> ids);

    Page<ProductEntity> findByCategoryId(Long id, Pageable pageable);

    boolean existsByNameAndCategoryId(String name, Long id);

    boolean existsByNameAndCategoryIdAndIdNot(String name, Long catId, Long id);

    @Modifying
    @Query(value = """
            select
            	*
            from
            	product
            order by
            	RANDOM()
            limit :productLimit
            """, nativeQuery = true)
    List<ProductEntity> getRandomProducts(@Param("productLimit") int size);

}
