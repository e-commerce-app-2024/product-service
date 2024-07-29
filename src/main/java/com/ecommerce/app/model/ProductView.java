package com.ecommerce.app.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table
@Entity(name = "PRODUCT_MV")
public class ProductView {

    @Id
    private Long id;
    private String name;
    private String description;
    private Long quantity;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Timestamp createdAt;

}
