package gac;

import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.VI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astarframework.IState;


public class GACState implements IState
{
	private final Map<Variable, VI>	vis;
	private final List<CI>				cis;
	
	
	public GACState(GACState old)
	{
		this.vis = new HashMap<Variable, VI>();
		for (VI vi : old.getVis().values())
		{
			vis.put(vi.getVarInCNET(), new VI(vi));
		}
		
		this.cis = new LinkedList<CI>();
		for (CI ci : old.getCis())
		{
			cis.add(new CI(ci, vis));
		}
	}
	
	
	/**
	 * @param vis
	 * @param cis
	 */
	public GACState(Map<Variable, VI> vis, List<CI> cis)
	{
		super();
		this.vis = vis;
		this.cis = cis;
	}
	
	
	@Override
	public boolean isTheSame(IState object)
	{
		GACState toCompare = (GACState) object;
		if (vis.size() != toCompare.getVis().size())
		{
			System.out.println("Compare types different!!!");
			return false;
		}
		for (VI vi : vis.values())
		{
			if (toCompare.getVis().get(vi.getVarInCNET()).getDomain().size() != vi.getDomain().size())
			{
				return false;
			}
			for (IDomainAttribute dom : vi.getDomain())
			{
				if (!toCompare.getVis().get(vi.getVarInCNET()).getDomain().contains(dom))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	
	public boolean isStillSolvable()
	{
		for (VI vi : vis.values())
		{
			if (vi.getDomain().size() == 0)
			{
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * @return the vis
	 */
	public Map<Variable, VI> getVis()
	{
		return vis;
	}
	
	
	/**
	 * @return the cis
	 */
	public List<CI> getCis()
	{
		return cis;
	}
	
	
}
