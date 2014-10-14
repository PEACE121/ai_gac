package gac;

import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.TodoRevise;
import gac.instances.VI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GACAlgorithm
{
	private final List<TodoRevise>		queue;
	
	
	private GACState							state;
	
	private final List<IGACObersvers>	observers	= new ArrayList<IGACObersvers>();
	
	
	/**
	 * initialization
	 * @param constraints
	 * @param vars
	 */
	public GACAlgorithm(List<Constraint> constraints, List<Variable> vars)
	{
		queue = new LinkedList<TodoRevise>();
		
		// Create cis and vis
		Map<Variable, VI> vis = new HashMap<Variable, VI>();
		for (Variable var : vars)
		{
			vis.put(var, new VI(var, var.getFullDomainCopy()));
		}
		List<CI> cis = new LinkedList<CI>();
		for (Constraint constraint : constraints)
		{
			CI ci = new CI(constraint, filterVariables(vis, constraint.getVariables().values()));
			cis.add(ci);
		}
		
		state = new GACState(vis, cis);
		
		// add to queue
		for (CI ci : cis)
		{
			for (VI vi : ci.getVIs())
			{
				queue.add(new TodoRevise(ci, vi));
			}
		}
	}
	
	
	private List<VI> filterVariables(Map<Variable, VI> vis, Collection<Variable> vars)
	{
		List<VI> filtered = new LinkedList<VI>();
		for (Variable var : vars)
		{
			filtered.add(vis.get(var));
		}
		return filtered;
	}
	
	
	public void domainFilteringLoop()
	{
		while (!queue.isEmpty())
		{
			TodoRevise todoRevise = queue.get(0);
			queue.remove(0);
			boolean domainReduced = todoRevise.getCi().revise(todoRevise.getVi());
			VI x = todoRevise.getVi();
			if (domainReduced)
			{
				inform(state);
				for (CI ci : state.getCis())
				{
					// if the constraint contains the focal variable x and ci does not equal to the just checked constraint
					if (ci.getConsInCNET().getVariables().containsValue(x.getVarInCNET()) && !ci.equals(todoRevise.getCi()))
					{
						for (VI vi : ci.getVIs())
						{
							// take all variable instances except x
							if (!vi.equals(x))
							{
								// and push them as TodoRevise onto the queue
								queue.add(new TodoRevise(ci, vi));
							}
						}
					}
				}
			}
		}
	}
	
	
	public void rerun(GACState gacState, VI x)
	{
		this.state = gacState;
		for (CI ci : gacState.getCis())
		{
			// if the constraint contains the focal variable x
			if (ci.getConsInCNET().getVariables().containsValue(x.getVarInCNET()))
			{
				for (VI vi : ci.getVIs())
				{
					// take all variable instances except x
					if (!vi.equals(x))
					{
						// and push them as TodoRevise onto the queue
						queue.add(new TodoRevise(ci, vi));
					}
				}
			}
		}
		domainFilteringLoop();
	}
	
	
	/**
	 * @return the state
	 */
	public GACState getState()
	{
		return state;
	}
	
	
	/**
	 * @param state the state to set
	 */
	public void setState(GACState state)
	{
		this.state = state;
	}
	
	
	public void register(IGACObersvers obs)
	{
		observers.add(obs);
	}
	
	
	public void inform(GACState x)
	{
		for (IGACObersvers obs : observers)
		{
			obs.update(x, false);
		}
	}
}
