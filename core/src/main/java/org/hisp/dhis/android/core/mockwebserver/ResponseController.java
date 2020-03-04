package org.hisp.dhis.android.core.mockwebserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ResponseController {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public static final String API_ME_PATH = "/api/me?.*";
    public static final String API_SYSTEM_INFO_PATH = "/api/system/info?.*";

    private Map<String, LinkedHashMap<String, String>> methodsMap;
    private Map<String, Integer> codeResponses;

    ResponseController(){
        initMaps();
    }

    private void initMaps() {
        codeResponses = new HashMap<>();
        methodsMap = new HashMap<>();
        methodsMap.put(GET, new LinkedHashMap<>());
        methodsMap.put(POST, new LinkedHashMap<>());
        methodsMap.put(PUT, new LinkedHashMap<>());
        methodsMap.put(DELETE, new LinkedHashMap<>());
    }

    void populateInternalResponses(){
        //move sdk dispatcher here
    }

    void addResponse(String method, String path, String responseName, Integer responseCode) {
        LinkedHashMap<String, String> resourcesMap = methodsMap.get(method);
        resourcesMap.put(path, responseName);
        codeResponses.put(responseName, responseCode);
    }

    String getBody(String method, String currentPath){
        Map<String, String> resourcesMap = methodsMap.get(method);
        String filename = EMPTY;

        List<String> paths = new ArrayList<>(resourcesMap.keySet());
        Collections.reverse(paths);
        for (String path : paths) {
            filename = findResponse(resourcesMap, path, currentPath);
            if (!filename.isEmpty()){
                break;
            }
        }
        return filename;
    }

    private String findResponse(Map<String, String> resourcesMap, String path, String currentPath) {
        String filename = EMPTY;
        Pattern pattern = Pattern.compile(path);
        Matcher matcher = pattern.matcher(currentPath);

        if (matcher.matches()){
            filename = resourcesMap.get(path);
        }
        return filename;
    }

    int getCode(String resource){
        if (resource == null || resource.isEmpty()) {
            throw new RuntimeException("Resource not not found");
        }
        return codeResponses.get(resource);
    }
}
