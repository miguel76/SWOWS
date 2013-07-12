/*
 * Copyright (c) 2011 Dario
 * 

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
package org.swows.producer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.swows.graph.DynamicChangingGraph;
import org.swows.graph.events.DynamicDataset;
import org.swows.graph.events.DynamicGraph;
import org.swows.runnable.LocalTimer;
import org.swows.util.GraphUtils;
import org.swows.vocabulary.DF;
import org.swows.xmlinrdf.DomEncoder;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TwitterProducer extends GraphProducer {

    private Document doc = null;
    private List<Status> statuses = null;
    private Element tweet = null;
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private Transformer transformer = null;
    private DOMSource source;
    StreamResult fileXml = null;
    Date date = null;
    private Element twitter, tweetText;
    private int tweetsLength;
    private DynamicChangingGraph dynamicChangingGraph = null;
    private String twitterUsername;
    private int tweetNumber;
    private Graph graph;
    
    
    public TwitterProducer(Graph conf, Node confRoot, ProducerMap map) {
        //public static Node getSingleValueProperty(Graph graph, Node subject, Node predicate)
        Node twitterUsernameNode = GraphUtils.getSingleValueProperty(conf, confRoot, DF.twitterUsername.asNode());
        if (twitterUsernameNode != null) {
            twitterUsername = twitterUsernameNode.getLiteralLexicalForm();
        }
        Node tweetNumberNode = GraphUtils.getSingleValueProperty(conf, confRoot, DF.tweetNumber.asNode());
        if (tweetNumberNode != null) {
            tweetNumber = Integer.parseInt(tweetNumberNode.getLiteralLexicalForm());
        }
    }

    public TwitterProducer(String username, int tweetNumber) {
        this.twitterUsername = username;
        this.tweetNumber = tweetNumber;
        setup();
    }

  // public static void main(String[] args) { //PER FARE PROVE

        // Thread thread = new Thread(new TwitterProducer());
        // thread.start();
      // new TwitterProducer("DarioLap", 5);
        //setup();
//se grafo = null, chiama setup
   // }

    @Override
    public DynamicGraph createGraph(DynamicDataset inputDataset) {
        if (dynamicChangingGraph == null) {
            setup();
        }
        return dynamicChangingGraph;
    }

    public boolean dependsFrom(Producer producer) {
        return false;
    }

    //setup
    public void setup() {

        //final Document doc = null;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        
        twitter = doc.createElement("Twitter");
        doc.appendChild(twitter);
       // twitter.setAttribute(twitterUsername, twitterUsername);
         Attr tweetNumberAttr = doc.createAttribute("tweetNumber");
            tweetNumberAttr.setValue(Integer.toString(tweetNumber));
            twitter.setAttributeNode(tweetNumberAttr);
                
        System.out.println("\n\n------- Prova Post Utente -------\n");
        final Twitter twitterElement = new TwitterFactory().getInstance();

        final String selectedQuery = twitterUsername;

        //final List<Status> statuses = null;

        try {
            statuses = twitterElement.getUserTimeline(selectedQuery);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        if (statuses.size() > 0) {
            date = statuses.get(0).getCreatedAt();
        }

        //Vector<String> tweetList = new Vector<String>();
        //tweetsLength = statuses.size();
        if (statuses.size() > tweetNumber) {
            tweetsLength = tweetNumber;
        } else {
            tweetsLength = statuses.size();
        }

        for (int i = 0; i < tweetsLength; i++) {

            //for (Status tweets : statuses) {
            System.out.println("@" + statuses.get(i).getUser().getScreenName() + " - " + statuses.get(i).getText());
            // System.out.println("@" + tweetList.get(i) + " - " + tweetList.get(i + 1));
            tweet = doc.createElement("tweet");
            twitter.appendChild(tweet);

            Attr tweetId = doc.createAttribute("id");
            tweetId.setValue(Integer.toString(i));
            tweet.setAttributeNode(tweetId);

          //  tweetUser = doc.createElement("username");
          //  tweetUser.appendChild(doc.createTextNode(statuses.get(i).getUser().getScreenName()));
          //  tweet.appendChild(tweetUser);

          //  tweetDate = doc.createElement("date");
          //  tweetDate.appendChild(doc.createTextNode(statuses.get(i).getCreatedAt().toString()));
          //  tweet.appendChild(tweetDate);

            tweetText = doc.createElement("text");
            tweetText.appendChild(doc.createTextNode(statuses.get(i).getText()));
            tweet.appendChild(tweetText);


            /*
* Attr data = doc.createAttribute("Data");
* data.appendChild(doc.createTextNode(statuses.get(i).getCreatedAt().toString()));
* tweet.setAttributeNode(data);
*/

            //tweet.appendChild(doc.createTextNode(statuses.get(i).getText()));
            //twitter.appendChild(doc.createElement("tweet"));

        }

        // Document docRet = null;


        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
        source = new DOMSource(doc);
        
        try {

            fileXml = new StreamResult(new FileOutputStream("situazioneIniziale.xml"));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }

        try {
            transformer = transformerFactory.newTransformer();
            transformer.transform(source, fileXml);
        } catch (TransformerException te) {
            te.printStackTrace();
        }

        graph = DomEncoder.encode(doc);

        ModelFactory.createModelForGraph(graph).write(System.out);
        //ModelFactory.createModelForGraph(graph).
        
        dynamicChangingGraph = new DynamicChangingGraph(graph);


        // ---------------------------------------------
        // -----------CONTROLLA AGGIORNAMENTI-----------
        // ---------------------------------------------

        //LocalTimer localTimer = new LocalTimer();

        //localTimer.get().schedule(null, 10000, 10000);
        //Timer timer = new Timer();

        LocalTimer.get().schedule(new TimerTask() {

            public void run() {

                try {
                    statuses = twitterElement.getUserTimeline(selectedQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }

                if (statuses.size() > tweetNumber) {
                    tweetsLength = tweetNumber;
                } else {
                    tweetsLength = statuses.size();
                }
                
                doc.removeChild(twitter);
                
                twitter = doc.createElement("Twitter");
               // twitter.setAttributeNode(tweetNumberAttr);
                doc.appendChild(twitter);
                
                for (int i = 0; i < tweetsLength; i++) {
                    // for (Status tweets : statuses) {
                    if (statuses.get(0).getCreatedAt().after(date)) {

                        //C'è stato un nuovo tweet
                        System.out.println("@" + statuses.get(i).getUser().getScreenName() + " - " + statuses.get(i).getText());
                        // System.out.println("@" + tweetList.get(i) + " - " + tweetList.get(i + 1));
                        
                        tweet = doc.createElement("tweet");
                        twitter.appendChild(tweet);

                        Attr tweetId = doc.createAttribute("id");
                        tweetId.setValue(Integer.toString(i));
                        tweet.setAttributeNode(tweetId);

                     //   tweetUser = doc.createElement("username");
                     //   tweetUser.appendChild(doc.createTextNode(statuses.get(i).getUser().getScreenName()));
                     //   tweet.appendChild(tweetUser);

                    //    tweetDate = doc.createElement("date");
                     //   tweetDate.appendChild(doc.createTextNode(statuses.get(i).getCreatedAt().toString()));
                    //    tweet.appendChild(tweetDate);

                        tweetText = doc.createElement("tweetText");
                        tweetText.appendChild(doc.createTextNode(statuses.get(i).getText()));
                        tweet.appendChild(tweetText);

                        transformerFactory = TransformerFactory.newInstance();
                        try {
                            transformer = transformerFactory.newTransformer();
                        } catch (TransformerException te) {
                            te.printStackTrace();
                        }

                        source = new DOMSource(doc);

                        fileXml = null;

                        try {

                            fileXml = new StreamResult(new FileOutputStream("situazioneAggiornata.xml"));
                        } catch (FileNotFoundException fnfe) {
                            fnfe.printStackTrace();
                        }

                        try {
                            transformer = transformerFactory.newTransformer();
                            transformer.transform(source, fileXml);
                        } catch (TransformerException te) {
                            te.printStackTrace();
                        }

                        System.out.println("NUOVO TWEET: @" + statuses.get(i).getUser().getScreenName() + " - " + statuses.get(i).getText());

                        Graph graphUpdate = DomEncoder.encode(doc);
                        ModelFactory.createModelForGraph(graphUpdate).write(System.out);

                        //DynamicChangingGraph.setBaseGraph(graphUpdate, graph);
                        dynamicChangingGraph.setBaseGraph(graphUpdate);
                        
                        
                    // dynamicChangingGraph = new DynamicChangingGraph (graphUpdate);
                        
                        
                    } else {
                        break;
                    }
                }

                if (statuses.get(0).getCreatedAt().after(date)) {
                    date = statuses.get(0).getCreatedAt();
                }

                try {
                    Thread.sleep(180000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }, 1000, 180000);

    }
}