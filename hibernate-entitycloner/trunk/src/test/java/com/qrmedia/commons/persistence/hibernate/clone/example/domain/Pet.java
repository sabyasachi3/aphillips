/*
 * @(#)Pet.java     31 Mar 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.example.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;

/**
 * A pet.
 * 
 * @author aphillips
 * @since 31 Mar 2009
 *
 */
@Entity
public class Pet extends AbstractEntity {
    
    @BusinessField
    @Column
    @Enumerated(EnumType.STRING)
    private Species species;
    
    @Column
    private int age;
    
    @ManyToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.EVICT })
    private Owner owner;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @Cascade(value = { org.hibernate.annotations.CascadeType.EVICT })
    private Set<Toy> toys = new HashSet<Toy>();
    
    @BusinessField
    @CollectionOfElements
    private Set<String> nicknames = new HashSet<String>();

    public Pet(Species species, int age, Owner owner, String... nicknames) {
        this.species = species;
        this.age = age;
        setOwner(owner);
        this.nicknames.addAll(Arrays.asList(nicknames));
    }
    
    public Pet() {}

    /* Getters and setters */
    
    /**
     * @return the species
     */
    public Species getSpecies() {
        return species;
    }

    /**
     * @param species the species to set
     */
    public void setSpecies(Species species) {
        this.species = species;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the owner
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
        owner.getPets().add(this);
    }

    /**
     * @return the toys
     */
    public Set<Toy> getToys() {
        return toys;
    }

    /**
     * @param toys the toys to set
     */
    public void setToys(Set<Toy> toys) {
        this.toys = toys;
    }

    /**
     * @return the nicknames
     */
    public Set<String> getNicknames() {
        return nicknames;
    }

    /**
     * @param nicknames the nicknames to set
     */
    public void setNicknames(Set<String> nicknames) {
        this.nicknames = nicknames;
    }
    
    
}
