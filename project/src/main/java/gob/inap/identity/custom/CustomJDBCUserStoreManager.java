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
    private List<CustomUserStore> userStores = new ArrayList<CustomUserStore>();

    public CustomJDBCUserStoreManager(org.wso2.carbon.user.api.RealmConfiguration realmConfig,
            int tenantId) {
        super(realmConfig, tenantId);
        try {
            loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomJDBCUserStoreManager(DataSource ds,
            org.wso2.carbon.user.api.RealmConfiguration realmConfig,
            int tenantId, boolean addInitData) throws UserStoreException {
        super(ds, realmConfig, tenantId, addInitData);
        try {
            loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
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
            loadProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String[] getRoleListOfUser(String userName) throws UserStoreException {
        String[] names = super.getRoleListOfUser(userName);
        String[] result = Arrays.copyOf(names,names.length+1);
        result[names.length] = "economia";
        return result;
    }


    @Override
    public boolean authenticate(String userName, Object credential) throws UserStoreException {

        boolean internal = super.authenticate(userName, credential);
        String password = (String) credential;
        boolean external = multivalidate(userName, password);
        return external || internal;
    }

    private void loadProperties() throws ConfigurationException, SQLException {

        File directory = new File(System.getProperty("carbon.home") + "/repository/conf/userStores");
        File[] stores = directory.listFiles();
        for (File store : stores) {
            if (store.isFile() && store.getName().endsWith(".properties")) {
                String nombre = store.getName().replace(".properties", "");
                System.out.println("loading "+nombre+" ...");
                PropertiesConfiguration configuration = new PropertiesConfiguration(store);
                CustomUserStore customUserStore = new CustomUserStore(nombre, configuration, realmConfig.getUserStoreProperty(JDBCRealmConstants.MAX_ACTIVE), realmConfig.getUserStoreProperty(JDBCRealmConstants.MIN_IDLE), realmConfig.getUserStoreProperty(JDBCRealmConstants.MAX_WAIT));
                this.userStores.add(customUserStore);

            }
        }
    }

    private boolean multivalidate(String userName, String password) {
        boolean response = false;
        for (CustomUserStore userStore : userStores) {
            if (validate(userStore, userName, password)) {
                response = true;
            }
        }
        return response;
    }

    private boolean validate(CustomUserStore userStore, String userName, String password) {        
        Connection dbConnection = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;
        String sqlstmt = null;
        boolean isAuthed = false;
        try {
            dbConnection = userStore.getConnection();
            dbConnection.setAutoCommit(false);
            sqlstmt = userStore.getSelectUser();

            if (log.isDebugEnabled()) {
                log.debug(sqlstmt);
            }
            prepStmt = dbConnection.prepareStatement(sqlstmt);
            prepStmt.setString(1, userName);
            prepStmt.setString(2, userStore.preparePassword(password));

            rs = prepStmt.executeQuery();

            if (rs.next() == true) {
                int count = rs.getInt("COUNT(*)");
                if (count > 0) {
                    isAuthed = true;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            log.error("Using sql : " + sqlstmt);
        } finally {
            DatabaseUtil.closeAllConnections(dbConnection, rs, prepStmt);
        }
        if (log.isDebugEnabled()) {
            log.debug("User " + userName + " login attempt. Login success :: " + isAuthed);
        }
        return isAuthed;
    }
}
