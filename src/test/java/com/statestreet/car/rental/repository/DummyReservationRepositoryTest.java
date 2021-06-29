package com.statestreet.car.rental.repository;

import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.types.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.statestreet.car.rental.types.CarType.HATCHBACK;
import static com.statestreet.car.rental.types.CarType.SPORTS_CAR;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DummyReservationRepositoryTest {
    private static final Car CAR_MINE = Car.builder("Hyundai i30", "KR884JM", HATCHBACK).color("Blue").year(2009).build();// :)

    @InjectMocks
    private DummyReservationRepository dummyReservationRepository;

    @BeforeEach
    public void setUp() {
        Map<CarType, List<Car>> cars = new HashMap<>();
        cars.putIfAbsent(HATCHBACK, new ArrayList<>());
        cars.get(HATCHBACK).add(CAR_MINE);
        cars.get(HATCHBACK).add(Car.builder("Hyundai i30", "WW123PK", HATCHBACK).build());
        cars.putIfAbsent(SPORTS_CAR, new ArrayList<>());
        cars.get(SPORTS_CAR).add(Car.builder("Hyundai i30 N", "WW111AB", SPORTS_CAR).build());
        ReflectionTestUtils.setField(dummyReservationRepository, "cars", cars);
    }

    @Test
    public void findCarsByCarType() {
        List<Car> carsByCarType = dummyReservationRepository.findCarsByCarType(CAR_MINE.getCarType());

        assertThat(carsByCarType).isNotEmpty();
        assertThat(carsByCarType.size()).isGreaterThan(1);
    }

    @Test
    public void findReservationsByCarType() {
        List<Reservation> reservationsByCarType = dummyReservationRepository.findReservationsByCarType(CAR_MINE.getCarType());

        assertThat(reservationsByCarType).isEmpty();
    }


    @Test
    public void reserve() {
        LocalDateTime beginDate = LocalDateTime.now();

        dummyReservationRepository.reserve(new Reservation(UUID.randomUUID(), CAR_MINE.getId(), UUID.randomUUID(), beginDate, beginDate.plusDays(1), CAR_MINE.getCarType()));

        List<Reservation> reservationsByCarType = dummyReservationRepository.findReservationsByCarType(CAR_MINE.getCarType());
        assertThat(reservationsByCarType.size()).isEqualTo(1);
    }

}