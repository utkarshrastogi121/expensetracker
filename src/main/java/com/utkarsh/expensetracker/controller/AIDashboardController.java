package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.CategoryExpenseDTO;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.repository.ExpenseRepository;
import com.utkarsh.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIDashboardController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public AIDashboardController(ExpenseRepository expenseRepository, UserRepository userRepository, @Value("${gemini.api.key}") String geminiApiKey) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.geminiApiKey = geminiApiKey;
    }

    @GetMapping("/generate-report")
    public ResponseEntity<Map<String, String>> generateReport(Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("report", "Unauthorized: No valid user session found."));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Long userId = user.getId();

        Double totalSpent = expenseRepository.getTotalExpenseByUserId(userId);
        List<CategoryExpenseDTO> categoryExpenses = expenseRepository.getCategoryWiseExpenses(userId);

        String breakdown = (categoryExpenses == null || categoryExpenses.isEmpty())
                ? "No dynamic expense metrics tracked yet."
                : categoryExpenses.stream()
                .map(c -> (c.getCategory() != null ? c.getCategory() : "Uncategorized") + ": ₹" + c.getAmount())
                .collect(Collectors.joining(", "));

        String systemPrompt = "You are an intelligent, supportive personal financial advisor. " +
                "Analyze the user's total aggregate metrics and breakdown data. " +
                "Provide a brief, clear summary paragraph identifying their biggest spending patterns, " +
                "then list 2 distinct, highly actionable strategic suggestions to improve their savings. " +
                "Format your response cleanly using markdown headings and bullet points.";

        String userPrompt = String.format(
                "User Account Profile: %s. Total Life-to-date Spending Volume: ₹%.2f. " +
                        "Category wise breakdown metrics matrix: [%s]. Please generate my financial report card.",
                email, totalSpent != null ? totalSpent : 0.0, breakdown
        );

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String targetedUrl = BASE_URL + "?key=" + geminiApiKey.trim();

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(targetedUrl, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentNode = (Map<String, Object>) candidates.get(0).get("content");
                    if (contentNode != null && contentNode.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentNode.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            String aiReport = (String) parts.get(0).get("text");
                            return ResponseEntity.ok(Map.of("report", aiReport));
                        }
                    }
                }
            }

            return ResponseEntity.status(502).body(Map.of("report", "Unexpected response structure from Gemini API."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("report", "AI analysis engine offline: " + e.getMessage()));
        }
    }
}