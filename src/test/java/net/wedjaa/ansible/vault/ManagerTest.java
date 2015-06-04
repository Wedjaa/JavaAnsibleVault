package net.wedjaa.ansible.vault;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ManagerTest
{
    Logger logger = LoggerFactory.getLogger(ManagerTest.class);

    @Test
    public void testYamlVault()
    {
        Manager manager  = new Manager();
        ProvisioningInfo provisioningInfo = new ProvisioningInfo();
        provisioningInfo.setApiUser("Secret User");
        provisioningInfo.setApiClientId("The provisioner ClientId");
        provisioningInfo.setApiPassword("The secret password");

        try
        {
            String provisioningVault = manager.writeToVault(provisioningInfo, "password");
            logger.info("Created vault:\n{}", provisioningVault);
            ProvisioningInfo backtoYa = (ProvisioningInfo) manager.getFromVault(provisioningInfo.getClass(), provisioningVault, "password");
            logger.info("Read Back: {}", backtoYa.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
