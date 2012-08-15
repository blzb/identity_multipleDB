/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.core.util.DatabaseUtil;



/**
 *
 * @author Lucasian
 */
public class UserStore {

    private static Log log = LogFactory.getLog(UserStore.class);

    private DataSource dataSource;
    private MessageDigest messageDigest;
    private String selectUser;
    private String nombre;
    private String encoding;

    private String role;

    public UserStore(String nombre, PropertiesConfiguration configuration) {
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
        System.out.println(ds);
        this.selectUser = configuration.getString("userstore.selectUser");
        this.encoding = configuration.getString("userstore.encoding");
        String algorithm = configuration.getString("userstore.algorithm");
        if (algorithm != null && !algorithm.isEmpty()) {
            try {
                messageDigest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException algorithmException) {

                System.out.println("Algoritmo " + algorithm + "no encontrado... no se cifrararan los passwords");
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
        if (messageDigest != null && this.encoding != null) {

            System.out.println("Cifrando password"+password);

            byte[] bytes = messageDigest.digest(password.getBytes());
            if(this.encoding.equalsIgnoreCase("BASE64")){
                bytes = Base64.encodeBase64(bytes);
                return new String(bytes);
            }else if(this.encoding.equalsIgnoreCase("HEX")){
                return HexUtils.convert(bytes);
            }                        
        }
        return password;
    }


    public boolean validate(String userName, String password) {        
        userName=userName.replace("&#64;", "@");
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

}
