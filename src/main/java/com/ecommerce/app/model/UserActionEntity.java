package com.ecommerce.app.model;

import com.ecommerce.app.enums.UserActionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_ACTION")
@EntityListeners({AuditingEntityListener.class})
public class UserActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private UserActionEnum actionType;
    private String requestBody;
    private Boolean success;
    private String errorCode;
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(name = "IS_ROLLED_BACK")
    private Boolean isRolledBack;
    private String requestId;

}
