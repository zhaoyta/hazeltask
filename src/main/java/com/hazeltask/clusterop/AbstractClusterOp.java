package com.hazeltask.clusterop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.hazelcast.nio.DataSerializable;
import com.hazeltask.Hazeltask;
import com.hazeltask.HazeltaskInstance;
import com.hazeltask.HazeltaskTopology;
import com.hazeltask.executor.DistributedExecutorService;

/**
 * Provides some utility methods for retrieving the executor service and holding the topology.
 * Basically, common necessities for the subclasses.
 * @author jclawson
 * @param <T> return type
 */
public abstract class AbstractClusterOp<T> implements Callable<T>, DataSerializable {
    private static final long serialVersionUID = 1L;
    private String topologyName;
    
    public AbstractClusterOp(String topology) {
        this.topologyName = topology;
    }

    public String getTopology() {
        return topologyName;
    }

    public void writeData(DataOutput out) throws IOException {
        out.writeUTF(topologyName);
        writChildData(out);
    }
    
    protected DistributedExecutorService getDistributedExecutorService() {
        HazeltaskInstance<?> ht = Hazeltask.getHazeltaskInstanceByTopology(topologyName);
        if(ht != null) {
            return (DistributedExecutorService) ht.getExecutorService();
        }
        throw new IllegalStateException("Hazeltask was null for topology: "+topologyName);
    }
    
    protected HazeltaskTopology getHazeltaskTopology() {
        HazeltaskInstance<?> ht = Hazeltask.getHazeltaskInstanceByTopology(topologyName);
        if(ht != null) {
            return ht.getTopology();
        }
        throw new IllegalStateException("Hazeltask was null for topology: "+topologyName);
    }

    public void readData(DataInput in) throws IOException {
        topologyName = in.readUTF();      
        readChildData(in);
    }  
    
    protected abstract void readChildData(DataInput in) throws IOException;
    protected abstract void writChildData(DataOutput out) throws IOException;
}