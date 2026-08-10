package com.january.demo.repository;

import com.january.demo.entity.Goal;
import com.january.demo.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUser_Id(Long userId);

    List<Goal> findByUser_IdAndStatus(Long userId, GoalStatus status);

    List<Goal> findByUser_IdAndDeadlineBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Goal> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndName(Long userId, String name);
}
