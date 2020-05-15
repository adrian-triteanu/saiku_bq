package com.projecta.monsai.actions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.projecta.monsai.connection.MondrianServerProvider;
import com.projecta.monsai.query.StoredQueryValidationResult;
import com.projecta.monsai.query.StoredQueryValidator;

import mondrian.util.Pair;

/**
 * Controller class for the API used by the DWH
 *
 * @author akuehnel
 */
@Controller
public class ActionsController {

    @Autowired private MondrianServerProvider mondrianServerProvider;
    @Autowired private StoredQueryValidator   storedQueryValidator;
    @Autowired private StatisticsProvider     statisticsProvider;

    /**
     * Flushes the mondrian caches
     */
    @RequestMapping( "/flush-caches" )
    @ResponseBody
    public String flushCaches() {
        return mondrianServerProvider.flushCaches();
    }


    /**
     * Validates all stored saiku queries by executing them
     */
    @RequestMapping( "/check-stored-queries" )
    @ResponseBody
    public Map<String, Object> checkStoredQueries() {

        Pair<Integer, List<StoredQueryValidationResult>> result = storedQueryValidator.validateStoredQueries();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put( "queries", result.left );
        response.put( "failed",  result.right );
        return response;
    }


    @RequestMapping( "/stats" )
    @ResponseBody
    public String stats() throws Exception {
        return statisticsProvider.getStatistics();
    }

}
