/*
 * @(#)AbstractBusinessObject.java     12 Jun 2009
 */
package com.qrmedia.commons.bean.businessobject;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;

/**
 * A convenient abstract base class for business objects.
 * 
 * @author aphillips
 * @since 12 Jun 2009
 *
 */
@BusinessObject
public abstract class AbstractBusinessObject {
 
    @Override
    public boolean equals(Object obj) {
        return BusinessObjectUtils.equals(this, obj);
    }
 
    @Override
    public int hashCode() {
        return BusinessObjectUtils.hashCode(this);
    }
 
    @Override
    public String toString() {
        return BusinessObjectUtils.toString(this);
    }
 
}

