/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.wso2.carbon.user.core.jdbc.JDBCRealmConstants;

/**
 *
 * @author Lucasian
 */
public class UserStoresLoader {
    public List<CustomUserStore> loadProperties() throws ConfigurationException, SQLException {
        List<CustomUserStore> userStores = new ArrayList<CustomUserStore>();
        File directory = new File(System.getProperty("carbon.home") + "/repository/conf/userStores");
        File[] stores = directory.listFiles();
        for (File store : stores) {
            if (store.isFile() && store.getName().endsWith(".properties")) {
                String nombre = store.getName().replace(".properties", "");
                System.out.println("loading "+nombre+" ...");
                PropertiesConfiguration configuration = new PropertiesConfiguration(store);
                CustomUserStore customUserStore = new CustomUserStore(nombre, configuration);
                userStores.add(customUserStore);
            }
        }
        return userStores;
    }
}
