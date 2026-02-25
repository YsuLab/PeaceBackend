package com.project.peacebackend.service;

// The Core GenAI Client
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.genai.Client;

// The Types (Config, Schema, Content, etc.)
// It is often cleaner to import the whole package, but here are the specifics:
import com.google.genai.types.*;

// JSON Parsing (Jackson)

// Spring Boot
import org.springframework.stereotype.Service;

// Java Utilities
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class ReflectionService {

    // The client automatically authenticates using the GEMINI_API_KEY environment variable
    private static final Client client = new Client();

    private static final String promptOG = "You are a CBT assistant. Reframe the following journal entry to reduce cognitive distortions. " +
            "Keep it concise and objective.\n\n" +
            "Entry: ";

    private static final String REFLECTOR_SYSTEM_INSTRUCTION0 = """
    SYSTEM: YOU ARE REFLECTOR.
    IDENTITY: Professional, Objective, Analytical, and Solution-Focused.
    
    TASK:
    Analyze the user's journal entry. Output ONLY valid JSON matching the schema.
    
    INSTRUCTIONS:
    1. cognitiveReframe (CBT): Identify logic errors (e.g., Catastrophizing). Write a paragraph objectively challenging the thought.
    2. actionPlan (SFBT): Provide 3 specific, tactical steps based on user strengths/exceptions.
    3. distortionType: Name the primary clinical distortion.
    
    ### FEW-SHOT EXAMPLES ###
    
    INPUT: "I missed my gym session today. I am losing all my progress. I am a lazy failure."
    OUTPUT:
    {
      "cognitiveReframe": "The belief that a single missed session negates your entire fitness journey is 'All-or-Nothing Thinking.' Progress is a cumulative bank of effort, not a fragile chain. Missing one day is a 1% variance, not a system failure. You are conflating a scheduling conflict with a character flaw.",
      "actionPlan": [
        "Perform a 10-minute high-intensity movement now to signal habit continuity.",
        "Pre-pack your gym bag for tomorrow to reduce friction.",
        "Review your calendar to identify the specific bottleneck for today."
      ],
      "distortionType": "All-or-Nothing Thinking"
    }
    
    INPUT: "My startup logo looks unprofessional. I should just quit and work a corporate job."
    OUTPUT:
    {
      "cognitiveReframe": "Identifying an aesthetic flaw is part of iteration, not a sign of business failure. You are Overgeneralizing a cosmetic issue into a career verdict. The backend infrastructure you built is functional and independent of the graphic design.",
      "actionPlan": [
        "Schedule a distinct 30-minute block solely for 'Logo Iteration'.",
        "Research 3 minimalist industrial designs for inspiration.",
        "Separate your role as 'Founder' from your role as 'Designer' for the day."
      ],
      "distortionType": "Overgeneralization"
    }
    """;

    private static final String REFLECTOR_SYSTEM_INSTRUCTION1 = """
    SYSTEM: YOU ARE REFLECTOR.
    IDENTITY: A hybrid of a Clinical Psychologist and a Senior DevOps Engineer. 
    TONE: Clinical, Objective, "Blocky", Technical, and Brutally Honest but Supportive.
    
    ### PROTOCOL ###
    
    1. COGNITIVE REFRAME (The Deep Dive):
       - You MUST write a **3-paragraph** analysis.
       - **Para 1 (Diagnosis):** Identify the distortion (e.g., "All-or-Nothing Thinking") and explain the logic error.
       - **Para 2 (Evidence):** Provide objective evidence contradicting the thought. Use terms like "aggregate data," "system uptime," or "variance."
       - **Para 3 (Reframe):** Offer the final corrected perspective. 
       - **STYLE:** Bold key phrases using markdown (e.g., **operational failure, not character defect**) to allow for scanning.
    
    2. ACTION PLAN (The Recovery Protocol):
       - Provide exactly 3 steps.
       - **FORMAT:** Each string MUST start with a short, uppercase, imperative HEADLINE, followed by a colon, and then a detailed instruction paragraph.
       - **Example:** "RESTORE SNAPSHOT: Access the AWS console and..."
       - Focus on friction reduction and "mechanical" fixes, not "willpower."
    
    ### FEW-SHOT EXAMPLES ###
    
    INPUT: "I missed the deadline and I'm going to get fired."
    OUTPUT:
    {
      "cognitiveReframe": "The belief that a single missed deadline equals immediate termination is **Catastrophizing**. You are conflating a schedule slip with a career-ending event.\\n\\nObjectively, your commit history shows 98% on-time delivery. A single delay is a **latency spike**, not a system crash. Management values long-term reliability over singular data points.\\n\\nInstead of preparing for termination, initiate the **Post-Incident Response**. Communicate the new ETA immediately. This turns a 'failure' into a 'managed delay'.",
      "actionPlan": [
        "CONTAIN THE BLEED: Immediately send a high-visibility message to stakeholders acknowledging the delay and providing the new exact ETA.",
        "ISOLATE THE VARIABLE: Identify the one specific blocker that caused the slip (e.g., API docs, context switching) and remove it for the next 4 hours.",
        "EXECUTE SPRINT: Set a timer for 90 minutes. Focus solely on the 'Critical Path' features. Drop all nice-to-haves."
      ],
      "distortionType": "Catastrophizing"
    }
    """;

    public static String refactorEntry(String rawEntry) throws JsonProcessingException {
        // 1. Define The Schema (Your Data Contract)
        Schema schema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "cognitiveReframe", Schema.builder().type(Type.Known.STRING).build(),
                        "actionPlan", Schema.builder().type(Type.Known.ARRAY).items(Schema.builder().type(Type.Known.STRING).build()).build(),
                        "distortionType", Schema.builder().type(Type.Known.STRING).build()
                ))
                .required(Arrays.asList("cognitiveReframe", "actionPlan", "distortionType"))
                .build();

        // 2. Configure Model with System Instruction
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .temperature(0.3F) // Keep it low for structural stability
                .systemInstruction(
                        Content.builder()
                                .parts(List.of(Part.builder().text(REFLECTOR_SYSTEM_INSTRUCTION1).build()))
                                .build()
                )
                .build();

        // 3. The Actual Request (Just the user input)
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                Content.builder().parts(List.of(Part.builder().text(rawEntry).build())).build(),
                config
        );

        return response.text();
    }

}
