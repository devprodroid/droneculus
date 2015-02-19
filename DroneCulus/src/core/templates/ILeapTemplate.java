package core.templates;

/**
 * Interface for Templates, which want to handle Controller AxisEvents
 *
 */
public interface ILeapTemplate {

	public void handleForward(double magnitude);

	public void handleBackward(double magnitude);

	public void handleLeft(double magnitude);

	public void handleRight(double magnitude);
	
	public void handleUp(double magnitude);

	public void handleDown(double magnitude);
	

	public ILeapTemplate copy();

	public void handleStart();
}
