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
import java.util.Arrays;
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
    private static final List<Integer> continuousAttributes = Arrays.asList(9,12,13,14,15,16,17,21);    
    private double[] mean;
    //private double[] deviation;
    
    
    public void run(String path, String separator) throws FileNotFoundException, IOException {

        List<String[]> instances;
        List<String[]> trainInstances;
        List<String[]> evaluationInstances;
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
        int totalEvaluationInstances = instances.size()/5;
        int startEvaluation = (int)(Math.random() * (totalInstances - totalEvaluationInstances));
        evaluationInstances = new ArrayList<>();
        trainInstances = new ArrayList<>();
        
        for(int i=0; i< totalInstances; i++){
            if((i >= startEvaluation) && (i < (startEvaluation + totalEvaluationInstances)))
                evaluationInstances.add(instances.get(i));
            else
                trainInstances.add(instances.get(i));            
        }
        
        continuousAttributes(trainInstances, totalEvaluationInstances);
        
        
        // Get attribute frequency for every attribute, except target attribute
        frequencyAttributes = new Attribute[allAttributes.length -1];
        for (int i = 0; i < allAttributes.length -1; i++) {            
                Map<String, AttributeFrequency> frequencyValues = (continuousAttributes.contains(i)) ? calculateFrequencyOfContinuousAttributeValues(trainInstances, i) : calculateFrequencyOfAttributeValues(trainInstances, i);
                frequencyAttributes[i] = new Attribute(allAttributes[i], frequencyValues);
        }
        
        
        evaluate(evaluationInstances);
                
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
    
    private Map<String, AttributeFrequency> calculateFrequencyOfContinuousAttributeValues(List<String[]> instances, int indexAttribute) {		
        Map<String, AttributeFrequency> targetValuesFrequency = new HashMap<>();
        for (String[] instance : instances) {
                String targetValue = instance[indexAttribute];
                Double d = Double.parseDouble(targetValue);                
                targetValue = d > (2*mean[indexAttribute]) ? "VeryBig" : (d > mean[indexAttribute]) ? "Big" : (d > mean[indexAttribute]/2) ? "Small" : "VerySmall";
                if (targetValuesFrequency.get(targetValue) == null)
                    targetValuesFrequency.put(targetValue, new AttributeFrequency(targetAttributeValues, instance[indexTargetAttribute]));
                else 
                    targetValuesFrequency.put(targetValue, targetValuesFrequency.get(targetValue).updateFrequency(instance[indexTargetAttribute]));
        }        
        return targetValuesFrequency;
    }
    
    private void continuousAttributes(List<String[]> trainInstances, int totalEvaluationInstances) throws NumberFormatException {
        // Atributos con valores continuos
        mean = new double[allAttributes.length];
        for(int i=0; i<allAttributes.length; i++){
            if(continuousAttributes.contains(i)){
                double media = 0d;
                for (String[] trainInstance : trainInstances) {
                    String value = trainInstance[i];
                    media += Integer.parseInt(value);
                }
                mean[i] = media / (totalInstances - totalEvaluationInstances);
            }
        }
        
        /*for(int i=0; i<allAttributes.length; i++){
        if(continuousAttributes.contains(i)){
        double standar = 0d;
        double media = mean[i];
        for (String[] trainInstance : trainInstances) {
        String value = trainInstance[i];
        standar += ((Integer.parseInt(value) - media)*(Integer.parseInt(value) - media));
        }
        deviation[i] = sqrt((1d/(totalInstances - totalEvaluationInstances -1)) * standar);
        }
        }*/
    }
    
    private void evaluate (List<String[]> evaluateInstances) throws FileNotFoundException, IOException {        
        Map<String, Result> resultsByTargetAttributeValues = new HashMap<>(); 

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
            
    private Probability CalculateProbability(String[] instance){        
        double total = 0d;
        double maxProbability = Double.NEGATIVE_INFINITY;
        String maxTargetValue = "";        
        for(String targetValue: targetAttributeValues.keySet()){            
            double probability = 1d;
            double n = (double)targetAttributeValues.get(targetValue);
            for(int i=0; i< instance.length -1; i++){
                //(e + m.p) . (n + m) -1   p = 1/k
                String value = instance[i];
                if(continuousAttributes.contains(i)){
                    double d = Double.parseDouble(value);
                    value = d > (2*mean[i]) ? "VeryBig" : (d > mean[i]) ? "Big" : (d > mean[i]/2) ? "Small" : "VerySmall";
                }
                
                double e = frequencyAttributes[i].getFrequency(value, targetValue);
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
