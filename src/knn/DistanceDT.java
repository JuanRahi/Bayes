/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

/**
 *
 * @author Admin
 */
public class DistanceDT {
    public int row, targetValue;
    public double distance;
    
    public DistanceDT(int row, double distance, int targetValue){
        this.distance = distance;
        this.row = row;
        this.targetValue = targetValue;
    }
    
    public void print(){
        System.out.println("[" + this.row + ", " + this.distance + "]");
    }
}
