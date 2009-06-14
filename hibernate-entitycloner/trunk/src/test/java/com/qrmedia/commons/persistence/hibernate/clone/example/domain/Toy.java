/*
 * @(#)Toy.java     31 Mar 2009
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

import javax.persistence.Column;
import javax.persistence.Entity;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;

/**
 * A toy belonging to a pet.
 * 
 * @author aphillips
 * @since 31 Mar 2009
 *
 */
@Entity
public class Toy extends AbstractEntity {
    @BusinessField
    @Column(unique = true)
    private String productName;
    
    @Column
    private int numChewMarks;

    public Toy(String productName, int numChewMarks) {
        this.productName = productName;
        this.numChewMarks = numChewMarks;
    }
    
    public Toy() {}
    
    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the numChewMarks
     */
    public int getNumChewMarks() {
        return numChewMarks;
    }

    /**
     * @param numChewMarks the numChewMarks to set
     */
    public void setNumChewMarks(int numChewMarks) {
        this.numChewMarks = numChewMarks;
    }
    
}
