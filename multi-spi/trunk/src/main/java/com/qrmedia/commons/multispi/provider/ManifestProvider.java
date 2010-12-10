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

import static com.google.common.base.Functions.constant;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

/**
 * A {@link ServiceImplementationProvider} that returns implementations listed in 
 * a <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">Java Manifest file</a>,
 * based on properties of the manifest.   
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public abstract class ManifestProvider extends ClasspathResourcesReadingProvider<Manifest> {
    private static final String MANIFEST_FILE_NAME = "META-INF/MANIFEST.MF";

    protected ManifestProvider() {
        super(constant(MANIFEST_FILE_NAME), new IoFunction<URL, Manifest>() {
                public Manifest apply(URL item) throws IOException {
                    return new Manifest(item.openStream());
                }
            });
    }
}
