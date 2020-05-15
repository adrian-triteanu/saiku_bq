package com.projecta.monsai.connection;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class to that provides the configuration information by reading the
 * cubes.properties file, the path to which must either be provided as the
 * system property or the context parameter with the name "cubes.config"
 */
@Component
public class Config {

    @Autowired private ServletContext servletContext;

    private Properties properties;


    /**
     * Loads the properties on spring intialisation
     */
    @PostConstruct
    public void loadProperties() {

        String configFileName = System.getProperty("cubes.config");
        if (StringUtils.isEmpty(configFileName)) {
            configFileName = servletContext.getInitParameter("cubes.config");
        }
        if (StringUtils.isEmpty(configFileName)) {
            throw new RuntimeException("Please set \"cubes.config\" as a system property or a context param");
        }

        properties = new Properties();
        try ( Reader reader = new InputStreamReader(new FileInputStream(configFileName), "UTF8") ) {
            properties.load(reader);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Retrieves the property with the given name
     */
    public String getProperty( String key ) {
        return properties.getProperty(key);
    }


    /**
     * Retrieves the list of properties
     */
    public Properties getProperties() {
        return properties;
    }


    /**
     * Retrieves the property with the given name. If the property is not set,
     * the default value is used.
     */
    public String getProperty( String key, String defaultValue ) {

        String value = properties.getProperty( key );
        return StringUtils.isBlank( value ) ? defaultValue : value;
    }


    /**
     * Retrieves the property with the given name. If the property is not set,
     * this will cause an exception
     */
    public String getRequiredProperty(String key) {

        String result = properties.getProperty(key);
        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("Property \"" + key + "\" not set");
        }
        return result;
    }

}
