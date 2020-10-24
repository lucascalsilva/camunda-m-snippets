package com.example.workflow.util;

import lombok.experimental.UtilityClass;
import org.camunda.bpm.engine.query.Query;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class CustomControllerHelper {

    public MultivaluedMap<String, String> convertMap(Map<String, String> map){
        MultivaluedMap<String, String> multiValueMap = new MultivaluedHashMap<String, String>();

        map.forEach(multiValueMap::add);

        return multiValueMap;
    }

    public List runQuery(Query query, Integer firstResult, Integer maxResults){
        List result;

        if (firstResult != null || maxResults != null) {
            result = executePaginatedQuery(query, firstResult, maxResults);
        } else {
            result = query.list();
        }

        return result;
    }

    private List executePaginatedQuery(Query query, Integer firstResult, Integer maxResults) {
        if (firstResult == null) {
            firstResult = 0;
        }
        if (maxResults == null) {
            maxResults = Integer.MAX_VALUE;
        }
        return query.listPage(firstResult, maxResults);
    }
}
