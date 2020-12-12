package com.tutorial.springbootmultitenancymongo.service;

/**
 * encryption interface service to store encrypted passwords.
 */
public interface EncryptionService {
    /**
     * @param strToEncrypt password to encrypt
     * @param secret       secret key
     * @param salt         salt key
     * @return String password encrypted or null
     */
    String encrypt(String strToEncrypt, String secret, String salt);

    /**
     * @param strToDecrypt password to decrypt
     * @param secret       secret key
     * @param salt         salt key
     * @return String password decrypted or null
     */
    String decrypt(String strToDecrypt, String secret, String salt);
}
