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
import info.jtrac.domain.ItemUser;
import info.jtrac.domain.Space;
import info.jtrac.domain.State;
import info.jtrac.domain.User;
import info.jtrac.domain.UserSpaceRole;
import info.jtrac.util.UserUtils;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.BoundCompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Bytes;

import com.pfs.jtrac.util.SpringBeanSupport;
import com.psf.svn.commands.SvnCommand;

/**
 * Create / Edit item form page
 */
public class SvnDiffPage extends BasePage {    
            
    public SvnDiffPage() {        
        Item item = new Item();
        item.setSpace(getCurrentSpace());
        item.setStatus(State.NEW);        
    }
    
    public SvnDiffPage(String path, long revision) {        
        //Item item = getJtrac().loadItem(itemId);      
        //add(new ItemForm("form", item));
        SvnCommand cmd = (SvnCommand) SpringBeanSupport.getInstance().getBean("svnCommand");
        try {
			String diff = cmd.diff(path, revision);
			System.out.println("path="+ path + ",revision="+revision+","+diff);
			add(new Label("diff",diff));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			add(new Label("diff",e.getMessage()));
		}
        
    }    
    
    
}
