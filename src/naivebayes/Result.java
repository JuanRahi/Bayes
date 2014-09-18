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
public class Result {
        
    private String targetValue;
    private int count;
    private double percentage;
    
    public Result(Probability probability){
        this.targetValue = probability.getTargetValue();        
        this.percentage = probability.getValue();
        this.count = 1;
    }
    
    public Result updateResult(double percent){
        this.count++;
        this.percentage += percent;
        return this;
    }
    
    public void print(int totalEvaluatedInstances){
        double average = percentage/count;        
        System.out.println(targetValue + " total:" + count +  " promedio total:" +((double)count/totalEvaluatedInstances)*100 + "% porcentaje promedio:" + average + "%");
    }
    
}
