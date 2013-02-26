package com.hazeltask.core.concurrent.collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hazeltask.core.concurrent.collections.grouped.GroupedPriorityQueue;
import com.hazeltask.core.concurrent.collections.grouped.GroupedQueueRouter;
import com.hazeltask.core.concurrent.collections.router.ListRouterFactory;
import com.hazeltask.core.concurrent.collections.router.RoundRobinRouter;
import com.hazeltask.core.concurrent.collections.tracked.ITrackedQueue;
import com.hazeltask.core.concurrent.collections.tracked.TrackedPriorityBlockingQueue.TimeCreatedAdapter;
import com.hazeltask.executor.task.HazeltaskTask;

public class HazelcastWorkGroupedQueueTest {
    
    private GroupedPriorityQueue<HazeltaskTask<String,String>, String> taskQueue;
    
    @Before
    public void setupData() {
        ListRouterFactory<Entry<String, ITrackedQueue<HazeltaskTask<String,String>>>> routerFactory = RoundRobinRouter.newFactory();
        taskQueue = new GroupedPriorityQueue<HazeltaskTask<String,String>, String>(new GroupedQueueRouter.GroupRouterAdapter<HazeltaskTask<String,String>, String>(routerFactory),
                new TimeCreatedAdapter<HazeltaskTask<String,String>>(){
            public long getTimeCreated(HazeltaskTask<String,String> item) {
                return item.getTimeCreated();
            }            
        });
        
        HazeltaskTask<String,String> work1 = mock(HazeltaskTask.class);
        HazeltaskTask<String,String> work2 = mock(HazeltaskTask.class);
        HazeltaskTask<String,String> work3 = mock(HazeltaskTask.class);
        HazeltaskTask<String,String> work4 = mock(HazeltaskTask.class);
        
        when(work1.getGroup()).thenReturn("1");
        when(work2.getGroup()).thenReturn("1");
        when(work3.getGroup()).thenReturn("1");
        when(work4.getGroup()).thenReturn("1");
        
        when(work1.getTimeCreated()).thenReturn(1L);
        when(work2.getTimeCreated()).thenReturn(2L);
        when(work3.getTimeCreated()).thenReturn(3L);
        when(work4.getTimeCreated()).thenReturn(4L);
        
        taskQueue.add(work3);
        taskQueue.add(work1);
        taskQueue.add(work4);
        taskQueue.add(work2);
        
    }
    
    
    @Test
    public void testOldestWorkCreatedTime() {
        Assert.assertEquals(1L, (long)taskQueue.getOldestQueueTime());
    }
    
    @Test
    public void testOldestWorkCreatedTimePop() {
        Assert.assertEquals(1L, (long)taskQueue.getOldestQueueTime());
        taskQueue.poll();
        Assert.assertEquals(2L, (long)taskQueue.getOldestQueueTime());
        taskQueue.poll();
        Assert.assertEquals(3L, (long)taskQueue.getOldestQueueTime());
        taskQueue.poll();
        Assert.assertEquals(4L, (long)taskQueue.getOldestQueueTime());
        taskQueue.poll();
        Assert.assertNull(taskQueue.getOldestQueueTime());
    }
}