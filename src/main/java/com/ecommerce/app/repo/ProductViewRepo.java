package com.ecommerce.app.repo;


import com.ecommerce.app.model.ProductView;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductViewRepo extends BaseRepo<ProductView> {

    Page<ProductView> findByCategoryId(Long id, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW PRODUCT_MV", nativeQuery = true)
    void refreshProductView();

}
