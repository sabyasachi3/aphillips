/*
 * @(#)BusinessObjectUtilsBenchmark.java     2 Apr 2009
 */
package com.qrmedia.commons.bean.businessobject;

import java.util.Random;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.qrmedia.commons.bean.businessobject.annotation.BusinessField;
import com.qrmedia.commons.bean.businessobject.annotation.BusinessObject;

/**
 * Micro benchmarks for the {@link BusinessObjectUtils}.
 * 
 * @author aphillips
 * @since 2 Apr 2009
 *
 */
public class BusinessObjectUtilsBenchmark {
    private static final int NUM_ITERATIONS = 3;
    private static final int NUM_COMPARISONS_PER_ITERATION = 50000;
    
    public static class Handler {
        private final String name;

        private Handler(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            
            if (!(obj instanceof Handler)) {
                return false;
            }
            
            return StringUtils.equals(name, ((Handler) obj).name);
        }

        @Override
        public int hashCode() {
            return ((name != null) ? name.hashCode() : 31);
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        
    }
    
    @BusinessObject(equivalentClasses = { Agent.class })
    public static class Agent {
        @BusinessField
        private final int codeNumber;
        @BusinessField
        private final String name;
        @BusinessField
        private final Handler handler;
        
        private Agent(int codeNumber, String name, Handler handler) {
            this.codeNumber = codeNumber;
            this.name = name;
            this.handler = handler;
        }

        public boolean equalsBusinessObject(Object obj) {
            return BusinessObjectUtils.equals(this, obj);
        }
        
        public boolean equalsStandard(Object obj) {

            if (this == obj) {
                return true;
            }
            
            if (!(obj instanceof Agent)) {
                return false;
            }
            
            Agent other = (Agent) obj;
            return (codeNumber == other.codeNumber) && StringUtils.equals(name, other.name)
                   && ObjectUtils.equals(handler, other.handler);
        }

        public boolean equalsBuilder(Object obj) {
            
            if (this == obj) {
                return true;
            }
            
            if (!(obj instanceof Agent)) {
                return false;
            }
            
            Agent other = (Agent) obj;
            return new EqualsBuilder().append(codeNumber, other.codeNumber)
                   .append(name, other.name).append(handler, other.handler).isEquals();
        }
        
