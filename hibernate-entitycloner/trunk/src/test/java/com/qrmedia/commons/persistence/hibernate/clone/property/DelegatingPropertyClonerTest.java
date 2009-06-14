/*
 * @(#)DelegatingPropertyClonerTest.java     9 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.property;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.Capture;
import org.junit.Test;

import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.StubHibernateEntity;
import com.qrmedia.commons.persistence.hibernate.clone.property.DelegatingPropertyCloner;
import com.qrmedia.commons.persistence.hibernate.clone.property.DelegatingPropertyCloner.SetPropertyCommand;

/**
 * Unit tests for the {@link DelegatingPropertyCloner}.
 * 
 * @author anph
 * @since 9 Feb 2009
 *
 */
public class DelegatingPropertyClonerTest {
    private DelegatingPropertyCloner propertyCloner = new DelegatingPropertyCloner();

    private HibernateEntityGraphCloner entityGraphCloner =
        createMock(HibernateEntityGraphCloner.class);
    
    @Test
    public void cloneTest() {
        StubHibernateEntity value = new StubHibernateEntity();
        
        // the property needs to be cloned and then wired up
        entityGraphCloner.addEntity(value);
        expectLastCall();

        Capture<SetPropertyCommand> setBeanPropertyCommandCapture = 
            new Capture<SetPropertyCommand>();
        entityGraphCloner.addGraphWiringCommand(capture(setBeanPropertyCommandCapture));
        expectLastCall();
        replay(entityGraphCloner);
        
        StubHibernateEntity target = new StubHibernateEntity();
        String propertyName = "nonSimpleBeanProperty";
        assertTrue(propertyCloner.cloneValue(null, target, propertyName, value, 
                                        entityGraphCloner));
        
        verify(entityGraphCloner);
        
        // check that the correct graph wiring commands were created
        assertEquals(new SetPropertyCommand(target, propertyName, value),
                     setBeanPropertyCommandCapture.getValue());        
    }    
    
}