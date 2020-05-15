package com.projecta.monsai.connection;

import java.util.Properties;

import org.olap4j.OlapConnection;
import org.saiku.datasources.connection.ISaikuConnection;

/**
 * Implementation of {@link ISaikuConnection} that always uses an internal
 * connection.
 *
 * @author akuehnel
 */
public class SaikuOlapConnection implements ISaikuConnection {

    private String name;

    public SaikuOlapConnection( String name ) {
        this.name = name;
    }

    @Override
    public void setProperties(Properties props) {
    }

    @Override
    public boolean connect(Properties props) throws Exception {
        return true;
    }

    @Override
    public boolean connect() throws Exception {
        return true;
    }

    @Override
    public boolean clearCache() throws Exception {
        return true;
    }

    @Override
    public boolean initialized() {
        return true;
    }

    @Override
    public String getDatasourceType() {
        return ISaikuConnection.OLAP_DATASOURCE;
    }

    @Override
    public OlapConnection getConnection() {
        return OlapConnectionProxy.getProxyInstance();
    }

    @Override
    public String getName() {
        return name;
    }

}

