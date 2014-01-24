package com.toms.scm.core.config.reader;


import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.thoughtworks.xstream.XStream;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.ProjectElement;

/**
 * 
 */
public class ProjectConfigurationReader implements ConfigurationReader<ProjectElement> {
	
    protected Logger log = LoggerFactory.getLogger(ProjectConfigurationReader.class);
    

	private ProjectElement projectElement ;
	
	private String configXmlLocation;
	
	public ProjectConfigurationReader(String configXmlLocation) throws Exception {
		this.configXmlLocation = configXmlLocation;
	}
	@Override
	public ProjectElement getConfiguration() throws Exception {
	    if(projectElement == null) {
	        XStream xstream = new XStream();
	        
	        java.io.File file = ResourceUtils.getFile(configXmlLocation);
	        
	        InputStream inputStream = new FileInputStream(file);
	        
	        xstream.processAnnotations(ProjectElement.class);
	        
	        projectElement =(ProjectElement) xstream.fromXML(inputStream);
	    }
		return projectElement;
	}

    public String getConfigXmlLocation() {
        return configXmlLocation;
    }

    public void setConfigXmlLocation(String configXmlLocation) {
        this.configXmlLocation = configXmlLocation;
    }

    public void setProjectElement(ProjectElement projectElement) {
        this.projectElement = projectElement;
    }

	
}
