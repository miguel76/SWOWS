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
package org.swows.test;

import java.io.File;

public class ParserTest {

    /**
     * Accept two command line arguments: the name of an XML file, and
     * the name of an XSLT stylesheet. The result of the transformation
     * is written to stdout.
     */
    public static void main(String[] args)
            throws javax.xml.transform.TransformerException {
        if (args.length != 3) {
            System.err.println("Usage:");
            System.err.println("  java " + ParserTest.class.getName(  )
                    + " inXmlFileName xsltFileName outXmlFileName");
            System.exit(1);
        }
 
        File xmlFile = new File(args[0]);
        File xsltFile = new File(args[1]);
        File outXmlFile = new File(args[2]);
 
        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(xsltFile);
        javax.xml.transform.Result result =
                new javax.xml.transform.stream.StreamResult(outXmlFile);
 
        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact =
                javax.xml.transform.TransformerFactory.newInstance(  );
 
        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
 
        trans.transform(xmlSource, result);
    }
}
