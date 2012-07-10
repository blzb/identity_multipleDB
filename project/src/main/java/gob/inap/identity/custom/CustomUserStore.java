/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
<<<<<<< HEAD
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
=======
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
>>>>>>> origin/master
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
<<<<<<< HEAD
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.core.util.DatabaseUtil;
=======
>>>>>>> origin/master

/**
 *
 * @author Lucasian
 */
public class CustomUserStore {

<<<<<<< HEAD
    private static Log log = LogFactory.getLog(CustomUserStore.class);
=======
>>>>>>> origin/master
    private DataSource dataSource;
    private MessageDigest messageDigest;
    private String selectUser;
    private String nombre;
<<<<<<< HEAD
    private String role;

    public CustomUserStore(String nombre, PropertiesConfiguration configuration) {
        this.role="economia";
=======

    public CustomUserStore(String nombre, PropertiesConfiguration configuration, String maxActive, String minIdle, String maxWait) {
>>>>>>> origin/master
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
<<<<<<< HEAD
                System.out.println("Algoritmo " + algorithm + "no encontrado... no se cifrararan los passwords");
                messageDigest = null;
=======
                System.out.println("Algoritmo "+algorithm+"no encontrado... no se cifrararan los passwords");
            messageDigest = null;
>>>>>>> origin/master
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
<<<<<<< HEAD
=======
            System.out.println("Cifrando password"+password);
>>>>>>> origin/master
            byte[] bytes = messageDigest.digest(password.getBytes());
            bytes = Base64.encodeBase64(bytes);
            return new String(bytes);
        }
        return password;
    }
<<<<<<< HEAD

    public boolean validate(String userName, String password) {
        Connection dbConnection = null;
        ResultSet rs = null;
        PreparedStatement prepStmt = null;
        String sqlstmt = null;
        boolean isAuthed = false;
        try {
            dbConnection = this.getConnection();
            dbConnection.setAutoCommit(false);
            sqlstmt = this.getSelectUser();

            if (log.isDebugEnabled()) {
                log.debug(sqlstmt);
            }
            prepStmt = dbConnection.prepareStatement(sqlstmt);
            prepStmt.setString(1, userName);
            prepStmt.setString(2, this.preparePassword(password));

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
=======
>>>>>>> origin/master
}
