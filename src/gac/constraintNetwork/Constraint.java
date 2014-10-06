package gac.constraintNetwork;


import gac.instances.VI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Constraint
{
	private final String						canonicalFormulation;
	private final Map<String, Variable>	variables;
	
	
	/**
	 * @param canonicalFormulation
	 */
	public Constraint(String canonicalFormulation, Map<String, Variable> variables)
	{
		super();
		this.canonicalFormulation = canonicalFormulation;
		this.variables = variables;
	}
	
	
	public boolean eval(Map<String, Integer> variableAssignments)
	{
		for (String key : variableAssignments.keySet())
		{
			if (!variables.containsKey(key))
			{
				System.out.println("The variables passed do not match to this constraint");
				return false;
			}
		}
		return NashornScriptEngine.getInstance().eval(variableAssignments, canonicalFormulation);
	}
	
	
	/**
	 * @return the canonicalFormulation
	 */
	public String getCanonicalFormulation()
	{
		return canonicalFormulation;
	}
	
	
	/**
	 * @return the variables
	 */
	public Map<String, Variable> getVariables()
	{
		return variables;
	}
	
	
	/**
	 * 
	 * @return a variable instance for every variable which is related to the constraint together with its full domain
	 */
	public List<VI> getFullVIs()
	{
		List<VI> vis = new LinkedList<VI>();
		for (Variable var : variables.values())
		{
			vis.add(new VI(var, var.getFullDomainCopy()));
		}
		return vis;
	}
	
	
	// ONLY FOR TESTING
	public static void main(String[] args)
	{
		Map<String, Variable> vars = new HashMap<String, Variable>();
		vars.put("x", new Variable("x", null));
		vars.put("y", new Variable("y", null));
		vars.put("z", new Variable("z", null));
		Constraint cons = new Constraint("x != y", vars);
		Map<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("x", 6);
		variables.put("y", 6);
		variables.put("z", 12);
		if (cons.eval(variables))
		{
			System.out.println("true");
		} else
		{
			System.out.println("false");
		}
	}
}
