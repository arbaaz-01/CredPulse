package com.ofss.project.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CardEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;

    private static final int TAG_LENGTH = 128;

    private final SecretKey secretKey;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public CardEncryptionService(
        @Value("${app.card.encryption-key}")
        String encryptionKey
) {

    byte[] keyBytes =
            encryptionKey.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length != 32) {
        throw new IllegalArgumentException(
                "Card encryption key must be exactly 32 bytes"
        );
    }

    this.secretKey =
            new SecretKeySpec(
                    keyBytes,
                    "AES"
            );
}
    public String encrypt(String cardNumber) {

        try {

            byte[] iv =
                    new byte[IV_LENGTH];

            secureRandom.nextBytes(iv);

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    spec
            );

            byte[] encrypted =
                    cipher.doFinal(
                            cardNumber.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            ByteBuffer buffer =
                    ByteBuffer.allocate(
                            iv.length + encrypted.length
                    );

            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder()
                    .encodeToString(
                            buffer.array()
                    );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to encrypt card number",
                    e
            );
        }
    }

    public String decrypt(String encryptedCardNumber) {

        try {

            byte[] decoded =
                    Base64.getDecoder()
                            .decode(encryptedCardNumber);

            ByteBuffer buffer =
                    ByteBuffer.wrap(decoded);

            byte[] iv =
                    new byte[IV_LENGTH];

            buffer.get(iv);

            byte[] encrypted =
                    new byte[buffer.remaining()];

            buffer.get(encrypted);

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    spec
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to decrypt card number",
                    e
            );
        }
    }
}