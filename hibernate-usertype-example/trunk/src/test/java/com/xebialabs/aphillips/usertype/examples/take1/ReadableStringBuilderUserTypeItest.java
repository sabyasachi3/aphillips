/*
 * @(#)ReadableStringBuilderUserTypeItest.java     Oct 2, 2007
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
package com.xebialabs.aphillips.usertype.examples.take1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qrmedia.commons.test.AbstractTransactionalDatasourceItest;

/**
 * Integration tests for the {@link ReadableStringBuilderUserType}.
 * 
 * @author anphilli
 * @since Nov 2, 2009
 */
@ContextConfiguration(locations = { "classpath:take1/dataSourceContext.xml "})
public class ReadableStringBuilderUserTypeItest extends AbstractTransactionalDatasourceItest {
    private final String propertyValue = "James";
    private Integer id;
    
    @Before
    public void populateDb() {
        EntityWithStringBuilderProperty holder = new EntityWithStringBuilderProperty();
        holder.getBuilder().append(propertyValue);
        session.save(holder);
        session.flush();
        
        id = holder.getId();
        session.evict(holder);
    }
    
    @Test
    public void hydrate() {
        
        // use "get" instead of "load" as otherwise a proxy is returned
        EntityWithStringBuilderProperty holder = 
            getEntity(EntityWithStringBuilderProperty.class, id);
        
        StringBuilder builder = holder.getBuilder();
        assertEquals(propertyValue, builder.toString());
    }

    @Test
    public void dehydrateReplaced() {
        EntityWithStringBuilderProperty holder = loadEntity(EntityWithStringBuilderProperty.class, id);
        StringBuilder newBuilder = new StringBuilder("James Bond");
        
        holder.setBuilder(newBuilder);
        session.flush();
        session.evict(holder);
        
        StringBuilder persistedBuilder = 
            ((EntityWithStringBuilderProperty) loadEntity(EntityWithStringBuilderProperty.class, id))
            .getBuilder();
        assertNotSame(newBuilder, persistedBuilder);
        assertEquals(newBuilder.toString(), persistedBuilder.toString());
    }
    
    @Test
    public void dehydrateNull() {
        EntityWithStringBuilderProperty holder = loadEntity(EntityWithStringBuilderProperty.class, id);
        
        holder.setBuilder(null);
        session.flush();
        session.evict(holder);
        
        assertNull(((EntityWithStringBuilderProperty) loadEntity(EntityWithStringBuilderProperty.class, id))
                   .getBuilder());
    }    

    @Test
    public void dehydrateModified() {
        EntityWithStringBuilderProperty holder = loadEntity(EntityWithStringBuilderProperty.class, id);
        StringBuilder builder = holder.getBuilder();
        
        String addition = " Bond";
        builder.append(addition);
        session.flush();
        session.evict(holder);
        
        StringBuilder persistedBuilder = 
            ((EntityWithStringBuilderProperty) loadEntity(EntityWithStringBuilderProperty.class, id))
            .getBuilder();
        assertNotSame(builder, persistedBuilder);
        assertEquals(builder.toString(), persistedBuilder.toString());
    }
    
}
