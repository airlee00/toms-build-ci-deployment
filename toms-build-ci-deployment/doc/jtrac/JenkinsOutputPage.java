/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.wicket;

import info.jtrac.domain.Item;
import info.jtrac.domain.State;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;

import com.pfs.jtrac.util.SpringBeanSupport;
import com.psf.ci.jenkins.JenkinsHttpAdapter;
import com.psf.ci.jenkins.vo.JobConfiguration;

/**
 * Create / Edit item form page
 */
public class JenkinsOutputPage extends BasePage {    
            
    public JenkinsOutputPage() {        
        Item item = new Item();
        item.setSpace(getCurrentSpace());
        item.setStatus(State.NEW);        
    }
    
    public JenkinsOutputPage(String refId) {        
        //Item item = getJtrac().loadItem(itemId);      
        //add(new ItemForm("form", item));
    	JenkinsHttpAdapter cmd = (JenkinsHttpAdapter) SpringBeanSupport.getInstance().getBean("jenkinsAdapter");
        try {
        	String jobName = refId.substring(0,refId.indexOf("-"));
    		
    		JobConfiguration jobConfig = cmd.readJobConfiguration(jobName);
    		
			String buildString = cmd.readConsoleOutput(jobName, jobConfig.getLastBuildNumber());
			System.out.println("path="+ refId + ",buildString="+buildString);
			add(new Label("buildString",buildString));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			add(new Label("diff",e.getMessage()));
		}
        
    }    
    
    
}
