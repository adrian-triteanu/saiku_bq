package com.projecta.monsai.connection;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.service.datasource.IDatasourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Saiku {@link IDatasourceManager} that always uses
 * internal connections
 *
 */
@Component("cubesDsManager")
public class SaikuDatasourceManager implements IDatasourceManager {

    private Map<String,SaikuDatasource> datasources = Collections.synchronizedMap(new HashMap<String, SaikuDatasource>());

    @Autowired private Config config;

    @Override
    @PostConstruct
    public void load() {

        String dataSourceName = config.getProperty( "dataSourceName", "Cubes" );

        Map<String,SaikuDatasource> sources = Collections.synchronizedMap(new HashMap<String, SaikuDatasource>());
        SaikuDatasource ds = new SaikuDatasource( dataSourceName, SaikuDatasource.Type.OLAP, new Properties() );
        sources.put( dataSourceName, ds );

        datasources = sources;
    }

    @Override
    public SaikuDatasource addDatasource(SaikuDatasource datasource) {
        return datasource;
    }

    @Override
    public SaikuDatasource setDatasource(SaikuDatasource datasource) {
        return addDatasource(datasource);
    }

    @Override
    public List<SaikuDatasource> addDatasources(List<SaikuDatasource> datasources) {
        for (SaikuDatasource ds : datasources) {
            addDatasource(ds);
        }
        return datasources;
    }

    @Override
    public boolean removeDatasource(String datasourceName) {
        return true;
    }

    @Override
    public Map<String,SaikuDatasource> getDatasources() {
        return datasources;
    }

    @Override
    public SaikuDatasource getDatasource(String datasourceName) {
        return datasources.get(datasourceName);
    }
}
