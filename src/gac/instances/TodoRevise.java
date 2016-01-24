package gac.instances;


public class TodoRevise
{
	private final CI	ci;
	private final VI	vi;
	
	
	/**
	 * @param ci
	 * @param vi
	 */
	public TodoRevise(CI ci, VI vi)
	{
		super();
		this.ci = ci;
		this.vi = vi;
	}
	
	
	/**
	 * @return the ci
	 */
	public CI getCi()
	{
		return ci;
	}
	
	
	/**
	 * @return the vi
	 */
	public VI getVi()
	{
		return vi;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		TodoRevise todoRevise = (TodoRevise) obj;
		return ci.getConsInCNET().getCanonicalFormulation()
				.equals(todoRevise.getCi().getConsInCNET().getCanonicalFormulation())
				&& vi.getVarInCNET().getName().equals(todoRevise.getVi().getVarInCNET().getName());
	}
	
	
}
