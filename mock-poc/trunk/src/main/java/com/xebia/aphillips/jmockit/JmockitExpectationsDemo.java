/*
 * @(#)JmockitDemo.java     26 May 2009
 */
package com.xebia.aphillips.jmockit;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.MockField;

import org.junit.Test;

/**
 * Jmockit demo using expectations.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
@SuppressWarnings("unused")
public class JmockitExpectationsDemo extends Expectations {

    public static class EntityX {
        private BigDecimal total;
        
        public Object getSomeProperty() {
            return "";
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

    public static final class ServiceA {

        public void doBusinessOperationXyz(EntityX data)
                throws InvalidItemStatus {
            List<?> items = 
                Database.find("select item from EntityY item where item.someProperty=?", 
                              data.getSomeProperty());

            BigDecimal total = new ServiceB().computeTotal(items);

            data.setTotal(total);
            Database.save(data);
        }
        
    }

    public static final class ServiceB {
        public BigDecimal computeTotal(List<?> items) throws InvalidItemStatus {
            BigDecimal total = new BigDecimal(0);
            // compute total while iterating items, or throws InvalidItemStatus exception
            return total;
        }
    }

    @MockField 
    private final Database unused = null;
    @MockField 
    private ServiceB serviceB;

    @Test
    public void doBusinessOperationXyz() throws Exception {
        EntityX data = new EntityX();
        BigDecimal total = new BigDecimal("125.40");

        List<?> items = expectFindItems();
        new ServiceB().computeTotal(items);
        returns(total);
        Database.save(data);
        endRecording();

        new ServiceA().doBusinessOperationXyz(data);

        assertEquals(total, data.getTotal());
    }

    private List<?> expectFindItems() {
        List<?> items = new ArrayList<Object>();
        Database.find(withSubstring("select"), withAny(""));
        returns(items);
        return items;
    }

    @Test(expected = InvalidItemStatus.class)
    public void doBusinessOperationXyzWithInvalidItemStatus() throws Exception {
        expectFindItems();
        new ServiceB().computeTotal((List<?>) withNotNull());
        throwsException(new InvalidItemStatus());
        endRecording();

        EntityX data = new EntityX();
        new ServiceA().doBusinessOperationXyz(data);
    }
}
