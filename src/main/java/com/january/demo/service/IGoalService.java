package com.january.demo.service;

import com.january.demo.dto.request.GoalContributionRequest;
import com.january.demo.dto.request.GoalRequest;
import com.january.demo.dto.response.GoalResponse;

import java.util.List;

public interface IGoalService {

    GoalResponse create(GoalRequest request);

    List<GoalResponse> getAll();

    GoalResponse getById(Long id);

    GoalResponse update(Long id, GoalRequest request);

    void delete(Long id);

    GoalResponse contribute(Long id, GoalContributionRequest request);
}