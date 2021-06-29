package com.statestreet.car.rental;

import com.statestreet.car.rental.exceptions.NoAvailableCarException;
import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.repository.ReservationRepository;
import com.statestreet.car.rental.services.ReservationService;
import com.statestreet.car.rental.types.CarType;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class End2EndTest {
    @Autowired
    private ReservationService reservationService;
    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    public void reserve() {
        int days = 1;
        Car car = Car.builder("carName", "111", CarType.SPORTS_CAR).build();
        when(reservationRepository.findCarsByCarType(CarType.SPORTS_CAR)).thenReturn(Lists.list(car));
        Reservation reservation = new Reservation(randomUUID(), car.getId(), randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusDays(days), car.getCarType());

        Car reservedCar = reservationService.reserve(reservation.getCustomerId(), reservation.getCarType(), reservation.getBeginDate(), days);

        assertThat(reservedCar.getId()).isEqualByComparingTo(reservation.getCarId());
        //and
		when(reservationRepository.findReservationsByCarType(any())).thenReturn(Lists.list(reservation));
		assertThatThrownBy(() -> reservationService.reserve(reservation.getCustomerId(), reservation.getCarType(), reservation.getBeginDate(), days))
				.isInstanceOf(NoAvailableCarException.class);
	}
}
