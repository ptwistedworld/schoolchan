package com.schoolchan.student;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * After signing in, the browser follows the {@code next} value with a GET.
 * A write endpoint only answers POST, so sending it there landed the student on
 * an error page instead of the home page.
 */
class StudentAuthControllerTest {

    @Test
    void emptyTargetGoesHome() {
        assertThat(StudentAuthController.safeNext(null)).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("  ")).isEqualTo("/");
    }

    @Test
    void normalPagesAreKept() {
        assertThat(StudentAuthController.safeNext("/boards/general")).isEqualTo("/boards/general");
        assertThat(StudentAuthController.safeNext("/boards/general/threads/1?page=2"))
                .isEqualTo("/boards/general/threads/1?page=2");
    }

    @Test
    void writeEndpointsFallBackHome() {
        assertThat(StudentAuthController.safeNext("/boards/general/threads/1/reply")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/posts/abc/vote")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/posts/abc/report")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/bookmarks/toggle")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/student/logout")).isEqualTo("/");
    }

    @Test
    void offSiteTargetsAreRejected() {
        assertThat(StudentAuthController.safeNext("https://example.com")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("//example.com")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/\\example.com")).isEqualTo("/");
        assertThat(StudentAuthController.safeNext("/ok\r\nLocation: https://example.com")).isEqualTo("/");
    }
}
