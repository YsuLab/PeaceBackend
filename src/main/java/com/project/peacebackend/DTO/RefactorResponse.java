package com.project.peacebackend.DTO;

import java.util.List;

public record RefactorResponse(
        String cognitiveReframe,
        List<String> actionPlan,
        String distortionType
) {}