package net.wedjaa.ansible.vault.crypto.decoders.inter;

import java.io.IOException;
import java.io.OutputStream;


public interface CypherInterface
{
    public void decrypt(OutputStream decodedStream, byte[] data, String password) throws IOException;
    public byte[] decrypt(byte[] encryptedData, String password) throws IOException;
    public void encrypt(OutputStream encodedStream, byte[] data, String password)  throws IOException;
    public byte[] encrypt(byte[] data, String password)  throws IOException;
    public String infoLine();
}
