package gac.instances;

import gac.IDomainAttribute;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CI
{
	private final Constraint	consInCNET;
	private final List<VI>		vis;
	
	
	public CI(CI oldCI, Map<Variable, VI> newVIs)
	{
		this.consInCNET = oldCI.consInCNET;
		this.vis = new LinkedList<VI>();
		for (VI oldVI : oldCI.getVIs())
		{
			vis.add(newVIs.get(oldVI.getVarInCNET()));
		}
	}
	
	
	/**
	 * @param consInCNET
	 * @param variables
	 */
	public CI(Constraint consInCNET, List<VI> variables)
	{
		super();
		this.consInCNET = consInCNET;
		this.vis = variables;
	}
	
	
	public boolean revise(VI x)
	{
		List<IDomainAttribute> toDeleteFromX = new LinkedList<IDomainAttribute>();
		List<VI> neighbours = new ArrayList<VI>();
		for (VI vi : vis)
		{
			if (!vi.equals(x))
			{
				neighbours.add(vi);
			}
		}
		int[][] allCombinations = createCombinations(x, neighbours);
		nextDom: for (IDomainAttribute dom : x.getDomain())
		{
			for (int i = 0; i < allCombinations.length; i++)
			{
				Map<String, Integer> varAssign = new HashMap<String, Integer>();
				varAssign.put(x.getVarInCNET().getName(), dom.getNumericalRepresentation());
				for (int j = 0; j < allCombinations[i].length; j++)
				{
					varAssign.put(neighbours.get(j).getVarInCNET().getName(), allCombinations[i][j]);
				}
				if (consInCNET.eval(varAssign))
				{
					continue nextDom;
				}
			}
			toDeleteFromX.add(dom);
		}
		x.getDomain().removeAll(toDeleteFromX);
		return !(toDeleteFromX.size() == 0);
	}
	
	
	/**
	 * assumes that all VIs have only one domain left
	 * @return
	 */
	public boolean consistencyCheck()
	{
		Map<String, Integer> variableAssignments = new HashMap<String, Integer>();
		for (VI vi : vis)
		{
			variableAssignments.put(vi.getVarInCNET().getName(), vi.getDomain().get(0).getNumericalRepresentation());
		}
		return consInCNET.eval(variableAssignments);
	}
	
	
	/**
	 * @return the consInCNET
	 */
	public Constraint getConsInCNET()
	{
		return consInCNET;
	}
	
	
	/**
	 * @return the variables
	 */
	public List<VI> getVIs()
	{
		return vis;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CI [vis=" + vis + ", cons=" + consInCNET + "]";
	}
	
	
	protected static int[][] createCombinations(VI X, List<VI> neighbors)
	{
		int total_size = 1;
		for (int i = 0; i < neighbors.size(); i++)
		{
			VI Y = neighbors.get(i);
			total_size *= Y.getDomain().size();
		}
		
		int[][] combinations = new int[total_size][neighbors.size()];
		for (int c_i = 0; c_i < total_size; c_i++)
		{
			int y_i = 0;
			int y_size = 1;
			for (int i = 0; i < neighbors.size(); i++)
			{
				VI Y = neighbors.get(i);
				y_size *= Y.getDomain().size();
				int tmp = c_i / (total_size / y_size);
				int y_index = tmp % Y.getDomain().size();
				combinations[c_i][y_i] = Y.getDomain().get(y_index).getNumericalRepresentation();
				y_i++;
			}
		}
		return combinations;
	}
	
	
}
