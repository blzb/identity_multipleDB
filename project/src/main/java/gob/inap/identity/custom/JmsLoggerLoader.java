/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.io.File;
import java.util.Properties;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author Angel
 */
public class JmsLoggerLoader {

    private Properties env = null;
    private String queue;

    public JmsLoggerLoader() {
        File file = new File(System.getProperty("carbon.home") + "/repository/conf/jms.properties");
        try {
            PropertiesConfiguration configuration = new PropertiesConfiguration(file);
            if (configuration != null) {
                env = new Properties();
                env.put("java.naming.provider.url", configuration.getString("java.naming.provider.url"));
                env.put("java.naming.factory.initial", configuration.getString("java.naming.factory.initial"));
                env.put("transport.jms.ConnectionFactoryJNDIName", configuration.getString("transport.jms.ConnectionFactoryJNDIName"));
                queue = configuration.getString("jms.queue");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getEnv() {
        return env;
    }

    public String getQueue() {
        return queue;
    }
}
