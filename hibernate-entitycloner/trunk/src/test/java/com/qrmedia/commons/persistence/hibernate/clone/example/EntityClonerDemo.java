/*
 * @(#)EntityClonerDemo.java     31 Mar 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.example;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qrmedia.commons.persistence.hibernate.clone.AbstractTransactionalPrepopulatedDatasourceItest;
import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphCloner;
import com.qrmedia.commons.persistence.hibernate.clone.HibernateEntityGraphClonerFactory;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Owner;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Pet;
import com.thoughtworks.xstream.XStream;

/**
 * Demonstrate entity cloning.
 * 
 * @author aphillips
 * @since 31 Mar 2009
 *
 */
public class EntityClonerDemo extends AbstractTransactionalPrepopulatedDatasourceItest {
    private final XStream xStream = new XStream();
    
    private HibernateEntityGraphCloner entityGraphCloner;
    
    @Test
    //@Ignore
    public void copy_naive() throws Exception {
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        Pet olderGarfield = new Pet(garfield.getSpecies(), garfield.getAge() + 1,
                garfield.getOwner(), garfield.getNicknames().toArray(new String[0]));
        olderGarfield.setToys(garfield.getToys());
        session.evict(garfield);
        
        Serializable olderGarfieldId = session.save(olderGarfield);
        session.flush();
        session.evict(olderGarfield);
        
        System.out.println(loadEntity(Pet.class, garfieldId).getToys());
        System.out.println(loadEntity(Pet.class, olderGarfieldId).getToys());
    }
    
    @Test
    @Ignore
    public void xStreamSerialize() {
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        System.out.println(xStream.toXML(garfield));
        
        Owner jon = (Owner) session.createCriteria(Owner.class).uniqueResult();
        Hibernate.initialize(jon.getPets());
        System.out.println(xStream.toXML(jon));
    }

    @Test
    @Ignore
    public void copy_clone() throws Exception {
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        Pet olderGarfield = entityGraphCloner.clone(garfield, true);
        session.evict(garfield);
        
        olderGarfield.setAge(garfield.getAge() + 1);
        
        // we want a *copy* of the entity
        olderGarfield.setId(null);
        Serializable olderGarfieldId = session.save(olderGarfield);
        session.flush();
        session.evict(olderGarfield);

        System.out.println(garfieldId == olderGarfieldId);
        Pet persistedGarfield = loadEntity(Pet.class, garfieldId);
        System.out.println(persistedGarfield.getToys());
        
        Pet persistedOlderGarfield = loadEntity(Pet.class, olderGarfieldId);
        System.out.println(persistedOlderGarfield.getToys());
        System.out.println(persistedGarfield.getOwner() 
                           == persistedOlderGarfield.getOwner());
    }
    
    @Test
    @Ignore
    public void xStreamSerialize_clone() {
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        System.out.println(xStream.toXML(entityGraphCloner.clone(garfield)));
        
        Owner jon = (Owner) session.get(Owner.class, jonArbuckleId);
        Hibernate.initialize(jon.getPets());
        System.out.println(xStream.toXML(entityGraphCloner.clone(jon, true)));
    }    

    @Autowired
    public void setHibernateEntityGraphClonerFactory(
            HibernateEntityGraphClonerFactory hibernateEntityGraphClonerFactory) {
        entityGraphCloner = hibernateEntityGraphClonerFactory.newInstance();
    }

}
