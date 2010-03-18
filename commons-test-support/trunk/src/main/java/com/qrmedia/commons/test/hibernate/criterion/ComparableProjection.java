/*
 * @(#)ComparableProjection.java     1 Aug 2008
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.qrmedia.commons.test.hibernate.criterion;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;
import org.hibernate.type.Type;

/**
 * A wrapper around a Hibernate <code>Projection</code> that supports a &quot;natural&quot;
 * (i.e. non-instance) equality. Using these classes,
 * <pre>
 *   <code>new ComparableProjection(Projections.property("foo")).equals(
 *         new ComparableProjection(Projections.property("foo")))</code>
 * </pre>
 * should evaluate to <code>true</code>.
 * <p>
 * Intended to be used for testing.
 * 
 * @author anph
 * @since 1 Aug 2008
 *
 */
public class ComparableProjection implements Projection {
    private static final long serialVersionUID = 1706035339580853034L;
    
    private Projection projection;
    
    /**
     * Creates a &quot;property-level&quot; comparable version of the given <code>Projection</code>.
     *  
     * @param projection the criterion to be made comparable at property level
     */
    public ComparableProjection(Projection projection) {
        this.projection = projection;
    }
    
    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#getAliases()
     */
    public String[] getAliases() {
        return projection.getAliases();
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#getColumnAliases(int)
     */
    public String[] getColumnAliases(int loc) {
        return projection.getColumnAliases(loc);
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#getColumnAliases(java.lang.String, int)
     */
    public String[] getColumnAliases(String alias, int loc) {
        return projection.getColumnAliases(alias, loc);
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#getTypes(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
            throws HibernateException {
        return projection.getTypes(criteria, criteriaQuery);
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#getTypes(java.lang.String, org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    public Type[] getTypes(String alias, Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return projection.getTypes(alias, criteria, criteriaQuery);
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#isGrouped()
     */
    public boolean isGrouped() {
        return projection.isGrouped();
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#toGroupSqlString(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    public String toGroupSqlString(Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return projection.toGroupSqlString(criteria, criteriaQuery);
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Projection#toSqlString(org.hibernate.Criteria, int, org.hibernate.criterion.CriteriaQuery)
     */
    public String toSqlString(Criteria criteria, int position,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return projection.toSqlString(criteria, position, criteriaQuery);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Projection)) {
            return false;
        }

        // actually comparing properties would be a safer way to do this 
        return toString().equals(obj.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        // using actual properties would be a safer way to do this 
        return new HashCodeBuilder().append(toString()).toHashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        // delegate to the wrapped projection since its "toString" is used to compare projections
        return projection.toString();
    }    

}
