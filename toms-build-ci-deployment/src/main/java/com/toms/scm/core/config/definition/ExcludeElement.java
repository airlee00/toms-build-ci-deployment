package com.toms.scm.core.config.definition;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("exclude")
public class ExcludeElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	
	
}
