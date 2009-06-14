/*
 * @(#)AbstractTransactionalPrepopulatedDatasourceItest.java     12 Jun 2009
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

import java.util.Locale;

import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Owner;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Pet;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Species;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Toy;
import com.qrmedia.commons.test.AbstractTransactionalDatasourceItest;

/**
 * Base integration test class that pre-populates a database.
 * 
 * @author aphillips
 * @since 12 Jun 2009
 *
 */
@ContextConfiguration(locations = { "classpath:example-applicationContext.xml" })
public abstract class AbstractTransactionalPrepopulatedDatasourceItest 
        extends AbstractTransactionalDatasourceItest {
    protected Integer jonArbuckleId;
    protected Integer garfieldId;
    protected Integer odieId;
    protected Integer fluffyBunnyId;
    
    @Before
    public void populateDatabase() {
        Owner jonArbuckle = new Owner("Jon Arbuckle", Locale.ENGLISH);
        Pet garfield = new Pet(Species.FELIS_CATUS, 5, jonArbuckle, "Garfield", "Gaaaarfield!!");
        Pet odie = new Pet(Species.CANIS_LUPUS_FAMILIARIS, 3, jonArbuckle, "Odie", "Odieeee!!");
        
        Toy fluffyBunny = new Toy("Peter Rabbit", 0);
        garfield.getToys().add(fluffyBunny);
        
        persist(jonArbuckle, garfield, odie);
        
        // save the IDs for access by test methods
        jonArbuckleId = jonArbuckle.getId();
        garfieldId = garfield.getId();
        odieId = odie.getId();
        fluffyBunnyId = fluffyBunny.getId();
    }
    
    private void persist(Object... objs) {
        assert (session != null);
        
        for (Object obj : objs) {
            session.save(obj);
        }

        session.flush();
        
        for (Object obj : objs) {
            session.evict(obj);
        }

    }
    
}
