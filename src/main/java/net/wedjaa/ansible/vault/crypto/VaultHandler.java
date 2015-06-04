package net.wedjaa.ansible.vault.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.wedjaa.ansible.vault.crypto.data.Util;
import net.wedjaa.ansible.vault.crypto.data.VaultInfo;
import net.wedjaa.ansible.vault.crypto.decoders.CypherFactory;
import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES256;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;
import org.apache.commons.io.IOUtils;

/**
 * Created by mrwho on 03/06/15.
 */
public class VaultHandler
{

    public final static String DEFAULT_CYPHER = CypherAES256.CYPHER_ID;

    public final static String CHAR_ENCODING = "UTF-8";


    public static byte [] encrypt(byte [] cleartext, String password, String cypher) throws IOException
    {
        CypherInterface cypherInstance = CypherFactory.getCypher(cypher);
        byte [] vaultData = cypherInstance.encrypt(cleartext, password);
        String vaultDataString = new String(vaultData);
        String vaultPackage = cypherInstance.infoLine() + "\n" + vaultDataString;
        return vaultPackage.getBytes();
    }

    public static byte [] encrypt(byte [] cleartext, String password) throws IOException
    {
        return encrypt(cleartext, password, DEFAULT_CYPHER);
    }

    public static void encrypt(InputStream clearText, OutputStream cipherText, String password, String cypher) throws IOException
    {
        String clearTextValue = IOUtils.toString(clearText, CHAR_ENCODING);
        cipherText.write(encrypt(clearTextValue.getBytes(), password, cypher));
    }

    public static void encrypt(InputStream clearText, OutputStream cipherText, String password) throws IOException
    {
        encrypt(clearText, cipherText, password, DEFAULT_CYPHER);
    }

    public static void decrypt(InputStream encryptedVault, OutputStream decryptedVault, String password) throws IOException
    {
        String encryptedValue = IOUtils.toString(encryptedVault, CHAR_ENCODING);
        decryptedVault.write(decrypt(encryptedValue.getBytes(), password));
    }

    public static byte[] decrypt(byte[] encrypted, String password) throws IOException
    {

        VaultInfo vaultInfo = Util.getVaultInfo(encrypted);
        if ( !vaultInfo.isEncryptedVault() ) {
            throw new IOException("File is not an Ansible Encrypted Vault");
        }

        if ( !vaultInfo.isValidVault() )
        {
            throw new IOException("The vault is not a format we can handle - check the cypher.");
        }

        byte [] encryptedData = Util.getVaultData(encrypted);

        return vaultInfo.getCypher().decrypt(encryptedData, password);
    }

}
