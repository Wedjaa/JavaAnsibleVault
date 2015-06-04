package net.wedjaa.ansible.vault.crypto.decoders;

import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES;
import net.wedjaa.ansible.vault.crypto.decoders.implementation.CypherAES256;
import net.wedjaa.ansible.vault.crypto.decoders.inter.CypherInterface;

/**
 * Created by mrwho on 03/06/15.
 */
public class CypherFactory
{
    public static CypherInterface getCypher(String cypherName)
    {
        if (cypherName.equals(CypherAES.CYPHER_ID))
        {
            return new CypherAES();
        }

        if (cypherName.equals(CypherAES256.CYPHER_ID))
        {
            return new CypherAES256();
        }

        return null;
    }

}
