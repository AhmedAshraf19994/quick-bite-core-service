package com.quick_bite.core_service.restaurant.exceptions;

import com.quick_bite.core_service.common.exception.AppException;

public class InvalidRestaurantStatus extends AppException {

    public InvalidRestaurantStatus() {
        super("Invalid restaurant status");
    }
}
