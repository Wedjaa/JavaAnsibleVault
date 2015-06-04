package net.wedjaa.ansible.vault.crypto;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;


import static org.junit.Assert.*;


public class VaultHandlerTest
{

    Logger logger = LoggerFactory.getLogger(VaultHandlerTest.class);

    @Test
    public void testByteArrayVault()
    {
        try
        {
            byte [] encryptedTest = VaultHandler.encrypt("This is  a test".getBytes(), "password");
            logger.debug("Encrypted vault:\n{}", new String(encryptedTest));
            byte [] decryptedTest = VaultHandler.decrypt(encryptedTest, "password");
            logger.debug("Decrypted vault:\n{}", new String(decryptedTest));

        } catch(Exception ex) {
            ex.printStackTrace();
            logger.warn("Failed to decode the test vault: " + ex.getMessage());
        }
    }

    @Test
    public void testStreamArrayVault()
    {
        try
        {
            FileOutputStream decodedStream = new FileOutputStream("test-out.dec");
            InputStream encodedStream = getClass().getClassLoader().getResourceAsStream("test-vault.yml");
            VaultHandler.decrypt(encodedStream, decodedStream, "password");
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.warn("Failed to decode the test vault: " + ex.getMessage());
        }
    }

}