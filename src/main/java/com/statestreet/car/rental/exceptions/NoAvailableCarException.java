package com.statestreet.car.rental.exceptions;

import com.statestreet.car.rental.types.CarType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class NoAvailableCarException extends RuntimeException {
    public NoAvailableCarException(CarType carType, LocalDateTime beginDate, LocalDateTime endDate) {
        super(String.format("There isn't any car for given %s type between %s and %s", carType, beginDate, endDate));
        log.warn("No {} available for dates between {} and {}", carType, beginDate, endDate);
    }
}
