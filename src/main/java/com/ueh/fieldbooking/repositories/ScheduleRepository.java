package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.dtos.ScheduleResponse;
import com.ueh.fieldbooking.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.date = :date AND s.startTime > :now")
    int deleteByDateAfterNow(@Param("date") LocalDate date, @Param("now") LocalTime now);

    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.date = :date")
    int deleteByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.field.id = :id AND s.date = :date AND s.status = 'AVAIBLE'")
    List<Schedule> findByFieldAndDate(@Param("id") Long id, @Param("date") LocalDate date);
}
