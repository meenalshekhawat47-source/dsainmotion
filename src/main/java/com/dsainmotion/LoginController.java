package com.dsainmotion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.dsainmotion.entity.User;
import com.dsainmotion.entity.Admin;
import com.dsainmotion.entity.Feedback;
import com.dsainmotion.repository.UserRepository;
import com.dsainmotion.repository.AdminRepository;
import com.dsainmotion.repository.FeedbackRepository;
import com.dsainmotion.repository.StudyVaultRepository;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dsainmotion.entity.StudyVault;
import com.dsainmotion.entity.User;


@Controller
public class LoginController {
    @Autowired
private StudyVaultRepository studyVaultRepository;
@Autowired
private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Helper method to generate user initials
    private String getInitials(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return "U";
        }
        return String.valueOf(Character.toUpperCase(firstName.trim().charAt(0)));
    }

    // Helper method to load user data and add to model
    private void loadUserData(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            User user = userRepository.getUserById(userId);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("name", user.getFirstName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("initials", getInitials(user.getFirstName()));
            }
        }
    }

    // Helper method to check if user is authenticated
    private String checkAuth(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return null;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String landing(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "landing";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "next", required = false) String next, Model model) {
        if (next != null && !next.trim().isEmpty()) {
            model.addAttribute("next", next.trim());
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "dashboard";
    }


    @GetMapping("/help")
    public String help(HttpSession session, Model model) {
        model.addAttribute("activePage", "help");
        loadUserData(session, model);
        return "help";
    }

    @GetMapping("/about")
    public String about(HttpSession session, Model model) {
        model.addAttribute("activePage", "about");
        loadUserData(session, model);
        return "about";
    }


    @GetMapping("/stack")
    public String stack(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "stack";
    }

    @GetMapping("/queue")
    public String queue(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "queue";
    }

    @GetMapping("/tree")
    public String tree(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "tree";
    }

    @GetMapping("/sorting")
    public String sorting(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "sorting";
    }

    @GetMapping("/searching")
    public String searching(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "searching";
    }

    @GetMapping("/array")
    public String array(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "array";
    }
        @GetMapping("/linkedlist")
    public String linkedList(HttpSession session, Model model) {
        String redirect = checkAuth(session);
        if (redirect != null) return redirect;
        model.addAttribute("activePage", "home");
        loadUserData(session, model);
        return "linkedlist";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam(name = "userid") String userid,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "next", required = false) String next,
            Model model,
            HttpSession session) {

        User user = userRepository.getUserById(userid);

        if (user != null && (passwordEncoder.matches(password, user.getPass()))) {
            session.setAttribute("userId", user.getUserId());
            model.addAttribute("name", user.getFirstName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("initials", getInitials(user.getFirstName()));
            if (next != null && !next.trim().isEmpty()) {
                return "redirect:/" + next;
            }
            return "redirect:/dashboard";
        } else {
            String errorUrl = "/login?error=true";
            if (next != null && !next.trim().isEmpty()) {
                errorUrl += "&next=" + next;
            }
            return "redirect:" + errorUrl;
        }
    }
    @GetMapping("/forgot-password")
public String forgotPasswordPage() {
    return "forgot-password";
}
@Autowired
private JavaMailSender mailSender;

@PostMapping("/forgot-password")
public String processForgotPassword(@RequestParam String email, Model model) {

    User user = userRepository.findByEmail(email);

    if(user != null) {
        String token = java.util.UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        String link = "http://localhost:8082/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Click this link to reset your password:\n" + link);

        mailSender.send(message);

        model.addAttribute("message", "Reset link sent to your email!");
    } else {
        model.addAttribute("message", "Email not found!");
    }

    return "forgot-password";
}

    @PostMapping("/admin-login")
    public String loginAdmin(
            @RequestParam(name = "adminId") String adminId,
            @RequestParam(name = "password") String password,
            Model model,
            HttpSession session) {

        System.out.println("Admin login attempt: adminId=" + adminId + ", password=" + password);

        Admin admin = adminRepository.getAdminById(adminId);

        System.out.println("Found admin: " + (admin != null ? admin.getAdminId() : "null"));

        if (admin != null) {
            System.out.println("Stored password: " + admin.getPassword());
            System.out.println("Password match: " + admin.getPassword().equals(password));
        }

        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("adminId", admin.getAdminId());
            session.setAttribute("adminName", admin.getFirstName() + " " + admin.getLastName());
            session.setAttribute("adminEmail", admin.getEmail());
            return "redirect:/admin-dashboard";
        } else {
            return "redirect:/login?adminError=true";
        }
    }

   @GetMapping("/admin-dashboard")
