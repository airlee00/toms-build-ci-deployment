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

import java.util.ArrayList;
import java.util.List;

import info.jtrac.domain.Field;
import info.jtrac.domain.History;
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.User;
import info.jtrac.util.DateUtils;
import info.jtrac.util.ItemUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import com.pfs.jtrac.util.SpringBeanSupport;
import com.psf.svn.commands.SvnCommand;
import com.psf.svn.commands.vo.SvnLogVo;

/**
 * dashboard page
 */
public class ItemViewPage extends BasePage {              
        
    private long itemId;

    public long getItemId() {
        return itemId;
    }    
    
    public ItemViewPage(PageParameters params) {        
        String refId = params.getString("0");
        logger.debug("item id parsed from url = '" + refId + "'");
        Item item;
        if(refId.indexOf('-') != -1) { 
            // this in the form SPACE-123
            item = getJtrac().loadItemByRefId(refId);
        } else {
            // internal id of type long
            item = getJtrac().loadItem(Long.parseLong(refId));
        }
        itemId = item.getId(); // required for itemRelatePanel
        addComponents(item);
    }  
    
    private void addComponents(final Item item) {  
        final ItemSearch itemSearch = getCurrentItemSearch();
        add(new ItemRelatePanel("relate", true));        
        Link link = new Link("back") {
            public void onClick() {
                itemSearch.setSelectedItemId(item.getId());
                if(itemSearch.getRefId() != null) {
                     // user had entered item id directly, go back to search page
                     setResponsePage(new ItemSearchFormPage(itemSearch));
                } else {
                     setResponsePage(ItemListPage.class);
                }
            }
        };
        if(itemSearch == null) {
            link.setVisible(false);
        }
        
        add(link);
        
        boolean isRelate = itemSearch != null && itemSearch.getRelatingItemRefId() != null;
        
        User user = getPrincipal();
        
        if(!user.getSpaces().contains(item.getSpace())) {
            logger.debug("user is not allocated to space");
            throw new RestartResponseAtInterceptPageException(ErrorPage.class);
        }
        
        add(new Link("edit") {
            public void onClick() {
                setResponsePage(new ItemFormPage(item.getId()));
            }
        }.setVisible(user.isAdminForAllSpaces()));                        
        
        add(new ItemViewPanel("itemViewPanel", item, isRelate || user.getId() == 0));
        
        if(user.isGuestForSpace(item.getSpace()) || isRelate) {        
            add(new WebMarkupContainer("itemViewFormPanel").setVisible(false));
        } else {            
            add(new ItemViewFormPanel("itemViewFormPanel", item, itemSearch));
        } 
        
        add(new Link("build") {
            public void onClick() {
                setResponsePage(new JenkinsBuildPage(item.getRefId(),item.getCusStr01()));
            }
        }.setVisible(user.isAdminForAllSpaces()));  
        
        add(new Link("jenkis-output") {
        	public void onClick() {
        		setResponsePage(new JenkinsOutputPage(item.getRefId()));
        	}
        }.setVisible(user.isAdminForAllSpaces()));  
        
        try{
        	
	        SvnCommand cmd = (SvnCommand) SpringBeanSupport.getInstance().getBean("svnCommand");
	        
	        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
	        List<SvnLogVo> history = cmd.log(item.getCusStr01(), item.getRefId());
	        logger.debug("Repository=" + item.getCusStr01() +",issueId=" + item.getRefId());
	        add(new ListView("history", history) {
	            protected void populateItem(ListItem listItem) {
	                if(listItem.getIndex() % 2 != 0) {
	                    listItem.add(sam);
	                }                    
	                final SvnLogVo h = (SvnLogVo) listItem.getModelObject();
	                Link link = new Link("path") {
	                    public void onClick() {
	                        setResponsePage(new SvnDiffPage(h.getPath(),h.getRevision()));
	                    }                    
	                };
	                
	                link.add(new Label("path2", new PropertyModel(h, "path")));
	                listItem.add(link);
	                listItem.add(new Label("revision", new PropertyModel(h, "revision")));
	                listItem.add(new Label("message", new PropertyModel(h, "message")));
	                listItem.add(new Label("kind", new PropertyModel(h, "kind")));
	                listItem.add(new Label("type", new PropertyModel(h, "type")));
	                listItem.add(new Label("skip", new PropertyModel(h, "skip")));
	            }                
	        });  
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
}
