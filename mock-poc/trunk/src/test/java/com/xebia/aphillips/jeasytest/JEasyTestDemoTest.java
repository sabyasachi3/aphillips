/*
 * @(#)JEasyTestDemo.java     26 May 2009
 */
package com.xebia.aphillips.jeasytest;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jeasytest.external.Util.arg;
import static org.jeasytest.external.Util.on;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.jeasytest.annotations.ClassUnderTest;
import org.jeasytest.annotations.JEasyTest;

/**
 * JEasyTest demo. Runs via Maven using:
 * <p>
 * <code>mvn -Prun-jeasytest test</code>
 * <p>
 * See the <a href="https://jeasytest.dev.java.net/">JEesyTest documentation</a> 
 * for more information.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
@ClassUnderTest(ServiceA.class)
public class JEasyTestDemoTest extends TestCase {
    
    public static class EntityX {
        private BigDecimal total;

        public Object getSomeProperty() {
            return "abc";
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getTotal() {
            return total;
        }

    }

    public static class InvalidItemStatus extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class Database {
        private Database() {}

        public static List<?> find(String query, Object param) {
            return null;
        }

        public static void save(Object entity) {}
    }

    public static class ServiceB {
        public BigDecimal computeTotal(List<?> items) throws InvalidItemStatus {
            BigDecimal total = new BigDecimal(0);
            // compute total while iterating items, or throws InvalidItemStatusexception
            return total;
        }
    }

    private EntityX entity;

    private ServiceA serviceA;
    private ServiceB serviceB;
    private final BigDecimal total = new BigDecimal("125.40");

    @Override
    protected void setUp() throws Exception {
        entity = new EntityX();
        serviceA = new ServiceA();
        serviceB = createMock(ServiceB.class);
    }

    @JEasyTest
    public void testBusinessOperation() throws InvalidItemStatus {
        on(Database.class).expectStaticNonVoidMethod("find").with(
                arg("select item from EntityY item where item.someProperty=?"),
                arg("abc")).andReturn(Collections.EMPTY_LIST);
        on(ServiceB.class).expectEmptyConstructor().andReturn(serviceB);
        on(Database.class).expectStaticVoidMethod("save").with(arg(entity));
        expect(serviceB.computeTotal(Collections.EMPTY_LIST)).andReturn(total);
        replay(serviceB);
        serviceA.doBusinessOperationXyz(entity);
        verify(serviceB);
        assertEquals(total, entity.getTotal());
    }
    
}
