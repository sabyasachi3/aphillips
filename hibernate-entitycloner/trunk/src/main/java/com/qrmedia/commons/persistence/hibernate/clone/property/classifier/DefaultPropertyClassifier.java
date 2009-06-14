/*
 * @(#)PropertyClassifier.java     10 Feb 2009
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
package com.qrmedia.commons.persistence.hibernate.clone.property.classifier;

import static com.qrmedia.commons.validation.ValidationUtils.*;
import org.springframework.beans.BeanUtils;

/**
 * A default implementation of the {@link PropertyClassifier}, based mainly on
 * {@link BeanUtils#isSimpleProperty(Class)}.
 *  
 * @author anph
 * @since 10 Feb 2009
 *
 */
public class DefaultPropertyClassifier extends PropertyClassifier {

    /**
     * Determines if a property is safe to be &quot;simply&quot; cloned, i.e. via
     * <code>clone.setProperty(entity.getProperty())</code>.
     * 
     * @param propertyClass the type of the property being cloned
     * @return  <code>true</code> iff the property is safe for &quot;simple&quot; cloning
     * @throws IllegalArgumentException if propertyClass is <code>null</code>
     * @see BeanUtils#isSimpleProperty(Class)
     */
    @Override
    public boolean isSimpleProperty(Class<?> propertyClass) {
        checkNotNull("'propertyClass' may not be null", propertyClass);
        return (BeanUtils.isSimpleProperty(propertyClass) || propertyClass.isEnum());
    }
    
}
