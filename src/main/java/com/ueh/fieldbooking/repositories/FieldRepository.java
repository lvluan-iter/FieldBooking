package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findByName(String name);
    List<Field> findByType(Type type);
}
