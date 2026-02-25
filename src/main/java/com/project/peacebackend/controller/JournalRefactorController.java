package com.project.peacebackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.peacebackend.DTO.RefactorRequest;
import com.project.peacebackend.DTO.RefactorResponse;
import com.project.peacebackend.service.ReflectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal")
public class JournalRefactorController {


    private final ReflectionService reflectionService;

    public JournalRefactorController(ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
    }

    @PostMapping("/reflector")
    public RefactorResponse refactorThought(@RequestBody RefactorRequest request) throws JsonProcessingException {

        // Combining the system instructions with the user's raw entry for a simple API call
        String jsonOutput = null;
        try {
            jsonOutput = ReflectionService.refactorEntry(request.rawEntry());
        } // Change for a better api response
        catch (Exception e) {
            e.printStackTrace();
        }


        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonOutput, RefactorResponse.class);// Returns the clean, refactored text to your mobile app
    }
    @PostMapping("/test-reflector")
    public RefactorResponse testReflector() {
        return new RefactorResponse(
                "The belief that a single missed deadline constitutes a permanent career failure is a clear example of **All-or-Nothing Thinking**. You are conflating a temporary scheduling conflict with an immutable character flaw. This is an **operational failure, not a character defect**.\n\nYour worth as a professional is an aggregate of long-term performance, not a daily binary state. Objectively, your infrastructure is still in a state of improved stability from previous efforts. Viewing this as a total system collapse is inefficient and factually incorrect.\n\nInstead of labeling yourself a failure, analyze the specific bottleneck. Was it a resource constraint or an estimation error? Reframe this as a data point for future optimization, not a final judgment on your competence.",
                java.util.List.of(
                        "RESTORE LAST SNAPSHOT: Access the AWS console via the emergency admin profile. Locate the RDS snapshot labeled 'pre-deployment-v4'. Initiate restore to a new instance ID to prevent overwriting current forensics logs. Estimated time: 15 mins.",
                        "DOCUMENT THE ERROR: Open the post-mortem template in Notion. Specifically detail the timestamp of the flawed migration script execution. Do not assign blame; focus on the mechanical failure point in the CI/CD pipeline that allowed unvalidated code to pass staging.",
                        "RESET STUDY BLOCK: Clear your calendar for the next two hours. Eliminate all notifications. Focus solely on reviewing the specific chapter that caused the confusion. Do not attempt to cover new material until the foundation is re-established."
                ),
                "CATASTROPHIZING"
        );
    }
}