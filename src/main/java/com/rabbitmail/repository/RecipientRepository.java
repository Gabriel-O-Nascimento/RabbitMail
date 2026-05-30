package com.rabbitmail.repository;

import com.rabbitmail.entity.Recipient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    List<Recipient> findByActiveTrueOrderByNameAsc();

    Optional<Recipient> findByEmail(String email);

    boolean existsByEmail(String email);
}
