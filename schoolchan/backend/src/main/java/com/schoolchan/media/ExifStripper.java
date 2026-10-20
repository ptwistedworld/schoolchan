package com.schoolchan.media;

/**
 * Removes JPEG APP1 (EXIF) and APP13 (IPTC) segments. Non-JPEG bytes are returned unchanged.
 */
public final class ExifStripper {

    private ExifStripper() {}

    public static byte[] strip(byte[] input) {
        if (input == null || input.length < 4) {
            return input;
        }
        if ((input[0] & 0xFF) != 0xFF || (input[1] & 0xFF) != 0xD8) {
            return input;
        }
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(input.length);
        out.write(0xFF);
        out.write(0xD8);
        int i = 2;
        while (i + 3 < input.length) {
            if ((input[i] & 0xFF) != 0xFF) {
                out.write(input, i, input.length - i);
                return out.toByteArray();
            }
            int marker = input[i + 1] & 0xFF;
            if (marker == 0xD9 || marker == 0xDA) {
                out.write(input, i, input.length - i);
                return out.toByteArray();
            }
            if (marker == 0x01 || (marker >= 0xD0 && marker <= 0xD7)) {
                out.write(input[i]);
                out.write(input[i + 1]);
                i += 2;
                continue;
            }
            int length = ((input[i + 2] & 0xFF) << 8) | (input[i + 3] & 0xFF);
            if (length < 2 || i + 2 + length > input.length) {
                out.write(input, i, input.length - i);
                return out.toByteArray();
            }
            boolean drop = marker == 0xE1 || marker == 0xED;
            if (!drop) {
                out.write(input, i, 2 + length);
            }
            i += 2 + length;
        }
        if (i < input.length) {
            out.write(input, i, input.length - i);
        }
        return out.toByteArray();
    }
}
