package gac.instances;

public class IterationStatus
{
	private VI	vi;
	private int	pos;
	private int	length;
	
	
	/**
	 * @param vi
	 * @param pos
	 * @param length
	 */
	public IterationStatus(VI vi, int pos, int length)
	{
		super();
		this.vi = vi;
		this.pos = pos;
		this.length = length;
	}
	
	
	/**
	 * @return the vi
	 */
	public VI getVi()
	{
		return vi;
	}
	
	
	/**
	 * @param vi the vi to set
	 */
	public void setVi(VI vi)
	{
		this.vi = vi;
	}
	
	
	/**
	 * @return the pos
	 */
	public int getPos()
	{
		return pos;
	}
	
	
	/**
	 * @param pos the pos to set
	 */
	public void setPos(int pos)
	{
		this.pos = pos;
	}
	
	
	/**
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}
	
	
	/**
	 * @param length the length to set
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	
	
}
