package com.projecta.monsai.query;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.saiku.web.rest.objects.acl.Acl;
import org.saiku.web.rest.resources.BasicRepositoryResource;
import org.saiku.web.rest.resources.BasicRepositoryResource2;
import org.saiku.web.rest.resources.BasicTagRepositoryResource;
import org.saiku.web.rest.resources.FilterRepositoryResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.projecta.monsai.connection.Config;

/**
 * BeanPostProcessor that configures the path where saiku queries will be stored
 *
 * @author akuehnel
 */
@Component
public class SaikuStorageDirConfigurer implements BeanPostProcessor {

    private String storageDir;

    @Autowired private Config config;


    @PostConstruct
    public void init() throws Exception {

        storageDir = config.getProperty( "saikuStorageDir" );
        if ( !StringUtils.isBlank( storageDir ) ) {
            storageDir = "file://" + storageDir;
        }
    }


    @Override
    public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException {
        return bean; // do nothing
    }


    /**
     * Overrides the configured storage directory path for several beans,
     * replacing it with a configurable value
     */
    @Override
    public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException {

        if ( StringUtils.isBlank( storageDir ) ) {
            return bean;
        }

        try {
            if ( bean instanceof BasicRepositoryResource ) {
                ((BasicRepositoryResource) bean).setPath( storageDir );
            }
            if ( bean instanceof BasicRepositoryResource2 ) {
                ((BasicRepositoryResource2) bean).setPath( storageDir );
            }
            else if ( bean instanceof Acl ) {
                ((Acl) bean).setPath( storageDir );
            }
            else if ( bean instanceof BasicTagRepositoryResource ) {
                ((BasicTagRepositoryResource) bean).setPath( storageDir );
            }
            else if ( bean instanceof FilterRepositoryResource ) {
                ((FilterRepositoryResource) bean).setPath( storageDir );
            }
        }
        catch ( Exception e ) {
            throw new BeanCreationException( beanName, "Error getting saiku storage dir", e );
        }

        return bean;
    }

}
