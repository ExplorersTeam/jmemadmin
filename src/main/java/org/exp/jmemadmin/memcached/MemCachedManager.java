package org.exp.jmemadmin.memcached;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemCachedManager {
    private static final AtomicInteger ID = new AtomicInteger(-1);
    private static MemCachedClient activeClient = null;
    private static Map<String, MemCachedClient> historyClients = new ConcurrentHashMap<>();

    public static boolean start() {
        // start.sh
        if (null == activeClient) {
            initActiveMem();
        } else {
            historyClients.put("mem" + ID.get(), activeClient);
            initActiveMem();
        }
        return true;
    }

    private static void initActiveMem() {
        String activeName = "mem" + ID.incrementAndGet();
        SockIOPool activePool = SockIOPool.getInstance(activeName);
        // set
        activePool.initialize();
        activeClient = new MemCachedClient(activeName);
    }

    public static Object get(String key) {
        return activeClient.get(key);
    }

    static void historyMonitor() {
        historyClients.forEach(new BiConsumer<String, MemCachedClient>() {
            @Override
            public void accept(String t, MemCachedClient u) {
                // check
                // SockIOPool.getInstance().shutDown();
            }
        });
    }

}
