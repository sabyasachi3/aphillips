/*
 * @(#)AbstractTransactionalDatasourceItest.java     8 Apr 2009
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
package com.qrmedia.commons.test;

import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * A integration test base class. Children should declare required contexts via a suitable
 * {@link ContextConfiguration @ContextConfiguration} annotation.
 * 
 * @author aphillips
 * @since 8 Apr 2009
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractTransactionalDatasourceItest {
    private SessionFactory sessionFactory;
    protected Session session;
    
    @Before
    public void prepareFixture() {
        session = sessionFactory.getCurrentSession();
    }
    
    @Test
    public void contextLoad() {
        assertNotNull(session);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T loadEntity(Class<T> entityClass, Serializable id) {
        return (T) session.load(entityClass, id);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T getEntity(Class<T> entityClass, Serializable id) {
        return (T) session.get(entityClass, id);
    }    

    /**
     * @param sessionFactory the sessionFactory to set
     */
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
