package com.rahul.auth_service.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoUtils {
    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    public static final String AES_ALGORITHM = "AES";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_SHA256_SIGNATURE_ALGORITHM = "SHA256WithRSA";

    private static final String PUBLIC_KEY_START_STRING = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_END_STRING = "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_START_STRING = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_END_STRING = "-----END PRIVATE KEY-----";

    public enum RsaEncryptionAlgorithm {
        ECB_PKCS_1_PADDING("RSA/ECB/PKCS1Padding"),
        ECB_OAEP_SHA1_MGF1_PADDING("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");

        private final String value;

        RsaEncryptionAlgorithm(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum EncryptionMode {
        AES_ECB("AES/ECB/PKCS5Padding"), AES_CBC("AES/CBC/PKCS5PADDING");

        private final String value;

        EncryptionMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static String encryptWithSecretKey(String input, String secretKey) {
        byte[] crypted;
        try {
            SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(EncryptionMode.AES_ECB.getValue());
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Base64.encodeBase64URLSafeString(crypted);
    }

    public static String encryptWithSecretKeyUsingAESECB(String input, String secretKey) {
        byte[] encrypted;
        try {
            SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(EncryptionMode.AES_ECB.getValue());
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            encrypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }

//    public static String encrypt(String input) {
//        String key = AuthConstants.keyValue;
//        return encryptWithSecretKey(input, key);
//    }


    public static String decryptWithSecretKey(String input, String secretKey) {
        byte[] output;
        try {
            SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(EncryptionMode.AES_ECB.getValue());
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decodeBase64(input));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return new String(output);

    }

    private static String encryptWithAESWithCBC(String content, String encryptionKey) {
        if (content == null) {
            return null;
        }
        byte[] initVector = new byte[16];
        try {
            SecureRandom.getInstanceStrong().nextBytes(initVector);
            encryptionKey = padTo16Characters(encryptionKey);

            return encryptWithAESWithCBCWithIv(content, encryptionKey, initVector);
        } catch (GeneralSecurityException e) {
            logger.error("Error while encrypting with AES_CBC", e);
            return null;
        }
    }

    public static String encryptWithAESWithCBCWithIv(String content, String encryptionKey, byte[] initVector)
        throws GeneralSecurityException {

        Key aesKey = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(EncryptionMode.AES_CBC.getValue());
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(initVector));

        byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(ArrayUtils.addAll(initVector, encrypted));
    }

    public static String decryptWithAESWithCBC(String encrypted, String encryptionKey) throws GeneralSecurityException {
        return decryptWithAESWithCBC(encrypted, encryptionKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptWithAESWithCBC(String encrypted, byte[] encryptionKey) throws GeneralSecurityException {
        byte[] base64Decoded = Base64.decodeBase64(encrypted);

        Key aesKey = new SecretKeySpec(encryptionKey, AES_ALGORITHM);

        byte[] ivBytes = Arrays.copyOfRange(base64Decoded, 0, 16);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(EncryptionMode.AES_CBC.getValue());
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);

        byte[] original = cipher.doFinal(base64Decoded);

        return new String(Arrays.copyOfRange(original, 16, original.length));
    }

    private static String padTo16Characters(String encryptionKey) {
        if (encryptionKey.getBytes().length < 16) {
            // Make it into 32 bytes by padding with 0s in the end
            int paddingLength = 16 - encryptionKey.getBytes().length;

            int j;
            for (int i = 0; i < paddingLength; i++) {
                j = i;
                if (i > 9) {
                    j = i % 10;
                }
                encryptionKey += j;
            }
        } else if (encryptionKey.getBytes(StandardCharsets.UTF_8).length > 16) {
            encryptionKey = encryptionKey.substring(0, 16);
        }
        return encryptionKey;
    }

}