package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :date AND b.startTime > :time")
    List<Booking> findByDateAfterNow(@Param("date") LocalDate date, @Param("time") LocalTime time);

    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :date")
    List<Booking> findByDate(@Param("date") LocalDate date);
}
