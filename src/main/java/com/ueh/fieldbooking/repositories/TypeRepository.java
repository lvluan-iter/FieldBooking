package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);
    Boolean existsByName(String name);
}
