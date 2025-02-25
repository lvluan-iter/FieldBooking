package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    Optional<Type> findByName(String name);
    Boolean existsByName(String name);
}
