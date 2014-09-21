/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

/**
 *
 * @author Admin
 */
public class DistanceDT {
    public int row;
    public double distance;
    
    public DistanceDT(int row, double distance){
        this.distance = distance;
        this.row = row;
    }
    
    public void print(){
        System.out.println("[" + this.row + ", " + this.distance + "]");
    }
}
