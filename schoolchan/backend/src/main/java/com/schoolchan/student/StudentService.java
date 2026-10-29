package com.schoolchan.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Student accounts are an access gate, not an identity system.
 *
 * <p>Nothing here is ever written to a post, a vote, or a report. Posts stay
 * "Anonymous" and continue to use the salted anonymous session hash, so
 * turning student login on does not make any post attributable to a person.
 */
@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final StudentAccountRepository accounts;
    private final StudentEnrollmentRequestRepository requests;
    private final com.schoolchan.board.StudentResetCodeRepository resetCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentAccountRepository accounts,
                          StudentEnrollmentRequestRepository requests,
                          com.schoolchan.board.StudentResetCodeRepository resetCodeRepository,
                          PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.requests = requests;
        this.resetCodeRepository = resetCodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Validates a student ID number: digits with an optional dash, e.g. 2024-12345. */
    public boolean isValidStudentId(String studentId) {
        if (studentId == null) {
            return false;
        }
        String trimmed = studentId.trim();
        return trimmed.matches("\\d{4,6}(-\\d{2,6})?");
    }

    public Optional<StudentAccount> find(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return Optional.empty();
        }
        return accounts.findByStudentIdIgnoreCase(studentId.trim());
    }

    /** Authenticates a student. Returns empty when the ID or password is wrong. */
    public Optional<StudentAccount> authenticate(String studentId, String password) {
        if (studentId == null || password == null) {
            return Optional.empty();
        }
        Optional<StudentAccount> account = find(studentId);
        if (account.isEmpty() || !account.get().isEnabled()) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(password, account.get().getPasswordHash())) {
            return Optional.empty();
        }
        return account;
    }

    @Transactional
    public StudentAccount provision(String studentId, String displayName, String gradeSection,
                                    String provisionedBy, String temporaryPassword) {
        StudentAccount account = new StudentAccount();
        account.setId(UUID.randomUUID());
        account.setStudentId(studentId.trim());
        account.setDisplayName(displayName == null || displayName.isBlank() ? null : displayName.trim());
        account.setGradeSection(gradeSection == null || gradeSection.isBlank() ? null : gradeSection.trim());
        account.setEnabled(true);
        account.setMustChangePassword(true);
        account.setProvisionedBy(provisionedBy);
        String password = (temporaryPassword == null || temporaryPassword.isBlank())
                ? generatePassword()
                : temporaryPassword;
        account.setPasswordHash(passwordEncoder.encode(password));
        return accounts.save(account);
    }

    @Transactional
    public String resetPassword(StudentAccount account, String newPassword) {
        String password = (newPassword == null || newPassword.isBlank()) ? generatePassword() : newPassword;
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setMustChangePassword(true);
        accounts.save(account);
        return password;
    }

    @Transactional
    public void setEnabled(StudentAccount account, boolean enabled) {
        account.setEnabled(enabled);
        accounts.save(account);
    }

    @Transactional
    public void changePassword(StudentAccount account, String newPassword) {
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        account.setMustChangePassword(false);
        accounts.save(account);
    }

    /** A student asks the staff to onboard a newly enrolled classmate. */
    @Transactional
    public StudentEnrollmentRequest requestEnrollment(String studentId, String displayName,
                                                      String gradeSection, String requestedBy) {
        StudentEnrollmentRequest request = new StudentEnrollmentRequest();
        request.setId(UUID.randomUUID());
        request.setStudentId(studentId == null ? "" : studentId.trim());
        request.setDisplayName(displayName == null || displayName.isBlank() ? null : displayName.trim());
        request.setGradeSection(gradeSection == null || gradeSection.isBlank() ? null : gradeSection.trim());
        request.setRequestedBy(requestedBy);
        request.setStatus("PENDING");
        return requests.save(request);
    }

    @Transactional
    public StudentEnrollmentRequest reject(StudentEnrollmentRequest request, String reviewer) {
        request.setStatus("REJECTED");
        request.setReviewedBy(reviewer);
        return requests.save(request);
    }

    /** Approves a request and provisions the account. Returns the temporary password. */
    @Transactional
    public Optional<String> approve(StudentEnrollmentRequest request, String reviewer) {
        if (find(request.getStudentId()).isPresent()) {
            return Optional.empty();
        }
        String temporaryPassword = generatePassword();
        provision(request.getStudentId(), request.getDisplayName(),
                request.getGradeSection(), reviewer, temporaryPassword);
        request.setStatus("APPROVED");
        request.setReviewedBy(reviewer);
        requests.save(request);
        return Optional.of(temporaryPassword);
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<StudentAccount> all() {
        return accounts.findAllByOrderByStudentIdAsc();
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentRequest> pendingRequests() {
        return requests.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public long enabledCount() {
        return accounts.countByEnabledTrue();
    }

    /**
     * Staff issues a one-time reset code. The student uses it with their
     * student ID to set a new password without a staff member in the room.
     */
    @Transactional
    public String issueResetCode(String studentId) {
        StudentAccount account = find(studentId).orElse(null);
        if (account == null) {
            return null;
        }
        com.schoolchan.board.StudentResetCode code = new com.schoolchan.board.StudentResetCode();
        code.setId(UUID.randomUUID());
        code.setStudentId(account.getStudentId());
        code.setCode(generatePassword().substring(0, 8));
        code.setExpiresAt(Instant.now().plus(24, java.time.temporal.ChronoUnit.HOURS));
        code.setUsed(false);
        resetCodeRepository.save(code);
        return code.getCode();
    }

    /** Uses a reset code to set a new password. */
    @Transactional
    public boolean useResetCode(String studentId, String code, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            return false;
        }
        var found = resetCodeRepository.findByStudentIdAndCodeAndUsedFalse(studentId, code);
        if (found.isEmpty() || found.get().getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        com.schoolchan.board.StudentResetCode reset = found.get();
        reset.setUsed(true);
        resetCodeRepository.save(reset);
        var account = find(studentId);
        account.ifPresent(a -> changePassword(a, newPassword));
        return account.isPresent();
    }

    @Transactional(readOnly = true)
    public List<com.schoolchan.board.StudentResetCode> resetCodes(String studentId) {
        return resetCodeRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    /** Loads a student for Spring Security. */
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                StudentAccount account = find(username)
                        .orElseThrow(() -> new UsernameNotFoundException("No such student"));
                return User.withUsername(account.getStudentId())
                        .password(account.getPasswordHash())
                        .roles("STUDENT")
                        .disabled(!account.isEnabled())
                        .build();
            }
        };
    }
}
