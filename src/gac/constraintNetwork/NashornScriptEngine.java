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
	
	private static boolean					HACK	= false;
	
	
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
	
	
	public boolean eval(Map<String, Integer> variableAssignments, String canonicalFormulation)
	{
		if (HACK)
		{
			List<Integer> elem = new ArrayList<Integer>();
			for (Integer i : variableAssignments.values())
			{
				elem.add(i);
			}
			return !elem.get(0).equals(elem.get(1));
		} else
		{
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
		}
	}
}
