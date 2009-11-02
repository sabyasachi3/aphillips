/*
 * @(#)StubItest.java     8 Apr 2009
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
package com.qrmedia.commons.test.example;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qrmedia.commons.test.AbstractTransactionalDatasourceItest;

/**
 * A stub integration test (mainly for observation of output).
 * 
 * @author anphilli
 * @since 8 Apr 2009
 *
 */
@ContextConfiguration(locations = { "classpath:dataSourceContext.xml" })
public class StubItest extends AbstractTransactionalDatasourceItest {
    
    @Test
    public void saveHolder_new() {
        UserTypePropertyHolder holder = new UserTypePropertyHolder();
        holder.getUserTypedProperty().add("James");
        session.save(holder);

        holder.getUserTypedProperty().add("Bond");
        session.flush();
    }
    
    @Test
    public void saveHolder_loaded() {
        UserTypePropertyHolder holder = new UserTypePropertyHolder();
        holder.getUserTypedProperty().add("James");
        session.save(holder);
        session.evict(holder);
        
        UserTypePropertyHolder persistedHolder = 
            loadEntity(UserTypePropertyHolder.class, holder.getId());
        persistedHolder.getUserTypedProperty().add("Bond");
        session.flush();
    }    
    
}
