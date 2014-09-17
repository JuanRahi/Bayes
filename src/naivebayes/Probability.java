/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

/**
 *
 * @author juan
 */
public class Probability {
    private String targetValue;
    private double value;
    
    public Probability(String target, double value){
        this.targetValue = target;
        this.value = value;
    }
    
    public void print(){
        System.out.println("Probability: " + targetValue + ": " + value);
    }
    
}