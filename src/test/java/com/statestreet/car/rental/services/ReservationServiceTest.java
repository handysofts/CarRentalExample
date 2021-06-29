package com.statestreet.car.rental.services;

import com.statestreet.car.rental.exceptions.NoAvailableCarException;
import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.repository.ReservationRepository;
import com.statestreet.car.rental.types.CarType;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        when(reservationRepository.findCarsByCarType(any())).thenReturn(Collections.emptyList());
        when(reservationRepository.findReservationsByCarType(any())).thenReturn(Collections.emptyList());
    }

    @Test
    public void getAvailableCars() {
        List<Car> availableCars = reservationService.getAvailableCars(CarType.SPORTS_CAR, LocalDateTime.now(), 3);

        Assertions.assertThat(availableCars).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-28T10:00,1,0",
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-28T10:00,10,0",
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-25T10:00,4,0",
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-28T10:00,4,0",
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-25T10:00,1,1",
            "2021-06-27T09:50,2021-06-30T09:50,2021-06-30T10:00,1,1",
    })
    public void getAvailableCarsAllCases(String reservedBeginDate, String reservedEndDate, String requestedBeginDate, int requestedDays, int expectedAvailableCarCount) {
        CarType carType = CarType.SPORTS_CAR;
        Car car = generateCar(carType);
        when(reservationRepository.findCarsByCarType(any())).thenReturn(Lists.list(car));
        when(reservationRepository.findReservationsByCarType(any())).thenReturn(Lists.list(new Reservation(UUID.randomUUID(), car.getId(), UUID.randomUUID(), parse(reservedBeginDate, ISO_LOCAL_DATE_TIME), parse(reservedEndDate, ISO_LOCAL_DATE_TIME), carType)));

        List<Car> availableCars = reservationService.getAvailableCars(carType, parse(requestedBeginDate, ISO_LOCAL_DATE_TIME), requestedDays);

        Assertions.assertThat(availableCars.size()).isEqualTo(expectedAvailableCarCount);
    }

    @Test
    public void reserve() {
        CarType carType = CarType.SPORTS_CAR;
        Car car = generateCar(carType);
        when(reservationRepository.findCarsByCarType(any())).thenReturn(Lists.list(car));

        Car reservedCar = reservationService.reserve(UUID.randomUUID(), carType, LocalDateTime.now(), 1);

        Assertions.assertThat(reservedCar).isEqualTo(car);
    }

    @Test
    public void reserve_whenNoCarAvailable() {
        CarType carType = CarType.SPORTS_CAR;

        Throwable thrown = Assertions.catchThrowable(() -> reservationService.reserve(UUID.randomUUID(), carType, LocalDateTime.now(), 1));

        Assertions.assertThat(thrown)
                .isInstanceOf(NoAvailableCarException.class)
                .hasMessageContaining(carType.toString());
    }


    private Car generateCar(CarType carType) {
        return Car.builder("carName", "111", carType).build();
    }

}