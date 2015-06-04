package net.wedjaa.ansible.vault.crypto.decoders.implementation;

import net.wedjaa.ansible.vault.crypto.data.VaultInfo;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;

import java.io.IOException;
import java.io.OutputStream;

public class CypherAES implements CypherInterface
{

    public final static String CYPHER_ID = "AES";

    public void decrypt(OutputStream decodedStream, byte[] data, String password) throws IOException
    {
        throw new IOException(CYPHER_ID + " is not implemented.");
    }

    public byte[] decrypt(byte[] data, String password) throws IOException
    {
        throw new IOException(CYPHER_ID + " is not implemented.");
    }

    public void encrypt(OutputStream encodedStream, byte[] data, String password) throws IOException
    {
        throw new IOException(CYPHER_ID + " is not implemented.");
    }

    public byte[] encrypt(byte[] data, String password) throws IOException
    {
        throw new IOException(CYPHER_ID + " is not implemented.");
    }

    public String infoLine()
    {
        return VaultInfo.vaultInfoForCypher(CYPHER_ID);
    }
}
