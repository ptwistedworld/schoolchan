package com.schoolchan.staff;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class MetadataCryptoService {

    private static final int GCM_TAG = 128;
    private static final int IV_LEN = 12;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public MetadataCryptoService(SchoolChanProperties properties) {
        byte[] raw = Base64.getDecoder().decode(properties.getMetadataKey());
        if (raw.length != 32) {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, 32));
            raw = padded;
        }
        this.key = new SecretKeySpec(raw, "AES");
    }

    public byte[] encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
        } catch (Exception e) {
            throw new IllegalStateException("encrypt failed", e);
        }
    }

    public String decrypt(byte[] payload) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(payload);
            byte[] iv = new byte[IV_LEN];
            buf.get(iv);
            byte[] ct = new byte[buf.remaining()];
            buf.get(ct);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("decrypt failed", e);
        }
    }
}
