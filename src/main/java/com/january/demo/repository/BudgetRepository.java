package com.january.demo.repository;

import com.january.demo.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser_Id(Long userId);

    List<Budget> findByUser_IdAndCategory_Id(Long userId, Long categoryId);

    List<Budget> findByUser_IdAndStartDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Budget> findByUser_IdAndEndDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Budget> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndName(Long userId, String name);
}
