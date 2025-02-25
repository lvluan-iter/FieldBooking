package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WHourRepository extends JpaRepository<WorkHour, Long> {
    Optional<WorkHour> findByDayOfWeek(String dayOfWeek);

    @Query("SELECT COUNT(w) FROM WorkHour w WHERE w.dayOfWeek = :dayOfWeek")
    int countWorkHoursByDay(@Param("dayOfWeek") String dayOfWeek);
}
