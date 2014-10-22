package gac;

import gac.instances.CI;
import gac.instances.TodoRevise;
import gac.instances.VI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


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
	public GACAlgorithm(GACState state)
	{
		this.state = state;
		queue = new LinkedList<TodoRevise>();
		
		// add to queue
		for (CI ci : state.getCis())
		{
			for (VI vi : ci.getVIs())
			{
				queue.add(new TodoRevise(ci, vi));
			}
		}
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
				if (x.getDomain().size() == 1)
				{
					state.setLastGuessed(x.getDomain().get(0));
					state.setLastGuessedVar(x.getVarInCNET());
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
