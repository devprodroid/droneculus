package core.templates;

import core.templates.TemplateVersions.Template;

public class ContTemplateFactory {

	public static IContTemplate makeTemplate(Template version) {
		
		switch(version) {		
			case Version1: 
				return new ContTemplate01();
				
			case Version2: 
				return new ContTemplate02();
				
			case LeapMotion: 
				return new ContTemplate02();
			
			case LeapMotionHMD: 
				return new ContTemplate02();
													
				
			default:
				return null;			
		}
	}
	
}
