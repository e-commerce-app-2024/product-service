package com.ecommerce.app.log;


import com.ecommerce.app.dto.CreatePurchaseRequest;
import com.ecommerce.app.dto.PurchaseResponse;
import com.ecommerce.app.enums.UserActionEnum;
import com.ecommerce.app.model.UserActionEntity;
import com.ecommerce.app.payload.AppResponse;
import com.ecommerce.app.repo.UserActionRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;


@Log4j2
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepo userActionRepo;

    @Async
    @Override
    public void saveUserAction(UserActionEnum userAction, String requestBody, String responseBody) {
        UserActionEntity userActionEntity = UserActionEntity.builder()
                .actionType(userAction)
                .requestBody(requestBody)
                .isRolledBack(false)
                .build();

        AppResponse<PurchaseResponse> appResponse = parseResponseBody(responseBody);

        boolean responseSuccess = appResponse.success();
        userActionEntity.setSuccess(responseSuccess);
        if (responseSuccess) {
            String requestId = appResponse.payload().requestId();
            userActionEntity.setRequestId(requestId);
        } else {
            userActionEntity.setRequestId(UUID.randomUUID().toString());
            userActionEntity.setErrorCode(appResponse.error().code().name());
        }
        userActionRepo.save(userActionEntity);
    }

    @SneakyThrows
    private AppResponse<PurchaseResponse> parseResponseBody(String responseBody) {
        TypeReference<AppResponse<PurchaseResponse>> typeReference = new TypeReference<>() {
        };
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(AUTO_CLOSE_SOURCE, true);
        return mapper.readValue(responseBody, typeReference);
    }

    @SneakyThrows
    private CreatePurchaseRequest parseRequestBody(String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(AUTO_CLOSE_SOURCE, true);
        return mapper.readValue(requestBody, CreatePurchaseRequest.class);
    }
}
