package com.schoolchan.media;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class PhashUtil {

    private PhashUtil() {}

    /** Simple dHash (difference hash) stub for blocklist matching. */
    public static String dHash(byte[] imageBytes) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (img == null) {
            return null;
        }
        Image scaled = img.getScaledInstance(9, 8, Image.SCALE_SMOOTH);
        BufferedImage small = new BufferedImage(9, 8, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = small.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();

        StringBuilder bits = new StringBuilder(64);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int left = small.getRGB(x, y) & 0xFF;
                int right = small.getRGB(x + 1, y) & 0xFF;
                bits.append(left < right ? '1' : '0');
            }
        }
        return bits.toString();
    }
}
