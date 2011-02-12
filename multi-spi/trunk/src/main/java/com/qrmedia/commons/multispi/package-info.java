/*
 * @(#)package-info.java     14 Dec 2010
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
/**
 * MultiSPI aims to make it
 * <ul>
 * <li>easy to detect and load SPI implementations within applications that provide an SPI
 * <li>easy to apply tailored validation and error checking strategies for implementations
 * <li>easy to extend the number of ways by which SPI implementors can provide implementations.
 * <p>
 * MultiSPI offers three main advantages over the {@link java.util.ServiceLoader} provided in Java 6:
 * <ol>
 * <li>it supports &quot;lenient&quot; loading of service implementations, e.g. allowing the host
 *   application to perform its own checks before loading the class and/or accepting it as a valid
 *   implementation
 * <li>out-of-the-box, it supports finding service implementations also on the basis of other 
 *   <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">Manifest</a>
 *   entries, as well as by scanning for marker annotations
 * <li>it provides pluggable support for additional, user-defined service implementation finding 
 *   strategies
 * </ol>
 */
package com.qrmedia.commons.multispi;