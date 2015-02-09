package core.templates;

import core.templates.TemplateVersions.Template;

public class LeapTemplateFactory {

	public static ILeapTemplate makeTemplate(Template version) {
		
		switch(version) {		
			case Version1:  //TODO: erstmal nur ein Template für Leapmotion
				return new LeapTemplate01();
				
			case Version2: 
				return new LeapTemplate01();
			
			case LeapMotion: 
				return new LeapTemplate01();
				
			case LeapMotionHMD: 
				return new LeapTemplate02();

				
			default:
				return null;			
		}
	}
	
}
