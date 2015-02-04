package core.templates;

import core.templates.TemplateVersions.Template;

public class ORTemplateFactory {
	
	public static IORTemplate makeTemplate(Template version) {
		
		switch(version) {		
			case Version1: 
				return new ORTemplate01();
				
			case Version2: 
				return new ORTemplate02();
				
			default:
				return null;		
		}
	}

}
