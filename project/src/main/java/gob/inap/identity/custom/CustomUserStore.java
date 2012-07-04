/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Lucasian
 */
public class CustomUserStore {

    private DataSource dataSource;
    private MessageDigest messageDigest;
    private String selectUser;
    private String nombre;

    public CustomUserStore(String nombre, PropertiesConfiguration configuration, String maxActive, String minIdle, String maxWait) {
        BasicDataSource ds = null;
        String driver = configuration.getString("userstore.driver");
        if (driver != null) {
            ds = new BasicDataSource();
            ds.setDriverClassName(driver);
            ds.setUrl(configuration.getString("userstore.url"));
            ds.setUsername(configuration.getString("userstore.username"));
            ds.setPassword(configuration.getString("userstore.password"));
            ds.setMaxActive(configuration.getInt("userstore.maxActive"));
            ds.setMinIdle(configuration.getInt("userstore.minIdle"));
            ds.setMaxWait(configuration.getInt("userstore.maxWait"));
        }
        this.dataSource = ds;
        this.selectUser = configuration.getString("userstore.selectUser");
        String algorithm = configuration.getString("userstore.algorithm");
        if (algorithm != null && !algorithm.isEmpty()) {
            try {
                messageDigest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException algorithmException) {
                System.out.println("Algoritmo "+algorithm+"no encontrado... no se cifrararan los passwords");
            messageDigest = null;
            }
            this.nombre = nombre;
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSelectUser() {
        return selectUser;
    }

    public void setSelectUser(String selectUser) {
        this.selectUser = selectUser;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Connection getConnection() throws SQLException {
        Connection dbConnection = this.dataSource.getConnection();
        dbConnection.setAutoCommit(false);
        dbConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return dbConnection;
    }

    public String preparePassword(String password) {
        if (messageDigest != null) {
            System.out.println("Cifrando password"+password);
            byte[] bytes = messageDigest.digest(password.getBytes());
            bytes = Base64.encodeBase64(bytes);
            return new String(bytes);
        }
        return password;
    }
}
