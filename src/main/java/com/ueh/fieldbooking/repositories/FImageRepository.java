package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.FieldImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FImageRepository extends JpaRepository<FieldImage, Long> {
    List<FieldImage> findByField(Field field);
}
