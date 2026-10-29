package com.schoolchan.board;

import com.schoolchan.media.ImageResizer;
import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
import com.schoolchan.media.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Images are shrunk when they arrive, and a small copy is kept for the board
 * previews. A phone photo is megabytes; the board only ever shows it small.
 */
@SpringBootTest
@ActiveProfiles("test")
class ImageResizerTest {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private MediaRepository mediaRepository;

    @Test
    void aBigImageIsScaledDown() throws Exception {
        byte[] big = png(2400, 1800);

        ImageResizer.Result result = ImageResizer.shrink(big, "image/png");

        assertTrue(result.resized(), "a 2400px image should be scaled down");
        assertEquals(ImageResizer.MAX_FULL_EDGE, Math.max(result.width(), result.height()));
        assertTrue(result.bytes().length < big.length, "the smaller image should use less space");
    }

    @Test
    void aSmallImageIsLeftAlone() throws Exception {
        byte[] small = png(200, 150);

        ImageResizer.Result result = ImageResizer.shrink(small, "image/png");

        assertTrue(!result.resized(), "a small image should not be rewritten");
        assertEquals(200, result.width());
    }

    @Test
    void aThumbnailIsMadeForABigImage() throws Exception {
        byte[] thumb = ImageResizer.thumbnail(png(2400, 1800), "image/png");

        assertNotNull(thumb, "a big image should get a thumbnail");
        BufferedImage read = ImageIO.read(new java.io.ByteArrayInputStream(thumb));
        assertEquals(ImageResizer.MAX_THUMB_EDGE, Math.max(read.getWidth(), read.getHeight()));
        assertTrue(thumb.length < 60 * 1024, "a thumbnail should be small");
    }

    @Test
    void aSmallImageNeedsNoThumbnail() throws Exception {
        assertNull(ImageResizer.thumbnail(png(200, 150), "image/png"),
                "an image that is already small can be used for the preview as it is");
    }

    @Test
    @Transactional
    void aStoredImageKeepsItsNameAndGetsAPreviewCopy() throws Exception {
        UUID postId = UUID.randomUUID();
        byte[] png = png(2400, 1800);
        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile("file", "photo.png", "image/png", png);

        MediaEntity media = mediaService.store(postId, file);

        assertTrue(media.getStoredName().endsWith(".png"), "the name should match the content");
        assertNotNull(media.getThumbnailName(), "a preview copy should exist");
        assertTrue(media.getSizeBytes() < png.length, "the stored file should be smaller");
        assertTrue(media.getWidth() <= ImageResizer.MAX_FULL_EDGE);
    }

    /** A plain PNG of the given size. */
    private byte[] png(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(new Color(40, 90, 160));
        g.fillRect(0, 0, width, height);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}
