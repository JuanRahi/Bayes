/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juan
 */
public class NaiveBayes {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String path = "diabetic_data.csv";            
            String separator = ",";     
            Algorithm naiveBayes = new Algorithm();        
            naiveBayes.run(path, separator);            
        } catch (IOException ex) {
            Logger.getLogger(NaiveBayes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
}
