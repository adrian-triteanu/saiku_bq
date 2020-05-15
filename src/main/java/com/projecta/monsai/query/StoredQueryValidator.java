package com.projecta.monsai.query;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.saiku.olap.dto.SaikuQuery;
import org.saiku.service.olap.OlapQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.projecta.monsai.connection.Config;

import mondrian.util.Pair;
import mondrian.xmla.XmlaException;

/**
 * Utility class that validates all saiku stored queries
 *
 * @author akuehnel
 */
@Component
public class StoredQueryValidator {

    @Autowired private Config             config;
    @Autowired private ApplicationContext applicationContext;

    private static final String[] SAIKU_QUERY_EXTENSIONS = { "saiku" };
    private static final Logger LOG = Logger.getLogger( StoredQueryValidator.class );


    /**
     * Validates all saiku stored queries
     */
    public Pair<Integer, List<StoredQueryValidationResult>> validateStoredQueries() {

        OlapQueryService olapQueryService = applicationContext.getBean( OlapQueryService.class );

        // check if a storage dir is configured
        String storageDir = config.getProperty( "saikuStorageDir" );
        if ( StringUtils.isEmpty( storageDir ) ) {
            return Pair.of( 0, null );
        }

        // retrieve all stored files
        Collection<File> queryFiles = FileUtils.listFiles( new File( storageDir ), SAIKU_QUERY_EXTENSIONS, true );

        List<StoredQueryValidationResult> failedQueries = new ArrayList<>();
        for ( File queryFile : queryFiles ) {

            SaikuQuery query = null;
            String     mdx   = null;
            try {

                // parse the file to retrieve the id and MDX
                Document doc = new SAXBuilder().build( queryFile );
                String queryId = doc.getRootElement().getAttributeValue( "name" );
                mdx = doc.getRootElement().getChildText( "MDX" );

                // read the xml file and load the saiku query
                String xml = FileUtils.readFileToString( queryFile, "UTF-8" );
                query = olapQueryService.createNewOlapQuery( queryId, xml );

                // try to execute the query
                olapQueryService.execute( query.getName() );
            }
            catch ( Throwable e ) {
                Throwable cause = XmlaException.getRootCause(e);
                LOG.error( "Error validating query " + queryFile.getAbsolutePath() );
                failedQueries.add( new StoredQueryValidationResult( queryFile.getAbsolutePath(), mdx,
                                       cause.getClass().getName() + ": " + cause.getMessage() ) );
            }
            finally {
                if ( query != null ) {
                    try {
                        olapQueryService.closeQuery( query.getName() );
                    }
                    catch ( Throwable e ) {
                    }
                }
            }
        }

        return Pair.of( queryFiles.size(), failedQueries );
    }
}
