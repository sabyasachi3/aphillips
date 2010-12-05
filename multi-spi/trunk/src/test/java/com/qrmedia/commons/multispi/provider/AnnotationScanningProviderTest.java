/*
 * @(#)MetaInfServicesProviderTest.java     4 Dec 2010
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

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.mi6.Agent;
import uk.gov.mi6.LicenseToKill;
import uk.gov.mi6.agent.JackGiddings;
import uk.gov.mi6.agent.JamesBond;

/**
 * Unit tests for the {@link AnnotationScanningProvider}.
 * 
 * @author aphillips
 * @since 4 Dec 2010
 *
 */
public class AnnotationScanningProviderTest {
    private final AnnotationScanningProvider provider = 
        new AnnotationScanningProvider(LicenseToKill.class, "uk.gov");
    
    @Test
    public void scansAnnotatedFilesInBasePackageAndSubpackages() {
        assertEquals(newHashSet(JackGiddings.class.getName(), JamesBond.class.getName()), 
                provider.findServiceImplementations(Agent.class));
    }
}
