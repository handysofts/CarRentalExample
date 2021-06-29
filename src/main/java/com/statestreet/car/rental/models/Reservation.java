package com.statestreet.car.rental.models;

import com.statestreet.car.rental.types.CarType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Reservation {
    private UUID id;
    private UUID carId;
    private UUID customerId;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private CarType carType;
}
