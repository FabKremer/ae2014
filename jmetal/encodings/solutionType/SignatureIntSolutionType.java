package jmetal.encodings.solutionType;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.SignatureInt;

/**
 * Class representing the solution type of solutions composed of Int variables 
 */
public class SignatureIntSolutionType extends SolutionType {
	
	/**
	 * Constructor
	 * @param problem  Problem to solve
	 */
	public SignatureIntSolutionType(Problem problem) {
		super(problem) ;
	} // Constructor

	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {
		Variable[] variables = new Variable[problem_.getNumberOfVariables()];

		for (int var = 0; var < problem_.getNumberOfVariables(); var++)
			variables[var] = new SignatureInt((int)problem_.getLowerLimit(var),
					(int)problem_.getUpperLimit(var));    

		return variables ;
	} // createVariables

} // IntSolutionType
