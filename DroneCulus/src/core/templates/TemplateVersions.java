package core.templates;


public class TemplateVersions {

	/**
	 * Template enum for Control Versions
	 * Register the name of new Versions here.
	 */
	public enum Template{
		Version1, Version2, LeapMotion, LeapMotionHMD,
	}
	
	public static String[] templatesToString() {
		
		int i = 0;
		
		String[] list = new String[Template.values().length];
		
		for(Template temp: Template.values()) {
			list[i] = temp.toString();
			i++;
		}
		
		return list;
	}
	
	
}