        public boolean equalsEclipse(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Agent other = (Agent) obj;
            if (codeNumber != other.codeNumber)
                return false;
            if (handler == null) {
                if (other.handler != null)
                    return false;
            } else if (!handler.equals(other.handler))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        public int hashCodeBusinessObject() {
            return BusinessObjectUtils.hashCode(this);
        }
        
        public int hashCodeBuilder() {
            return new HashCodeBuilder().append(codeNumber).append(name).append(handler)
                   .toHashCode();
        }
        
        public int hashCodeEclipse() {
            final int prime = 31;
            int result = 1;
            result = prime * result + codeNumber;
            result = prime * result
                    + ((handler == null) ? 0 : handler.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /**
         * @return the codeNumber
         */
        public int getCodeNumber() {
            return codeNumber;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the handler
         */
        public Handler getHandler() {
            return handler;
        }

    }
    
    public static void main(String[] args) {

        // warm up the JIT
        final int numWarmupRuns = 100;
        Agent[][] warmupAgents = new Agent[numWarmupRuns][2];
        initializeTargetObjects(warmupAgents, 50);

        for (int j = 0; j < numWarmupRuns; j++) {

            // make sure the compiler can't optimize this away
            if ((warmupAgents[j][0].equalsStandard(warmupAgents[j][1]) 
                    != warmupAgents[j][1].equalsStandard(warmupAgents[j][0]))
                 || (warmupAgents[j][0].equalsBuilder(warmupAgents[j][1]) 
                    != warmupAgents[j][1].equalsBuilder(warmupAgents[j][0])) 
                 || (warmupAgents[j][0].equalsEclipse(warmupAgents[j][1]) 
                    != warmupAgents[j][1].equalsEclipse(warmupAgents[j][0]))
                 || (warmupAgents[j][0].equalsBusinessObject(warmupAgents[j][1]) 
                    != warmupAgents[j][1].equalsBusinessObject(warmupAgents[j][0]))) {
                throw new AssertionError("Standard comparison failed!");
            }
            
            if (warmupAgents[j][0].equalsStandard(warmupAgents[j][1])) {
                
                if ((warmupAgents[j][0].hashCodeBuilder() 
                        != warmupAgents[j][1].hashCodeBuilder())
                    || (warmupAgents[j][0].hashCodeEclipse() 
                            != warmupAgents[j][1].hashCodeEclipse())
                    || (warmupAgents[j][0].hashCodeBusinessObject() 
                            != warmupAgents[j][1].hashCodeBusinessObject())) {
                    throw new AssertionError("Hashcode calculation failed!");
                }
                
            } else {
                /*
                 * The hashCode contract doesn't actually require unequal object to have 
                 * distinct hash codes, but we'll try to, here.
                 */
                if ((warmupAgents[j][0].hashCodeBuilder() 
                        == warmupAgents[j][1].hashCodeBuilder())
                    || (warmupAgents[j][0].hashCodeEclipse() 
                            == warmupAgents[j][1].hashCodeEclipse())
                    || (warmupAgents[j][0].hashCodeBusinessObject() 
                            == warmupAgents[j][1].hashCodeBusinessObject())) {
                    throw new AssertionError("Hashcode calculation failed!");
                }
                
            }
            
        }
        
        // tag
        System.gc();
        
        // delete
        System.gc();
        
        for (int i = 0; i <= 100; i += 10) {
            run(i);
            
            // tag
            System.gc();
            
            // delete
            System.gc();
        }


    }
    
    private static void run(int equalityLikelihood) {
        long totalStandardTime = 0;
        long totalBuilderTime = 0;
        long totalEclipseTime = 0;
        long totalBusinessObjectTime = 0;
        
        long numStandardEqual = 0;
        long numStandardUnequal = 0;

        long totalBuilderHashCodeTime = 0;
        long totalEclipseHashCodeTime = 0;
        long totalBusinessObjectHashCodeTime = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            
            // (# comparisons) rows x (# agents to compare) columns
            Agent[][] agents = new Agent[NUM_COMPARISONS_PER_ITERATION][2];
            boolean[] expectedResult = initializeTargetObjects(agents, equalityLikelihood);
            
            // standard
            long startStandard = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].equalsStandard(agents[j][1]) != expectedResult[j]) 
                        || (agents[j][1].equalsStandard(agents[j][0]) != expectedResult[j])) {
                    throw new AssertionError("Standard comparison failed!");
                }
                
                if (agents[j][0].equalsStandard(agents[j][1])) {
                    numStandardEqual++;
                } else {
                    numStandardUnequal++;
                }
                
            }
            
            totalStandardTime += System.currentTimeMillis() - startStandard;
            
