package com.statestreet.car.rental.services;

import com.statestreet.car.rental.exceptions.NoAvailableCarException;
import com.statestreet.car.rental.models.Car;
import com.statestreet.car.rental.models.Reservation;
import com.statestreet.car.rental.repository.ReservationRepository;
import com.statestreet.car.rental.types.CarType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReadWriteLock reservationLock = new ReentrantReadWriteLock();//this solution will just work for single instance in multithreaded env
    private final ReservationRepository reservationRepository;

    public List<Car> getAvailableCars(CarType carType, LocalDateTime beginDate, int days) {
        reservationLock.readLock().lock();
        try {
            return findAvailableCars(carType, beginDate, beginDate.plusDays(days));
        } finally {
            reservationLock.readLock().unlock();
        }
    }

    public Car reserve(UUID customerId, CarType carType, LocalDateTime beginDate, int days) {
        LocalDateTime endDate = beginDate.plusDays(days);
        reservationLock.writeLock().lock();
        try {
            List<Car> availableCars = findAvailableCars(carType, beginDate, endDate);
            if (availableCars.isEmpty())
                throw new NoAvailableCarException(carType, beginDate, endDate);

            Car selectedCar = availableCars.get(0);
            reservationRepository.reserve(new Reservation(UUID.randomUUID(), selectedCar.getId(), customerId, beginDate, endDate, carType));
            return selectedCar;
        } finally {
            reservationLock.writeLock().unlock();
        }
    }

    private List<Car> findAvailableCars(CarType carType, LocalDateTime beginDate, LocalDateTime endDate) {
        Set<UUID> notAvailableCarIds = reservationRepository.findReservationsByCarType(carType).stream()
                .filter(reservation -> (reservation.getBeginDate().compareTo(beginDate) <= 0 && reservation.getEndDate().compareTo(beginDate) >= 0) ||
                        (reservation.getBeginDate().compareTo(beginDate) >= 0 && reservation.getBeginDate().compareTo(endDate) <= 0))
                .map(Reservation::getCarId)
                .collect(Collectors.toSet());

        return reservationRepository.findCarsByCarType(carType).stream()
                .filter(car -> !notAvailableCarIds.contains(car.getId()))
                .collect(Collectors.toList());
    }
}
