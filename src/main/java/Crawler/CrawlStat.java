package Crawler;

import com.sun.tools.corba.se.idl.InterfaceGen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class CrawlStat {
    private int totalProcessedPages;
    private int totalSuccessPages;
    private long totalLinks;
    private int totalFailPages;
    private Map<Integer,Integer> statusMap = new HashMap<>();
    private Map<Integer,Integer> fileSizeMap = new HashMap<>();
    private Map<String,Integer> typeMap = new HashMap<>();
    private Set<String> uniqueURLSet = new HashSet<>();
    private Set<String> uniqueURLinSite = new HashSet<>();

    public int getTotalProcessedPages() {
        return totalProcessedPages;
    }

    public void incProcessedPages() {
        this.totalProcessedPages++;
    }

    public void incSuccessPages() {
        this.totalSuccessPages++;
    }

    public int getTotalSuccessPages() {
        return this.totalSuccessPages;
    }

    public void incFailPages() {
        this.totalFailPages++;
    }

    public int getTotalFailPages() {
        return this.totalFailPages;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public void incStatusCodes(int status) {
        statusMap.put(status, statusMap.getOrDefault(status,0) + 1);
    }

    public Map<Integer,Integer> getStatusMap() {
        return statusMap;
    }
    public void incFileSize(int fileSize) {
        int size = fileSize / 1024;
        if (size < 1) {
            fileSizeMap.put(0,fileSizeMap.getOrDefault(0,0) + 1);
        } else if (size < 10) {
            fileSizeMap.put(1,fileSizeMap.getOrDefault(1,0) + 1);
        } else if (size < 100) {
            fileSizeMap.put(10,fileSizeMap.getOrDefault(10,0) + 1);
        } else if (size < 1024) {
            fileSizeMap.put(100,fileSizeMap.getOrDefault(100,0) + 1);
        } else {
            fileSizeMap.put(1024,fileSizeMap.getOrDefault(1024,0) + 1);
        }
    }
    public Map<Integer,Integer> getFileSizeMap() {
        return fileSizeMap;
    }
    public void incType(String type) {
        typeMap.put(type,typeMap.getOrDefault(type,0) + 1);
    }
    public Map<String,Integer> getTypeMap() {
        return typeMap;
    }
    public void incUniqueURL(String href) {
        uniqueURLSet.add(href);
    }
    public void incUniqueURLinsite(String href) {
        uniqueURLinSite.add(href);
    }
    public Set<String> getUniqueURL() {
        return uniqueURLSet;
    }
    public Set<String> getUniqueURLInSite() {
        return uniqueURLinSite;
    }
}