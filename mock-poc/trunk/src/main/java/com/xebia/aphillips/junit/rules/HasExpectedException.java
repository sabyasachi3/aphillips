/*
 * @(#)ExpectedException.java     10 Nov 2009
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
package com.xebia.aphillips.junit.rules;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The ExpectedException Rule allows in-test specification of expected exception
 * types and messages.
 * 
 */
public class HasExpectedException {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throwsNothing() {
        assertTrue(true);
    }

    @Test
    public void throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        throw new NullPointerException();
    }

    @Test
    public void throwsNullPointerExceptionWithMessage() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("happened?");
        thrown.expectMessage(startsWith("What"));
        throw new NullPointerException("What happened?");
    }
}