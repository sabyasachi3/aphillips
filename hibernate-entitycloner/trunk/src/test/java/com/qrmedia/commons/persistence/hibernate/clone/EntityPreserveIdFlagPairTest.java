/*
 * @(#)EntityPreserveIdFlagPairTest.java     23 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.qrmedia.commons.persistence.hibernate.clone.EntityPreserveIdFlagPair;

/**
 * Unit tests for the {@link EntityPreserveIdFlagPair}.
 * 
 * @author anph
 * @since 23 Feb 2009
 *
 */
@RunWith(value = Parameterized.class)
public class EntityPreserveIdFlagPairTest {
    private final EntityPreserveIdFlagPair entityPreserveIdFlagPair1;
    private final EntityPreserveIdFlagPair entityPreserveIdFlagPair2;
    
    private boolean expectedEquality;
        
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        data.add(new Object[] { "007", true, "008", true, false });
        data.add(new Object[] { "007", true, "007", false, false });
        
        Object entity = new Object(); 
        data.add(new Object[] { entity, true, entity, false, false });
        
        // equality is based on entity *identity*
        data.add(new Object[] { new Integer(1), true, new Integer(1), true, false });
        data.add(new Object[] { entity, false, entity, false, true });
        return data;
    }

    // called for each parameter set in the test data
    public EntityPreserveIdFlagPairTest(Object entity1, boolean preserveId1,
            Object entity2, boolean preserveId2, boolean expectedEquality) {
        entityPreserveIdFlagPair1 = new EntityPreserveIdFlagPair(entity1, preserveId1);
        entityPreserveIdFlagPair2 = new EntityPreserveIdFlagPair(entity2, preserveId2);
        this.expectedEquality = expectedEquality;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_nullEntity() {
        new EntityPreserveIdFlagPair(null, true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void construct_nullPreserveId() {
        new EntityPreserveIdFlagPair(new Object(), null);
    }    
    
    @Test
    public void equals() {
        assertEquals(expectedEquality, entityPreserveIdFlagPair1.equals(entityPreserveIdFlagPair2));
    }
    
    @Test
    public void hashCodeTest() {
        assertEquals(expectedEquality, 
                (entityPreserveIdFlagPair1.hashCode() == entityPreserveIdFlagPair2.hashCode()));
    }    
    
}
