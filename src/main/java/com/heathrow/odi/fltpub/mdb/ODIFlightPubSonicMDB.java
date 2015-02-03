package com.heathrow.odi.fltpub.mdb;


import org.apache.log4j.Logger;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ODIFlightPubSonicMDB implements MessageListener {

    private Logger logger = Logger.getLogger(ODIFlightPubSonicMDB.class);

    private String topicName = null;
    private String tcfName = null;
    private Topic ouboundTopic = null;
    private TopicConnectionFactory outboundTcf = null;

    public ODIFlightPubSonicMDB() {
        System.out.println("ODIFlightPubSonicMDB.constructor");
    }

    @Override
    public void onMessage(Message message) {
        logger.debug("onMessage fired");

        // create internal (WebLogic) context
        Context ctx = null;

        try {
            // create internal (WebLogic) context
            ctx = new InitialContext();
            // lookup Topic, configured in ejb-jar.xml
            if (topicName == null || ouboundTopic == null) {
                topicName = (String)ctx.lookup("java:comp/env/WLFlightPubTopic");
                ouboundTopic = (Topic) ctx.lookup(topicName);
                logger.debug("Topic {" + topicName + "} lookup ok");
            }
            if (tcfName == null || outboundTcf == null) {
                // retrieve topic connection factory, configured in ejb-jar.xml
                tcfName = (String)ctx.lookup("java:comp/env/WLFlightPubTCF");
                outboundTcf = (TopicConnectionFactory)ctx.lookup(tcfName);
                logger.debug("TopicConnectionFactory {" + tcfName + "} lookup ok");
            }

            // create a topic connection
            TopicConnection topicConnection = outboundTcf.createTopicConnection();
            logger.debug("Created TopicConnection");

            // create JMS session
            TopicSession topicSession = topicConnection.createTopicSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            logger.debug("Created TopicSession");

            // create topic publisher
            TopicPublisher topicPublisher = topicSession.createPublisher(ouboundTopic);
            logger.debug("Created TopicPublisher");

            // copy message into new TextMessage
            TextMessage outMessage = topicSession.createTextMessage();
            if (message instanceof TextMessage) {
                TextMessage inMessage = (TextMessage)message;
                // set message text
                outMessage.setText(inMessage.getText());
                // set JMS properties
                outMessage.setJMSCorrelationID(
                        inMessage.getJMSCorrelationID());
                outMessage.setJMSCorrelationIDAsBytes(
                        inMessage.getJMSCorrelationIDAsBytes());
                outMessage.setJMSDeliveryMode(
                        inMessage.getJMSDeliveryMode());
                outMessage.setJMSDestination(
                        inMessage.getJMSDestination());
                outMessage.setJMSExpiration(
                        inMessage.getJMSExpiration());
                outMessage.setJMSMessageID(
                        inMessage.getJMSMessageID());
                outMessage.setJMSPriority(
                        inMessage.getJMSPriority());
                outMessage.setJMSRedelivered(
                        inMessage.getJMSRedelivered());
                outMessage.setJMSReplyTo(
                        inMessage.getJMSReplyTo());
                outMessage.setJMSTimestamp(
                        inMessage.getJMSTimestamp());
                outMessage.setJMSType(
                        inMessage.getJMSType());
                // other properties
            }

            // publish the message to the topic
            topicPublisher.publish(outMessage);
            logger.debug("Sent message to Topic [" +
                    ouboundTopic.getTopicName() + "]");

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
