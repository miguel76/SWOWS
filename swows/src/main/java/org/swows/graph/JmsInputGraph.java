package org.swows.graph;

import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;

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
import org.apache.bcel.generic.NEW;
import org.apache.log4j.Logger;
import org.swows.reader.ReaderFactory;
import org.swows.runnable.LocalTimer;
import org.swows.runnable.RunnableContext;
import org.swows.runnable.RunnableContextFactory;
import org.swows.vocabulary.Instance;
import org.swows.vocabulary.xmlInstance;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

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
		@Override
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
        		@Override
        		public void onMessage(Message message) {
        	        try {
        	            if (message instanceof TextMessage) {
        	                TextMessage txtMsg = (TextMessage) message;
        	                String msg = txtMsg.getText(); 
        	                logger.debug("[" + this + "] Received: '" + msg + "' (length " + msg.getBytes().length + ")");
        	                Model model = ModelFactory.createDefaultModel();
        	                model.read(new StringReader(msg), baseURI == null ? Instance.getURI() : baseURI, syntax);
        	                final Graph newGraph = model.getGraph();
        	                RunnableContextFactory.getDefaultRunnableContext().run(new Runnable() {
        						@Override
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
