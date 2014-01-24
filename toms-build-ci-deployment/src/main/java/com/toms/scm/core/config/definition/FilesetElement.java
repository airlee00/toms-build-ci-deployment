package com.toms.scm.core.config.definition;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("fileset")
public class FilesetElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String dir;
	
	@XStreamImplicit
	private List<ReplaceElement> replace = new LinkedList<ReplaceElement>();

	@XStreamImplicit
	private List<ExcludeElement> exclude = new LinkedList<ExcludeElement>();
	
	@XStreamImplicit
	private List<IncludeElement> include = new LinkedList<IncludeElement>();

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}


	public List<ExcludeElement> getExclude() {
		return exclude;
	}

	public void setExclude(List<ExcludeElement> exclude) {
		this.exclude = exclude;
	}

	public List<IncludeElement> getInclude() {
		return include;
	}

	public void setInclude(List<IncludeElement> include) {
		this.include = include;
	}

	public List<ReplaceElement> getReplace() {
		return replace;
	}

	public void setReplace(List<ReplaceElement> replace) {
		this.replace = replace;
	}




	
	
}
