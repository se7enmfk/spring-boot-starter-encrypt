/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * A generic and pooled Properties for storing system parameters of the
 * whole application
 */
public class PropertiesUtil {
    private static Hashtable<String, PropertiesUtil> PropertiesUtilPool;
    private String _applicationName = null;
    private Properties properties = null;

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    static {
        PropertiesUtilPool = new Hashtable<String, PropertiesUtil>();
    }

    /**
     * Please call getInstance(String applicationName) instead of constructor
     */
    private PropertiesUtil(String applicationName) {
        properties = new Properties();
        _applicationName = applicationName;
        refresh();
    }

    /**
     * To get a pooled to properties file <applicationName>.properties
     *
     * @param applicationName
     * @return PropertiesUtil
     */
    public static PropertiesUtil getInstance(String applicationName) {
        PropertiesUtil cm = null;
        if (!PropertiesUtilPool.containsKey(applicationName)) {
            logger.info("create new instance for " + applicationName);
            cm = new PropertiesUtil(applicationName);
            PropertiesUtilPool.put(applicationName, cm);
        } else {
            cm = (PropertiesUtil) PropertiesUtilPool.get(applicationName);
        }
        return cm;
    }

    /**
     * refresh() should not be called directly especially during production as
     * it will impact performance and is not thread safe. use at your own risk.
     */
    synchronized public void refresh() {
        InputStream in = null;
        String propertiesFileName = "/" + _applicationName + ".properties";
        try {
            in = PropertiesUtil.class.getResourceAsStream(propertiesFileName);
            if (in == null) {
                logger.error(propertiesFileName + " file not found");
            } else {
                properties.load(in);
            }

        } catch (Exception e) {
            logger.error(
                    "Error loading " + propertiesFileName + " : "
                            + e.getMessage(), e);
        } finally {
            try {
                in.close();
            } catch (Exception eIgnored) {
                logger.error("Error closing " + propertiesFileName + " : "
                        + eIgnored.getMessage(), eIgnored);
            }
            in = null;
        }
    }

    /**
     * get a property from current properties file
     *
     * @param key
     * @return value
     */
    public String getProperty(String key) {
        return properties.getProperty(key).trim();
    }

    public int getPropertyAsInt(String key) {
        String temp = this.getProperty(key).trim();
        return Integer.parseInt(temp);
    }

    public Map<String, Object> getPropertiesMap() {
        Map<String, Object> map = new HashMap<>();
        for (String s : properties.stringPropertyNames()) {
            String property = properties.getProperty(s);
            map.put(s, property);
        }
        return map;
    }

}
