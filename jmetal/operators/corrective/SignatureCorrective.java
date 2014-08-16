package jmetal.operators.corrective;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.solutionType.SignatureIntSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.problems.SignatureAssignment;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SignatureCorrective extends Corrective{
  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(SignatureIntSolutionType.class) ;
  
  private Double mutationProbability_ = 1.0 ;

  /** 
   * Constructor
   */
  public SignatureCorrective(HashMap<String, Object> parameters) {    
  	super(parameters) ;
  	
//  	if (parameters.get("probability") != null)
//  		mutationProbability_ = (Double) parameters.get("probability") ;  		
  } // Constructor


  public void doCorrection(double probability, Solution solution) throws JMException {   
	    if (solution.getType().getClass() == SignatureIntSolutionType.class) {
    			
			int cantidad_alumnos = solution.getDecisionVariables().length/4;
		    HashMap<Integer,Integer> materia_creditos = ((SignatureAssignment)solution.getProblem()).getMateria_creditos();
		    HashMap<Integer,Integer> alumno_creditos = ((SignatureAssignment)solution.getProblem()).getAlumno_creditos(); 

			for (int i = 0; i < cantidad_alumnos ; i++){     				
				// recorro la solucion para obtener los codigos de materias asignadas hata el momento
				double[] codigos_alumno = new double[4];
				double[] creditos_alumno = new double[4]; 
				int creditos_totales_alumno = 0;
				
				//obtengo los codigos del alumno i
				for (int j=0; j<4; j++){
					codigos_alumno[j] = solution.getDecisionVariables()[i+(cantidad_alumnos*j)].getValue();  
				}
				
				
				for (int j=0; j<4; j++){
					if (codigos_alumno[j]!=0){
						double[] codigos_alumno_sin_j = new double[3];
						int zero_to_three = 0;
						for (int iter=0;iter<4;iter++){
							if (iter!=j){
								codigos_alumno_sin_j[zero_to_three]=codigos_alumno[iter];
								zero_to_three++;
							}    							
						}
						
						double codigo_iterator = codigos_alumno[j];
						while (este(codigos_alumno_sin_j,codigo_iterator)){
							int[] signatureCodes = ((SignatureAssignment)solution.getProblem()).getSignatureCodes_();
							int aux = PseudoRandom.randInt(0,signatureCodes.length-1);
							int random_signature = signatureCodes[aux];
							codigo_iterator = random_signature;
						}
						codigos_alumno[j] = codigo_iterator;
						solution.getDecisionVariables()[i+(cantidad_alumnos*j)].setValue(codigo_iterator);
					}    					 
				}  
				
				for (int j=0; j<4; j++){
					if (codigos_alumno[j]!= 0){
						creditos_alumno[j] = materia_creditos.get((int)codigos_alumno[j]);
					}else{
						creditos_alumno[j] = 0;
					}
					creditos_totales_alumno += creditos_alumno[j];
				}
				
				// mientras la cantidad de creditos hasta el momento sea mayor al maximo elegido por el alumno
				// quitarle una materia.
				
				//obtengo los creditos maximos para el alumno i
				int creditos_max_alumno = alumno_creditos.get(i);
				Random rand = new Random();
			    int k = rand.nextInt(4);
				while (creditos_totales_alumno > creditos_max_alumno){			
					solution.getDecisionVariables()[i+(cantidad_alumnos*k)].setValue(0);
					codigos_alumno[k] = 0;
					creditos_totales_alumno -= creditos_alumno[k];
					k = rand.nextInt(4);
				}
			}	    		
	    } // if
	    else  {
	      Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " +
	          ""+ solution.getDecisionVariables()[0].getVariableType());

	      Class cls = java.lang.String.class;
	      String name = cls.getName(); 
	      throw new JMException("Exception in " + name + ".doMutation()") ;
	    }
  } // doCorrection

  private boolean este(double[] codigos_alumno_sin_j, double codigo_iterator) {
	// TODO Auto-generated method stub
	boolean esta = false;
	int iterador=0;
	while (!esta && iterador<codigos_alumno_sin_j.length){
		esta = (codigo_iterator == codigos_alumno_sin_j[iterador]);
		iterador++;
	}
	return esta;
}


/**
   * Executes the operation
   * @param object An object containing the solution to mutate
   * @return an object containing the mutated solution
   * @throws JMException 
   */
  public Object execute(Object object) throws JMException {
    Solution solution = (Solution)object;
    
	if (!VALID_TYPES.contains(solution.getType().getClass())) {
		Configuration.logger_.severe("SwapMutation.execute: the solution " +
				"is not of the right type. The type should be 'Binary', " +
				"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

		Class cls = java.lang.String.class;
		String name = cls.getName();
		throw new JMException("Exception in " + name + ".execute()");
	} // if 

    
    this.doCorrection(mutationProbability_, solution);
    return solution;
  } // execute  
} // SwapMutation
