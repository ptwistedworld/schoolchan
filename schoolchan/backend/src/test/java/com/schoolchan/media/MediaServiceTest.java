package com.schoolchan.media;

import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The board shows the small copy of a picture ({@code <name>.thumb.jpg}), but
 * the media row is stored under the original name. Serving a thumbnail used to
 * answer 404, which is why board previews looked broken.
 */
class MediaServiceTest {

    @TempDir
    Path root;

    @Test
    void servesTheThumbnailWhenTheNameEndsWithTheThumbSuffix() throws Exception {
        MediaEntity entity = releasedImage("photo.png");
        Files.writeString(root.resolve(entity.getStoredName()), "original");
        Files.writeString(root.resolve(entity.getThumbnailName()), "small");

        MediaService service = serviceFor(entity);

        assertThat(service.loadReleased(entity.getThumbnailName())).isPresent();
        assertThat(service.loadReleased(entity.getThumbnailName()).orElseThrow().contentLength()).isEqualTo(5);
        assertThat(service.contentType(entity.getThumbnailName())).contains("image/jpeg");
    }

    @Test
    void fallsBackToTheOriginalWhenTheThumbnailFileIsGone() throws Exception {
        MediaEntity entity = releasedImage("photo.png");
        Files.writeString(root.resolve(entity.getStoredName()), "original");

        MediaService service = serviceFor(entity);

        assertThat(service.loadReleased(entity.getThumbnailName())).isPresent();
        assertThat(service.loadReleased(entity.getThumbnailName()).orElseThrow().contentLength()).isEqualTo(8);
    }

    @Test
    void neverServesAQuarantinedImage() throws Exception {
        MediaEntity entity = releasedImage("photo.png");
        entity.setQuarantineStatus("QUARANTINED");
        Files.writeString(root.resolve(entity.getStoredName()), "original");

        MediaService service = serviceFor(entity);

        assertThat(service.loadReleased(entity.getStoredName())).isEmpty();
    }

    @Test
    void rejectsPathTraversalNames() throws Exception {
        MediaEntity entity = releasedImage("photo.png");
        Files.writeString(root.resolve(entity.getStoredName()), "original");

        MediaService service = serviceFor(entity);

        assertThat(service.loadReleased("../schoolchan.mv.db")).isEmpty();
        assertThat(service.loadReleased("..\\schoolchan.mv.db")).isEmpty();
        assertThat(service.loadReleased("nested/photo.png")).isEmpty();
    }

    @Test
    void doesNotServeAThumbnailNameTheRowDoesNotOwn() throws Exception {
        MediaEntity entity = releasedImage("photo.png");
        entity.setThumbnailName("someone-elses.png.thumb.jpg");
        Files.writeString(root.resolve("someone-elses.png.thumb.jpg"), "small");

        MediaService service = serviceFor(entity);

        assertThat(service.loadReleased("someone-elses.png.thumb.jpg")).isEmpty();
    }

    private MediaEntity releasedImage(String storedName) {
        MediaEntity entity = new MediaEntity();
        entity.setId(UUID.randomUUID());
        entity.setStoredName(storedName);
        entity.setThumbnailName(storedName + ".thumb.jpg");
        entity.setContentType("image/png");
        entity.setQuarantineStatus("RELEASED");
        return entity;
    }

    private MediaService serviceFor(MediaEntity entity) {
        SchoolChanProperties properties = new SchoolChanProperties();
        properties.getMedia().setRoot(root.toString());

        MediaRepository repository = mock(MediaRepository.class);
        when(repository.findByStoredName(entity.getStoredName())).thenReturn(Optional.of(entity));
        when(repository.findByStoredName(any())).thenAnswer(invocation ->
                entity.getStoredName().equals(invocation.getArgument(0))
                        ? Optional.of(entity)
                        : Optional.empty());

        MediaPhashBlocklistRepository blocklist = mock(MediaPhashBlocklistRepository.class);
        return new MediaService(properties, repository, blocklist);
    }
}
