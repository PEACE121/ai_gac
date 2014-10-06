package gac.constraintNetwork;

import gac.IDomainAttribute;

import java.util.ArrayList;
import java.util.List;


public class Variable
{
	private final String							name;
	private final List<IDomainAttribute>	fullDomain;
	
	
	/**
	 * @param name
	 * @param fullDomain
	 */
	public Variable(String name, List<IDomainAttribute> fullDomain)
	{
		super();
		this.name = name;
		this.fullDomain = fullDomain;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * @return the fullDomain
	 */
	public List<IDomainAttribute> getFullDomainCopy()
	{
		List<IDomainAttribute> copy = new ArrayList<IDomainAttribute>();
		for (IDomainAttribute domainAttribute : fullDomain)
		{
			copy.add(domainAttribute);
		}
		return copy;
	}
}
