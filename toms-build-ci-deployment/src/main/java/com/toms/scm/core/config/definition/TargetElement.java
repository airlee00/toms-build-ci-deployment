package com.toms.scm.core.config.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("target")
public class TargetElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String name;
	
	@XStreamAsAttribute
	private String depends;
	
	@XStreamImplicit
	private List<FtpElement> ftpElement = new ArrayList<FtpElement>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FtpElement> getFtpConfig() {
		return ftpElement;
	}


	public void setFtpConfig(List<FtpElement> ftpElement) {
		this.ftpElement = ftpElement;
	}

	public String getDepends() {
		return depends;
	}

	public void setDepends(String depends) {
		this.depends = depends;
	}
	
}
