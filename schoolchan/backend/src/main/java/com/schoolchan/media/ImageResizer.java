package com.schoolchan.media;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Shrinks images so the board stays fast and the disk stays small.
 *
 * <p>A phone photo is often 4000 pixels wide and 4 MB. The board only ever
 * shows it small, so the full size is scaled down to a sensible maximum and a
 * small thumbnail is written alongside it for the previews.
 */
public final class ImageResizer {

    /** The longest side kept for the full-size copy. */
    public static final int MAX_FULL_EDGE = 1600;
    /** The longest side kept for the preview thumbnail. */
    public static final int MAX_THUMB_EDGE = 320;
    /** JPEG quality for the rewritten image. */
    private static final float JPEG_QUALITY = 0.82f;

    private ImageResizer() {
    }

    public record Result(byte[] bytes, String contentType, int width, int height, boolean resized) {
    }

    /**
     * Scales an image down if it is too big, and writes a thumbnail.
     *
     * <p>Formats the JDK cannot read (WebP) are stored as they came in; only
     * the thumbnail is skipped for those, since there is no decoder.
     */
    public static Result shrink(byte[] original, String contentType) throws IOException {
        BufferedImage source = read(original);
        if (source == null) {
            // Unknown format to the JDK. Store it as it is and skip the thumbnail.
            return new Result(original, contentType, 0, 0, false);
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int longest = Math.max(width, height);

        byte[] full = original;
        String type = contentType;
        int outWidth = width;
        int outHeight = height;
        boolean resized = false;
        if (longest > MAX_FULL_EDGE) {
            double scale = (double) MAX_FULL_EDGE / longest;
            outWidth = Math.max(1, (int) Math.round(width * scale));
            outHeight = Math.max(1, (int) Math.round(height * scale));
            full = write(scale(source, outWidth, outHeight), contentType);
            type = outputType(contentType);
            resized = true;
        }
        return new Result(full, type, outWidth, outHeight, resized);
    }

    /** Writes a small copy for the board previews. Returns null if it cannot. */
    public static byte[] thumbnail(byte[] original, String contentType) throws IOException {
        BufferedImage source = read(original);
        if (source == null) {
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int longest = Math.max(width, height);
        if (longest <= MAX_THUMB_EDGE) {
            // Already small. The preview can use the file itself.
            return null;
        }
        double scale = (double) MAX_THUMB_EDGE / longest;
        int w = Math.max(1, (int) Math.round(width * scale));
        int h = Math.max(1, (int) Math.round(height * scale));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(scale(source, w, h), "jpg", out);
        return out.toByteArray();
    }

    private static BufferedImage read(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return null;
        }
    }

    private static BufferedImage scale(BufferedImage source, int width, int height) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return out;
    }

    private static byte[] write(BufferedImage image, String contentType) throws IOException {
        String format = format(contentType);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, format, out);
        return out.toByteArray();
    }

    private static String outputType(String contentType) {
        return "jpg".equals(format(contentType)) ? "image/jpeg" : contentType;
    }

    /** "jpg" or "png", the two formats the JDK can write. */
    private static String format(String contentType) {
        if (contentType == null) {
            return "png";
        }
        String lower = contentType.toLowerCase(Locale.ROOT);
        if (lower.contains("jpeg") || lower.contains("jpg")) {
            return "jpg";
        }
        if (lower.contains("png")) {
            return "png";
        }
        return "png";
    }
}
