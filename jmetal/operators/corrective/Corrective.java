package jmetal.operators.corrective;

import jmetal.core.Operator;

import java.util.HashMap;

/**
 * This class represents the super class of all the crossover operators
 */
public abstract class Corrective extends Operator {

	public Corrective(HashMap<String, Object> parameters) {
	  super(parameters);
  }
} // Crossover
