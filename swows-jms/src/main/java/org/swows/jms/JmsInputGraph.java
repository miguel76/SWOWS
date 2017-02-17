package org.swows.jms;
/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

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


import java.io.StringReader;

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
import org.swows.graph.DynamicChangingGraph;
import org.swows.runnable.RunnableContextFactory;
import org.swows.vocabulary.SWI;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class JmsInputGraph extends DynamicChangingGraph {
	
//    private Session session;
//    private Destination destination;
//    private String subject = "rfid_queue";

//    private Logger logger = new Logger() {
//    	
//    };
		private Logger logger = Logger.getLogger(getClass());

//    private String user = ActiveMQConnection.DEFAULT_USER;
//    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
//    private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
//	private String filenameOrURI, baseURI, rdfSyntax;
	
	private ExceptionListener exceptionListener = new ExceptionListener() {
		public void onException(JMSException ex) {
	        System.out.println("[" + this + "] Caught: " + ex);
	        ex.printStackTrace();
	    }
	};

	public JmsInputGraph(String url, String user, String password, String subject, final String baseURI, final String syntax) {

		logger.debug("Preparing connection...");
		
        ActiveMQConnectionFactory connectionFactory =
        		new ActiveMQConnectionFactory(
        				user == null ? ActiveMQConnection.DEFAULT_USER : user,
        				password == null ? ActiveMQConnection.DEFAULT_PASSWORD : password,
        				url == null ? ActiveMQConnection.DEFAULT_BROKER_URL : url );
//		final RunnableContext runnableCtxt = RunnableContextFactory.getDefaultRunnableContext();

        try {

        	Connection connection = connectionFactory.createConnection();
    		logger.debug("Connection created!");
        	connection.setExceptionListener(exceptionListener);
        	connection.start();
    		logger.debug("Connection started!");
        	Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    		logger.debug("Session created!");
        	Destination destination = session.createQueue(subject);
        	MessageConsumer consumer = session.createConsumer(destination);
        	consumer.setMessageListener(new MessageListener() {
        		public void onMessage(Message message) {
        	        try {
        	            if (message instanceof TextMessage) {
        	                TextMessage txtMsg = (TextMessage) message;
        	                String msg = txtMsg.getText(); 
        	                logger.debug("[" + this + "] Received: '" + msg + "' (length " + msg.getBytes().length + ")");
        	                Model model = ModelFactory.createDefaultModel();
        	                model.read(new StringReader(msg), baseURI == null ? SWI.getURI() : baseURI, syntax);
        	                final Graph newGraph = model.getGraph();
        	                RunnableContextFactory.getDefaultRunnableContext().run(new Runnable() {
        						public void run() {
        							setBaseGraph(newGraph);
        						}
        					});
        	            }
        	        } catch (JMSException e) {
        	            exceptionListener.onException(e);
        	        } 
        		}
        	});
    		logger.debug("Listener connected!");
        
        } catch(JMSException e) {
        	exceptionListener.onException(e);
        }

	}
	
}
