package com.statestreet.car.rental.repository;

import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.types.CarType;

import java.util.List;

public interface ReservationRepository {
    List<Car> findCarsByCarType(CarType carType);

    List<Reservation> findReservationsByCarType(CarType carType);

    void reserve(Reservation reservation);
}
