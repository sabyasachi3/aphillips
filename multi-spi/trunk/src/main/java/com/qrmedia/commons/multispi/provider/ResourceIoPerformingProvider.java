/*
 * @(#)ResourceIoPerformingProvider.java     5 Dec 2010
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

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

/**
 * A {@code ServiceImplementationProvider} that performs file I/O and potentially
 * throws exceptions.
 * 
 * @author aphillips
 * @since 5 Dec 2010
 *
 */
public abstract class ResourceIoPerformingProvider implements ServiceImplementationProvider {

    /* (non-Javadoc)
     * @see com.qrmedia.commons.multispi.provider.ServiceImplementationProvider#findServiceImplementations(java.lang.Class)
     */
    public Set<String> findServiceImplementations(Class<?> serviceClass) {
        try {
            return findServiceImplementationsWithIo(serviceClass);
        } catch (IOException exception) {
            handleIoException(exception);
            return ImmutableSet.of();
        }
    }
    
    protected abstract @Nonnull Set<String> findServiceImplementationsWithIo(
            @Nonnull Class<?> serviceClass) throws IOException;

    // override me!
    protected void handleIoException(@Nonnull IOException exception) {
        System.err.println(exception);
    }
}
