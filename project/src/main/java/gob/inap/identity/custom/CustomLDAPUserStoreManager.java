/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.config.RealmConfiguration;

import org.wso2.carbon.user.core.ldap.LDAPUserStoreManager;

import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;

/**
 *
 * @author Lucasian
 */
public class CustomLDAPUserStoreManager extends LDAPUserStoreManager {

    private List<UserStore> userStores = new ArrayList<UserStore>();
    private UserStoresLoader loader = new UserStoresLoader();
    private JmsLogin jmsLogin;

    public CustomLDAPUserStoreManager(RealmConfiguration realmConfig, ClaimManager claimManager, ProfileConfigurationManager profileManager) throws org.wso2.carbon.user.core.UserStoreException {
        super(realmConfig, claimManager, profileManager);
        try {
            this.userStores = loader.loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            jmsLogin = new JmsLogin();
        } catch (Exception e) {
            e.printStackTrace();
            jmsLogin = null;
        }
    }

    public CustomLDAPUserStoreManager(RealmConfiguration realmConfig, Map<String, Object> properties, ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId) throws UserStoreException {
        super(realmConfig, properties, claimManager, profileManager, realm, tenantId);
        try {
            this.userStores = loader.loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            jmsLogin = new JmsLogin();
        } catch (Exception e) {
            e.printStackTrace();
            jmsLogin = null;
        }
    }

    @Override
    public String[] getRoleListOfUser(String userName) throws UserStoreException {
        String[] names = super.getRoleListOfUser(userName);
        String[] result = Arrays.copyOf(names, names.length + 1);
        result[names.length] = "economia";
        return result;
    }

    @Override
    public boolean authenticate(String userName, Object credential) throws UserStoreException {
        long start = new Date().getTime(); //start time       
        boolean internal = super.authenticate(userName, credential);
        String password = (String) credential;
        boolean external = multivalidate(userName, password);
        boolean result = external || internal;
        long elapseTime = new Date().getTime() - start;
        try {
            if (jmsLogin != null) {
                jmsLogin.sendUserLogin(userName, result ? "LOGIN OK" : "LOGIN FAIL", elapseTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean multivalidate(String userName, String password) {
        boolean response = false;
        for (UserStore userStore : userStores) {
            if (userStore.validate(userName, password)) {
                response = true;
            }
        }
        return response;
    }
}
