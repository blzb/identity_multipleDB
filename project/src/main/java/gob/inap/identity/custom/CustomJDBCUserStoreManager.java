/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.io.File;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.user.core.Permission;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.internal.UMListenerServiceComponent;
import org.wso2.carbon.user.core.jdbc.JDBCRealmConstants;
import org.wso2.carbon.user.core.jdbc.JDBCUserStoreManager;
import org.wso2.carbon.user.core.ldap.ApacheDSUserStoreManager;
import org.wso2.carbon.user.core.listener.UserStoreManagerListener;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.user.core.util.DatabaseUtil;
import org.wso2.carbon.user.core.util.UserCoreUtil;

/**
 *
 * @author Lucasian
 */
public class CustomJDBCUserStoreManager extends JDBCUserStoreManager {

    private static Log log = LogFactory.getLog(CustomJDBCUserStoreManager.class);
    private List<UserStore> userStores = new ArrayList<UserStore>();
    private UserStoresLoader loader = new UserStoresLoader();
    private JmsLogin jmsLogin;

    public CustomJDBCUserStoreManager(org.wso2.carbon.user.api.RealmConfiguration realmConfig,
            int tenantId) throws UserStoreException {
        super(realmConfig, tenantId);
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

    public CustomJDBCUserStoreManager(DataSource ds,
            org.wso2.carbon.user.api.RealmConfiguration realmConfig,
            int tenantId, boolean addInitData) throws UserStoreException {
        super(ds, realmConfig, tenantId, addInitData);
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

    public CustomJDBCUserStoreManager(org.wso2.carbon.user.api.RealmConfiguration realmConfig,
            Map<String, Object> properties,
            ClaimManager claimManager,
            ProfileConfigurationManager profileManager,
            UserRealm realm, Integer tenantId)
            throws UserStoreException {
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
        long start = new java.util.Date().getTime(); //start time       
        boolean internal = super.authenticate(userName, credential);
        String password = (String) credential;
        boolean external = multivalidate(userName, password);
        boolean result = external || internal;
        long elapseTime = new java.util.Date().getTime() - start;
        try {
            if (jmsLogin != null) {
                jmsLogin.sendUserLogin(userName, result ? "LOGIN OK" : "LOGIN FAIL", elapseTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void loadProperties() throws ConfigurationException, SQLException {

        File directory = new File(System.getProperty("carbon.home") + "/repository/conf/userStores");
        File[] stores = directory.listFiles();
        for (File store : stores) {
            if (store.isFile() && store.getName().endsWith(".properties")) {
                String nombre = store.getName().replace(".properties", "");
                PropertiesConfiguration configuration = new PropertiesConfiguration(store);
                UserStore customUserStore = new UserStore(nombre, configuration);
                this.userStores.add(customUserStore);

            }
        }
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
