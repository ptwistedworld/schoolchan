package com.schoolchan.media;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExifStripperTest {

    @Test
    void strip_removesJpegApp1ExifSegment() {
        byte[] jpeg = new byte[] {
                (byte) 0xFF, (byte) 0xD8,
                (byte) 0xFF, (byte) 0xE1, 0x00, 0x08,
                'E', 'x', 'i', 'f', 0x00, 0x00,
                (byte) 0xFF, (byte) 0xDA, 0x00, 0x02, 0x01, 0x02,
                (byte) 0xFF, (byte) 0xD9
        };
        byte[] out = ExifStripper.strip(jpeg);
        String asText = new String(out);
        assertFalse(asText.contains("Exif"));
        assertEquals(0xFF, out[0] & 0xFF);
        assertEquals(0xD8, out[1] & 0xFF);
        assertEquals(0xDA, out[3] & 0xFF);
    }

    @Test
    void strip_leavesNonJpegUntouched() {
        byte[] png = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47};
        assertEquals(4, ExifStripper.strip(png).length);
    }
}
