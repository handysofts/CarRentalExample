package com.statestreet.car.rental.repository;

import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.types.CarType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class is just for demo purpose, real one will be using DB to fetch, store data
 */
@Repository
public class DummyReservationRepository implements ReservationRepository {
    //Those field here just to simulate DB
    private final Map<CarType, List<Car>> cars = new ConcurrentHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    public List<Car> findCarsByCarType(CarType carType) {
        return cars.get(carType);
    }

    @Override
    public List<Reservation> findReservationsByCarType(CarType carType) {
        return reservations.stream()
                .filter(reservation -> reservation.getCarType().equals(carType))
                .collect(Collectors.toList());
    }

    @Override
    public void reserve(Reservation reservation) {
        reservations.add(reservation);
    }


}
