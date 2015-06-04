package net.wedjaa.ansible.vault;


public class ProvisioningInfo
{

        String apiUser;
        String apiClientId;
        String apiPassword;

        public ProvisioningInfo()
        {

        }

        public String getApiUser()
        {
            return apiUser;
        }

        public void setApiUser(String apiUser)
        {
            this.apiUser = apiUser;
        }

        public String getApiClientId()
        {
            return apiClientId;
        }

        public void setApiClientId(String apiClientId)
        {
            this.apiClientId = apiClientId;
        }

        public String getApiPassword()
        {
            return apiPassword;
        }

        public void setApiPassword(String apiPassword)
        {
            this.apiPassword = apiPassword;
        }

        public String toString()
        {
            return apiUser + "@" + apiClientId + " - " + apiPassword;
        }
}
