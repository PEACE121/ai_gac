package gac.instances;

import gac.IDomainAttribute;
import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CI
{
	private final Constraint	consInCNET;
	private final List<VI>		variables;
	
	
	public CI(CI oldCI, Map<Variable, VI> newVIs)
	{
		this.consInCNET = oldCI.consInCNET;
		this.variables = new LinkedList<VI>();
		for (VI oldVI : oldCI.getVIs())
		{
			variables.add(newVIs.get(oldVI.getVarInCNET()));
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
		this.variables = variables;
	}
	
	
	public boolean revise(VI x)
	{
		List<IterationStatus> status = new LinkedList<IterationStatus>();
		for (VI notX : variables)
		{
			if (!notX.equals(x))
			{
				if (notX.getDomain().size() > 0)
				{
					status.add(new IterationStatus(notX, 0, notX.getDomain().size()));
				} else
				{
					return false;
				}
			}
		}
		
		List<IDomainAttribute> toDeleteFromX = new LinkedList<IDomainAttribute>();
		for (IDomainAttribute dom : x.getDomain())
		{
			for (IterationStatus stat : status)
			{
				stat.setPos(0);
			}
			boolean delete = true;
			while (true)
			{
				Map<String, Integer> varAssign = new HashMap<String, Integer>();
				varAssign.put(x.getVarInCNET().getName(), dom.getNumericalRepresentation());
				boolean inc = false;
				for (IterationStatus stat : status)
				{
					varAssign.put(stat.getVi().getVarInCNET().getName(), stat.getVi().getDomain().get(stat.getPos())
							.getNumericalRepresentation());
					if (!inc && stat.getPos() + 1 != stat.getLength())
					{
						stat.setPos(stat.getPos() + 1);
						inc = true;
					}
				}
				if (consInCNET.eval(varAssign))
				{
					delete = false;
					break;
				}
				if (!inc)
				{
					break;
				}
			}
			if (delete)
			{
				toDeleteFromX.add(dom);
			}
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
		for (VI vi : variables)
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
		return variables;
	}
	
}
