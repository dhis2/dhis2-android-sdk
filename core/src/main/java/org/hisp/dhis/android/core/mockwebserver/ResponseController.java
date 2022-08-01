/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
