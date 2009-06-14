/*
 * @(#)ComparableOrder.java     5 May 2008
 *
 * Copyright (c) 2006 TomTom International B.V. All rights reserved.
 * TomTom PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.qrmedia.commons.test.hibernate.criterion;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

/**
 * A wrapper around a Hibernate <code>Order</code> that supports a &quot;natural&quot;
 * (i.e. non-instance) equality. Using these classes,
 * <pre>
 *   <code>new ComparableOrder(Order.asc("foo")).equals(
 *       new ComparableOrder(Order.asc("foo")))</code>
 * </pre>
 * should evaluate to <code>true</code>.
 * <p>
 * Intended to be used for testing.
 * 
 * @author anph
 * @since 5 May 2008
 *
 */
public class ComparableOrder extends Order {
    private static final long serialVersionUID = -4182764555712170067L;

    private Order order;
    
    /**
     * Creates a &quot;property-level&quot; comparable version of the given <code>Order</code>.
     *  
     * @param order the order to be made comparable at property level
     */
    public ComparableOrder(Order order) {
        /*
         * The properties of the wrapped order are not accessible, so default values are used.
         * This mangles the "propertyName" and "ascending" properties of this instance, but 
         * that is not a big problem since the "toSqlString()" method, which is what this 
         * instance will be used to generate, delagates to the wrapped order.
         * In any case, this class is mainly intended for testing.
         */
        super(null, true);
        
        this.order = order;
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Order#ignoreCase()
     */
    @Override
    public Order ignoreCase() {
        return order.ignoreCase();
    }

    /* (non-Javadoc)
     * @see org.hibernate.criterion.Order#toSqlString(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
     */
    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return order.toSqlString(criteria, criteriaQuery);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Order)) {
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
     * @see org.hibernate.criterion.Order#toString()
     */
    @Override
    public String toString() {
        return order.toString();
    }
    
}
