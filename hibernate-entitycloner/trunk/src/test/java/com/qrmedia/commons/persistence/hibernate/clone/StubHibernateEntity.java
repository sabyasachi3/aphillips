/*
 * @(#)StubHibernateEntity.java     8 Feb 2009
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

import java.lang.Thread.State;
import java.util.Collection;

import javax.naming.Name;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * A stub Hibernate entity object.
 * 
 * @author anph
 * @since 8 Feb 2009
 *
 */
public class StubHibernateEntity {
    private static String staticProperty;
    
    @Id
    private Long id;
    
    @Version
    private int version;
    
    // no setter
    private String nonBeanProperty;
    protected String simpleBeanProperty;
    private State enumBeanProperty;
    
    // for ease of testing, choose a Cloneable that is mockable
    private Name cloneableBeanProperty;
    protected StubHibernateEntity nonSimpleBeanProperty;
    private Collection<String> simpleCollectionBeanProperty;
    private Collection<StubHibernateEntity> nonSimpleCollectionBeanProperty;
    
    /**
     * Constructs a <code>StubHibernateEntity</code>.
     * 
     * @param id    the entity's ID
     * @param version the entity's version
     * @param nonBeanProperty the value of the non-bean property
     * @param simpleBeanProperty the value of the simple bean property
     * @param enumBeanProperty the value of the enum bean property
     * @param cloneableBeanProperty the value of the clonable bean property
     * @param nonSimpleBeanProperty the value of the non-simple bean property
     * @param simpleCollectionBeanProperty the value of the simple collection bean property
     * @param nonSimpleCollectionBeanProperty the value of the non-simple collection bean property
     */
    public StubHibernateEntity(Long id, int version, String nonBeanProperty,
            String simpleBeanProperty, State enumBeanProperty,
            Name cloneableBeanProperty, StubHibernateEntity nonSimpleBeanProperty,
            Collection<String> simpleCollectionBeanProperty,
            Collection<StubHibernateEntity> nonSimpleCollectionBeanProperty) {
        this.id = id;
        this.version = version;
        this.nonBeanProperty = nonBeanProperty;
        this.simpleBeanProperty = simpleBeanProperty;
        this.enumBeanProperty = enumBeanProperty;
        this.cloneableBeanProperty = cloneableBeanProperty;
        this.nonSimpleBeanProperty = nonSimpleBeanProperty;
        this.simpleCollectionBeanProperty = simpleCollectionBeanProperty;
        this.nonSimpleCollectionBeanProperty = nonSimpleCollectionBeanProperty;
    }
    
    /**
     * Default constructor, for convenience in generating objects that don't need
     * specific property values. 
     */
    public StubHibernateEntity() { }    

    /* Getter(s) and setter(s) */
    
    /**
     * Getter for staticProperty.
     *
     * @return the staticProperty.
     */
    public static String getStaticProperty() {
        return staticProperty;
    }

    /**
     * @param staticProperty the staticProperty to set
     */
    public static void setStaticProperty(String staticProperty) {
        StubHibernateEntity.staticProperty = staticProperty;
    }

    /**
     * Getter for id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for version.
     *
     * @return the version.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /**
     * Getter for nonBeanProperty.
     *
     * @return the nonBeanProperty.
     */
    public String getNonBeanProperty() {
        return nonBeanProperty;
    }    

    /**
     * Getter for simpleBeanProperty.
     *
     * @return the simpleBeanProperty.
     */
    public String getSimpleBeanProperty() {
        return simpleBeanProperty;
    }

    /**
     * @param simpleBeanProperty the simpleBeanProperty to set
     */
    public void setSimpleBeanProperty(String simpleBeanProperty) {
        this.simpleBeanProperty = simpleBeanProperty;
    }

    /**
     * Getter for enumBeanProperty.
     *
     * @return the enumBeanProperty.
     */
    public State getEnumBeanProperty() {
        return enumBeanProperty;
    }

    /**
     * @param enumBeanProperty the enumBeanProperty to set
     */
    public void setEnumBeanProperty(State enumBeanProperty) {
        this.enumBeanProperty = enumBeanProperty;
    }

    /**
     * Getter for cloneableBeanProperty.
     *
     * @return the cloneableBeanProperty.
     */
    public Name getCloneableBeanProperty() {
        return cloneableBeanProperty;
    }

    /**
     * @param cloneableBeanProperty the cloneableBeanProperty to set
     */
    public void setCloneableBeanProperty(Name cloneableBeanProperty) {
        this.cloneableBeanProperty = cloneableBeanProperty;
    }

    /**
     * Getter for nonSimpleBeanProperty.
     *
     * @return the nonSimpleBeanProperty.
     */
    public StubHibernateEntity getNonSimpleBeanProperty() {
        return nonSimpleBeanProperty;
    }

    /**
     * @param nonSimpleBeanProperty the nonSimpleBeanProperty to set
     */
    public void setNonSimpleBeanProperty(StubHibernateEntity nonSimpleBeanProperty) {
        this.nonSimpleBeanProperty = nonSimpleBeanProperty;
    }

    /**
     * Getter for simpleCollectionBeanProperty.
     *
     * @return the simpleCollectionBeanProperty.
     */
    public Collection<String> getSimpleCollectionBeanProperty() {
        return simpleCollectionBeanProperty;
    }

    /**
     * @param simpleCollectionBeanProperty the simpleCollectionBeanProperty to set
     */
    public void setSimpleCollectionBeanProperty(
            Collection<String> simpleCollectionBeanProperty) {
        this.simpleCollectionBeanProperty = simpleCollectionBeanProperty;
    }

    /**
     * Getter for nonSimpleCollectionBeanProperty.
     *
     * @return the nonSimpleCollectionBeanProperty.
     */
    public Collection<StubHibernateEntity> getNonSimpleCollectionBeanProperty() {
        return nonSimpleCollectionBeanProperty;
    }

    /**
     * @param nonSimpleCollectionBeanProperty the nonSimpleCollectionBeanProperty to set
     */
    public void setNonSimpleCollectionBeanProperty(
            Collection<StubHibernateEntity> nonSimpleCollectionBeanProperty) {
        this.nonSimpleCollectionBeanProperty = nonSimpleCollectionBeanProperty;
    }

}
