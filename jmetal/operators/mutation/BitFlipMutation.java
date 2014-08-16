//  BitFlipMutation.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.SignatureIntSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.problems.SignatureAssignment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solution as a single encodings.variable.
 */
public class BitFlipMutation extends Mutation {
  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
      BinaryRealSolutionType.class,
      IntSolutionType.class,
      SignatureIntSolutionType.class) ;

  private Double mutationProbability_ = null ;
  
	/**
	 * Constructor
	 * Creates a new instance of the Bit Flip mutation operator
	 */
	public BitFlipMutation(HashMap<String, Object> parameters) {
		super(parameters) ;
  	if (parameters.get("probability") != null)
  		mutationProbability_ = (Double) parameters.get("probability") ;  		
	} // BitFlipMutation

	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public void doMutation(double probability, Solution solution) throws JMException {
		try {
			if ((solution.getType().getClass() == BinarySolutionType.class) ||
					(solution.getType().getClass() == BinaryRealSolutionType.class)) {
				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					for (int j = 0; j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
						if (PseudoRandom.randDouble() < probability) {
							((Binary) solution.getDecisionVariables()[i]).bits_.flip(j);
						}
					}
				}

				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					((Binary) solution.getDecisionVariables()[i]).decode();
				}
			} // if
			else { // Integer representation
				for (int i = 0; i < solution.getDecisionVariables().length; i++)
					if (PseudoRandom.randDouble() < probability) {
						int first_objetive = 0;//PseudoRandom.randInt(0,1);
						if(first_objetive == 0){
							//debo ir a conocer todas las materias del alumno i mod cantidad_alumnos
							int cantidad_alumnos = solution.getDecisionVariables().length/4;
							int alumno = i % cantidad_alumnos;
							int random_signature = (int)solution.getDecisionVariables()[i].getValue();
							
						    HashMap<Integer,double[]> materias_probabilidades = ((SignatureAssignment)solution.getProblem()).getMateria_probabilidades();
							
							//obtengo los codigos del alumno para que no se repitan en la mutacion
							int[] codigos_alumno = new int[4];
							for (int j=0; j<4; j++){
								codigos_alumno[j] = (int)solution.getDecisionVariables()[alumno+(cantidad_alumnos*j)].getValue();  
							}
							
							//para la materia i del alumno la cambio por una de mejor preferencia
							int materia = (int)solution.getDecisionVariables()[i].getValue();
							
							//si la materia es > 0 entonces es una materia valida. La voy a buscar y ver la probabilidad
							//en los datos
							if(materia>0){						
								//encuentro alguna materia que tenga mejor preferencia
								double preferencia_materia = materias_probabilidades.get(materia)[alumno];
								double preferencia_nueva = -1;
								if (preferencia_materia<0.7){
									int[] signatureCodes = ((SignatureAssignment)solution.getProblem()).getSignatureCodes_();
									int aux;
									while(preferencia_nueva<preferencia_materia){
										aux = PseudoRandom.randInt(0,signatureCodes.length-1);
										random_signature = signatureCodes[aux];
										int mat = random_signature;
										if ((random_signature != materia) && (!Arrays.asList(codigos_alumno).contains(mat))){
											preferencia_nueva = materias_probabilidades.get(random_signature)[alumno];
										}
									}
								}		
							}else{
								//elijo la mejor de las posibles soluciones para el alumno
								int[] signatureCodes = ((SignatureAssignment)solution.getProblem()).getSignatureCodes_();
								int iter = 0;
								random_signature = signatureCodes[0];
								
								double preferencia_nueva = -1;
								double preferencia_iterator;
								while(iter<signatureCodes.length){
									preferencia_iterator = materias_probabilidades.get(signatureCodes[iter])[alumno];
									if (preferencia_nueva <= preferencia_iterator){
										preferencia_nueva = preferencia_iterator;
										random_signature = signatureCodes[iter];
									}
									iter++;
								}
							}
							int materia_nueva = random_signature;
							solution.getDecisionVariables()[i].setValue(materia_nueva);
						}
//						else{
//							HashMap<Integer,Integer> cant_alumnos_por_materia = new HashMap<Integer,Integer>();
//							int codigo_materia;
//							for (int iterator=0; iterator< solution.getDecisionVariables().length; iterator++){
//								codigo_materia = (int)solution.getDecisionVariables()[iterator].getValue();
//								if(codigo_materia!=0){
//									if(cant_alumnos_por_materia.get(codigo_materia)==null)
//										cant_alumnos_por_materia.put(codigo_materia, 1);
//									else
//										cant_alumnos_por_materia.put(codigo_materia, cant_alumnos_por_materia.get(codigo_materia)+1);	
//								}
//							}// for
//							
//							int [] signatureCodes = ((SignatureAssignment)solution.getProblem()).getSignatureCodes_();
//							int codigo_min = 0;
//							int min = 100000;
//							for( int key : signatureCodes){
//								if(cant_alumnos_por_materia.get(key)== null){
//									codigo_min = key;
//									break;
//								}
//								else if(cant_alumnos_por_materia.get(key)< min){
//									min = cant_alumnos_por_materia.get(key);
//									codigo_min = key;
//								}//if
//							}//for
//							solution.getDecisionVariables()[i].setValue(codigo_min);
//						}// if de seleccion de objetivo
//						
						
					} // if
			} // else
		} catch (ClassCastException e1) {
			Configuration.logger_.severe("BitFlipMutation.doMutation: " +
					"ClassCastException error" + e1.getMessage());
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".doMutation()");
		}
	} // doMutation

	/**
	 * Executes the operation
	 * @param object An object containing a solution to mutate
	 * @return An object containing the mutated solution
	 * @throws JMException 
	 */
	public Object execute(Object object) throws JMException {
		Solution solution = (Solution) object;
		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if 

		doMutation(mutationProbability_, solution);
		return solution;
	} // execute
} // BitFlipMutation
