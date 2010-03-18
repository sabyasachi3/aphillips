/*
 * @(#)CompositeAnnotationValidationProcessorTest.java     26 May 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic.Kind;

import org.junit.Test;

import com.qrmedia.commons.test.annotation.processing.AbstractAnnotationProcessorTest;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidCompositeAnnotationInconsistentTarget;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidCompositeAnnotationInvalidRetention;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidCompositeAnnotationNonuniqueLeafAnnotationTypes;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidLeafAnnotationInvalidCustomFactories;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidLeafAnnotationInvalidRetention;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidLeafAnnotationNonAnnotationReturnType;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidLeafAnnotationUsage;
import com.qrmedia.pattern.compositeannotation.validation.sample.InvalidStandardAndCompositeUsage;
import com.qrmedia.pattern.compositeannotation.validation.sample.ValidCompositeAnnotation;

/**
 * Unit tests for the {@code CompositeAnnotationValidationProcessor}.
 * <p>
 * Requires the <em>source</em> of the sample classes (i.e. the {@code .java}
 * files) to be available on the classpath.
 * 
 * @author aphillips
 * @since 26 May 2009
 * 
 */
public class CompositeAnnotationValidationProcessorTest extends
        AbstractAnnotationProcessorTest {
    /*
     * (non-Javadoc)
     * 
     * @see com.qrmedia.pattern.compositeannotation.validation.AbstractAnnotationProcessorTest#getProcessors()
     */
    @Override
    protected Collection<Processor> getProcessors() {
        return Arrays.<Processor> asList(new CompositeAnnotationValidationProcessor());
    }

    @Test
    public void leafAnnotationOnNonCompositeMember() {
        assertCompilationReturned(Kind.ERROR, 22,
                compileTestCase(InvalidLeafAnnotationUsage.class));
    }

    @Test
    public void invalidLeafAnnotation_nonAnnotationReturnType() {
        assertCompilationReturned(Kind.ERROR, 26,
                compileTestCase(InvalidLeafAnnotationNonAnnotationReturnType.class));
    }

    @Test
    public void invalidLeafAnnotation_invalidRetention() {
        assertCompilationReturned(Kind.ERROR, 28,
                compileTestCase(InvalidLeafAnnotationInvalidRetention.class));
    }

    @Test
    public void invalidLeafAnnotation_invalidCustomFactories() {
        assertCompilationReturned(new Kind[] { Kind.ERROR, Kind.ERROR }, new long[] { 26, 30 },
                compileTestCase(InvalidLeafAnnotationInvalidCustomFactories.class));
    }

    @Test
    public void invalidCompositeAnnotation_invalidRetention() {
        assertCompilationReturned(Kind.ERROR, 22,
                compileTestCase(InvalidCompositeAnnotationInvalidRetention.class));
    }

    @Test
    public void invalidCompositeAnnotation_inconsistentTarget() {
        assertCompilationReturned(Kind.ERROR, 27,
                compileTestCase(InvalidCompositeAnnotationInconsistentTarget.class));
    }

    @Test
    public void invalidCompositeAnnotation_nonuniqueLeafAnnotationType() {
        assertCompilationReturned(Kind.ERROR, 24,
                compileTestCase(InvalidCompositeAnnotationNonuniqueLeafAnnotationTypes.class));
    }

    @Test
    public void nonuniqueAnnotationType_fromStandardAndComposite() {
        assertCompilationReturned(Kind.ERROR, 22,
                compileTestCase(InvalidStandardAndCompositeUsage.class));
    }

    @Test
    public void validCompositeAnnotation() {
        assertCompilationSuccessful(compileTestCase(ValidCompositeAnnotation.class));
    }

}
