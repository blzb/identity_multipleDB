/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Angel
 */
public class UserStoreTest {
    private PropertiesConfiguration con;
    private String name;
    public UserStoreTest() {
        con = new PropertiesConfiguration();
        con.addProperty("userstore.username", "INFRA");
        con.addProperty("userstore.password", "MnsGwji32");
        con.addProperty("userstore.url", "jdbc:oracle:thin:@172.18.11.185:1521:TUEMPDES");
        con.addProperty("userstore.driver", "oracle.jdbc.OracleDriver");
        con.addProperty("userstore.maxActive","50");
        con.addProperty("userstore.minIdle", "5");
        con.addProperty("userstore.maxWait", "60000");
        con.addProperty("userstore.selectUser", "Select count(*) from V_USUARIO_LOGIN where CVE_USUARIO like ? and PASSWORD like ?");
        con.addProperty("userstore.algorithm", "SHA-1");
        con.addProperty("userstore.encoding","HEX");
        name="test";
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of getConnection method, of class UserStore.
     */
    @Test
    public void testGetConnection() throws Exception {
        System.out.println("getConnection");
        UserStore instance = new UserStore(name, con);
        Connection expResult = instance.getConnection();
        assertNotNull(expResult);
    }
    /**
     * Test of preparePassword method, of class UserStore.
     */
    @Test
    public void testPreparePassword() {
        System.out.println("preparePassword");
        String password = "angel";
        UserStore instance = new UserStore(name, con);
        String expResult = "";
        String result = instance.preparePassword(password);
        System.out.println(result);
        assertNotNull(result);
    }

    /**
     * Test of validate method, of class UserStore.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");
        String userName = "getsec@hotmail.com";
        String password = "Passw0rd";
        UserStore instance = new UserStore(name, con);
        boolean expResult = true;
        boolean result = instance.validate(userName, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
}
