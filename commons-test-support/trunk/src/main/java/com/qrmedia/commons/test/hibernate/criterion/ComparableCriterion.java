/*
 * @(#)ComparableCriterion.java     5 May 2008
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
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

/**
 * A wrapper around a Hibernate <code>Criterion</code> that supports a &quot;natural&quot;
 * (i.e. non-instance) equality. Using these classes,
 * <pre>
 *   <code>new ComparableCriterion(Restrictions.eq("foo", 1)).equals(
 *         new ComparableCriterion(Restrictions.eq("foo", 1)))</code>
 * </pre>
 * should evaluate to <code>true</code>.
 * <p>
 * Intended to be used for testing.
 * 
 * @author anph
 * @since 5 May 2008
 */
public class ComparableCriterion implements Criterion {
    private static final long serialVersionUID = -8891417168151593239L;
    
    private Criterion criterion;

    /**
     * Creates a &quot;property-level&quot; comparable version of the given <code>Criterion</code>.
     *  
     * @param criterion the criterion to be made comparable at property level
     */
    public ComparableCriterion(Criterion criterion) {
        this.criterion = criterion;
    }
    
    /* (non-Javadoc)
     * @see org.hibernate.criterion.Criterion#getTypedValues(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return criterion.getTypedValues(criteria, criteriaQuery);
    }


    /* (non-Javadoc)
     * @see org.hibernate.criterion.Criterion#toSqlString(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return criterion.toSqlString(criteria, criteriaQuery);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Criterion)) {
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
        
        // delegate to the wrapped criterion since its "toString" is used to compare criteria
        return criterion.toString();
    }

}
