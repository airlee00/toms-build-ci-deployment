package com.toms.scm.core.config.reader;

import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.thoughtworks.xstream.XStream;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.SvnElement;
/**
 * read svn config xml 
 * @author airlee00@gmail.com
 * @param <T>
 * 
 */
public class SvnConfigurationReader implements ConfigurationReader<SvnElement>{
	
    protected Logger log = LoggerFactory.getLogger(SvnConfigurationReader.class);
    
	private SvnElement svnConfig ;
	
	private String configXmlLocation;
	
	public SvnConfigurationReader(String configXmlLocation) throws Exception {
		this.configXmlLocation = configXmlLocation;
	}
	@Override
	public SvnElement getConfiguration() throws Exception {
	    if(svnConfig == null) {
	        XStream xstream = new XStream();
	        
	        java.io.File file = ResourceUtils.getFile(configXmlLocation);
	        
	        InputStream inputStream = new FileInputStream(file);
	        
	        xstream.processAnnotations(SvnElement.class);
	        
	        svnConfig =(SvnElement) xstream.fromXML(inputStream);
	    }
		return svnConfig;
	}

    public void setConfigXmlLocation(String configXmlLocation) {
        this.configXmlLocation = configXmlLocation;
    }



	
}
