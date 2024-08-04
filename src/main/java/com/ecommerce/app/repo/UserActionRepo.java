package com.ecommerce.app.repo;


import com.ecommerce.app.model.UserActionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserActionRepo extends BaseRepo<UserActionEntity> {

    Optional<UserActionEntity> findByRequestIdAndSuccess(String requestId, boolean isSuccess);

    int deleteByRequestId(String requestId);
}
