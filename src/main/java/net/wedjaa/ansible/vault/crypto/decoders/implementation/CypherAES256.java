package net.wedjaa.ansible.vault.crypto.decoders.implementation;

import net.wedjaa.ansible.vault.crypto.data.Util;
import net.wedjaa.ansible.vault.crypto.data.VaultContent;
import net.wedjaa.ansible.vault.crypto.data.VaultInfo;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class CypherAES256 implements CypherInterface
{
    Logger logger = LoggerFactory.getLogger(CypherAES256.class);

    public final static String CYPHER_ID = "AES256";
    public final static int AES_KEYLEN = 256;
    public final static String CHAR_ENCODING = "UTF-8";
    public final static String KEYGEN_ALGO = "HmacSHA256";
    public final static String CYPHER_KEY_ALGO = "AES";
    public static final String CYPHER_ALGO = "AES/CTR/NoPadding";
    private static final String JDK8_UPF_URL = "http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html";

    private static final int SALT_LENGTH = 32;
    public final static int KEYLEN = 32;
    public final static int IVLEN = 16;
    public final static int ITERATIONS = 10000;

    private boolean hasValidAESProvider()
    {
        boolean canCrypt = false;
        try
        {
            int maxKeyLen = Cipher.getMaxAllowedKeyLength(CYPHER_ALGO);
            logger.debug("Available keylen: {}", maxKeyLen);
            if (maxKeyLen >= AES_KEYLEN)
            {
                canCrypt = true;
            }
            else
            {
                logger.warn("JRE doesn't support {} keylength for {}\nInstall unrestricted policy files from:\n{}",
                        AES_KEYLEN, CYPHER_KEY_ALGO, JDK8_UPF_URL);
            }
        }
        catch (Exception ex)
        {
            logger.warn("Failed to check for proper cypher algorithms: {}", ex.getMessage());
        }
        return canCrypt;
    }

    public byte[] calculateHMAC(byte[] key, byte[] data) throws IOException
    {
        byte[] computedMac = null;

        try
        {
            SecretKeySpec hmacKey = new SecretKeySpec(key, KEYGEN_ALGO);
            Mac mac = Mac.getInstance(KEYGEN_ALGO);
            mac.init(hmacKey);
            computedMac = mac.doFinal(data);
        }
        catch (Exception ex)
        {
            throw new IOException("Error decrypting HMAC hash: " + ex.getMessage());
        }

        return computedMac;
    }

    public boolean verifyHMAC(byte[] hmac, byte[] key, byte[] data) throws IOException
    {
        boolean matches = false;
        byte[] calculated = calculateHMAC(key, data);
        return Arrays.equals(hmac, calculated);
    }

    public int paddingLength(byte[] decrypted)
    {
        if (decrypted.length == 0)
        {
            logger.debug("Empty decoded text has no padding.");
            return 0;
        }

        logger.debug("Padding length: {}", decrypted[decrypted.length - 1]);
        return decrypted[decrypted.length - 1];
    }

    public byte[] unpad(byte[] decrypted)
    {
        int length = decrypted.length - paddingLength(decrypted);
        return Arrays.copyOfRange(decrypted, 0, length);
    }

    public byte[] pad(byte[] cleartext) throws IOException
    {
        byte[] padded = null;

        try
        {
            int blockSize = Cipher.getInstance(CYPHER_ALGO).getBlockSize();
            logger.debug("Padding to block size: {}", blockSize);
            int padding_length = (blockSize - (cleartext.length % blockSize));
            if (padding_length == 0)
            {
                padding_length = blockSize;
            }
            padded = Arrays.copyOf(cleartext, cleartext.length + padding_length);
            padded[padded.length - 1] = (byte) padding_length;

        }
        catch (Exception ex)
        {
            new IOException("Error calculating padding for " + CYPHER_ALGO + ": " + ex.getMessage());
        }

        return padded;
    }

    public byte[] decryptAES(byte[] cypher, byte[] key, byte[] iv) throws IOException
    {

        SecretKeySpec keySpec = new SecretKeySpec(key, CYPHER_KEY_ALGO);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try
        {
            Cipher cipher = Cipher.getInstance(CYPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(cypher);
            return unpad(decrypted);
        }
        catch (Exception ex)
        {
            throw new IOException("Failed to decrypt data: " + ex.getMessage());
        }
    }

    public byte[] encryptAES(byte[] cleartext, byte[] key, byte[] iv) throws IOException
    {
        SecretKeySpec keySpec = new SecretKeySpec(key, CYPHER_KEY_ALGO);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try
        {
            Cipher cipher = Cipher.getInstance(CYPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(cleartext);
            return encrypted;
        }
        catch (Exception ex)
        {
            throw new IOException("Failed to encrypt data: " + ex.getMessage());
        }
    }

    public byte[] decrypt(byte[] encryptedData, String password) throws IOException
    {
        byte[] decrypted = null;

        if (!hasValidAESProvider())
        {
            throw new IOException("Missing valid AES256 provider - install unrestricted policy profiles.");
        }

        VaultContent vaultContent = new VaultContent(encryptedData);

        byte[] salt = vaultContent.getSalt();
        byte[] hmac = vaultContent.getHmac();
        byte[] cypher = vaultContent.getData();
        logger.debug("Salt: {} - {}", salt.length, Util.hexit(salt, 100));
        logger.debug("HMAC: {} - {}", hmac.length, Util.hexit(hmac, 100));
        logger.debug("Data: {} - {}", cypher.length, Util.hexit(cypher, 100));

        EncryptionKeychain keys = new EncryptionKeychain(salt, password, KEYLEN, IVLEN, ITERATIONS, KEYGEN_ALGO);
        keys.createKeys();

        byte[] cypherKey = keys.getEncryptionKey();
        logger.debug("Key 1: {} - {}", cypherKey.length, Util.hexit(cypherKey, 100));
        byte[] hmacKey = keys.getHmacKey();
        logger.debug("Key 2: {} - {}", hmacKey.length, Util.hexit(hmacKey, 100));
        byte[] iv = keys.getIv();
        logger.debug("IV: {} - {}", iv.length, Util.hexit(iv, 100));

        if (verifyHMAC(hmac, hmacKey, cypher))
        {
            logger.debug("Signature matches - decrypting");
            decrypted = decryptAES(cypher, cypherKey, iv);
            logger.debug("Decoded:\n{}", new String(decrypted, CHAR_ENCODING));
        }
        else
        {
            throw new IOException("HMAC Digest doesn't match - possibly it's the wrong password.");
        }

        return decrypted;
    }

    public void decrypt(OutputStream decodedStream, byte[] encryptedData, String password) throws IOException
    {
        decodedStream.write(decrypt(encryptedData, password));
    }

    public void encrypt(OutputStream encodedStream, byte[] data, String password) throws IOException
    {
        encodedStream.write(encrypt(data, password));
    }

    public String infoLine()
    {
        return VaultInfo.vaultInfoForCypher(CYPHER_ID);
    }

    public byte[] encrypt(byte[] data, String password) throws IOException
    {
        EncryptionKeychain keys = new EncryptionKeychain(SALT_LENGTH, password, KEYLEN, IVLEN, ITERATIONS, KEYGEN_ALGO);
        keys.createKeys();
        byte[] cypherKey = keys.getEncryptionKey();
        logger.debug("Key 1: {} - {}", cypherKey.length, Util.hexit(cypherKey, 100));
        byte[] hmacKey = keys.getHmacKey();
        logger.debug("Key 2: {} - {}", hmacKey.length, Util.hexit(hmacKey, 100));
        byte[] iv = keys.getIv();
        logger.debug("IV: {} - {}", iv.length, Util.hexit(iv, 100));
        logger.debug("Original data length: {}", data.length);
        data = pad(data);
        logger.debug("Padded data length: {}", data.length);
        byte[] encrypted = encryptAES(data, keys.getEncryptionKey(), keys.getIv());
        byte[] hmacHash = calculateHMAC(keys.getHmacKey(), encrypted);
        VaultContent vaultContent = new VaultContent(keys.getSalt(), hmacHash, encrypted);
        return vaultContent.toByteArray();
    }

}
