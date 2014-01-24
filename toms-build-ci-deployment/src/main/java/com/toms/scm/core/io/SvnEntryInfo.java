package com.toms.scm.core.io;

import java.io.Serializable;
/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class SvnEntryInfo implements Serializable {
	private static final long serialVersionUID = -7689637721896431336L;
	// 하나의 issue#에 똑 같은 소스를 2번이상 commit하였다면 맨 나중의 버전을 적용해야 한다.
	// 이때 맨 나중이 아는 모든 버전은 skip이 true이다. (svn copy가 일어나지 않음)
	private boolean skip;
	private String path; //원본 파일 path
	private String destPath; //목적지 path
	private long revision ;
	private String message ;//commit 메시지 
	private String kind;// file, dir
	private char type;// 
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	
	public String toString() {
		return "skip=" + skip + ",path="+ path +",destPath="+destPath +",revision=" + revision
		     + ",message=" + message + ",kind=" + kind + ",type="+type +"\n"; 
	}
	
}
