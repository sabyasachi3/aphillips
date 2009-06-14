/*
 * @(#)CollectionFactoryTest.java     8 Apr 2009
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
package com.qrmedia.commons.persistence.hibernate.usertype;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.collections.bag.HashBag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for the {@link CollectionFactory}.
 * 
 * @author aphillips
 * @since 8 Apr 2009
 *
 */
@RunWith(Parameterized.class)
public class CollectionFactoryTest {
    @SuppressWarnings("unchecked")
    private final Class<? extends Collection> collectionClass;
    @SuppressWarnings("unchecked")
    private final Class<? extends Collection> expectedSubtypeClass;
    private final boolean classSupported;
    
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        // unsupported collection types
        data.add(new Object[] { HashBag.class, null, false });
        data.add(new Object[] { ArrayBlockingQueue.class, null, false });
        
        // supported collection types
        data.add(new Object[] { LinkedList.class, List.class, true });
        data.add(new Object[] { HashSet.class, Set.class, true });
        return data;
    }
    
    // called once for each parameter in the test set
    public CollectionFactoryTest(Class<? extends Collection<?>> collectionClass,
            Class<? extends Collection<?>> expectedSubtypeClass, boolean classSupported) {
        this.collectionClass = collectionClass;
        this.expectedSubtypeClass = expectedSubtypeClass;
        this.classSupported = classSupported;
    }
    
    @Test
    public void newInstance_unsupportedClass() {
        
        if (!classSupported) {
            
            try {
                CollectionFactory.newInstance(collectionClass);
                fail();
            } catch (IllegalArgumentException exception) {
                
                // expected
            }
            
        }
        
    }

    @Test
    public void newInstance() {
        
        if (classSupported) {
            assertTrue(expectedSubtypeClass.isAssignableFrom(
                    CollectionFactory.newInstance(collectionClass).getClass()));
        }
        
    }
    
}