public String adminDashboard(HttpSession session, Model model) {

    if (session.getAttribute("adminId") == null) {
        return "redirect:/login";
    }


    model.addAttribute("adminName", session.getAttribute("adminName"));
    model.addAttribute("adminEmail", session.getAttribute("adminEmail"));


    model.addAttribute("users", userRepository.findAll());
    model.addAttribute("feedbacks", feedbackRepository.findAll());

    model.addAttribute("studyVault", studyVaultRepository.findAll());

    return "admin-dashboard";
}
@GetMapping("/admin/edit/{id}")
public String editPage(@PathVariable int id, Model model) {

    StudyVault data = studyVaultRepository.findById(id).orElse(null);

    model.addAttribute("topic", data);

    return "admin-edit";
}
@PostMapping("/admin/update")
public String update(@RequestParam int id,
                     @RequestParam String contentJson) {

    StudyVault data = studyVaultRepository.findById(id).orElse(null);

    if (data != null) {
        data.setContentJson(contentJson);
        studyVaultRepository.save(data);
    }

    return "redirect:/admin-dashboard";
}

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam("userId") String userId, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            System.err.println("Failed to delete user: " + userId);
            e.printStackTrace();
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/feedback/read")
    @ResponseBody
    public String markFeedbackRead(@RequestParam("feedbackId") Integer feedbackId, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "unauthorized";
        }
        try {
            Feedback fb = feedbackRepository.findById(feedbackId).orElse(null);
            if (fb != null) {
                fb.setIsRead(true);
                feedbackRepository.save(fb);
                return "success";
            }
            return "not_found";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/admin/profile")
    public String adminProfile(HttpSession session, Model model) {
        String adminId = (String) session.getAttribute("adminId");
        if (adminId == null) return "redirect:/login";

        Admin admin = adminRepository.getAdminById(adminId);
        if (admin != null) {
            model.addAttribute("admin", admin);
            model.addAttribute("adminName", admin.getFirstName() + " " + admin.getLastName());
            String initials = admin.getFirstName().trim().isEmpty() ? "A" : String.valueOf(Character.toUpperCase(admin.getFirstName().trim().charAt(0)));
            model.addAttribute("initials", initials);
            model.addAttribute("adminEmail", admin.getEmail());
        }
        return "admin-profile";
    }

    @PostMapping("/admin/profile/update")
    public String updateAdminProfile(
            @RequestParam(name = "firstName") String firstName,
            @RequestParam(name = "lastName") String lastName,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phone") String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("adminId");
        if (adminId == null) return "redirect:/login";

        Admin admin = adminRepository.getAdminById(adminId);
        if (admin != null) {
            if (!firstName.matches("^[A-Za-z]+$") || !lastName.matches("^[A-Za-z]+$")) {
                redirectAttributes.addFlashAttribute("error", "Name must contain only alphabets.");
                return "redirect:/admin/profile";
            }
            if (!phone.matches("^\\d{10}$")) {
                redirectAttributes.addFlashAttribute("error", "Phone must be exactly 10 digits.");
                return "redirect:/admin/profile";
            }
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                redirectAttributes.addFlashAttribute("error", "Enter a valid email address.");
                return "redirect:/admin/profile";
            }

            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setPhone(phone);
            adminRepository.save(admin);

            // Update session attributes
            session.setAttribute("adminName", admin.getFirstName() + " " + admin.getLastName());
            session.setAttribute("adminEmail", admin.getEmail());

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Admin not found.");
        }

        return "redirect:/admin/profile";
    }

    @PostMapping("/admin/profile/change-password")
    public String changeAdminPassword(
            @RequestParam(name = "currentPassword") String currentPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("adminId");
        if (adminId == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("pwdError", "New passwords do not match.");
            return "redirect:/admin/profile";
        }

        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            redirectAttributes.addFlashAttribute("pwdError", "Password must be 8+ chars with A-Z, a-z, 0-9 & special char.");
            return "redirect:/admin/profile";
        }

        Admin admin = adminRepository.getAdminById(adminId);
        if (admin != null) {
            if (admin.getPassword().equals(currentPassword)) {
                admin.setPassword(newPassword);
                adminRepository.save(admin);
                redirectAttributes.addFlashAttribute("pwdSuccess", "Password changed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("pwdError", "Current password is incorrect.");
            }
        } else {
            redirectAttributes.addFlashAttribute("pwdError", "Admin not found.");
        }

        return "redirect:/admin/profile";
    }


    @GetMapping("/register")
    public String registerPage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "success", required = false) String success,
            Model model) {

        if ("userexists".equals(error)) {
            model.addAttribute("message", "User ID already exists. Please choose another.");
            model.addAttribute("messageType", "error");
        } else if ("emailexists".equals(error)) {
            model.addAttribute("message", "Email already registered. Please login or use a different email.");
            model.addAttribute("messageType", "error");
        } else if ("true".equals(error)) {
            model.addAttribute("message", "Registration failed due to server error. Please try again.");
            model.addAttribute("messageType", "error");
        } else if (success != null) {
            model.addAttribute("message", "Registration successful! Please login.");
            model.addAttribute("messageType", "success");
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam(name = "user_id") String user_id,
            @RequestParam(name = "first_name") String first_name,
            @RequestParam(name = "last_name") String last_name,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "pass") String pass,
            Model model) {

        System.out.println("[registerUser] userid='" + user_id + "' email='" + email + "'");

        // Preserve form data
        model.addAttribute("user_id", user_id);
        model.addAttribute("first_name", first_name);
        model.addAttribute("last_name", last_name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);

        if (userRepository.existsByUserId(user_id)) {
            model.addAttribute("message", "User ID already exists. Please choose another.");
            model.addAttribute("messageType", "error");
            return "register";
        }
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("message", "Email already registered. Please login or use a different email.");
            model.addAttribute("messageType", "error");
            return "register";
        }

        try {
            User user = new User();
            user.setUserId(user_id);
            user.setFirstName(first_name);
            user.setLastName(last_name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPass(passwordEncoder.encode(pass));

            userRepository.save(user);
            System.out.println("[registerUser] success user saved: " + user_id);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Registration failed due to server error. Please try again.");
            model.addAttribute("messageType", "error");
            return "register";
        }
    }
    @GetMapping("/mail-test")
@ResponseBody
public String testMail() {
    try {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("YOUR_EMAIL@gmail.com");
        msg.setSubject("Test");
        msg.setText("Working");

        mailSender.send(msg);
        return "Mail sent!";
    } catch (Exception e) {
        e.printStackTrace();
        return "Error: " + e.getMessage();
    }
}

}