/*
 * @(#)ServiceA.java     26 May 2009
 */
package com.xebia.aphillips.jeasytest;

import java.math.BigDecimal;
import java.util.List;

import com.xebia.aphillips.jeasytest.JEasyTestDemoTest.Database;
import com.xebia.aphillips.jeasytest.JEasyTestDemoTest.EntityX;
import com.xebia.aphillips.jeasytest.JEasyTestDemoTest.InvalidItemStatus;
import com.xebia.aphillips.jeasytest.JEasyTestDemoTest.ServiceB;


public final class ServiceA {

    public void doBusinessOperationXyz(EntityX data)
            throws InvalidItemStatus {
        List<?> items = Database.find(
                "select item from EntityY item where item.someProperty=?",
                data.getSomeProperty());

        BigDecimal total = new ServiceB().computeTotal(items);

        data.setTotal(total);
        Database.save(data);
    }

}