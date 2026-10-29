package com.schoolchan.student;

import com.schoolchan.staff.AuditLog;
import com.schoolchan.staff.AuditLogRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * Staff management of student accounts and enrollment requests.
 */
@Controller
@RequestMapping("/staff/students")
public class StaffStudentController {

    private final StudentService studentService;
    private final AuditLogRepository auditLogRepository;

    public StaffStudentController(StudentService studentService, AuditLogRepository auditLogRepository) {
        this.studentService = studentService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("accounts", studentService.all());
        model.addAttribute("requests", studentService.pendingRequests());
        model.addAttribute("pageTitle", "Student accounts — SchoolChan staff");
        // Temporary passwords are handed over through the session, never a URL.
        var session = request.getSession(false);
        if (session != null) {
            Object temp = session.getAttribute("sc-temp-password");
            if (temp != null) {
                model.addAttribute("tempPassword", temp.toString());
                session.removeAttribute("sc-temp-password");
            }
            Object code = session.getAttribute("sc-reset-code");
            if (code != null) {
                model.addAttribute("resetCode", code.toString());
                session.removeAttribute("sc-reset-code");
            }
            Object err = session.getAttribute("sc-student-error");
            if (err != null) {
                model.addAttribute("error", err.toString());
                session.removeAttribute("sc-student-error");
            }
        }
        return "staff/students";
    }

    @PostMapping("/provision")
    public String provision(@RequestParam String studentId,
                            @RequestParam(required = false) String displayName,
                            @RequestParam(required = false) String gradeSection,
                            @RequestParam(required = false) String password,
                            @AuthenticationPrincipal UserDetails user,
                            HttpServletRequest request) {
        if (!studentService.isValidStudentId(studentId)) {
            request.getSession().setAttribute("sc-student-error", "That does not look like a student ID number.");
            return "redirect:/staff/students";
        }
        if (studentService.find(studentId).isPresent()) {
            request.getSession().setAttribute("sc-student-error", "That student ID already has an account.");
            return "redirect:/staff/students";
        }
        String temp = (password == null || password.isBlank()) ? studentService.generatePassword() : password;
        StudentAccount account = studentService.provision(studentId, displayName, gradeSection,
                user == null ? "staff" : user.getUsername(), temp);
        request.getSession().setAttribute("sc-temp-password", account.getStudentId() + " / " + temp);
        audit("STUDENT_PROVISION", account.getStudentId(), user);
        return "redirect:/staff/students";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam UUID id, @AuthenticationPrincipal UserDetails user) {
        studentService.all().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .ifPresent(a -> {
                    studentService.setEnabled(a, !a.isEnabled());
                    audit("STUDENT_TOGGLE", a.getStudentId(), user);
                });
        return "redirect:/staff/students";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam UUID id,
                                @AuthenticationPrincipal UserDetails user,
                                HttpServletRequest request) {
        studentService.all().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .ifPresent(a -> {
                    String newPassword = studentService.resetPassword(a, null);
                    request.getSession().setAttribute("sc-temp-password",
                            a.getStudentId() + " / " + newPassword);
                    audit("STUDENT_RESET", a.getStudentId(), user);
                });
        return "redirect:/staff/students";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@RequestParam UUID id,
                          @AuthenticationPrincipal UserDetails user,
                          HttpServletRequest request) {
        studentService.pendingRequests().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .ifPresent(r -> {
                    var provisioned = studentService.approve(r, user == null ? "staff" : user.getUsername());
                    provisioned.ifPresent(password ->
                            request.getSession().setAttribute("sc-temp-password",
                                    r.getStudentId() + " / " + password));
                    audit("STUDENT_ENROLL_APPROVE", r.getStudentId(), user);
                });
        return "redirect:/staff/students";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@RequestParam UUID id, @AuthenticationPrincipal UserDetails user) {
        studentService.pendingRequests().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .ifPresent(r -> {
                    studentService.reject(r, user == null ? "staff" : user.getUsername());
                    audit("STUDENT_ENROLL_REJECT", r.getStudentId(), user);
                });
        return "redirect:/staff/students";
    }

    @PostMapping("/reset-code")
    public String issueResetCode(@RequestParam String studentId,
                                 @AuthenticationPrincipal UserDetails user,
                                 HttpServletRequest request) {
        String code = studentService.issueResetCode(studentId);
        if (code == null) {
            request.getSession().setAttribute("sc-student-error", "No account with that student ID.");
        } else {
            request.getSession().setAttribute("sc-reset-code", studentId + " / " + code);
            audit("STUDENT_RESET_CODE", studentId, user);
        }
        return "redirect:/staff/students";
    }

    private void audit(String action, String detail, UserDetails user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail + (user != null ? " by " + user.getUsername() : ""));
        auditLogRepository.save(log);
    }
}
