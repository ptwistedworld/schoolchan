package com.schoolchan.student;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.BookmarkService;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.Vote;
import com.schoolchan.board.VoteRepository;
import com.schoolchan.reputation.Report;
import com.schoolchan.reputation.ReportRepository;
import com.schoolchan.reputation.ReportService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The student account is a gate, never an identity.
 *
 * <p>These tests post, vote, report and bookmark while a student is signed in and
 * then check that the student ID appears nowhere in the content tables.
 */
@SpringBootTest
@ActiveProfiles("test")
class StudentAnonymityTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void studentAccountIsNeverAttachedToAPost() {
        String studentId = "2024-99999";
        var account = studentService.provision(studentId, "Anonymity Test", "11 - Test", "test", "secret123");

        // Sign in as the student.
        var authenticated = studentService.authenticate(studentId, "secret123");
        assertTrue(authenticated.isPresent(), "student should be able to sign in");

        // Post, vote, report and bookmark using the anonymous session hash only.
        String anonymousSessionHash = "anonymous-session-hash-for-test";
        Post post = boardService.createThread("general", "Anonymity check", "hello world",
                false, false, anonymousSessionHash, "10.9.9.9");
        boardService.vote(post.getId(), anonymousSessionHash, (short) 1);
        reportService.report(post.getId(), anonymousSessionHash, "SPAM", null);
        bookmarkService.toggle(anonymousSessionHash, post.getThreadId());

        // The stored post must not mention the student anywhere.
        Post stored = postRepository.findById(post.getId()).orElseThrow();
        assertFalse(stored.getOriginalText().contains(studentId));
        assertFalse(stored.getOriginalText().contains(account.getStudentId()));

        // No content table may contain the student id.
        assertFalse(tableContains("posts", studentId), "posts table must not contain the student id");
        assertFalse(tableContains("post_sessions", studentId), "post_sessions must not contain the student id");
        assertFalse(tableContains("votes", studentId), "votes table must not contain the student id");
        assertFalse(tableContains("reports", studentId), "reports table must not contain the student id");
        assertFalse(tableContains("bookmarks", studentId), "bookmarks table must not contain the student id");
        assertFalse(tableContains("moderation_results", studentId), "moderation_results must not contain the student id");

        // The only thing stored is the anonymous session hash.
        Vote vote = voteRepository.findByPostIdAndSessionHash(post.getId(), anonymousSessionHash).orElseThrow();
        assertEquals(anonymousSessionHash, vote.getSessionHash());
        Report report = reportRepository.findByPostIdAndSessionHash(post.getId(), anonymousSessionHash).orElseThrow();
        assertEquals(anonymousSessionHash, report.getSessionHash());
    }

    @Test
    @Transactional
    void studentIdIsStoredOnlyInTheRoster() {
        String studentId = "2024-88888";
        studentService.provision(studentId, "Roster Test", "11 - Test", "test", "secret123");
        // The id lives in the roster table and nowhere else.
        assertTrue(tableContains("student_accounts", studentId));
        assertFalse(tableContains("posts", studentId));
        assertFalse(tableContains("post_sessions", studentId));
    }

    @Test
    void invalidStudentIdsAreRejected() {
        assertFalse(studentService.isValidStudentId("abc"));
        assertFalse(studentService.isValidStudentId("12"));
        assertFalse(studentService.isValidStudentId(""));
        assertTrue(studentService.isValidStudentId("2024-12345"));
        assertTrue(studentService.isValidStudentId("123456"));
    }

    @Test
    void wrongPasswordDoesNotAuthenticate() {
        studentService.provision("2024-77777", "Wrong Pass", "11 - Test", "test", "rightpassword");
        assertTrue(studentService.authenticate("2024-77777", "rightpassword").isPresent());
        assertFalse(studentService.authenticate("2024-77777", "wrongpassword").isPresent());
        assertFalse(studentService.authenticate("2024-00000", "rightpassword").isPresent());
    }

    @Test
    void repeatedFailuresLockTheAccountOut() {
        StudentLoginThrottle throttle = new StudentLoginThrottle();
        String id = "2024-60000";
        assertFalse(throttle.isLocked(id));
        assertFalse(throttle.recordFailure(id, 3, 10));
        assertFalse(throttle.recordFailure(id, 3, 10));
        assertTrue(throttle.recordFailure(id, 3, 10), "third failure should lock the account");
        assertTrue(throttle.isLocked(id));
        // A success clears the counter.
        throttle.recordSuccess(id);
        assertFalse(throttle.isLocked(id));
        assertEquals(3, throttle.remainingAttempts(id, 3));
    }

    @Test
    @Transactional
    void studentCanChangeTheirOwnPassword() {
        studentService.provision("2024-60001", "Change Pass", "11 - Test", "test", "firstpass1");
        var account = studentService.find("2024-60001").orElseThrow();
        assertTrue(account.isMustChangePassword());
        studentService.changePassword(account, "secondpass2");
        assertTrue(studentService.authenticate("2024-60001", "secondpass2").isPresent());
        assertFalse(studentService.authenticate("2024-60001", "firstpass1").isPresent());
    }

    /** Scans a table's text columns for a value. */
    private boolean tableContains(String table, String value) {
        List<?> rows = entityManager.createNativeQuery(
                "SELECT * FROM " + table).getResultList();
        for (Object row : rows) {
            if (row instanceof Object[] cells) {
                for (Object cell : cells) {
                    if (cell != null && cell.toString().contains(value)) {
                        return true;
                    }
                }
            } else if (row != null && row.toString().contains(value)) {
                return true;
            }
        }
        return false;
    }
}
