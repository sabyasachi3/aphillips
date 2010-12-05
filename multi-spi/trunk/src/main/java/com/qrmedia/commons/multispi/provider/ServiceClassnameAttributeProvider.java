/*
 * @(#)ManifestEntryProvider.java     4 Dec 2010
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
package com.qrmedia.commons.multispi.provider;

import static com.google.common.base.Predicates.compose;
import static com.google.common.collect.Iterables.any;

import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Returns those entries in a manifest that contain an attribute whose name matches
 * the requested service class' {@link Class#getSimpleName() simple name}.
 * <p>
 * Does <strong>not</strong> check if the classes listed in the configuration file
 * <em>actually</em> implement the requested interface!
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
@ThreadSafe
public class ServiceClassnameAttributeProvider extends ManifestEntryProvider {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.multispi.provider.ManifestEntryProvider#implementationManifestEntry(java.lang.Class, java.util.jar.Attributes)
     */
    @Override
    protected boolean implementationManifestEntry(Class<?> serviceClass,
            Attributes entryAttributes) {
        return any(entryAttributes.keySet(), compose(
                new EqualToIgnoreCase(serviceClass.getSimpleName()), 
                new Function<Object, String>() {
                    public String apply(Object from) {
                        // the attributes map of a manifest entry contains keys of type Attributes.Name
                        return ((Name) from).toString();
                    }
                }));
    }
    
    // move to StringPredicates
    private static class EqualToIgnoreCase implements Predicate<String> {
        private final String stringToFind;
        
        public EqualToIgnoreCase(String stringToFind) {
            this.stringToFind = stringToFind;
        }
        
        public boolean apply(String input) {
            return StringUtils.equalsIgnoreCase(input, stringToFind);
        }
    }
    
    // move to Functions
    public static <T> Function<Object, T> cast(final Class<T> targetClass) {
        return new Function<Object, T>() {
                public T apply(Object from) {
                        return targetClass.cast(from);
                }
        };
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ServiceClassnameAttributeProvider.class.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }
}
