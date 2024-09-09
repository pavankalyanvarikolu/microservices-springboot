package com.pavan.microservices.order.controller;

import com.pavan.microservices.order.dto.OrderRequest;
import com.pavan.microservices.order.service.OrderService;
import com.pavan.microservices.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest){
        return CompletableFuture.supplyAsync(() -> {
            orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully");
        }).exceptionally(ex -> handleException(ex));
    }

    private ResponseEntity<String> handleException(Throwable ex) {
        if (ex instanceof CompletionException && ex.getCause() instanceof OutOfStockException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Oops! Something went wrong, please order after some time!");
        }
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<String> handleOutOfStockException(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
