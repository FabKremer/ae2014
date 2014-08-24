package jmetal.problems;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.SignatureIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem SignatureAssignment
 */
public class SignatureAssignment extends Problem { 
	
	private int[] signatureCodes_ = {1937,1918,5914,1628,1438,1444,1849,9999,1445,1331,1939,5915,1631,1835,1941,1749,1316,1328,1333,1450,1057,1767,1340,1758,1442,1766,1751,1740,1759,1933,1731,1449,1448};
    private HashMap<Integer,Integer> materia_creditos;
    private HashMap<Integer,double[]> materia_probabilidades;
    private HashMap<Integer,Integer> alumno_creditos; 
    
  /** 
   * Constructor.
   * Creates a default instance of the SignatureAssignment problem.
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
   */
  public SignatureAssignment(String solutionType) throws ClassNotFoundException {
    this(solutionType, 4);
  } // SignatureAssignment
  
  /** 
   * Constructor.
   * Creates a new instance of the AssignatureAssigment problem.
   * @param numberOfVariables Number of variables of the problem 
   * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal". 
   */
  public SignatureAssignment(String solutionType, Integer numberOfVariables) {
    numberOfVariables_   = numberOfVariables;
    numberOfObjectives_  = 2                            ;
    numberOfConstraints_ = 0                            ;
    problemName_         = "SignatureAssignment"        ;
    
    upperLimit_ = new double[numberOfVariables_] ;
    lowerLimit_ = new double[numberOfVariables_] ;
       
    for (int i = 0; i < numberOfVariables_; i++) {
      lowerLimit_[i] = 1057 ;
      upperLimit_[i] = 9999 ;
    } // for
        
    materia_creditos = new HashMap<Integer,Integer>();
    materia_probabilidades = new HashMap<Integer,double[]>();
    alumno_creditos = new HashMap<Integer,Integer>(); 
    initialize_hashes_data();
    
    if (solutionType.compareTo("SignatureInt") == 0)
    	solutionType_ = new SignatureIntSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }
    
  } // Signature Assignment
    

  private void initialize_hashes_data() {
	  try
		{
			FileInputStream file = new FileInputStream(new File("Planillas_AE.xlsx"));
	
			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
	
			//Get first/desired sheet from the workbook
			XSSFSheet codigos = workbook.getSheetAt(1); // materias y creditos
			XSSFSheet preferencias = workbook.getSheetAt(2); // preferencias de alumnos
			XSSFSheet creditos = workbook.getSheetAt(3); // creditos maximos por alumno

			int cantidad_alumnos = numberOfVariables_/4;
			
			//para todos los alumnos, inicializo el hash de cantidad maxima de materias
			int iterator = 0;
			while (creditos.getRow(iterator)!= null){
				alumno_creditos.put(iterator, (int)creditos.getRow(iterator).getCell(0).getNumericCellValue());
//				System.out.println(alumno_creditos.get(iterator));
				iterator++;

			}
			
			//para todas las materias, inicializo el hash que tengan que ver con las materias
			iterator = 0;
			while (codigos.getRow(iterator)!= null){
				int materia = (int)codigos.getRow(iterator).getCell(2).getNumericCellValue();
				materia_creditos.put(materia, (int)codigos.getRow(iterator).getCell(1).getNumericCellValue());
//				System.out.println(materia_creditos.get(materia));

				int iterator_alumnos = 0;
				double[] preferencias_alumno = new double[cantidad_alumnos];
				while(iterator_alumnos < cantidad_alumnos){
					preferencias_alumno[iterator_alumnos] = preferencias.getRow(iterator).getCell(iterator_alumnos).getNumericCellValue();
					iterator_alumnos++;
				}
				materia_probabilidades.put(materia, preferencias_alumno);
//				System.out.println(Arrays.toString(materia_probabilidades.get(materia)));

				iterator++;
			}			
			
			file.close();	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	  	
	
}

/** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */
  
  public void evaluate(Solution solution) throws JMException {
	double [] f = new double[numberOfObjectives_];
	int cantidad_alumnos = solution.getDecisionVariables().length/4;
	int codigo_alumno_j;
	int materia_credito;
	double materia_probabilidad;
	
	for (int i = 0; i < cantidad_alumnos ; i++){   
		for (int j=0; j<4; j++){
			codigo_alumno_j = (int)solution.getDecisionVariables()[i+(cantidad_alumnos*j)].getValue();
			if (codigo_alumno_j!= 0){
				materia_credito = materia_creditos.get(codigo_alumno_j);
				materia_probabilidad = materia_probabilidades.get(codigo_alumno_j)[i];
				f[0] += materia_probabilidad;
				
//				f[1] += /*(int)codigos.getRow(k).getCell(2).getNumericCellValue()*/ codigo_alumno_j;
			}
		}			
	}
	
	HashMap<Integer,Integer> alumnos_por_materia = new HashMap<Integer,Integer>();
	int codigo_alumno;
	for (int iterator=0; iterator< solution.getDecisionVariables().length; iterator++){
		codigo_alumno = (int)solution.getDecisionVariables()[iterator].getValue();
		if(codigo_alumno!=0){
			if(alumnos_por_materia.get(codigo_alumno)==null)
				alumnos_por_materia.put(codigo_alumno, 1);
			else
				alumnos_por_materia.put(codigo_alumno, alumnos_por_materia.get(codigo_alumno)+1);
		}
		
	}
	
	f[1]=0;
	int difference = 0;
	int limit = (solution.getDecisionVariables().length/4)/signatureCodes_.length;
	for(int iterator=0; iterator<signatureCodes_.length; iterator++){
		for(int iterator2=0; iterator2<signatureCodes_.length; iterator2++){
			if(iterator2 > iterator){
				
				int key = signatureCodes_[iterator];
				int key2 = signatureCodes_[iterator2];
				
				int alumnos_mat_1 = 0;
				int alumnos_mat_2 = 0;
				if(alumnos_por_materia.get(key)!=null)
					alumnos_mat_1 = alumnos_por_materia.get(key);
				if(alumnos_por_materia.get(key2)!=null)
					alumnos_mat_2 = alumnos_por_materia.get(key2);	
				difference =Math.abs(alumnos_mat_1 - alumnos_mat_2);
				if(difference <= limit/2)
					f[1]+=1;
			}
		}
	}
	
	// SignatrueAssignment is a maximization problem: make the inverse to minimize
    solution.setObjective(0,1000.0/f[0]);
    solution.setObjective(1,1000.0/f[1]);
    
  } // evaluate

public int[] getSignatureCodes_() {
	return signatureCodes_;
}

public void setSignatureCodes_(int[] signatureCodes_) {
	this.signatureCodes_ = signatureCodes_;
}

public HashMap<Integer,Integer> getMateria_creditos() {
	return materia_creditos;
}

public HashMap<Integer,double[]> getMateria_probabilidades() {
	return materia_probabilidades;
}

public HashMap<Integer,Integer> getAlumno_creditos() {
	return alumno_creditos;
}

} // SignatureAssignment
