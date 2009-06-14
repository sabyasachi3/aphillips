/*
 * @(#)HibernateEntityGraphClonerWorkflowItest.java     8 Feb 2009
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;

import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Pet;
import com.qrmedia.commons.persistence.hibernate.clone.example.domain.Toy;

/**
 * Workflow integration test(s) for the {@link HibernateEntityGraphCloner}.
 * <p>
 * In contrast to the {@link HibernateEntityGraphClonerItest}, the focus here is on
 * multi-step (load-modify-save) operations.
 * 
 * @author anph
 * @since 8 Feb 2009
 *
 */
public class HibernateEntityGraphClonerWorkflowItest 
        extends AbstractTransactionalPrepopulatedDatasourceItest {
    private HibernateEntityGraphCloner entityGraphCloner;
    
    /**
     * Checks if the entityGraphCloner is able to be re-used, i.e. correctly cleans up
     * its internal state.
     * <p>
     * This does <b>not</b> make it thread-safe, of course!
     */
    @Test
    public void clone_repeated() {
        SimplePropertyEqualStubHibernateEntity entity1Property =
            new SimplePropertyEqualStubHibernateEntity("007");
        NonSimplePropertyEqualStubHibernateEntity entity1 = 
            new NonSimplePropertyEqualStubHibernateEntity(entity1Property);
        
        StubHibernateEntity entity1Clone = entityGraphCloner.clone(entity1);
     
        assertNotSame(entity1, entity1Clone);
        assertEquals(entity1, entity1Clone);
        
        /*
         * Not equal to entity1Property, so any wiring up commands remaining for 
         * entity1Property will fail.
         */
        SimplePropertyEqualStubHibernateEntity entity2Property =
            new SimplePropertyEqualStubHibernateEntity("008");
        NonSimplePropertyEqualStubHibernateEntity entity2 = 
            new NonSimplePropertyEqualStubHibernateEntity(entity2Property);
        
        StubHibernateEntity entity2Clone = entityGraphCloner.clone(entity2);
        
        assertNotSame(entity2, entity2Clone);
        assertEquals(entity2, entity2Clone);
        
        /*
         * Correct cloning of the subproperties is already checked in the
         * HibernateEntityGraphClonerItest; this test is about verifying that there are no 
         * internal state "leaks".
         */
    }
    
    /*Owner jonArbuckle = new Owner("Jon Arbuckle", Locale.ENGLISH);
    Pet garfield = new Pet(Species.FELIS_CATUS, 5, jonArbuckle, "Garfield", "Gaaaarfield!!");
    Pet odie = new Pet(Species.CANIS_LUPUS_FAMILIARIS, 3, jonArbuckle, "Odie", "Odieeee!!");
    
    Toy fluffyBunny = new Toy();
    garfield.getToys().add(fluffyBunny);*/
    /**
     * Uses a clone to update the original object, ensuring that the cloned associated
     * entities are <b>updated<b>, rather than being added as new items.
     * <p>
     * Preserves ID fields during the clone.
     */
    @Test
    public void clone_updateOriginal_preserveId() {
        // use get rather than load to get associated collections
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        Pet garfieldClone = entityGraphCloner.clone(garfield, true);
        session.evict(garfield);
        
        int newAge = 6;
        garfieldClone.setAge(newAge);
        garfieldClone.getToys().clear();

        session.update(garfieldClone);
        flushAndEvict(garfieldClone);
        
        // load the persisted object and compare
        Pet updatedGarfield = loadEntity(Pet.class, garfieldId);
        assertEquals(garfield, updatedGarfield);
        assertEquals(newAge, updatedGarfield.getAge());
        assertTrue("Expected empty collection", updatedGarfield.getToys().isEmpty());
        
        // check that no duplicate object has been created
        assertEquals(1, session.createQuery("from Pet where species = ?")
                        .setParameter(0, garfield.getSpecies()).list().size());
    }
    
    private void flushAndEvict(Object entity) {
        session.flush();
        session.evict(entity);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void clone_updateOriginal_modifiedAssociatedEntities() {
        // use get rather than load to get associated collections
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        Pet garfieldClone = entityGraphCloner.clone(garfield, true);
        session.evict(garfield);
        
        // modify an associated object
        int newNumChewMarks = 7;
        Set<Toy> garfieldCloneToys = garfieldClone.getToys();
        garfieldCloneToys.iterator().next().setNumChewMarks(newNumChewMarks);
        
        String newToyProductName = "Paddington Bear";
        Toy cuddlyBear = new Toy(newToyProductName, 0);
        garfieldCloneToys.add(cuddlyBear);
        
        session.update(garfieldClone);
        flushAndEvict(garfieldClone);

        // load the persisted object and compare
        Pet updatedGarfield = loadEntity(Pet.class, garfieldId);
        assertEquals(garfield, updatedGarfield);
        
        // check that one new associated object was added and an existing one modified
        List<Toy> updatedToys = new ArrayList<Toy>(updatedGarfield.getToys()); 
        Collections.sort(updatedToys, new PropertyComparator("numChewMarks", false, true));
        assertEquals(garfield.getToys().size() + 1, updatedToys.size());
        assertEquals(newToyProductName, updatedToys.get(0).getProductName());
        assertEquals(newNumChewMarks, updatedToys.get(1).getNumChewMarks());
        
        // check that no duplicate object has been created
        assertEquals(1, session.createQuery("from Pet where species = ?")
                        .setParameter(0, garfield.getSpecies()).list().size());
    }    

    @Test
    public void clone_saveAsNew() {
        // use get rather than load to get associated collections
        Pet garfield = (Pet) session.get(Pet.class, garfieldId);
        Pet garfieldClone = entityGraphCloner.clone(garfield);
        session.evict(garfield);

        // we want the same owner, not a copy
        garfieldClone.getOwner().setId(garfield.getOwner().getId());
        
        // modify the business key of the entity
        garfieldClone.getNicknames().add("Fat boy");
        
        // the clone will have its own set of toys
        Set<Toy> garfieldCloneToys = garfieldClone.getToys();
        garfieldCloneToys.clear();
        
        String newToyProductName = "Paddington Bear";
        Toy cuddlyBear = new Toy(newToyProductName, 0);
        garfieldCloneToys.add(cuddlyBear);

        session.save(garfieldClone);
        flushAndEvict(garfieldClone);

        // load the persisted object and compare
        Pet newPet = loadEntity(Pet.class, garfieldClone.getId());
        assertEquals(garfieldClone, newPet);
        assertFalse("Expected different objects", newPet.equals(garfield));
        assertEquals(garfield.getNicknames().size() + 1, newPet.getNicknames().size());
        assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(cuddlyBear), newPet.getToys()));
        
        // check that the original object still exists and hasn't been modified
        Pet originalGarfield = loadEntity(Pet.class, garfieldId);
        assertEquals(garfield.getNicknames(), originalGarfield.getNicknames());
        assertEquals(garfield.getToys(), originalGarfield.getToys());

        // check that a new object has been created
        assertEquals(2, session.createQuery("from Pet where species = ?")
                        .setParameter(0, garfield.getSpecies()).list().size());        
    }
    
    /**
     * @param entityGraphClonerFactory the entityGraphClonerFactory to set
     */
    @Autowired
    public void setEntityGraphClonerFactory(
            HibernateEntityGraphClonerFactory entityGraphClonerFactory) {
        entityGraphCloner = entityGraphClonerFactory.newInstance();
    }

    /**
     * @param entityGraphCloner the entityGraphCloner to set
     */
    @Autowired
    public void setEntityGraphCloner(HibernateEntityGraphCloner entityGraphCloner) {
        this.entityGraphCloner = entityGraphCloner;
    }

}
