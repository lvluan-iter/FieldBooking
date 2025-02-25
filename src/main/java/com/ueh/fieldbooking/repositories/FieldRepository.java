package com.ueh.fieldbooking.repositories;

import com.ueh.fieldbooking.models.Field;
import com.ueh.fieldbooking.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findByName(String name);
    List<Field> findByType(Type type);

    @Query(value = "SELECT * FROM fields " +
            "WHERE to_tsvector('english', name || ' ' || address) @@ to_tsquery(:key)",
            nativeQuery = true)
    List<Field> searchFields(@Param("key") String key);
}
