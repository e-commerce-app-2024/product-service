package com.ecommerce.app.kafka;


import com.ecommerce.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class RollbackPurchaseConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "rollback_purchase", groupId = "rollback_purchase")
    public void rollbackPurchase(String requestId) {
        log.info("consume the message from rollback_purchase-topic :: {}", requestId);
        productService.rollbackPurchase(requestId);
    }

}
