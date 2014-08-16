package jmetal.operators.corrective;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * Class implementing a factory for Mutation objects.
 */
public class CorrectiveFactory {
  
  /**
   * Gets a crossover operator through its name.
   * @param name of the operator
   * @return the operator
   * @throws JMException 
   */
  public static Corrective getCorrectiveOperator(String name, HashMap parameters) throws JMException{
 
    if (name.equalsIgnoreCase("SignatureCorrective"))
      return new SignatureCorrective(parameters);
    else
    {
      Configuration.logger_.severe("Operator '" + name + "' not found ");
      Class cls = java.lang.String.class;
      String name2 = cls.getName() ;    
      throw new JMException("Exception in " + name2 + ".getCorrectiveOperator()") ;
    }        
  } // getMutationOperator
} // MutationFactory
