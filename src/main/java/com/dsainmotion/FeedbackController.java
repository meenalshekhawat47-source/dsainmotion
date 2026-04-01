package com.dsainmotion;

import com.dsainmotion.entity.Feedback;
import com.dsainmotion.repository.FeedbackRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/feedback")
    public Map<String, Object> submitFeedback(
            @RequestParam String topic,
            @RequestParam String message,
            HttpSession session
    ) {

        Feedback feedback = new Feedback();

        // ✅ get user id
        String userId = (String) session.getAttribute("userId");

        feedback.setUserId(userId);
        feedback.setTopic(topic);
        feedback.setMessage(message);
        feedback.setIsRead(false);

        // 💾 save to DB
        feedbackRepository.save(feedback);

        // ✅ response for frontend
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        return response;
    }
}