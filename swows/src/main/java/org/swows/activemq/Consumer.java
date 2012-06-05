/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open Web Server (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;


public class Consumer implements MessageListener, ExceptionListener {



    private Session session;
    private Destination destination;
//    private MessageProducer replyProducer;
    private String subject = "rfid_queue";

    private Logger logger;

    private String user = ActiveMQConnection.DEFAULT_USER;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private String url = ActiveMQConnection.DEFAULT_BROKER_URL;

//    private long messagesReceived = 0;

    public Consumer() {
    	this(Logger.getRootLogger());
    }

    public Consumer(Logger logger) {
        try {
           
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            Connection connection = connectionFactory.createConnection();
            
            connection.setExceptionListener(this);
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            
            destination = session.createQueue(subject);
  
            MessageConsumer consumer = session.createConsumer(destination);
   
            consumer.setMessageListener(this);

        } catch (Exception e) {
//            System.out.println("[" + this.getName() + "] Caught: " + e);
            logger.debug("[" + this.getName() + "] Caught: " + e);
            e.printStackTrace();
        }
    }
    
    private String getName() {
    	return "SimpleConsumer";
    }

    public void onMessage(Message message) {

//		messagesReceived++;

        try {

            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String msg = txtMsg.getText();  //in questo punto hai tutto il testo del messaggio xml dentro la stringa msg e
						// puoi utilizzare i vari metodi di ricerca dei nodi dell'xml con la libreria che vuoi
               
//                System.out.println("[" + this.getName() + "] Received: '" + msg + "' (length " + length + ")");
                logger.debug("[" + this.getName() + "] Received: '" + msg + "' (length " + msg.getBytes().length + ")");
            }
        } catch (JMSException e) {
            System.out.println("[" + this.getName() + "] Caught: " + e);
            e.printStackTrace();
        } 
    }

    public void onException(JMSException ex) {
        System.out.println("[" + this.getName() + "] JMS Exception occured.  Shutting down client.");
//        running = false;
    }

}