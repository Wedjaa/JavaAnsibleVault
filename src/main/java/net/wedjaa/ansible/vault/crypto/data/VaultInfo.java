package net.wedjaa.ansible.vault.crypto.data;

import net.wedjaa.ansible.vault.crypto.decoders.CypherFactory;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VaultInfo
{
    Logger logger = LoggerFactory.getLogger(VaultInfo.class);

    public final static String INFO_SEPARATOR = ";";
    public final static int INFO_ELEMENTS = 3;
    public final static int MAGIC_PART = 0;
    public final static int VERSION_PART = 1;
    public final static int CYPHER_PART = 2;

    public final static String VAULT_MAGIC="$ANSIBLE_VAULT";
    public final static String VAULT_VERSION="1.1";

    private boolean validVault;
    private String vaultVersion;
    private String vaultCypher;

    public static String vaultInfoForCypher(String vaultCypher)
    {
        String infoLine = VAULT_MAGIC+";"+VAULT_VERSION+";"+vaultCypher;
        return infoLine;
    }

    public VaultInfo(String infoLine)
    {
        logger.debug("Ansible Vault info: {}", infoLine);

        String [] infoParts = infoLine.split(INFO_SEPARATOR);
        if (infoParts.length == INFO_ELEMENTS)
        {
            if ( infoParts[MAGIC_PART].equals(VAULT_MAGIC) ) {
                validVault = true;
                vaultVersion = infoParts[VERSION_PART];
                vaultCypher = infoParts[CYPHER_PART];
            }
        }
    }

    public boolean isEncryptedVault()
    {
        return validVault;
    }

    public CypherInterface getCypher()
    {
        return CypherFactory.getCypher(vaultCypher);
    }

    public String getVaultVersion()
    {
        return vaultVersion;
    }

    public boolean isValidVault()
    {
        return isEncryptedVault() && getCypher() != null;
    }

}
