package core.templates;

/**
 * Interface for Templates, which want to handle Controller AxisEvents
 *
 */
public interface IContTemplate {
	public void handleUp(double magnitude);
	public void handleDown(double magnitude);
	public void handleLeft(double magnitude);
	public void handleRight(double magnitude);
	
	public IContTemplate copy();
}
