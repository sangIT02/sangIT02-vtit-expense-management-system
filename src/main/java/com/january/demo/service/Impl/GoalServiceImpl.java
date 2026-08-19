package com.january.demo.service.Impl;

import com.january.demo.dto.request.GoalContributionRequest;
import com.january.demo.dto.request.GoalRequest;
import com.january.demo.dto.response.GoalResponse;
import com.january.demo.entity.Goal;
import com.january.demo.entity.User;
import com.january.demo.enums.GoalStatus;
import com.january.demo.exception.ConflictException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.repository.GoalRepository;
import com.january.demo.repository.UserRepository;
import com.january.demo.service.IGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements IGoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GoalResponse create(GoalRequest request) {
        Long userId = getCurrentUserId();
        if (goalRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Muc tieu da ton tai");
        }

        Goal goal = Goal.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung")))
                .name(request.name())
                .targetAmount(request.targetAmount())
                .currentAmount(request.currentAmount() == null ? BigDecimal.ZERO : request.currentAmount())
                .deadline(request.deadline())
                .description(request.description())
                .build();
        refreshStatus(goal);
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Override
    public List<GoalResponse> getAll() {
        Long userId = getCurrentUserId();
        return goalRepository.findByUser_Id(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public GoalResponse getById(Long id) {
        return toResponse(findOwned(id));
    }

    @Override
    @Transactional
    public GoalResponse update(Long id, GoalRequest request) {
        Long userId = getCurrentUserId();
        Goal goal = findOwned(id);

        if (!goal.getName().equals(request.name())
                && goalRepository.existsByUser_IdAndName(userId, request.name())) {
            throw new ConflictException("Muc tieu da ton tai");
        }

        goal.setName(request.name());
        goal.setTargetAmount(request.targetAmount());
        goal.setDeadline(request.deadline());
        goal.setDescription(request.description());
        refreshStatus(goal);
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        goalRepository.delete(findOwned(id));
    }

    @Override
    @Transactional
    public GoalResponse contribute(Long id, GoalContributionRequest request) {
        Goal goal = findOwned(id);
        if (goal.getStatus() == GoalStatus.CANCELLED) {
            throw new ConflictException("Muc tieu da bi huy khong the gop tien");
        }

        goal.setCurrentAmount(goal.getCurrentAmount().add(request.amount()));
        refreshStatus(goal);
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    private Goal findOwned(Long id) {
        return goalRepository.findByIdAndUser_Id(id, getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Khong tim thay muc tieu"));
    }

    private void refreshStatus(Goal goal) {
        if (goal.getStatus() == GoalStatus.CANCELLED) {
            return;
        }
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }
    }

    private GoalResponse toResponse(Goal goal) {
        int percentage = goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0
                ? 0
                : goal.getCurrentAmount().multiply(BigDecimal.valueOf(100))
                        .divide(goal.getTargetAmount(), 0, RoundingMode.HALF_UP)
                        .intValue();

        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                percentage,
                goal.getDeadline(),
                goal.getStatus(),
                goal.getDescription(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}