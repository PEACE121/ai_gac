package gac.constraintNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;


public class NashornScriptEngine
{
	private static NashornScriptEngine	instance;
	private final ScriptEngine				engine;
	private EEvaluationType					evalType	= EEvaluationType.NONO_HACK;
	
	
	private NashornScriptEngine()
	{
		engine = new ScriptEngineManager().getEngineByName("nashorn");
	}
	
	
	public static NashornScriptEngine getInstance()
	{
		if (instance == null)
		{
			instance = new NashornScriptEngine();
		}
		return instance;
	}
	
	
	private void setGlobalVariables(Map<String, Integer> variableAssignments)
	{
		Bindings bindings = new SimpleBindings();
		for (Entry<String, Integer> variable : variableAssignments.entrySet())
		{
			bindings.put(variable.getKey(), variable.getValue());
		}
		engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
	}
	
	
	private static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}
	
	
	public boolean eval(Map<String, Integer> variableAssignments, String canonicalFormulation)
	{
		switch (evalType)
		{
			case GRAPH_COLORING_HACK:
				// COLORING HACK
				List<Integer> elem = new ArrayList<Integer>();
				for (Integer i : variableAssignments.values())
				{
					elem.add(i);
				}
				return !elem.get(0).equals(elem.get(1));
			case NONO_HACK:
			case MAX_FLOW_HACK:
			case MAX_FLOW_SHORTER_CONSTRAINTS_HACK:
				
				// FlowProblem Hack
				String[] andFormulas = { canonicalFormulation };
				if (canonicalFormulation.contains("||"))
				{
					andFormulas = canonicalFormulation.split(" \\|\\| ");
				}
				for (String andFormula : andFormulas)
				{
					andFormula = andFormula.replaceAll("\\(", "");
					andFormula = andFormula.replaceAll("\\)", "");
					String[] comparisons = { andFormula };
					if (andFormula.contains(" && "))
					{
						comparisons = andFormula.split(" && ");
					}
					boolean comparisonCorrect = true;
					for (String comparison : comparisons)
					{
						boolean reverse = false;
						if (comparison.substring(0, 1).equals("!"))
						{
							reverse = true;
							comparison = comparison.substring(1);
						}
						String[] variables;
						if (comparison.contains("!="))
						{
							variables = comparison.split("!=");
							variables[0] = variables[0].replaceAll(" ", "");
							variables[1] = variables[1].replaceAll(" ", "");
							if (isNumeric(variables[1]))
							{
								if ((variableAssignments.get(variables[0]) + "").equals(variables[1]))
								{
									comparisonCorrect = false;
								}
							} else
							{
								if (variableAssignments.get(variables[0] + "").equals(variableAssignments.get(variables[1])))
								{
									comparisonCorrect = false;
								}
							}
						} else
						{
							variables = comparison.split("==");
							
							variables[0] = variables[0].replaceAll(" ", "");
							variables[1] = variables[1].replaceAll(" ", "");
							if (isNumeric(variables[1]))
							{
								if (!variableAssignments.get(variables[0] + "").equals(variables[1]))
								{
									comparisonCorrect = false;
								}
							} else
							{
								if (!variableAssignments.get(variables[0] + "").equals(
										variableAssignments.get(variables[1] + "")))
								{
									comparisonCorrect = false;
								}
							}
						}
						
						if (reverse)
						{
							comparisonCorrect = !comparisonCorrect;
						}
					}
					if (comparisonCorrect)
					{
						return true;
					}
				}
				return false;
			case NASHORN:
				setGlobalVariables(variableAssignments);
				try
				{
					return (boolean) engine.eval(canonicalFormulation);
				} catch (ClassCastException e)
				{
					System.out.println("The constraint '" + canonicalFormulation
							+ "' cannot be parsed. It did not returned a boolean! \n");
					e.printStackTrace();
					return false;
				} catch (ScriptException e)
				{
					System.out.println("The constraint '" + canonicalFormulation + "' cannot be parsed. \n");
					e.printStackTrace();
					return false;
				}
			default:
				return false;
		}
	}
	
	
	/**
	 * @return the evalType
	 */
	public EEvaluationType getEvalType()
	{
		return evalType;
	}
	
	
	/**
	 * @param evalType the evalType to set
	 */
	public void setEvalType(EEvaluationType evalType)
	{
		this.evalType = evalType;
	}
	
	
}
