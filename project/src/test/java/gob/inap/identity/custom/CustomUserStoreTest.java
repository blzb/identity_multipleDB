/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Lucasian
 */
public class CustomUserStoreTest {
    
    public CustomUserStoreTest() {
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
     * Test of getDataSource method, of class CustomUserStore.
     */

    /**
     * Test of preparePassword method, of class CustomUserStore.
     */
    @Test
    public void testPreparePassword() throws NoSuchAlgorithmException,UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String password = "angel";
            byte[] bytes = digest.digest(password.getBytes());
            bytes = Base64.encodeBase64(bytes);
        System.out.println(new String(bytes));
        Assert.assertTrue(true);
    }
}
