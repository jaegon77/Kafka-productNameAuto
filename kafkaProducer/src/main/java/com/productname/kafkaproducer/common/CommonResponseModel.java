package com.productname.kafkaproducer.common;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CommonResponseModel<T> {
    private HttpStatus status;
    private String message;
    private T data;
}
