/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.util.Map;

/**
 *
 * @author juan
 */
public class Attribute {
    private String attributeName;
    private Map<String, AttributeFrequency> attributeFrequency;
    
    public Attribute(String name, Map frequency){
        this.attributeName = name;
        this.attributeFrequency = frequency;
    }
    
    public int getFrequency(String attributeValue, String targetAttributeValue){
        return this.attributeFrequency.get(attributeValue).getFrequency(targetAttributeValue);
    }
    
    public void print(){
        System.out.println(attributeName);
        for(String key: attributeFrequency.keySet()){
            System.out.println("   " + key + ": " + attributeFrequency.get(key));
        }
    }
            
}

