/*
 * @(#)AbstractElementValidator.java     7 Jun 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An element validator base class that supports error messages.
 * 
 * @author aphillips
 * @since 7 Jun 2009
 *
 */
public class AbstractElementValidator {
    protected final Collection<String> errorMessages = new ArrayList<String>();

    /**
     * @return  {@code true} iff the element is valid
     */
    protected boolean isValid() {
        return errorMessages.isEmpty();
    }

    /**
     * @return any error messages for this element; if the element is valid, this
     *         collection will be empty
     */
    protected Collection<String> getErrorMessages() {
        return errorMessages;
    }

}