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

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.filterValues;

import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * A {@link ServiceImplementationProvider} that returns implementations listed in 
 * a <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">Java Manifest file</a>,
 * based on properties of the implementation's manifest entry.   
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public abstract class ManifestEntryProvider extends ManifestProvider {
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final char RESOURCE_PATH_SEPARATOR = '/';
    private static final char PACKAGE_SEPARATOR = '.';

    protected Set<String> processResource(Manifest manifest, final Class<?> serviceClass) {
        return ImmutableSet.copyOf(transform(
                filterValues(manifest.getEntries(), new Predicate<Attributes>() {
                        public boolean apply(Attributes input) {
                            return isManifestEntryOfImplementation(input, serviceClass);
                        }
                    })
                .keySet(), 
                new ResourcePathsToClassNames()));
    }
    
    /**
     * @param entryAttributes the attributes of a Java manifest entry
     * @param serviceClass the class of the service requested
     * @return {@code true} iff the attributes indicate that the manifest entry is
     *         an implementation of the service class
     */
    protected abstract boolean isManifestEntryOfImplementation(@Nonnull Attributes entryAttributes, 
            @Nonnull Class<?> serviceClass);

    private static class ResourcePathsToClassNames implements Function<String, String> {
        public String apply(String from) {
            return StringUtils.removeEnd(from, CLASS_FILE_SUFFIX)
                   .replace(RESOURCE_PATH_SEPARATOR, PACKAGE_SEPARATOR);
        }
    }
}
