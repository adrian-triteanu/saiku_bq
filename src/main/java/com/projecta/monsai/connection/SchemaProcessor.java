
package com.projecta.monsai.connection;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import mondrian.olap.Util;
import mondrian.spi.DynamicSchemaProcessor;

import org.codehaus.jackson.map.ObjectMapper;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Schema processor that replaces different currencies in the mondrian schema,
 * depending on the configuration in the cubes.properties
 *
 * @author akuehnel
 */
@Component
public class SchemaProcessor implements DynamicSchemaProcessor {

    private static String schemaXML;

    @Autowired private Config config;
    @Autowired private SchemaVersioning schemaVersioning;

    /**
     * (a) read schema, (b) replace currency placeholder with the on currency symbol
     * specified in cubes properties (c) store the processed schema for later access
     */
    public void readSchema() throws Exception {

        String mondrianSchemaFile  = config.getRequiredProperty( "mondrianSchemaFile" );
        String currencyCode        = config.getRequiredProperty( "currencyCode" );
        String currencyPlaceholder = config.getRequiredProperty( "currencyPlaceholder" );

        // configuration to use include files
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        factory.setXIncludeAware( true );

        // parse the schema.xml
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document doc = documentBuilder.parse(mondrianSchemaFile);

        // generate a string again
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult((new StringWriter()));
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        // add a timestamp to the XML to force a schema reload even if nothing has changed
        String schema = result.getWriter().toString()
                      + "<!-- " + System.currentTimeMillis() + "-->";

        // change EUR string to the matching symbol
        if (currencyCode.equals("EUR"))
            currencyCode = "€";

        // append white space in case replaceWith contains more than one character
        if (currencyCode.length() > 1)
            currencyCode = " " + currencyCode;

        schema = schema.replace(currencyPlaceholder, currencyCode);

        System.out.print("Schema versioning start");
        String versionMap = schemaVersioning.getSchemaVersionList();

        Map<Object, Object> mapping =
                new ObjectMapper().readValue(versionMap, Map.class);

        for (Entry<Object, Object> entry : mapping.entrySet()) {
            /** temporarly **/
            schema = schema.replace("schema=\"" + entry.getKey() + "\"", "schema=\"" + entry.getKey() + entry.getValue() + "\"");
        }

        System.out.print("Schema versioning end");

        schemaXML = schema;
    }


    @Override
    public String processSchema( String schemaUrl, Util.PropertyList connectInfo ) throws Exception {
        return schemaXML;
    }
}
