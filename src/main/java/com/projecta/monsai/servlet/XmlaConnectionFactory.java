package com.projecta.monsai.servlet;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.olap4j.OlapConnection;

import com.projecta.monsai.connection.MondrianServerProvider;

import mondrian.xmla.XmlaHandler;


/**
 * Connection factory for the XmlaServlet that uses the MondrianServerProvider
 * to retrieve the currently running server.
 *
 * @author akuehnel
 */
public class XmlaConnectionFactory implements XmlaHandler.ConnectionFactory {

    @Override
    public OlapConnection getConnection( String catalog, String schema, String roleName, Properties props )
        throws SQLException {

        return MondrianServerProvider.getMondrianServer().getConnection( catalog, schema, roleName, props );
    }

    @Override
    public Map<String, Object> getPreConfiguredDiscoverDatasourcesResponse() {
        return null;
    }

}


