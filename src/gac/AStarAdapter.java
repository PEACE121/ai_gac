package gac;

import gac.constraintNetwork.Constraint;
import gac.constraintNetwork.Variable;
import gac.instances.CI;
import gac.instances.VI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astarframework.IAreaOfApplication;
import astarframework.IState;


public class AStarAdapter implements IAreaOfApplication
{
	private GACState							gacStartState;
	
	private GACAlgorithm						gacAlgorithm;
	
	private final List<IGACObersvers>	observers			= new ArrayList<IGACObersvers>();
	
	protected ENextVariable					chooseNextComplex	= ENextVariable.COMPLEX2;
	
	private VI									lastFocal			= null;
	
	
	/**
	 * @return the lastFocal
	 */
	public VI getLastFocal()
	{
		return lastFocal;
	}
	
	
	/**
	 * @param gacStartState
	 */
	public AStarAdapter(List<Constraint> constraints, List<Variable> vars, ENextVariable next)
	{
		super();
		this.chooseNextComplex = next;
		
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
		this.gacAlgorithm = new GACAlgorithm(new GACState(vis, cis));
		this.gacStartState = gacAlgorithm.getState();
	}
	
	
	public AStarAdapter()
	{
	}
	
	
	public static List<VI> filterVariables(Map<Variable, VI> vis, Collection<Variable> vars)
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
		gacAlgorithm.domainFilteringLoop();
	}
	
	
	@Override
	public int getHeuristic(IState state)
	{
		// simplest heuristic, just sum up the amount of domains of all variable instances
		GACState gacState = (GACState) state;
		int h = 0;
		for (VI vi : gacState.getVis().values())
		{
			h += vi.getDomain().size() - 1;
			// if ((vi.getDomain().size() - 1) != 0)
			// {
			// h += 1000;
			// }
		}
		// for (CI ci : gacState.getCis())
		// {
		// int diffDomains = 0;
		// for (VI vi : ci.getVIs())
		// {
		// diffDomains += (vi.getDomain().size() - 1);
		// }
		// if (diffDomains != 0)
		// {
		// h += 1;
		// }
		// }
		return h;
	}
	
	
	@Override
	public IState getStart()
	{
		return gacStartState;
	}
	
	
	@Override
	public boolean isSolution(IState state)
	{
		return (checkState((GACState) state) == 1) && isApplicationSolution((GACState) state);
	}
	
	
	public boolean isApplicationSolution(GACState state)
	{
		return true;
	}
	
	
	@Override
	public List<IState> generateAllSuccessors(IState state)
	{
		GACState gacState = (GACState) state;
		List<IState> successors = new LinkedList<IState>();
		
		int maxDegreeOfReduced = 0;
		int minDegreeOfFreedom = Integer.MAX_VALUE;
		
		int maxConstrainted = 0;
		VI nextVariable = null;
		
		if (chooseNextComplex.equals(ENextVariable.SIMPLE))
		{
			// approach: choose the maximal constrainted variable
			for (VI vi : gacState.getVis().values())
			{
				if (vi.getDomain().size() > 1)
				{
					int constraintNumber = 0;
					for (CI ci : gacState.getCis())
					{
						if (ci.getVIs().contains(vi))
						{
							constraintNumber++;
						}
					}
					if (constraintNumber > maxConstrainted)
					{
						nextVariable = vi;
						maxConstrainted = constraintNumber;
					}
				}
			}
		} else
		{
			
			// approach: for each variable: follow each constraint it belongs to, count the domains of the other variables
			// in this constraint (c1) or count the already reduced amount of domains (c2)
			for (VI vi : gacState.getVis().values())
			{
				if (vi.getDomain().size() > 1)
				{
					if (nextVariable == null)
					{
						nextVariable = vi;
					}
					int degreeLocal = 0;
					for (CI ci : gacState.getCis())
					{
						if (ci.getVIs().contains(vi))
						{
							for (VI relatedVi : ci.getVIs())
							{
								if (!relatedVi.equals(vi))
								{
									if (chooseNextComplex.equals(ENextVariable.COMPLEX1))
									{
										degreeLocal += relatedVi.getDomain().size() - 1;
									} else
									{
										degreeLocal += relatedVi.getVarInCNET().getFullDomainCopy().size()
												- relatedVi.getDomain().size();
									}
								}
							}
						}
					}
					// take minimum for degree of freedom and maximum for degree of reduced
					if ((chooseNextComplex.equals(ENextVariable.COMPLEX1) && degreeLocal < minDegreeOfFreedom)
							|| (chooseNextComplex.equals(ENextVariable.COMPLEX2) && degreeLocal > maxDegreeOfReduced))
					{
						minDegreeOfFreedom = degreeLocal;
						maxDegreeOfReduced = degreeLocal;
						nextVariable = vi;
					}
				}
			}
		}
		// for the selected variable, for each domain, add a successor which has only this single domain (assumption)
		if (nextVariable == null)
		{
			return successors;
		}
		System.out.println(nextVariable.getVarInCNET().getName());
		lastFocal = nextVariable;
		
		return generateSuccesorsOfVI(nextVariable, gacState);
	}
	
	
	protected List<IState> generateSuccesorsOfVI(VI vi, GACState gacState)
	{
		List<IState> successors = new LinkedList<IState>();
		for (int j = 0; j < vi.getDomain().size(); j++)
		{
			GACState newState = new GACState(gacState);
			IDomainAttribute newDom = newState.getVis().get(vi.getVarInCNET()).getDomain().get(j);
			List<IDomainAttribute> newDoms = new LinkedList<IDomainAttribute>();
			newDoms.add(newDom);
			newState.getVis().get(vi.getVarInCNET()).setDomain(newDoms);
			newState.setLastGuessed(newDom);
			newState.setLastGuessedVar(vi.getVarInCNET());
			gacAlgorithm.rerun(newState, newState.getVis().get(vi.getVarInCNET()));
			// only add the state if it is solvable! ignore states with contradictions, that are dead ends
			if (newState.isStillSolvable() && !isApplicationDeadEnd(newState))
			{
				successors.add(newState);
			}
			// inform(newState);
		}
		return successors;
	}
	
	
	/**
	 * to overwrite
	 * @return
	 */
	public boolean isApplicationDeadEnd(GACState state)
	{
		return false;
	}
	
	
	@Override
	public int cost(IState from, IState to)
	{
		return 0;
	}
	
	
	/**
	 * 
	 * @param state
	 * @return -1 state is inconsistent (either there is an empty domain or a constraint is not met)
	 *         0 consistent state but there are still domains which more than one value
	 *         1 solution state
	 */
	private int checkState(GACState state)
	{
		// test if there is an empty domain
		boolean solutionPossible = true;
		for (VI vi : state.getVis().values())
		{
			if (vi.getDomain().size() == 0)
			{
				System.out.println("Variables with an empty domain: >=1");
				return -1;
			} else if (vi.getDomain().size() > 1)
			{
				solutionPossible = false;
			}
		}
		// test if all constraints are fulfilled
		if (solutionPossible)
		{
			// check for consistency
			for (CI ci : state.getCis())
			{
				if (!ci.consistencyCheck())
				{
					System.out.println("Unsatisfied constraints: >=1");
					return -1;
				}
			}
			System.out.println("Variables with an empty domain: 0");
			System.out.println("Unsatisfied constraints: 0");
			return 1;
		}
		return 0;
	}
	
	
	public void register(IGACObersvers obs)
	{
		observers.add(obs);
		gacAlgorithm.register(obs);
	}
	
	
	public void inform(GACState x)
	{
		for (IGACObersvers obs : observers)
		{
			obs.update(x, false);
		}
	}
	
	
	/**
	 * @return the gacAlgorithm
	 */
	public GACAlgorithm getGacAlgorithm()
	{
		return gacAlgorithm;
	}
	
	
	/**
	 * @param gacStartState the gacStartState to set
	 */
	public void setGacStartState(GACState gacStartState)
	{
		this.gacStartState = gacStartState;
	}
	
	
	/**
	 * @param gacAlgorithm the gacAlgorithm to set
	 */
	public void setGacAlgorithm(GACAlgorithm gacAlgorithm)
	{
		this.gacAlgorithm = gacAlgorithm;
	}
	
	
	/**
	 * @param chooseNextComplex the chooseNextComplex to set
	 */
	public void setChooseNextComplex(ENextVariable chooseNextComplex)
	{
		this.chooseNextComplex = chooseNextComplex;
	}
	
	
}
