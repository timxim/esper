/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.regressionlib.support.multithread;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.regressionlib.framework.RegressionEnvironment;
import com.espertech.esper.common.internal.support.SupportBean;
import com.espertech.esper.regressionlib.support.bean.SupportMarketDataBean;
import com.espertech.esper.regressionlib.support.util.SupportMTUpdateListener;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class StmtListenerRouteCallable implements Callable {
    private final int numThread;
    private final RegressionEnvironment env;
    private final EPStatement statement;
    private final int numRepeats;

    public StmtListenerRouteCallable(int numThread, RegressionEnvironment env, EPStatement statement, int numRepeats) {
        this.numThread = numThread;
        this.env = env;
        this.numRepeats = numRepeats;
        this.statement = statement;
    }

    public Object call() throws Exception {
        try {
            for (int loop = 0; loop < numRepeats; loop++) {
                StmtListenerRouteCallable.MyUpdateListener listener = new StmtListenerRouteCallable.MyUpdateListener(env, numThread);
                statement.addListener(listener);
                env.sendEventBean(new SupportBean(), "SupportBean");
                statement.removeListener(listener);
                listener.assertCalled();
            }
        } catch (AssertionFailedError ex) {
            log.error("Assertion error in thread " + Thread.currentThread().getId(), ex);
            return false;
        } catch (Exception ex) {
            log.error("Error in thread " + Thread.currentThread().getId(), ex);
            return false;
        }
        return true;
    }

    private class MyUpdateListener implements UpdateListener {
        private final RegressionEnvironment env;
        private final int numThread;
        private boolean isCalled;
        private EPCompiled compiled;

        public MyUpdateListener(RegressionEnvironment env, int numThread) {
            this.env = env;
            this.numThread = numThread;
            compiled = env.compile("@name('t" + numThread + "') select * from SupportMarketDataBean where volume=" + numThread);
        }

        public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime) {
            isCalled = true;

            // create statement for thread - this can be called multiple times as other threads send SupportBean
            env.deploy(compiled);
            SupportMTUpdateListener listener = new SupportMTUpdateListener();
            env.statement("t" + numThread).addListener(listener);

            Object theEvent = new SupportMarketDataBean("", 0, (long) numThread, null);
            env.sendEventBean(theEvent, theEvent.getClass().getSimpleName());
            env.undeployModuleContaining("t" + numThread);

            EventBean[] eventsReceived = listener.getNewDataListFlattened();

            boolean found = false;
            for (int i = 0; i < eventsReceived.length; i++) {
                if (eventsReceived[i].getUnderlying() == theEvent) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }

        public void assertCalled() {
            Assert.assertTrue(isCalled);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(StmtListenerRouteCallable.class);
}
