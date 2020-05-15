package com.projecta.monsai.util;


import org.apache.log4j.Logger;
import org.codehaus.enunciate.modules.jersey.EnunciateJerseyServletContainer;
import org.codehaus.enunciate.modules.jersey.EnunciateSpringComponentProviderFactory;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

/**
 * Extension of {@link EnunciateJerseyServletContainer} to provide better logging
 * if the spring initialisation fails.
 *
 * @author akuehnel
 */
public class LoggingEnunciateJerseyServletContainer extends EnunciateJerseyServletContainer {

    private static final long serialVersionUID = -6049464767438601489L;
    private static final Logger LOG = Logger.getLogger( LoggingEnunciateJerseyServletContainer.class );


    /**
     * Loads the spring component provider factory
     */
    @Override
    protected IoCComponentProviderFactory loadResourceProviderFacotry(ResourceConfig rc) {
        try {
            return new EnunciateSpringComponentProviderFactory( rc, getServletContext() );
        }
        catch (Throwable e) {
            LOG.error( "Unable to load the spring component provider factory.", e );
            throw new RuntimeException( e );
      }
    }
}
