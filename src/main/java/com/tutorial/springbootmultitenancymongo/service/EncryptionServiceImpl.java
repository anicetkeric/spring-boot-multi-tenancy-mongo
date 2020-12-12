package com.tutorial.springbootmultitenancymongo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {

    public static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final String CIPHER = "AES/CBC/PKCS5Padding";
    public static final String KEY_ALGORITHM = "AES";
    public static final int ITERATION_COUNT = 65536;
    public static final int KEY_LENGTH = 256;

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(String strToEncrypt, String secret, String salt) {
        try {
            Assert.notNull(strToEncrypt, "encrypt password cannot be null");
            IvParameterSpec ivspec = getIvParameterSpec();

            SecretKeySpec secretKey = getSecretKeySpec(secret, salt);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            log.error("Error while encrypting: ", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(String strToDecrypt, String secret, String salt) {
        try {
            Assert.notNull(strToDecrypt, "decrypt password cannot be null");
            IvParameterSpec ivspec = getIvParameterSpec();

            SecretKeySpec secretKey = getSecretKeySpec(secret, salt);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error while decrypting: ", e);
            return null;
        }
    }

    private IvParameterSpec getIvParameterSpec() {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec getSecretKeySpec(String secret, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), KEY_ALGORITHM);
    }

}
