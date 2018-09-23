package org.exp.jmemadmin.agent;

import org.exp.jmemadmin.entity.MemInstance;
import org.exp.jmemadmin.entity.Response;

/**
 * Agent service interface.
 *
 * @author ChenJintong
 *
 */
public interface AgentService {
    Response start(MemInstance instance);

    Response stop(MemInstance instance);

}
