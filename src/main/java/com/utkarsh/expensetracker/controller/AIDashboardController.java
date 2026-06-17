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

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

    public AIDashboardController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
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

        String breakdown = categoryExpenses.isEmpty()
                ? "No dynamic expense metrics tracked yet."
                : categoryExpenses.stream()
                .map(c -> c.getCategory() + ": ₹" + c.getAmount())
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
                "model", "gemini-2.5-flash",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(geminiApiKey);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            String aiReport = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            return ResponseEntity.ok(Map.of("report", aiReport));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("report", "AI analysis engine offline: " + e.getMessage()));
        }
    }
}