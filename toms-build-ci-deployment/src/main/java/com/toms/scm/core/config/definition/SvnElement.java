package com.toms.scm.core.config.definition;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
@XStreamAlias("svn")
public class SvnElement implements Serializable {
	@XStreamOmitField
	private static final long serialVersionUID = 2153922131501770141L;
	@XStreamAsAttribute
	private String rootUrl = "svn://localhost/test";
	@XStreamAsAttribute
	private String srcRootPath = "/trunk";
	@XStreamAsAttribute
	private String targetRootPath = "/tag/1.0";
	@XStreamAsAttribute
	private String userName = "airlee";
	@XStreamAsAttribute
	private String password = "1111";
	@XStreamAsAttribute
	private long startRevision = -1;
	@XStreamAsAttribute
	private long endRevision = 0;
	
	public SvnElement() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SvnElement(String rootUrl, String srcRootPath,
			String targetRootPath, String userName, String password) {
		super();
		this.rootUrl = rootUrl;
		this.srcRootPath = srcRootPath;
		this.targetRootPath = targetRootPath;
		this.userName = userName;
		this.password = password;
	}
	public String getRootUrl() {
		return rootUrl;
	}
	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	public String getSrcRootPath() {
		return srcRootPath;
	}
	public void setSrcRootPath(String srcRootPath) {
		this.srcRootPath = srcRootPath;
	}
	public String getTargetRootPath() {
		return targetRootPath;
	}
	public void setTargetRootPath(String targetRootPath) {
		this.targetRootPath = targetRootPath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getStartRevision() {
		return startRevision;
	}
	public void setStartRevision(long startRevision) {
		this.startRevision = startRevision;
	}
	public long getEndRevision() {
		return endRevision;
	}
	public void setEndRevision(long endRevision) {
		this.endRevision = endRevision;
	}

	

}
