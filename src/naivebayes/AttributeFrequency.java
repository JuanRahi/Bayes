/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author juan
 */
public class AttributeFrequency {    
    private Map<String, Integer> frequencyPerTargetValue;    
    
    public AttributeFrequency(Map<String, Integer> targetAttributeValues, String targetValue){        
        this.frequencyPerTargetValue = new HashMap<>();
        for(String key: targetAttributeValues.keySet()){
            this.frequencyPerTargetValue.put(key, 0);
        }                    
        this.frequencyPerTargetValue.put(targetValue, 1);
    }    
    
    public AttributeFrequency updateFrequency(String targetValue){
        this.frequencyPerTargetValue.put(targetValue, this.frequencyPerTargetValue.get(targetValue) + 1);
        return this;
    }
    
    public double getFrequency(String targetValue){
        return this.frequencyPerTargetValue.get(targetValue);
    }
    
    
    @Override
    public String toString() {
        String output = "";
        for(String targetValue: frequencyPerTargetValue.keySet()){
            output += targetValue + ": " + frequencyPerTargetValue.get(targetValue) + " | ";
        }
        return output;
    }
            
}
