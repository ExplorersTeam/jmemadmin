package org.exp.jmemadmin.agent;

import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.Response;

public interface Agent {
    Response start(MemInstance instance);

    Response stop(MemInstance instance);

}
