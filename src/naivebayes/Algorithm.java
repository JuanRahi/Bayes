/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author juan
 */
public class Algorithm {
    
        
    private String[] allAttributes; 
    private int indexTargetAttribute = -1; 
    private Map<String, Integer> targetAttributeValues = new HashMap<>(); 
    private int totalInstances = -1; 
    private Attribute [] frequencyAttributes;
    private double m = 0.15;
    
    public void run(String path, String separator) throws FileNotFoundException, IOException {

        List<String[]> instances;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            allAttributes = line.split(separator);
            indexTargetAttribute = allAttributes.length - 1;
            instances = new ArrayList<>();
            while (((line = reader.readLine()) != null) ) {
                String[] lineSplit = line.split(separator);
                instances.add(lineSplit);
                String targetValue = lineSplit[indexTargetAttribute];
                if(targetAttributeValues.get(targetValue) == null)
                    targetAttributeValues.put(targetValue, 1);
                else
                    targetAttributeValues.put(targetValue, targetAttributeValues.get(targetValue) +1);
                           
            }
        }
        
        totalInstances = instances.size();
        
        // Get attribute frequency for every attribute, except target attribute
        frequencyAttributes = new Attribute[allAttributes.length -1];
        for (int i = 2; i < allAttributes.length -1; i++) {
            Map<String, AttributeFrequency> frequencyValues = calculateFrequencyOfAttributeValues(instances, i);
            frequencyAttributes[i] = new Attribute(allAttributes[i], frequencyValues);
        }
                
    }
    
    public void evaluate (String path, String separator) throws FileNotFoundException, IOException {
        List<String[]> evaluateInstances;
        Map<String, Result> resultsByTargetAttributeValues = new HashMap<>(); 
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            evaluateInstances = new ArrayList<>();
            while (((line = reader.readLine()) != null) ) {
                String[] lineSplit = line.split(separator);
                evaluateInstances.add(lineSplit);                           
            }
        }
        for(String[] evaluateInstance: evaluateInstances){            
            Probability probability = CalculateProbability(evaluateInstance);
            /*System.out.println("Valor esperado: " + evaluateInstance[evaluateInstance.length -1]
                    + " - Valor obtenido: " + probability.getTargetValue());*/
            if(resultsByTargetAttributeValues.get(probability.getTargetValue()) == null){
                boolean error = !evaluateInstance[evaluateInstance.length -1].equals(probability.getTargetValue());
                Result result = new Result(probability, error);
                resultsByTargetAttributeValues.put(probability.getTargetValue(), result);                
            }
            else{
                boolean error = !evaluateInstance[evaluateInstance.length -1].equals(probability.getTargetValue());
                Result result = resultsByTargetAttributeValues.get(probability.getTargetValue()).updateResult(probability.getValue(), error);
                resultsByTargetAttributeValues.put(probability.getTargetValue(), result);
            }                                
        }
        
        for(String target:resultsByTargetAttributeValues.keySet()){
            resultsByTargetAttributeValues.get(target).print(evaluateInstances.size());
        }
    }
    
    private Map<String, AttributeFrequency> calculateFrequencyOfAttributeValues(List<String[]> instances, int indexAttribute) {		
        Map<String, AttributeFrequency> targetValuesFrequency = new HashMap<>();
        for (String[] instance : instances) {
                String targetValue = instance[indexAttribute];
                if (targetValuesFrequency.get(targetValue) == null)
                    targetValuesFrequency.put(targetValue, new AttributeFrequency(targetAttributeValues, instance[indexTargetAttribute]));
                else 
                    targetValuesFrequency.put(targetValue, targetValuesFrequency.get(targetValue).updateFrequency(instance[indexTargetAttribute]));
        }        
        return targetValuesFrequency;
    }
    
    private Probability CalculateProbability(String[] instance){        
        double total = 0d;
        double maxProbability = Double.NEGATIVE_INFINITY;
        String maxTargetValue = "";        
        for(String targetValue: targetAttributeValues.keySet()){            
            double probability = 1d;
            double n = (double)targetAttributeValues.get(targetValue);
            for(int i=2; i< instance.length -1; i++){
                //(e + m.p) . (n + m) -1   p = 1/k
                double e = frequencyAttributes[i].getFrequency(instance[i], targetValue);
                double p = (1d/frequencyAttributes[i].getDifferentValuesCount());                
                probability *=  (e + (m*p))/(n+m);                 
            }
            probability *= (double)targetAttributeValues.get(targetValue);
            if (probability > maxProbability){
                    maxProbability = probability;
                    maxTargetValue = targetValue;
            }
            total += probability;
        }
            
        return new Probability(maxTargetValue, (maxProbability/total)*100);
    }
    
    private void print(){
        for (Attribute attribute: frequencyAttributes) {                        
            if(attribute != null)
                attribute.print();
        }        
    }
                               
}
