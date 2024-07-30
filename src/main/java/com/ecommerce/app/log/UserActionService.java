package com.ecommerce.app.log;


import com.ecommerce.app.enums.UserActionEnum;

public interface UserActionService {

    void saveUserAction(UserActionEnum userAction, String requestBody, String responseBody);

}
