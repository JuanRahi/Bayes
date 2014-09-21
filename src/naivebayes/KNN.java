/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Admin
 */
public class KNN {
    enum columnType {UNIQUE, NUMERIC, TEXT, RANGE};
    columnType[] typeOfColumn=null;
    public static int DATA_SIZE;
    public static int TARGET_SIZE;
    public static int ATTRIBUTE_SIZE;
    
    public columnType[] getColumnTypes(){
        if(typeOfColumn==null)
        {
            typeOfColumn = new columnType[ATTRIBUTE_SIZE];
            for(int i=0; i<22; i++){
                switch (i){
                    case 0: case 1:
                        typeOfColumn[i] = columnType.UNIQUE;
                        break;
                    case 9: case 12: case 13: case 14: case 15: case 16: case 17: case 21:  
                        typeOfColumn[i] = columnType.NUMERIC;
                        break;
                    case 4: case 5:
                        typeOfColumn[i] = columnType.RANGE;
                        break;
                }
            }
            for (int i=0; i<ATTRIBUTE_SIZE; i++)
                if(typeOfColumn[i] == null)
                    typeOfColumn[i] = columnType.TEXT;
        }
        return typeOfColumn;
    }   
    
    public String [][] getDataFromFile(BufferedReader reader, String separator, int[] max, int[] min, LinkedList<LinkedList<String>> uniques) throws FileNotFoundException, IOException{                           
        String line = reader.readLine();
        String [] lineValues = line.split(separator);
        String [][] allValues = new String[DATA_SIZE][ATTRIBUTE_SIZE];
        typeOfColumn = getColumnTypes();
        int lineCounter = 0;
        while((line = reader.readLine())!= null){
            lineValues = line.split(separator);            
            for(int column=0; column<ATTRIBUTE_SIZE; column++){
                //Si el valor es numérico, guardamos el maximo y minimo
                if(typeOfColumn[column].equals(columnType.NUMERIC)) {
                    if(Integer.parseInt(lineValues[column]) > max[column]) {
                        max[column] = Integer.parseInt(lineValues[column]);
                    }
                    if(Integer.parseInt(lineValues[column]) < min[column]) {
                       min[column] = Integer.parseInt(lineValues[column]);
                    }
                }
                
                if(typeOfColumn[column].equals(columnType.RANGE)){
                    if(lineValues[column].startsWith("[")){
                        int indexOfHiphen = lineValues[column].indexOf("-");
                        int value = Integer.parseInt(lineValues[column].substring(1,indexOfHiphen));
                        if(value > max[column]) {
                            max[column] = value;
                        }
                        if(value < min[column]) {
                            min[column] = value;
                        }
                    }
                    else if(lineValues[column].startsWith(">")){
                        int value = Integer.parseInt(lineValues[column].substring(1));
                        if(value > max[column]) {
                            max[column] = value;
                        }
                        if(value < min[column]) {
                            min[column] = value;
                        }
                    }
                }
                    
                //Guardamos el valor en la lista
                allValues[lineCounter][column] = lineValues[column];
                //Si es unico, lo guardamos en la lista de unicos para la columna
                LinkedList currentColumn = uniques.get(column);
                if(!currentColumn.contains(lineValues[column]))
                    currentColumn.add(lineValues[column]);
                
            }
            //System.out.println("Linea " + lineCounter + " completada");
            lineCounter++;                       
        }
        return allValues;
    }
    
    public double[] normalizeValues(String [] values, LinkedList<LinkedList<String>> uniques, int[]max, int[]min){        
        LinkedList currentColumnInUniques;
        double [] normalizedLine = new double[ATTRIBUTE_SIZE];
        for(int column = 0; column < ATTRIBUTE_SIZE; column++){
            currentColumnInUniques = uniques.get(column);
            columnType currentColumnType = typeOfColumn[column];
            //Si la columna es numerica, normalizamos a valores entre 0 y 1 para luego calcular distancias
            switch(currentColumnType){
                case NUMERIC:
                    normalizedLine[column] = (Integer.parseInt(values[column]) - min[column])/(max[column]-min[column]);
                    break;
                case RANGE:
                    //Posibles formatos de rangos son [a-b), ?, >x
                    //Si es ?, marcamos el valor normalizado como -1 para diferenciarlo
                    if(values[column].equals("?"))
                        normalizedLine[column] = -1;
                    //Si es un rango [a-b), tomamos a y lo normalizamos
                    else if(values[column].startsWith("[")){
                        int indexOfHiphen = values[column].indexOf("-");
                        normalizedLine[column] = Double.parseDouble(values[column].substring(1,indexOfHiphen));
                        normalizedLine[column] = (normalizedLine[column] - min[column])/(max[column]-min[column]);
                    }
                    //Si es del tipo >x
                    else
                        normalizedLine[column] = (Integer.parseInt(values[column].substring(1)) - min[column])/(max[column]-min[column]);
                    break;
                //En este caso no usamos numeros del 0 al 1, sino el indice del valor unico en uniques
                case TEXT:
                    normalizedLine[column] = currentColumnInUniques.indexOf(values[column]);
                    break;
                //Si son valores unicos, simplemente los guardamos para su comparacion luego
                case UNIQUE:
                    normalizedLine[column]= Double.parseDouble(values[column]);
            }
        }
        return normalizedLine;
    }
    
    public LinkedList<LinkedList<String>> normalizeTrainingValues(double[][] normalizedValues, double[][] normalizedTargets, String path, String separator, String [][] targets) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        //Obtener todos los nombres de atributos. El ultimo asumimos es el target
        String [] lineValues = line.split(separator);                   
        
