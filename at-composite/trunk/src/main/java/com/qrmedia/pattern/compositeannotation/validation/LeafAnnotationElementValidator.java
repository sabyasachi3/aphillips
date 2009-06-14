/*
 * @(#)LeafAnnotationElementValidator.java     3 Jun 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.qrmedia.commons.lang.model.ElementUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;
import com.qrmedia.pattern.compositeannotation.api.LeafAnnotationFactory;

/**
 * A validator for {@code LeafAnnotation LeafAnnotation} elements.
 * <p>
 * Duplicates the validation logic from the
 * {@link com.qrmedia.pattern.compositeannotation.metadata.LeafAnnotationDescriptor
 * LeafAnnotationDescriptor}, but for {@link ExecutableElement ExecutableElements}.
 * 
 * @author aphillips
 * @since 3 Jun 2009
 * 
 */
class LeafAnnotationElementValidator extends AbstractElementValidator {
    private static final String FACTORY_CLASS_MEMBER_NAME = "factoryClass";
    
    private final DeclaredType leafType;
    private final Set<ElementType> leafTarget;
    
    /**
     * Creates a validator for the given leaf annotation element.
     * 
     * @param element
     *            the element carrying the leaf annotation
     * @param typeUtils a type utility for the current processing environment           
     * @param elementUtils an element utility for the current processing environment
     * @see ProcessingEnvironment#getTypeUtils()
     * @see ProcessingEnvironment#getElementUtils()
     */
    LeafAnnotationElementValidator(ExecutableElement element, Types typeUtils, Elements elementUtils) {
    	assert ((element != null) && (element.getAnnotation(LeafAnnotation.class) != null)
    		&& ElementUtils.isMethod(element)) : element;
    	assert (typeUtils != null);
    	assert (elementUtils != null);
    	
        // the method must be a member of a composite annotation
    	Element enclosingElement = element.getEnclosingElement();
    	
        if (!ElementUtils.isAnnotation(enclosingElement)
                || (enclosingElement.getAnnotation(CompositeAnnotation.class) == null)) {
            errorMessages.add(element + " is not a member of a @CompositeAnnotation");
        }
        
        // the leaf member must return an annotation
        TypeMirror elementReturnType = element.getReturnType();

        if (!ElementUtils.isAnnotation(typeUtils.asElement(elementReturnType))) {
            errorMessages.add(element + " does not return an annotation");
            
            // final fields must be initialized
            leafType = null;
            leafTarget = null;
        } else {
            leafType = (DeclaredType) elementReturnType;
            Element leafTypeElement = leafType.asElement();
            
            // getRetention will return a non-null value
            if (!ElementUtils.getRetention(leafTypeElement).equals(RetentionPolicy.RUNTIME)) {
                errorMessages.add("Leaf annotation type " + leafType + " does not have RUNTIME retention");
            }
            
            // default to "all element types" if no target is explicitly defined
            leafTarget = ElementUtils.getTarget(leafTypeElement);
        }
        
        /*
         * A custom factory class must have a public no-arg constructor and the correct generic 
         * type parameters.
         */
        TypeElement leafAnnotationElement = elementUtils.getTypeElement(LeafAnnotation.class.getName());
        AnnotationValue leafAnnotationFactoryClassValue = 
            ElementUtils.getAnnotationMirror(element, leafAnnotationElement).getElementValues()
            .get(ElementUtils.getEnclosedElement(leafAnnotationElement, FACTORY_CLASS_MEMBER_NAME));
        
        // "getElementValues" does not return implicitly set (i.e. default) values
        if (leafAnnotationFactoryClassValue != null) {
            assert (leafAnnotationFactoryClassValue.getValue() instanceof DeclaredType)
            : leafAnnotationFactoryClassValue;
            DeclaredType leafAnnotationFactoryClass = 
                (DeclaredType) leafAnnotationFactoryClassValue.getValue();
            
            // the factory class type must be a *typed* subtype of LeafAnnotationFactory<LeafType, CompositeType>
            if (!typeUtils.isSubtype(leafAnnotationFactoryClass, 
                    typeUtils.getDeclaredType(elementUtils.getTypeElement(
                            LeafAnnotationFactory.class.getName()), elementReturnType, 
                            enclosingElement.asType()))) {
                errorMessages.add(leafAnnotationFactoryClass + " is not a subtype of " 
                        + typeUtils.getDeclaredType(elementUtils.getTypeElement(
                                LeafAnnotationFactory.class.getName()), elementReturnType, 
                                enclosingElement.asType()));
            }
            
            // the element for a DeclaredType is a TypeElement
            if (!ElementUtils.hasPublicNoargConstructor(leafAnnotationFactoryClass.asElement())) {
                errorMessages.add("Custom factory class " 
                        + leafAnnotationFactoryClass.asElement().getSimpleName() 
                        + " has no public, no-argument constructor");
            }
            
        }
        
    }
    
    /**
     * @return  the declared type of the leaf annotation, which will be an {@link Annotation}
     *          subclass. If the element is not a valid leaf annotation, the return value is
     *          undefined.  
     */
    DeclaredType getType() {
        return leafType;
    }
    
    /**
     * @return  the element types the leaf annotation may be applied to (as specified by a
     *          {@code @Target} annotation on the leaf element type, if present). If the element 
     *          is not a valid leaf annotation, the return value is undefined.
     */
    Set<ElementType> getTarget() {
        return leafTarget;
    }    

}
