#!/usr/bin/env python3
"""One-shot generator for SchoolChan backend sources (run from repo maintenance)."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "src/main/java/com/schoolchan"
TEST = ROOT / "src/test/java/com/schoolchan"
RES = ROOT / "src/main/resources"

FILES: dict[Path, str] = {}

def add(rel: str, content: str, test: bool = False):
    base = TEST if test else MAIN
    FILES[base / rel] = content.strip() + "\n"

# --- config ---
add("config/WebConfig.java", '''
package com.schoolchan.config;

import com.schoolchan.access.AccessWriteBlockFilter;
import com.schoolchan.session.AnonymousSessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    }

    @Bean
    FilterRegistrationBean<AnonymousSessionFilter> anonymousSessionFilter(AnonymousSessionFilter filter) {
        FilterRegistrationBean<AnonymousSessionFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(1);
        return bean;
    }

    @Bean
    FilterRegistrationBean<AccessWriteBlockFilter> accessWriteBlockFilter(AccessWriteBlockFilter filter) {
        FilterRegistrationBean<AccessWriteBlockFilter> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(2);
        return bean;
    }
}
''')

add("config/SecurityConfig.java", '''
package com.schoolchan.config;

import com.schoolchan.staff.StaffUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/staff/login", "/css/**", "/media/**", "/error").permitAll()
                        .requestMatchers("/staff/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/staff/login")
                        .loginProcessingUrl("/staff/login")
                        .defaultSuccessUrl("/staff/dashboard", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/staff/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/staff/api/**"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
''')

# --- board enums & entities ---
add("board/PostStatus.java", '''
package com.schoolchan.board;

public enum PostStatus {
    PENDING_SCREENING,
    PUBLISHED,
    HELD_FOR_REVIEW,
    BLOCKED
}
''')

add("board/ThreadStatus.java", '''
package com.schoolchan.board;

public enum ThreadStatus {
    ACTIVE,
    SINKING,
    ARCHIVED
}
''')

add("board/Board.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "boards")
public class Board {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String slug;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(name = "active_thread_cap", nullable = false)
    private int activeThreadCap;

    @Column(name = "bump_limit", nullable = false)
    private int bumpLimit;

    @Column(name = "image_limit", nullable = false)
    private int imageLimit;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getActiveThreadCap() { return activeThreadCap; }
    public void setActiveThreadCap(int activeThreadCap) { this.activeThreadCap = activeThreadCap; }
    public int getBumpLimit() { return bumpLimit; }
    public void setBumpLimit(int bumpLimit) { this.bumpLimit = bumpLimit; }
    public int getImageLimit() { return imageLimit; }
    public void setImageLimit(int imageLimit) { this.imageLimit = imageLimit; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
''')

add("board/ThreadEntity.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "threads")
public class ThreadEntity {

    @Id
    private UUID id;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(length = 200)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ThreadStatus status = ThreadStatus.ACTIVE;

    @Column(name = "bumped_at", nullable = false)
    private Instant bumpedAt = Instant.now();

    @Column(name = "reply_count", nullable = false)
    private int replyCount;

    @Column(name = "image_count", nullable = false)
    private int imageCount;

    @Column(nullable = false)
    private boolean locked;

    @Column(nullable = false)
    private boolean pinned;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBoardId() { return boardId; }
    public void setBoardId(UUID boardId) { this.boardId = boardId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public ThreadStatus getStatus() { return status; }
    public void setStatus(ThreadStatus status) { this.status = status; }
    public Instant getBumpedAt() { return bumpedAt; }
    public void setBumpedAt(Instant bumpedAt) { this.bumpedAt = bumpedAt; }
    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }
    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
''')

add("board/Post.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    private UUID id;

    @Column(name = "thread_id", nullable = false)
    private UUID threadId;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(name = "board_post_number", nullable = false)
    private long boardPostNumber;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "translated_text", columnDefinition = "TEXT")
    private String translatedText;

    @Column(name = "detected_language", length = 32)
    private String detectedLanguage;

    @Column(name = "translation_model", length = 128)
    private String translationModel;

    @Column(name = "translation_confidence")
    private Double translationConfidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PostStatus status = PostStatus.PENDING_SCREENING;

    @Column(nullable = false)
    private boolean spoiler;

    @Column(name = "spoiler_label", length = 128)
    private String spoilerLabel;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private boolean sage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getThreadId() { return threadId; }
    public void setThreadId(UUID threadId) { this.threadId = threadId; }
    public UUID getBoardId() { return boardId; }
    public void setBoardId(UUID boardId) { this.boardId = boardId; }
    public long getBoardPostNumber() { return boardPostNumber; }
    public void setBoardPostNumber(long boardPostNumber) { this.boardPostNumber = boardPostNumber; }
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }
    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }
    public String getTranslationModel() { return translationModel; }
    public void setTranslationModel(String translationModel) { this.translationModel = translationModel; }
    public Double getTranslationConfidence() { return translationConfidence; }
    public void setTranslationConfidence(Double translationConfidence) { this.translationConfidence = translationConfidence; }
    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }
    public boolean isSpoiler() { return spoiler; }
    public void setSpoiler(boolean spoiler) { this.spoiler = spoiler; }
    public String getSpoilerLabel() { return spoilerLabel; }
    public void setSpoilerLabel(String spoilerLabel) { this.spoilerLabel = spoilerLabel; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public boolean isSage() { return sage; }
    public void setSage(boolean sage) { this.sage = sage; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
''')

add("board/Vote.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "session_hash", nullable = false, length = 128)
    private String sessionHash;

    @Column(nullable = false)
    private short value;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getSessionHash() { return sessionHash; }
    public void setSessionHash(String sessionHash) { this.sessionHash = sessionHash; }
    public short getValue() { return value; }
    public void setValue(short value) { this.value = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
''')

add("board/Quote.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(length = 255)
    private String attribution;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getAttribution() { return attribution; }
    public void setAttribution(String attribution) { this.attribution = attribution; }
}
''')

add("board/SiteSetting.java", '''
package com.schoolchan.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "site_settings")
public class SiteSetting {

    @Id
    @Column(length = 128)
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
''')

add("board/BoardRepository.java", '''
package com.schoolchan.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    Optional<Board> findBySlug(String slug);
}
''')

add("board/ThreadRepository.java", '''
package com.schoolchan.board;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreadRepository extends JpaRepository<ThreadEntity, UUID> {

    List<ThreadEntity> findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(UUID boardId, ThreadStatus status);

    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.boardId = :boardId AND t.status = com.schoolchan.board.ThreadStatus.ACTIVE")
    long countActiveByBoard(@Param("boardId") UUID boardId);

    List<ThreadEntity> findByBoardIdAndStatusOrderByBumpedAtAsc(UUID boardId, ThreadStatus status);
}
''')

add("board/PostRepository.java", '''
package com.schoolchan.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByThreadIdOrderByCreatedAtAsc(UUID threadId);

    Optional<Post> findByBoardIdAndBoardPostNumber(UUID boardId, long boardPostNumber);

    @Query(value = "SELECT nextval('board_post_seq')", nativeQuery = true)
    Long nextBoardPostNumber();
}
''')

add("board/VoteRepository.java", '''
package com.schoolchan.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByPostIdAndSessionHash(UUID postId, String sessionHash);
    long countByPostId(UUID postId);
}
''')

add("board/QuoteRepository.java", '''
package com.schoolchan.board;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {}
''')

add("board/SiteSettingRepository.java", '''
package com.schoolchan.board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {}
''')

if __name__ == "__main__":
    for path, content in FILES.items():
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
    print(f"Wrote {len(FILES)} files under {MAIN}")
