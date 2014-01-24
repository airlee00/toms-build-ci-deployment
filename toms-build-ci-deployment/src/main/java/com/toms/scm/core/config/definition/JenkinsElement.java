package com.toms.scm.core.config.definition;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("jenkins")
public class JenkinsElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private String username;
	@XStreamAsAttribute
	private String password;
	@XStreamAsAttribute
	private String rootUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRootUrl() {
        return rootUrl;
    }
    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }
}
