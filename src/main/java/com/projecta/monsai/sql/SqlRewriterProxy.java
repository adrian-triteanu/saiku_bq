package com.projecta.monsai.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * A dynamic proxy that intercepts methods calls in order to rewrite SQL statements
 *
 * @author akuehnel
 */
public class SqlRewriterProxy<T> implements InvocationHandler {

    private Object     object;
    private Class<T>   clazz;
    private Connection connection;

    private static final Logger LOG = Logger.getLogger( SqlRewriterProxy.class );


    // automatically register this proxy as a JDBC driver
    static {
        try {
            DriverManager.registerDriver( createProxy( null, Driver.class, null ) );
        }
        catch ( SQLException e ) {
            LOG.error( e, e );
        }
    }


    /**
     * Creates a new proxy instance for the given interface
     */
    private static<T> T createProxy( Object object, Class<T> clazz, Connection connection ) {

        SqlRewriterProxy<T> proxy = new SqlRewriterProxy<T>();
        proxy.object     = object;
        proxy.clazz      = clazz;
        proxy.connection = connection;
        return (T) Proxy.newProxyInstance( SqlRewriterProxy.class.getClassLoader(), new Class[] { clazz }, proxy );
    }


    /**
     * Handles the intercepted methods calls
     */
    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {

        try {
            String methodName = method.getName();

            // intercept Connection.createStatement()
            if ( clazz == Connection.class ) {

                if ( methodName.equals( "createStatement" ) ) {
                    Statement statement = (Statement) method.invoke( object, args );
                    return createProxy( statement, Statement.class, connection );
                }
            }

            // intercept Statement.executeQuery()
            if ( clazz == Statement.class ) {

                if ( methodName.equals( "executeQuery" ) ) {
                    SqlRewriter rewriter = new SqlRewriter();
                    String rewrittenSql = rewriter.rewrite( (String) args[0], connection );
                    if ( rewrittenSql != null ) {
                        return method.invoke( object, new Object[] { rewrittenSql } );
                    }
                }
            }

            // Driver.toString() and Driver.connect()
            else if ( clazz == Driver.class ) {

                if ( methodName.equals( "toString" ) ) {
                    return SqlRewriterProxy.class.getName();
                }

                if ( object == null ) {
                    // proxy the postgres driver
                    object = new org.postgresql.Driver();
                }

                // proxy every connection
                if ( methodName.equals( "connect" ) && StringUtils.startsWithIgnoreCase( (String) args[0], "jdbc:postgresql" ) ) {
                    Connection connection = (Connection) method.invoke( object, args );
                    return createProxy( connection, Connection.class, connection );
                }
            }

            // dont do anything different
            return method.invoke( object, args );
        }
        catch ( InvocationTargetException e ) {
            throw e.getCause();
        }
        catch ( Exception e ) {
            throw new RuntimeException(e);
        }
    }

}
