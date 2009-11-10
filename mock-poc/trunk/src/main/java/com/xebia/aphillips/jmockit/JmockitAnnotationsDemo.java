/*
 * @(#)JmockitAnnotationsDemo.java     26 May 2009
 */
package com.xebia.aphillips.jmockit;

import static mockit.Mockit.setUpMock;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import mockit.Mock;
import mockit.MockClass;
import mockit.integration.junit4.JMockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xebia.aphillips.jmockit.JmockitExpectationsDemo.Database;
import com.xebia.aphillips.jmockit.JmockitExpectationsDemo.EntityX;
import com.xebia.aphillips.jmockit.JmockitExpectationsDemo.InvalidItemStatus;
import com.xebia.aphillips.jmockit.JmockitExpectationsDemo.ServiceA;
import com.xebia.aphillips.jmockit.JmockitExpectationsDemo.ServiceB;

/**
 * Jmockit demo using annotations. Needs to be run using the Jmockit
 * Java agent, by adding e.g. the following to your command line:
 * <p>
 * <code>-javaagent:<em>M2_REPO</em>/mockit/jmockit/0.97/jmockit-0.97.jar=coverage=::target/coverage-report</code>
 * <p>
 * See the <a href="https://jmockit.dev.java.net/tutorial/CodeCoverage.html">
 * Jmockit documentation</a> for details of coverage arguments.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
@SuppressWarnings("unused")
@RunWith(JMockit.class)
public class JmockitAnnotationsDemo {

    @MockClass(realClass = Database.class)
    public static class MockDatabase {
        @Mock(invocations = 1)
        public static List<?> find(String query, Object arg) {
            assertNotNull(query);
            assertNotNull(arg);
            return Collections.EMPTY_LIST;
        }

        @Mock(maxInvocations = 1)
        public static void save(Object o) {
            assertNotNull(o);
        }
    }

    @Before
    public void setUp() {
        setUpMocks(MockDatabase.class);
    }

    @Test
    public void doBusinessOperationXyz() throws Exception {
        final BigDecimal total = new BigDecimal("125.40");

        setUpMock(ServiceB.class, new Object() {
            @Mock(invocations = 1)
            public BigDecimal computeTotal(List<?> items) {
                assertNotNull(items);
                return total;
            }
        });

        EntityX data = new EntityX();
        new ServiceA().doBusinessOperationXyz(data);

        assertEquals(total, data.getTotal());
    }

    @Test(expected = InvalidItemStatus.class)
    public void doBusinessOperationXyzWithInvalidItemStatus() throws Exception {
        setUpMock(ServiceB.class, new Object() {
            @Mock
            public BigDecimal computeTotal(List<?> items) throws InvalidItemStatus {
                throw new InvalidItemStatus();
            }
        });

        EntityX data = new EntityX();
        new ServiceA().doBusinessOperationXyz(data);
    }

    @After
    public void tearDown() {
        tearDownMocks();
    }
    
}
