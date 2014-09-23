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
    private Map<String, Integer> evaluationTargetAttributeValues = new HashMap<>(); 
    private int totalInstances = -1; 
    private int totalTrainInstances = -1; 
    private int totalEvaluationInstances = -1; 
    private Attribute [] frequencyAttributes;
    private double m = 1;
    private static final List<Integer> continuousAttributes = Arrays.asList(9,12,13,14,15,16,17, 21);    
    private static final List<Integer> excludedAttributes = Arrays.asList(0,1);
    private double[] mean;
    private double[] maxAttributeValue;
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
            }
        }
        
        
        
        totalInstances = instances.size();
       
        evaluationInstances = new ArrayList<>();
        trainInstances = new ArrayList<>();
                
        for(int i=0; i< totalInstances; i++){
            if(i % 4 == 0){
                evaluationInstances.add(instances.get(i));
                String targetValue = instances.get(i)[indexTargetAttribute];
                if(evaluationTargetAttributeValues.get(targetValue) == null)
                    evaluationTargetAttributeValues.put(targetValue, 1);
                else
                    evaluationTargetAttributeValues.put(targetValue, evaluationTargetAttributeValues.get(targetValue) +1);
            }
            else{
                trainInstances.add(instances.get(i));
                String targetValue = instances.get(i)[indexTargetAttribute];
                if(targetAttributeValues.get(targetValue) == null)
                    targetAttributeValues.put(targetValue, 1);
                else
                    targetAttributeValues.put(targetValue, targetAttributeValues.get(targetValue) +1);
            }
        }
        
        totalEvaluationInstances = evaluationInstances.size();
        totalTrainInstances = trainInstances.size();

        //splitData(startEvaluation, evaluationInstances, instances, trainInstances);
        
        continuousAttributes2(trainInstances);
        
        
        // Get attribute frequency for every attribute, except target attribute
        frequencyAttributes = new Attribute[allAttributes.length -1];
        for (int i = 0; i < allAttributes.length -1; i++) {  
                if(!excludedAttributes.contains(i)){
                    Map<String, AttributeFrequency> frequencyValues = (continuousAttributes.contains(i)) ? calculateFrequencyOfContinuousAttributeValues(trainInstances, i) : calculateFrequencyOfAttributeValues(trainInstances, i);
                    frequencyAttributes[i] = new Attribute(allAttributes[i], frequencyValues);
                }
        }
                
        evaluate(evaluationInstances);
        
        printExpectedResults(); 
        
        //print();
    }

    private void splitData(int startEvaluation, List<String[]> evaluationInstances, List<String[]> instances, List<String[]> trainInstances) {
        for(int i=0; i< instances.size(); i++){
            if((i >= startEvaluation) && (i < (startEvaluation + totalEvaluationInstances))){
                evaluationInstances.add(instances.get(i));
                String targetValue = instances.get(i)[indexTargetAttribute];
                if(evaluationTargetAttributeValues.get(targetValue) == null)
                    evaluationTargetAttributeValues.put(targetValue, 1);
                else
                    evaluationTargetAttributeValues.put(targetValue, evaluationTargetAttributeValues.get(targetValue) +1);
            }
            else{
                trainInstances.add(instances.get(i));
                String targetValue = instances.get(i)[indexTargetAttribute];
                if(targetAttributeValues.get(targetValue) == null)
                    targetAttributeValues.put(targetValue, 1);
                else
                    targetAttributeValues.put(targetValue, targetAttributeValues.get(targetValue) +1);
            }
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
    
    private Map<String, AttributeFrequency> calculateFrequencyOfContinuousAttributeValues(List<String[]> instances, int indexAttribute) {		
        Map<String, AttributeFrequency> targetValuesFrequency = new HashMap<>();
        for (String[] instance : instances) {
                String targetValue = instance[indexAttribute];
                try{
                    Double d = Double.parseDouble(targetValue);                
                    int indexRange = (int)(d/maxAttributeValue[indexAttribute]);
                    targetValue = "[ " + indexRange + " - " + (indexRange + 1) +" ]" ;
                    if (targetValuesFrequency.get(targetValue) == null)
                        targetValuesFrequency.put(targetValue, new AttributeFrequency(targetAttributeValues, instance[indexTargetAttribute]));
                    else 
                        targetValuesFrequency.put(targetValue, targetValuesFrequency.get(targetValue).updateFrequency(instance[indexTargetAttribute]));
                }
                catch(Exception ex){
                    
                }
        }        
        return targetValuesFrequency;
    }
    
    private void continuousAttributes(List<String[]> trainInstances) throws NumberFormatException {
        // Atributos con valores continuos
        mean = new double[allAttributes.length];
        for(int i=0; i<allAttributes.length; i++){
            if(continuousAttributes.contains(i)){
                double media = 0d;
                for (String[] trainInstance : trainInstances) {
                    String value = trainInstance[i];
                    try{
                        media += Double.parseDouble(value);
                    }
                    catch(Exception ex){
                        
                    }
                    
                }
                mean[i] = media / totalTrainInstances;
            }
        }       
    }
    
       private void continuousAttributes2(List<String[]> trainInstances) throws NumberFormatException {
        // Atributos con valores continuos
        maxAttributeValue = new double[allAttributes.length];        
        for(int i=0; i<allAttributes.length; i++){            
            if(continuousAttributes.contains(i)){                
                double max = 0d;
                for (String[] trainInstance : trainInstances) {
                    String value = trainInstance[i];
                    try{
                        double val = Double.parseDouble(value);
                        if (val > max)
                            max = val;
                    }
                    catch(Exception ex){
                        
                    }
                    
                }
                maxAttributeValue[i] = max;
            }
        }       
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
                if(!excludedAttributes.contains(i)){
                    //(e + m.p) . (n + m) -1   p = 1/k
                    String value = instance[i];
                    if(!value.equals("?")){
                        if(continuousAttributes.contains(i)){
                            try{
                                double d = Double.parseDouble(value);
                                //value = d > (2*mean[i]) ? "VeryBig" : (d > mean[i]) ? "Big" : (d > mean[i]/2) ? "Small" : "VerySmall";
                                int indexRange = (int)(d/maxAttributeValue[i]);
                                value = "[ " + indexRange + " - " + (indexRange + 1) +" ]" ;
                            }
                            catch(Exception ex){
                            }
                        }
                        double noise = frequencyAttributes[i].getFrequency("?", targetValue);
                        double e = frequencyAttributes[i].getFrequency(value, targetValue);
                        double p = (1d/frequencyAttributes[i].getDifferentValuesCount());                
                        probability *=  ((e + (m*p))/(n-noise+m));                 
                    }
                }
            }
            probability *= ((double)targetAttributeValues.get(targetValue)/(totalTrainInstances));
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
    
    private void printExpectedResults(){ 
        System.out.println("-------- Expected Results ------");
        for(String target :evaluationTargetAttributeValues.keySet()){
            System.out.print(target);
            System.out.println(": " + evaluationTargetAttributeValues.get(target) + " - " + (evaluationTargetAttributeValues.get(target)*100)/totalEvaluationInstances + " %");
            
        }   
        System.out.println("total evaluation instances: " + totalEvaluationInstances);
    }
        
    private void printTrainResults(){     
        System.out.println("-------- Expected Results ------");
        for(String target :targetAttributeValues.keySet()){
            System.out.println(target);
            System.out.println(targetAttributeValues.get(target));
            
        }
    }
                               
}
