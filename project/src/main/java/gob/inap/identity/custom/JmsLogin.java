/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.inap.identity.custom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.jms.*;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.activemq.ActiveMQSession;

/**
 *
 * @author Angel
 */
public class JmsLogin {

    private QueueConnection connection;
    
    private QueueSession session;
    private QueueSender sender;
    private String queue;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");    

    public void sendUserLogin(String username, String status, Long elapsedTime) {
        String message = "<p:addUser xmlns:p=\"http://ws.wso2.org/dataservice\">"
                + "<xs:usuario xmlns:xs=\"http://ws.wso2.org/dataservice\">" + username + "</xs:usuario>"
                + "<xs:status xmlns:xs=\"http://ws.wso2.org/dataservice\">" + status + "</xs:status>"
                + "<xs:fecha xmlns:xs=\"http://ws.wso2.org/dataservice\">" + dateFormat.format(new Date()) + "</xs:fecha>"
                + "<xs:elapsed xmlns:xs=\"http://ws.wso2.org/dataservice\">"+elapsedTime+"</xs:elapsed>"
                + "</p:addUser>";
        try{
            TextMessage tm = session.createTextMessage(message);
            sender.send(tm);
        }catch(JMSException e){
            System.out.println("Reconecting to JMS Server");
            try{
                create();
                TextMessage tm = session.createTextMessage(message);
                sender.send(tm);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public JmsLogin() throws JMSException,NamingException{
        create();
    }
    public void create() throws JMSException, NamingException {
        
        JmsLoggerLoader jmsLoggerLoader = new JmsLoggerLoader();
        Properties env = jmsLoggerLoader.getEnv();
        this.queue = jmsLoggerLoader.getQueue();        
        InitialContext ic = new InitialContext(env);
        System.out.println(ic.getEnvironment());
        NamingEnumeration e = ic.listBindings("");

        while (e.hasMore()) {
            Binding binding = (Binding) e.next();
            System.out.println();
            System.out.println("Name: " + binding.getName());
            System.out.println("Type: " + binding.getClassName());
            System.out.println("Value: " + binding.getObject());
            System.out.println();
        }
        QueueConnectionFactory confac = (QueueConnectionFactory) ic.lookup("ConnectionFactory");
        connection = confac.createQueueConnection();
        session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        sender = session.createSender((Queue) ic.lookup(queue));
    }

    public void close() throws JMSException {
        sender.close();
        session.close();
        connection.close();
    }
}
