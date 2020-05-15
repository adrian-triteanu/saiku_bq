package com.projecta.monsai.connection;

import org.saiku.datasources.connection.ISaikuConnection;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.web.impl.SecurityAwareConnectionManager;

/**
 * Extension of SecurityAwareConnectionManager that always uses the internal
 * datasource and prevents the user from flushing the caches
 *
 */
public class SaikuConnectionManager extends SecurityAwareConnectionManager {

    private static final long serialVersionUID = 265675089208665427L;

    @Override
    protected ISaikuConnection getInternalConnection(String name, SaikuDatasource datasource) {
        return new SaikuOlapConnection(name);
    }

    @Override
    public ISaikuConnection getConnection(String name) {
        return getInternalConnection(name, new SaikuDatasource());
    }

}
