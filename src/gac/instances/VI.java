package gac.instances;

import gac.IDomainAttribute;
import gac.constraintNetwork.Variable;

import java.util.LinkedList;
import java.util.List;


public class VI
{
	private final Variable				varInCNET;
	private List<IDomainAttribute>	domain;
	
	
	/**
	 * @param domain the domain to set
	 */
	public void setDomain(List<IDomainAttribute> domain)
	{
		this.domain = domain;
	}
	
	
	/**
	 * @param varInCNET
	 * @param domain
	 */
	public VI(Variable varInCNET, List<IDomainAttribute> domain)
	{
		super();
		this.varInCNET = varInCNET;
		this.domain = domain;
	}
	
	
	public VI(VI vi)
	{
		this.varInCNET = vi.getVarInCNET();
		this.domain = new LinkedList<IDomainAttribute>();
		for (IDomainAttribute dom : vi.getDomain())
		{
			domain.add(dom);
		}
		
	}
	
	
	/**
	 * @return the varInCNET
	 */
	public Variable getVarInCNET()
	{
		return varInCNET;
	}
	
	
	/**
	 * @return the domain
	 */
	public List<IDomainAttribute> getDomain()
	{
		return domain;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String out = "";
		for (IDomainAttribute dom : domain)
		{
			out += dom.getNumericalRepresentation() + ",";
		}
		return "VI [" + varInCNET.getName() + "," + out + "]";
	}
	
}
