/*
 * Copyright 2016 - Fabio "MrWHO" Torchetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wedjaa.ansible.vault;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ManagerTest
{
    Logger logger = LoggerFactory.getLogger(ManagerTest.class);

    final static String API_USER = "Secret User";
    final static String API_CLIENT = "ClientID";
    final static String API_PASSWORD = "VerySuperSecretPassword";
    final static String WRONG_PASS_EX = "HMAC Digest doesn't match - possibly it's the wrong password.";

    @Test
    public void testValidYamlVault()
    {
        Manager manager  = new Manager();
        logger.info("Testing valid password");
        ProvisioningInfo provisioningInfo = new ProvisioningInfo();
        provisioningInfo.setApiUser(API_USER);
        provisioningInfo.setApiClientId(API_CLIENT);
        provisioningInfo.setApiPassword(API_PASSWORD);

        try
        {
            String provisioningVault = manager.writeToVault(provisioningInfo, "password");
            logger.debug("Created vault:\n{}", provisioningVault);
            ProvisioningInfo backtoYa = (ProvisioningInfo) manager.getFromVault(provisioningInfo.getClass(), provisioningVault, "password");
            logger.debug("Read Back:\n{}", backtoYa.toString());
            assertEquals(provisioningInfo.toString(), backtoYa.toString());
        }
        catch (IOException e)
        {
            fail("Exception while testing valid password vault: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidYamlVault()
    {
        Manager manager  = new Manager();
        logger.info("Testing invalid password");
        ProvisioningInfo provisioningInfo = new ProvisioningInfo();
        provisioningInfo.setApiUser(API_USER);
        provisioningInfo.setApiClientId(API_CLIENT);
        provisioningInfo.setApiPassword(API_PASSWORD);

        try
        {
            String provisioningVault = manager.writeToVault(provisioningInfo, "password");
            logger.debug("Created vault:\n{}", provisioningVault);
            ProvisioningInfo backtoYa = (ProvisioningInfo) manager.getFromVault(provisioningInfo.getClass(), provisioningVault, "wrong_password");
            logger.debug("Read Back:\n{}", backtoYa.toString());
            fail("Exception while testing invalid password vault- correct value obtained: " + backtoYa.toString());
        }
        catch (IOException e)
        {
            logger.debug("Expected exception: " + WRONG_PASS_EX);
            assertEquals(WRONG_PASS_EX, e.getMessage());
        }
    }


}
