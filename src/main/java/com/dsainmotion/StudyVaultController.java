package com.dsainmotion;

import com.dsainmotion.entity.StudyVault;
import com.dsainmotion.entity.User;
import com.dsainmotion.repository.StudyVaultRepository;
import com.dsainmotion.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@Controller
public class StudyVaultController {

    @Autowired
    private StudyVaultRepository studyVaultRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Load user info (header)
    private void loadUserData(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");

        if (userId != null) {
            User user = userRepository.getUserById(userId);

            if (user != null) {
                String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "User";
                model.addAttribute("name", firstName);
                model.addAttribute("email", user.getEmail());

                String initials = firstName.isEmpty()
                        ? "U"
                        : String.valueOf(Character.toUpperCase(firstName.charAt(0)));

                model.addAttribute("initials", initials);
            }
        }
    }

    // ✅ Study Vault Page
    @GetMapping("/study-vault")
    public String studyVault(HttpSession session, Model model) {
        model.addAttribute("activePage", "study-vault");
        model.addAttribute("isLoggedIn", session.getAttribute("userId") != null);
        loadUserData(session, model);
        return "study-vault";
    }

    // 🚀 MAIN PAGE (FIXED)
    @GetMapping("/study/{slug}")
    public String dynamicPage(@PathVariable String slug,
                              HttpSession session,
                              Model model) {

        // 🔐 Login check
        if (session.getAttribute("userId") == null) {
            return "redirect:/login?next=study/" + slug;
        }

        loadUserData(session, model);
        model.addAttribute("activePage", "study-vault");

        Optional<StudyVault> studyVault = studyVaultRepository.findBySlug(slug);

        if (studyVault.isPresent()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, Object> content =
                        objectMapper.readValue(
                                studyVault.get().getContentJson(),
                                Map.class
                        );

                // 🔥 FIX: ensure sections is a LIST (not string)
                Object sectionsObj = content.get("sections");

                if (sectionsObj instanceof String) {
                    List<Map<String, Object>> sectionsList =
                            objectMapper.readValue((String) sectionsObj, List.class);
                    content.put("sections", sectionsList);
                }

                model.addAttribute("content", content);
                model.addAttribute("topic", studyVault.get().getTopic());
                model.addAttribute("subtopic", studyVault.get().getSubtopic());

            } catch (Exception e) {
                e.printStackTrace(); // helpful for debugging
                model.addAttribute("content", null);
            }
        }

        return "dynamic-template";
    }
}