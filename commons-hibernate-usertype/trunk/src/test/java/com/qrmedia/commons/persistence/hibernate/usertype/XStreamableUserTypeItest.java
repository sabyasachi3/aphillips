/*
 * @(#)XStreamableUserTypeItest.java     Oct 2, 2007
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qrmedia.commons.test.AbstractTransactionalDatasourceItest;
import com.qrmedia.commons.test.example.UserTypePropertyHolder;

/**
 * Integration tests for the {@link XStreamableUserType}.
 * <p>
 * This is really nothing more than an XStream integration test...
 * 
 * @author rvd
 * @author anph
 * @since Oct 2, 2007
 */
@ContextConfiguration(locations = { "classpath:dataSourceContext.xml "})
public class XStreamableUserTypeItest extends AbstractTransactionalDatasourceItest {
    private final String propertyMember = "James";
    private Integer id;
    
    @Before
    public void populateDb() {
        UserTypePropertyHolder holder = new UserTypePropertyHolder();
        holder.getUserTypedProperty().add(propertyMember);
        session.save(holder);
        session.flush();
        
        id = holder.getId();
        session.evict(holder);
    }
    
    @Test
    public void hydrate() {
        
        // use "get" instead of "load" as otherwise a proxy is returned
        UserTypePropertyHolder holder = (UserTypePropertyHolder) 
                session.get(UserTypePropertyHolder.class, id);
        
        // the typesafe user type should return the *same* runtime type, i.e. an ArrayList
        Collection<String> userTypedProperty = holder.getUserTypedProperty();
        assertEquals(ArrayList.class, userTypedProperty.getClass());
        assertEquals(Arrays.asList(propertyMember), userTypedProperty);
    }

    @Test
    public void dehydrate() {
        UserTypePropertyHolder holder = loadEntity(UserTypePropertyHolder.class, id);
        Collection<String> holderProperty = holder.getUserTypedProperty();
        
        String newPropertyMember = "Bond";
        holderProperty.add(newPropertyMember);
        session.flush();
        session.evict(holder);
        
        Collection<String> persistedHolderProperty = 
            ((UserTypePropertyHolder) loadEntity(UserTypePropertyHolder.class, id)).getUserTypedProperty();
        assertNotSame(holderProperty, persistedHolderProperty);
        assertEquals(holderProperty, persistedHolderProperty);
    }
    
}
