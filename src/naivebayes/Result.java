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
    private int error;
    private double percentage;
    
    public Result(Probability probability, boolean error){
        this.targetValue = probability.getTargetValue();        
        this.percentage = probability.getValue();
        this.count = 1;
        this.error = error? 1 : 0;
    }
    
    public Result updateResult(double percent, boolean error){
        this.count++;
        this.percentage += percent;
        if(error)
            this.error++;
        return this;
    }
    
    public void print(int totalEvaluatedInstances){
        double average = percentage/count;        
        double success = ((count - error)/(double)count)*100;
        System.out.println("----- " + targetValue +" ------");        
        System.out.println("Total:" + count);
        System.out.println ("Promedio Total:" +((double)count/totalEvaluatedInstances)*100 + "%");
        System.out.println("Porcentaje de Aciertos:" + success + "%");
        System.out.println("Porcentaje Promedio:" + average + "%");
        System.out.println("");
    }
    
}
