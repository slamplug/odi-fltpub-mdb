package com.heathrow.odi.fltpub.mdb;


import org.apache.log4j.Logger;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ODIFlightPubSonicMDB implements MessageListener {

    private Logger logger = Logger.getLogger(ODIFlightPubSonicMDB.class);

    @Override
    public void onMessage(Message message) {
        logger.debug("onMessage fired");
        System.out.println("onMessage fired");

        // create internal (WebLogic) context
        Context ctx = null;

        try {
            if (message instanceof TextMessage) {
                System.out.println("message: " + ((TextMessage)message).getText());
            }
            // create internal (WebLogic) context
            ctx = new InitialContext();
            // retrieve topic connection factory, configured in ejb-jar.xml
            TopicConnectionFactory tcf = (TopicConnectionFactory)
                    ctx.lookup("java:comp/env/WLFlightPubTCF");
            // lookup Topic, configured in ejb-jar.xml
            Topic destination = (Topic)
                    ctx.lookup("java:comp/env/WLFlightPubTopic");
            // create topic connection
            TopicConnection connection = tcf.createTopicConnection();
            // create JMS session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // start connection
            connection.start();
            // message producer
            MessageProducer producer = session.createProducer(destination);
            // send the message
            System.out.println("sending message to Topic [" + destination.getTopicName() + "]");
            logger.debug("sending message to Topic [" + destination.getTopicName() + "]");
            producer.send(message);

        } catch (NamingException e) {
            logger.error("NamingException: error: " + e.getMessage());
            e.printStackTrace();
        } catch (JMSException e) {
            logger.error("JMSException : error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {} // dont care
            }
        }
    }
}
