package org.exp.jmemadmin.utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.exp.jmemadmin.entity.KeysBean;
import org.exp.jmemadmin.instance.MemCachedAdmin;
import org.exp.jmemadmin.instance.MemCachedManager;

/**
 * 
 * Operations to MemCached instances.
 *
 */
public class MCUtils {
    private MCUtils() {
        // Do nothing.
    }
    
    public static void start(String host, int port, int memorySize, boolean isMaster) throws Exception {
    	MemCachedManager.start(host, port, memorySize, isMaster);
    }

    public static void stop(String host, int port) throws Exception {
    	MemCachedManager.stop(host, port);
    }
    
    public static boolean keyExists(String key) {
    	return MemCachedAdmin.keyExists(key);
    }
    
    public static Object get(String key) {
    	return MemCachedAdmin.get(key);
    }
    public static boolean set(String key, Object value) {
    	return MemCachedAdmin.set(key, value);
    }

    public static boolean delete(String key) {
    	return MemCachedAdmin.delete(key);
    }
    
    public static Map<String, KeysBean> list() throws NumberFormatException, UnsupportedEncodingException {
    	return MemCachedAdmin.listKeys();
    }
    public static Map<String, Map<String, String>> stats(){
    	return MemCachedAdmin.stats();
    }
    
    public static Map<String, Map<String, String>> stats(String[] servers){
    	return MemCachedAdmin.stats(servers);
    }
    
    public static boolean add(String key, Object value) {
    	return MemCachedAdmin.add(key, value);
    }
    
    public static boolean flushAll() {
    	return MemCachedAdmin.flushAll();
    }

    public static boolean flushAll(String[] servers) {
    	return MemCachedAdmin.flushAll(servers);
    }
}
