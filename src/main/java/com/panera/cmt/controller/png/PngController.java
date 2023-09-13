package com.panera.cmt.controller.png;

import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeEmail;
import com.panera.cmt.dto.proxy.subscription_service.GiftCoffeeSubscription;
import com.panera.cmt.dto.subscription_service.GiftCoffeeEmailDTO;
import com.panera.cmt.service.png.IPngService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "PNG Controller", description = "PNG functionality", produces = "application/json")
@RestController
@RequestMapping(path = "/api/v1/png")
@Slf4j
public class PngController {

    private final IPngService pngService;

    public PngController(IPngService pngService) {
        this.pngService = pngService;
    }

    @PostMapping(value = "/resendGiftCoffeeEmail/{giftCode}", produces = "application/json")
    public ResponseEntity<?> resendGiftCoffeeEmail(@PathVariable String giftCode, // @Validated
    @RequestBody GiftCoffeeEmail giftCoffeeEmail) {
        pngService.resendGiftCoffeeSubscriptionEmail(giftCoffeeEmail, giftCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
