package com.adventure.repository;

import com.adventure.model.Adventure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
}
