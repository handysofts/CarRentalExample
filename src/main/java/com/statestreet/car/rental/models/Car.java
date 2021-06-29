package com.statestreet.car.rental.models;

import com.statestreet.car.rental.types.CarType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Car {
    private UUID id;
    private String name;
    private String numberPlate;
    private CarType carType;
    private String color;
    private int year;
    //... other remaining fields goes here

    public static CarBuilder builder(String name, String numberPlate, CarType carType) {
        return new CarBuilder().id(UUID.randomUUID()).name(name).numberPlate(numberPlate).carType(carType);
    }
}
