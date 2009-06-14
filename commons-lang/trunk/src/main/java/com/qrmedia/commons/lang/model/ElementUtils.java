/*
 * @(#)ElementUtils.java     7 Jun 2009
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
package com.qrmedia.commons.lang.model;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject.Kind;

/**
 * Utility methods for {@link Element Elements}.
 * 
 * @author aphillips
 * @see Elements
 * @since 7 Jun 2009
 *
 */
public final class ElementUtils {
    /**
     * The {@link Target documented} default value is &quot;all element types&quot;.
     */
    private static final Set<ElementType> DEFAULT_TARGET = asUnmodifiableSet(ElementType.values());
    
    /**
     * The {@link Retention documented} default value is CLASS.
     */
    private static final RetentionPolicy DEFAULT_RETENTION = RetentionPolicy.CLASS;
    
    private static <E> Set<E> asUnmodifiableSet(E... objs) {
        return Collections.unmodifiableSet(new HashSet<E>(Arrays.asList(objs)));
    }
    
    /**
     * Checks if an {@link Element} is of a certain {@link Kind}.
     * 
     * @param element   the element to be checked
     * @param kind      the kind to be checked for
     * @return  {@code true} iff the element is of the given kind
     * @see #isAnnotation(Element)
     * @see #isConstructor(Element)
     */
    public static boolean isOfKind(Element element, ElementKind kind) {
        return ((element != null) && (kind != null) && element.getKind().equals(kind));
    }

    /**
     * Checks if an {@link Element} is an {@link ElementKind#ANNOTATION_TYPE annotation}
     * 
     * @param element   the element to be checked
     * @return  {@code true} iff the element is an annotation
     * @see #isConstructorKind(Element)
     * @see #isMethod(Element)
     * @see #isOfKind(Element, ElementKind)
     */
    public static boolean isAnnotation(Element element) {
        return isOfKind(element, ElementKind.ANNOTATION_TYPE);
    }
    
    /**
     * Checks if an {@link Element} is a {@link ElementKind#CONSTRUCTOR constructor}
     * 
     * @param element   the element to be checked
     * @return  {@code true} iff the element is a constructor
     * @see #isAnnotation(Element)
     * @see #isMethod(Element)
     * @see #isOfKind(Element, ElementKind)
     */
    public static boolean isConstructor(Element element) {
        return isOfKind(element, ElementKind.CONSTRUCTOR);
    }

    /**
     * Checks if an {@link Element} is a {@link ElementKind#METHOD method}
     * 
     * @param element   the element to be checked
     * @return  {@code true} iff the element is a method
     * @see #isAnnotation(Element)
     * @see #isConstructor(Element)
     * @see #isOfKind(Element, ElementKind)
     */
    public static boolean isMethod(Element element) {
        return isOfKind(element, ElementKind.METHOD);
    }
    
    /**
     * @param element    the element whose target is required
     * @return  the value of the {@link Target @Target} annotation on the given element, if present,
     *          or the documented default value; for a {@code null} element or an element that
     *          is not an {@link Annotation} subtype, returns {@code null}                          
     */
    public static Set<ElementType> getTarget(Element element) {
        
        if ((element == null) || !isAnnotation(element)) {
            return null;
        }
        
        Target targetAnnotation = element.getAnnotation(Target.class);
        return ((targetAnnotation != null) ? asUnmodifiableSet(targetAnnotation.value()) 
                                           : DEFAULT_TARGET);
    }

    /**
     * @param annotationType    the annotation type class whose retention policy is required
     * @return  the value of the {@link Retention @Retention} annotation on the given class, if 
     *          present, or the documented default value; for a {@code null} element or an element that
     *          is not an {@link Annotation} subtype, returns {@code null}
     */
    public static RetentionPolicy getRetention(Element element) {
        
        if ((element == null) || !isAnnotation(element)) {
            return null;
        }
        
        Retention retentionAnnotation = element.getAnnotation(Retention.class);
        return ((retentionAnnotation != null) ? retentionAnnotation.value() : DEFAULT_RETENTION);
    }    
    
    /**
     * @param element   the element to be checked
     * @return  {@code true} iff the element has a public, no-argument constructor
     */
    public static boolean hasPublicNoargConstructor(Element element) {
        
        if (element == null) {
            return false;
        }
        
        for (Element enclosedElement : element.getEnclosedElements()) {
            // an element of type CONSTRUCTOR is an ExecutableElement
            if (isConstructor(enclosedElement) 
                    && enclosedElement.getModifiers().contains(Modifier.PUBLIC)
                    && ((ExecutableElement) enclosedElement).getParameters().isEmpty()) {
                return true;
            }
            
        }
        
        return false;
    }
    
    /**
     * Gets the {@link AnnotationMirror} corresponding to the given annotation type, if present.
     * 
     * @param element   the annotated element
     * @param annotationElement the (element corresponding to the) type of annotation to be returned 
     * @return  the annotation mirror for the element's annotation of the requested type, or
     *          {@code null} if none is found
     */
    public static AnnotationMirror getAnnotationMirror(Element element,
            TypeElement annotationElement) {

        if ((element == null) || (annotationElement == null)) {
            return null;
        }

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            
            if (annotationMirror.getAnnotationType().asElement().equals(annotationElement)) {
                return annotationMirror;
            }
            
        }
        
        return null;
    }    
    
    /**
     * @param element     the element
     * @param enclosedElementName        the name of the enclosed element required
     * @return  the enclosed element of the given name, or {@code null} if not found
     */
    public static Element getEnclosedElement(Element element, String enclosedElementName) {

        if ((element == null) || (enclosedElementName == null)) {
            return null;
        }

        for (Element enclosedElement : element.getEnclosedElements()) {
            
            if (enclosedElement.getSimpleName().contentEquals(enclosedElementName)) {
                return enclosedElement;
            }
            
        }
        
        return null;
    }    
    
    /**
     * @param element   the element
     * @param annotationType the type (class) of the annotation to be searched for
     * @return  the enclosed elements of the given element that are annotated with the requested
     *          annotation
     */
    public static List<Element> getAnnotatedEnclosedElements(Element element,
            Class<? extends Annotation> annotationType) {
        
        if ((element == null) || (annotationType == null)) {
            return new ArrayList<Element>(0);
        }

        List<Element> annotatedEnclosedElements = new ArrayList<Element>();
        
        for (Element enclosedElement : element.getEnclosedElements()) {
            
            if (enclosedElement.getAnnotation(annotationType) != null) {
                annotatedEnclosedElements.add(enclosedElement);
            }
            
        }
        
        return annotatedEnclosedElements;
    }
    
    /**
     * @param element   the element
     * @return  the declared types of any annotations on the element, in the same order as
     *          the annotations returned by {@link Element#getAnnotationMirrors()} 
     */
    public static List<DeclaredType> getAnnotationTypes(Element element) {
        
        if (element == null) {
            return new ArrayList<DeclaredType>(0);
        }
        
        List<DeclaredType> annotationTypes = new ArrayList<DeclaredType>();
        
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            annotationTypes.add(annotationMirror.getAnnotationType());
        }
        
        return annotationTypes;
    }
    
}
