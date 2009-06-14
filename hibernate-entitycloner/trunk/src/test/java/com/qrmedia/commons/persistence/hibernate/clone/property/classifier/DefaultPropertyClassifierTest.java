/*
 * @(#)PropertyClassifierTest.java     10 Feb 2009
 *
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.commons.persistence.hibernate.clone.property.classifier;

import static org.junit.Assert.assertEquals;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;

/**
 * Unit tests for the {@link DefaultPropertyClassifier}.
 * 
 * @author anph
 * @since 10 Feb 2009
 *
 */
@RunWith(value = Parameterized.class)
public class DefaultPropertyClassifierTest {
    private final DefaultPropertyClassifier propertyClassifier = 
        new DefaultPropertyClassifier();
    private final Class<?> propertyClass;
    private final boolean simpleClass;
    
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        // some simple types: String, primitives, wrappers, Class, enums
        data.add(new Object[] { Integer.TYPE, true });
        data.add(new Object[] { Long.class, true });
        data.add(new Object[] { String.class, true });
        data.add(new Object[] { Class.class, true });
        data.add(new Object[] { State.class, true });
        
        // some non-simple types
        data.add(new Object[] { StubHibernateEntity.class, false });
        data.add(new Object[] { Calendar.class, false });
        data.add(new Object[] { Collection.class, false });
        
        return data;
    }

    // called for each parameter set in the test data
    public DefaultPropertyClassifierTest(Class<?> propertyClass, boolean simpleClass) {
        this.propertyClass = propertyClass;
        this.simpleClass = simpleClass;
    }

    // will be repeated many times, but that can't be helped
    @Test(expected = IllegalArgumentException.class)
    public void isSimpleProperty_null() {
        propertyClassifier.isSimpleProperty(null);
    }
    
    @Test
    public void isSimpleProperty() {
        assertEquals(simpleClass, propertyClassifier.isSimpleProperty(propertyClass));
    }
    
}
