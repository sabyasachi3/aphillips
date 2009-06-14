/*
 * @(#)CompositeAnnotationElementValidator.java     7 Jun 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import com.qrmedia.commons.lang.model.ElementUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * A validator for {@code CompositeAnnotation CompositeAnnotation} elements.
 * <p>
 * Duplicates the validation logic from the
 * {@link com.qrmedia.pattern.compositeannotation.metadata.CompositeAnnotationDescriptor
 * CompositeAnnotationDescriptor}, but for {@link ExecutableElement ExecutableElements}.
 *
 * @author aphillips
 * @since 7 Jun 2009
 *
 */
class CompositeAnnotationElementValidator extends AbstractElementValidator {
    private final Set<DeclaredType> leafTypes = new HashSet<DeclaredType>();
    
    /**
     * Creates a validator for the given composite annotation element.
     * 
     * @param element the element carrying the leaf annotation
     * @param leafValidators    leaf validators for leaf annotation elements; must contain validators
     *                          for <em>at least</em> the leaf annotations defined in the given
     *                          composite  
     */
    CompositeAnnotationElementValidator(TypeElement element,
            Map<Element, LeafAnnotationElementValidator> leafValidators) {
        assert ((element != null) && (element.getAnnotation(CompositeAnnotation.class) != null)
                && ElementUtils.isAnnotation(element)) : element;
        assert (leafValidators != null);
        
        // the composite must have RUNTIME retention (note that getRetention returns a non-null value)
        if (!ElementUtils.getRetention(element).equals(RetentionPolicy.RUNTIME)) {
            errorMessages.add("Composite annotation " + element.getSimpleName() 
                              + " does not have RUNTIME retention");
        }
        
        for (Element leafAnnotationElement 
                : ElementUtils.getAnnotatedEnclosedElements(element, LeafAnnotation.class)) {
            assert (leafValidators.containsKey(leafAnnotationElement)) 
            : new Object[] { leafValidators, leafAnnotationElement };
            
            /*
             * A leaf annotation must
             * 
             * 1) be valid (i.e. creating a descriptor does not throw an exception)
             * 2) have the same target as the composite
             * 3) be the only one of its type within a composite
             */
            LeafAnnotationElementValidator leafValidator = leafValidators.get(leafAnnotationElement);
            
            if (!leafValidator.isValid()) {
                errorMessages.add("Composite annotation " + element.getSimpleName() 
                        + " contains invalid leaf annotation " + leafAnnotationElement.getSimpleName());
            } else {
                
                if (!leafValidator.getTarget().equals(ElementUtils.getTarget(element))) {
                    errorMessages.add("The target of leaf annotation " + leafAnnotationElement 
                            + " does not match the target of the composite");
                }
                
                DeclaredType leafType = leafValidator.getType();
                
                if (leafTypes.contains(leafType)) {
                    errorMessages.add("Multiple leaf annotations of type " + leafType + " defined");
                }
                
                leafTypes.add(leafType);
            }
            
        }
        
    }
    
    /**
     * @return  the declared types of the leaf annotations of this composite, which will be 
     *          {@link Annotation} subclasses. If the element is not a valid composite annotation, 
     *          the return value is undefined.  
     */
    Set<DeclaredType> getTypes() {
        return leafTypes;
    }    
    
}