        //Creamos una lista para cada atributo, donde guardamos los valores unicos
        LinkedList<LinkedList<String>> uniques = new LinkedList();
        for(int i= 0; i < ATTRIBUTE_SIZE; i++)
            uniques.add(new LinkedList<String>()); 
        
        int [] max = new int[ATTRIBUTE_SIZE];
        int [] min = new int[ATTRIBUTE_SIZE];
        
        System.out.println("Obteniendo attributos unicos...");
        String [][] allValues = getDataFromFile(reader, separator, max, min, uniques);
        //Una vez obtenida toda la data necesaria, normalizamos los valores.
        
        System.out.println("Normalizano data...");
        for(int row = 0; row < DATA_SIZE; row++){
            normalizedValues[row] = normalizeValues(allValues[row], uniques, max, min);
            //System.out.println("Fila " + row + " normalizada");
        }             
        for(int row = 0; row < TARGET_SIZE; row++){
            normalizedTargets[row] = normalizeValues(targets[row], uniques, max, min);
        }
        return uniques;
    }
        
    public LinkedList<DistanceDT> run(double[][] normalizedValues, double[] normalizedTarget, LinkedList<LinkedList<String>> uniques){
        LinkedList<DistanceDT> distances = new LinkedList<>();
        double currentDistance, tmp;
        for(int row = 0; row < DATA_SIZE; row++){
            currentDistance = 0;
            //Si encontramos una linea con igual valor de identificar unico, salimos, ya que tenemos coincidencia exacta
            if(normalizedTarget[0] == normalizedValues[row][0]){
                distances.add(new DistanceDT(row, currentDistance, (int)normalizedValues[row][ATTRIBUTE_SIZE - 1]));
                break;                
            }             
            for(int column =2; column < ATTRIBUTE_SIZE; column++){
                //No sumamos distancias si alguno de los valores es '?'
                if(!((normalizedValues[row][column] == (double)-1) || (normalizedTarget[column] == (double)-1))) {
                    switch(typeOfColumn[column]){
                        case NUMERIC: case RANGE:
                            if(normalizedValues[row][column] != (double)-1){
                                tmp = normalizedValues[row][column] - normalizedTarget[column];
                                //Para evitar posibles distancias negativas
                                currentDistance += (tmp < 0) ? (tmp * -1) : tmp;                        
                            }
                            break;
                            //En el caso de ser de tipo texto, la distancia es 0 en coincidencia, sino 1/cantidadDeValoresDistintosParaElAtributo                        
                        case TEXT:
                            tmp = (Double.compare(normalizedValues[row][column],normalizedTarget[column]) == 0) ? (0) : (double)1/uniques.get(column).size();
                            currentDistance += tmp;
                    }
                }
            }
            //Si se trata del mismo paciente, achicamos la distncia un 20% para darle mas peso
            if(normalizedTarget[1] == normalizedValues[row][1]){
                currentDistance *= 0.8;
            }
            distances.add(new DistanceDT(row, currentDistance, (int)normalizedValues[row][ATTRIBUTE_SIZE - 1]));
        }
        
        //Ordernamos la coleccion por distancia
        Collections.sort(distances, new Comparator() {

            @Override
            public int compare(Object t, Object t1) {
                return Double.compare(((DistanceDT)t).distance, ((DistanceDT)t1).distance);
            }
        });
                       
        return distances;
    }    
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String dataPath = "C:\\diabetic_data.csv";         
        String targetPath = "C:\\target.csv";
        String splitter = ",";
        int k = 5;
        
        //Obtener el total de lineas del archivo de entrenamiento
        BufferedReader reader = new BufferedReader(new FileReader(dataPath));        
        String line = reader.readLine();        
        //Quitamos uno para dejar fuera del calculo al atributo a predecir
        ATTRIBUTE_SIZE = line.split(splitter).length;
        int lineCount = 1;
        while((line = reader.readLine()) != null){
            lineCount++;
        }        
        DATA_SIZE = lineCount - 2;        
        
        System.out.println("Primer pasada realizada. DATA_SIZE=" + DATA_SIZE);
        //Obtener el total de lsineas del archivo de targets
        reader = new BufferedReader(new FileReader(targetPath));
        lineCount = 0;
        while(reader.readLine() != null){
            lineCount++;
        }
        TARGET_SIZE = lineCount;
        System.out.println("Archivo target leadio. TARGET_SIZE=" + TARGET_SIZE);
        
        //Guardamos los targets en un String[]
        String[][] targets = new String[TARGET_SIZE][ATTRIBUTE_SIZE];        
        reader = new BufferedReader(new FileReader(targetPath));
        lineCount = 0;
        while((line = reader.readLine()) != null){
            targets[lineCount] = line.split(splitter);
            lineCount++;
        }       
        System.out.println("Archivo target procesado");
        double[][] normalizedValues = new double[DATA_SIZE][ATTRIBUTE_SIZE];
        double[][] normalizedTargets = new double[TARGET_SIZE][ATTRIBUTE_SIZE];;
        
        KNN knn = new KNN();
        LinkedList<LinkedList<String>> uniques = knn.normalizeTrainingValues(normalizedValues, normalizedTargets, dataPath, splitter, targets);
        LinkedList<DistanceDT> distances = null;
        System.out.println("Data normalizada");
        Iterator it;
        for(int i = 0; i < TARGET_SIZE; i++){        
             distances = knn.run(normalizedValues, normalizedTargets[i], uniques);
             it = distances.iterator();
             for(int j = 0; j<k; j++){
                 //Calcular resultado final!
             }
             
        }
    }
}