            // builder
            long startBuilder = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].equalsBuilder(agents[j][1]) != expectedResult[j]) 
                        || (agents[j][1].equalsBuilder(agents[j][0]) != expectedResult[j])) {
                    throw new AssertionError("Builder comparison failed!");
                }
                
            }
            
            totalBuilderTime += System.currentTimeMillis() - startBuilder;
            
            // Eclipse
            long startEclipse = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].equalsEclipse(agents[j][1]) != expectedResult[j]) 
                        || (agents[j][1].equalsEclipse(agents[j][0]) != expectedResult[j])) {
                    throw new AssertionError("Eclipse comparison failed!");
                }
                
            }
            
            totalEclipseTime += System.currentTimeMillis() - startEclipse;    
            
            // Business Objects
            long startBusinessObject = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].equalsBusinessObject(agents[j][1]) != expectedResult[j]) 
                        || (agents[j][1].equalsBusinessObject(agents[j][0]) != expectedResult[j])) {
                    throw new AssertionError("BO comparison failed!");
                }
                
            }
            
            totalBusinessObjectTime += System.currentTimeMillis() - startBusinessObject;
            
            // builder hashCode
            startBuilder = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].hashCodeBuilder() == agents[j][1].hashCodeBuilder()) 
                        != expectedResult[j]) { 
                    throw new AssertionError("Builder hashCode failed!");
                }
                
            }
            
            totalBuilderHashCodeTime += System.currentTimeMillis() - startBuilder;
            
            // Eclipse
            startEclipse = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].hashCodeEclipse() == agents[j][1].hashCodeEclipse()) 
                        != expectedResult[j]) { 
                    throw new AssertionError("Eclipse hashCode failed!");
                }
                
            }
            
            totalEclipseHashCodeTime += System.currentTimeMillis() - startEclipse;    
            
            // Business Objects
            startBusinessObject = System.currentTimeMillis();
            for (int j = 0; j < NUM_COMPARISONS_PER_ITERATION; j++) {
                
                if ((agents[j][0].hashCodeBusinessObject() == agents[j][1].hashCodeBusinessObject()) 
                        != expectedResult[j]) { 
                    throw new AssertionError("BO hashCode failed!");
                }
                
            }
            
            totalBusinessObjectHashCodeTime += System.currentTimeMillis() - startBusinessObject;                
            
        }

        System.out.format("Time for standard implementation %.2fms (total time %dms, # equal %d, # unequal %d, actual equality %% %d%%)%n", 
                ((float) totalStandardTime) / NUM_ITERATIONS, totalStandardTime, 
                numStandardEqual, numStandardUnequal, 
                (numStandardEqual * 100) / (numStandardUnequal + numStandardEqual));
        System.out.format("Time for builder implementation %.2fms (total time %dms)%n", 
                ((float) totalBuilderTime) / NUM_ITERATIONS, totalBuilderTime);
        System.out.format("Time for Eclipse implementation %.2fms (total time %dms)%n", 
                ((float) totalEclipseTime) / NUM_ITERATIONS, totalEclipseTime);
        System.out.format("Time for BusinessObjects implementation %.2fms (total time %dms)%n", 
                ((float) totalBusinessObjectTime) / NUM_ITERATIONS, totalBusinessObjectTime);
        System.out.format("%.2f\t%.2f\t%.2f\t%.2f\t%n", 
                ((float) totalStandardTime) / NUM_ITERATIONS,
                ((float) totalBuilderTime) / NUM_ITERATIONS,
                ((float) totalEclipseTime) / NUM_ITERATIONS,
                ((float) totalBusinessObjectTime) / NUM_ITERATIONS);
        
        System.out.format("Time for builder hashCode implementation %.2fms (total time %dms)%n", 
                ((float) totalBuilderHashCodeTime) / NUM_ITERATIONS, totalBuilderHashCodeTime);
        System.out.format("Time for Eclipse hashCode implementation %.2fms (total time %dms)%n", 
                ((float) totalEclipseHashCodeTime) / NUM_ITERATIONS, totalEclipseHashCodeTime);
        System.out.format("Time for BusinessObjects hashCode implementation %.2fms (total time %dms)%n", 
                ((float) totalBusinessObjectHashCodeTime) / NUM_ITERATIONS, totalBusinessObjectHashCodeTime);
        System.out.format("%.2f\t%.2f\t%.2f\t%n", 
                ((float) totalBuilderHashCodeTime) / NUM_ITERATIONS,
                ((float) totalEclipseHashCodeTime) / NUM_ITERATIONS,
                ((float) totalBusinessObjectHashCodeTime) / NUM_ITERATIONS);
    }

    private static boolean[] initializeTargetObjects(Agent[][] agents, int equalityLikelihood) {
        Random random = new Random(System.currentTimeMillis());
        
        final int numRequiredAgents = agents.length;
        boolean[] expectedResults = new boolean[numRequiredAgents];
        
        for (int i = 0; i < numRequiredAgents; i++) {
            int codeNumber = random.nextInt();
            String name = randomString(random);
            Handler handler = new Handler(randomString(random));
            
            agents[i][0] = new Agent(codeNumber, name, handler);
            boolean equalAgents = (random.nextInt(100) <= equalityLikelihood);
            
            agents[i][1] = (equalAgents ? new Agent(codeNumber, name, new Handler(handler.name))
                                        : newUnequalAgent(random, codeNumber, name, handler.name));
            expectedResults[i] = equalAgents;
        }
        
        return expectedResults;
    }
    
    private static String randomString(Random random) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return String.valueOf(bytes);
    }

    private static Agent newUnequalAgent(Random random, int codeNumber, String name,
            String handlerName) {
        boolean unequalGuaranteed = false;
        
        // all properties have a 50/50 chance of differing
        int otherCodeNumber;
        
        if (random.nextBoolean()) {
            otherCodeNumber = codeNumber;
        } else {
            otherCodeNumber = random.nextInt();
            unequalGuaranteed = true;
        }

        String otherName;
        
        if (random.nextBoolean()) {
            otherName = name;
        } else {
            otherName = randomString(random);
            unequalGuaranteed = true;
        }
        
        String otherHandlerName;
        
        // may only be equal if some other property is already different
        if (unequalGuaranteed && random.nextBoolean()) {
            otherHandlerName = handlerName;
        } else {
            otherHandlerName = randomString(random);
        }
        
        return new Agent(otherCodeNumber, otherName, new Handler(otherHandlerName));
    }

}
