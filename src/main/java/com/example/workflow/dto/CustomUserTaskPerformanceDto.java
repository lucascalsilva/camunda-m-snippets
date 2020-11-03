package com.example.workflow.dto;

import lombok.Data;

@Data
public class CustomUserTaskPerformanceDto {

    private String userName;
    private Long countOfTasks;
    private Long userTime;
    private Long totalTimeInMinutes;
    private Long numberOfUnclaims;
}
