/*
 * @(#)CompositeAnnotationValidatingProcessor.java     13 May 2009
 */
package com.qrmedia.pattern.compositeannotation.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.qrmedia.commons.lang.model.ElementUtils;
import com.qrmedia.pattern.compositeannotation.annotation.CompositeAnnotation;
import com.qrmedia.pattern.compositeannotation.annotation.LeafAnnotation;

/**
 * Checks for correct usage of {@link CompositeAnnotation composite annotations}.
 *
 * <h2>Usage</h2>
 * 
 * Note that, as the validation processor uses the {@link Processor} API, it requires Java 6.
 * 
 * <h4>Command line/Ant/Maven etc.</h4>
 * 
 * The <strong>@Composite</strong> JAR contains a service provider configuration file for the 
 * validation processor (as described in the <a href="http://java.sun.com/javase/6/docs/technotes/tools/windows/javac.html#processing">
 * {@code javac} documentation</a>), so it should be found and invoked automatically if the JAR
 * is on the classpath, which it presumably is.
 * <p>
 * Annotation processing, and thus validation, can be disabled by supplying 
 * <a href="http://java.sun.com/javase/6/docs/technotes/tools/windows/javac.html#options">
 * {@code -proc:none}</a> as a compiler argument (for instructions on how to do this in Maven, 
 * see the <a href="http://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html">
 * compiler plugin documentation</a>). If other annotation processors <em>are</em> needed, the
 * list of processors can be defined explicitly using the 
 * <a href="http://java.sun.com/javase/6/docs/technotes/tools/windows/javac.html#options">
 * {@code -processor}</a> option.
 * 
 * <h4>Eclipse</h4>
 * 
 * Support for the Java 6 annotation APIs was added to the 
 * <a href="http://www.eclipse.org/jdt/apt/index.html">JDT-APT</a> project in Eclipse 3.3.
 * In order to enable the validation processor for your project, 
 * 
 * <ol>
 * <li>open the project properties dialog and go to <em>Java Compiler -> Annotation Processing -> Factory Path</em>
 * <li>add the {@code at-composite} and {@code commons-lang} JARs to the factory path (<tt>commons-lang</tt>
 * does not contain any processors, but unfortunately in Eclipse all dependencies of 
 * {@code at-composite} need to be added to the factory path too).<br />
 * You can avoid absolute references by extending the {@code M2_REPO} variable (the <em>Add Variable...</em>
 * button), resulting in e.g.<br/>
 * {@code M2_REPO/com/qrmedia/pattern/at-composite/1.0-SNAPSHOT/at-composite-1.0-SNAPSHOT.jar} and<br />
 * {@code M2_REPO/com/qrmedia/commons/commons-lang/1.0-SNAPSHOT/commons-lang-1.0-SNAPSHOT.jar}<br/>
 * as references.
 * <li>go one step up to the <em>Annotation Processing</em> page and ensure <em>Enable annotation
 * processing</em> is checked
 * </ol>
 * 
 * For both of the last steps, you will probably need to select <em>Enable project specific settings</em>,
 * unless these features are enabled by default in your workspace. Note that, at the time of writing, 
 * Eclipse (3.3 and 3.4) can only run Java 6 annotation processors</a> <u>once a file is saved</u>, 
 * i.e. not whilst editing. 
 * 
 * @author aphillips
 * @since 13 May 2009
 * 
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({ "*" })
public class CompositeAnnotationValidationProcessor extends AbstractProcessor {
    
    /* (non-Javadoc)
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        
        // immediately return on any rounds in which there are no annotations to process
        if (annotations.isEmpty()) {
            return false;
        }

        Messager messager = processingEnv.getMessager();
        Types typeUtils = processingEnv.getTypeUtils();
        
        Set<TypeElement> compositeAnnotations = getCompositeAnnotations(annotations, roundEnv);
        
        Map<Element, LeafAnnotationElementValidator> leafValidators = 
            new HashMap<Element, LeafAnnotationElementValidator>();
        
        /*
         * Validate *all* the leaf annotations - both those being processed in this round
         * (as children of composite annotations being processed) and those compiled elsewhere
         * (e.g. in composite annotations in libraries).
         */
        for (Element element : getLeafAnnotations(compositeAnnotations, roundEnv)) {
            assert (element instanceof ExecutableElement) : element;
            LeafAnnotationElementValidator leafValidator = 
                new LeafAnnotationElementValidator((ExecutableElement) element, typeUtils, 
                        processingEnv.getElementUtils());
            leafValidators.put(element, leafValidator);
            
            if (!leafValidator.isValid()) {
                addErrors(messager, element, leafValidator.getErrorMessages());
            }
            
        }

        // validate all the composite annotations
        for (Element element : compositeAnnotations) {
            assert (element instanceof TypeElement) : element;
            CompositeAnnotationElementValidator compositeValidator = 
                new CompositeAnnotationElementValidator((TypeElement) element, leafValidators);
            
            if (!compositeValidator.isValid()) {
                addErrors(messager, element, compositeValidator.getErrorMessages());
            }
            
            Set<DeclaredType> leafTypes = compositeValidator.getTypes();

            /*
             * Check that the elements annotated with the composite do not define "standard"
             * annotations of the same type as one of the leaf annotations
             */
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith((TypeElement) element)) {

                // can't use CollectionUtils.containsAny because TypeMirror doesn't obey equals
                if (containsAny(ElementUtils.getAnnotationTypes(annotatedElement), leafTypes, 
                        typeUtils)) {
                    messager.printMessage(Kind.ERROR, 
                            annotatedElement + " is annotated with a 'standard' annotation of the same type as one of the leaf annotations declared in "
                                + element, 
                            annotatedElement);
                }
                
            }            
            
        }

        // the annotations should be available for processing by anyone else, so are not claimed 
        return false;
    }

    private static void addErrors(Messager messager, Element element, 
            Collection<String> errorMessages) {
        
        for (String errorMessage : errorMessages) {
            messager.printMessage(Kind.ERROR, errorMessage, element);
        }
        
    }

    @SuppressWarnings("unchecked")
    private static Set<TypeElement> getCompositeAnnotations(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        Set<TypeElement> compositeAnnotations = new HashSet<TypeElement>();
        
        // first, get all the annotations being processed (which are already compiled)        
        for (TypeElement annotation : annotations) {
            
            if (annotation.getAnnotation(CompositeAnnotation.class) != null) {
                compositeAnnotations.add(annotation);
            }
            
        }
        
        /*
         * Now add all the composite annotations being processed for compilation. Using a set
         * prevents duplicates.
         */
        compositeAnnotations.addAll((Set<TypeElement>) 
                roundEnv.getElementsAnnotatedWith(CompositeAnnotation.class));
        
        return compositeAnnotations;
    }

    @SuppressWarnings("unchecked")
    private static Set<ExecutableElement> getLeafAnnotations(Set<TypeElement> compositeAnnotations,
            RoundEnvironment roundEnv) {
        Set<ExecutableElement> leafAnnotations = new HashSet<ExecutableElement>();
        
        // first, get all the leaf annotations that are members of composites
        for (TypeElement compositeAnnotation : compositeAnnotations) {
            
            for (Element leafAnnotationElement 
                    : ElementUtils.getAnnotatedEnclosedElements(compositeAnnotation, LeafAnnotation.class)) {
                // leaf annotations are only allowed on methods
                assert (leafAnnotationElement instanceof ExecutableElement) : leafAnnotationElement;
                leafAnnotations.add((ExecutableElement) leafAnnotationElement);
            }
            
        }
    
        /*
         * Now add all the "loose" leaf annotations being processed (which are invalid, by the way).
         * Using a set prevents duplicates. 
         */
        leafAnnotations.addAll((Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(LeafAnnotation.class));
        return leafAnnotations;
    }

    private static boolean containsAny(Collection<? extends TypeMirror> collection1,
            Collection<? extends TypeMirror> collection2, Types typeUtils) {
        // not, ahem, the most efficient implementation possible, but...
        for (TypeMirror type1 : collection1) {
            
            for (TypeMirror type2 : collection2) {
                
                if (typeUtils.isSameType(type1, type2)) {
                    return true;
                }
                
            }
            
        }
        
        return false;
    }

}
