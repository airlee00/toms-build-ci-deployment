package com.toms.scm.core.config.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("ftp")
public class FtpElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String host;// ftp ip address
	@XStreamAsAttribute
	private int    port; // ftp port
	@XStreamAsAttribute
	private String username; // ftp user id
	@XStreamAsAttribute
	private String password; // ftp password
	@XStreamAsAttribute
	private String rootPath; // ftp 초기 접속 디렉토리
	@XStreamAsAttribute
	private boolean actualTransfer = false; // 실제 전송할 것인지 
	@XStreamImplicit
	private List<FilesetElement> fileset = new ArrayList<FilesetElement>();
	
	//파일구분자
	@XStreamAsAttribute
	private String remoteFileSeparator = "/";//file.separator
	//상세로그 기록여부
	@XStreamAsAttribute
	private boolean verbose = true;
	//하나의 파일이 실패하면 skip할지 중단할지 여부 
	@XStreamAsAttribute
	private boolean skipFailedTransfers = true;
	//디렉토리생성등에서 에러가 발생해도 무시할지 여부 
	@XStreamAsAttribute
	private boolean ignoreNoncriticalErrors = true;
	//원격디렉토리 파일 권한
	@XStreamAsAttribute
	private String chmod = null;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getRemoteFileSeparator() {
		return remoteFileSeparator;
	}
	public void setRemoteFileSeparator(String remoteFileSeparator) {
		this.remoteFileSeparator = remoteFileSeparator;
	}
	public boolean isVerbose() {
		return verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public boolean isSkipFailedTransfers() {
		return skipFailedTransfers;
	}
	public void setSkipFailedTransfers(boolean skipFailedTransfers) {
		this.skipFailedTransfers = skipFailedTransfers;
	}
	public boolean isIgnoreNoncriticalErrors() {
		return ignoreNoncriticalErrors;
	}
	public void setIgnoreNoncriticalErrors(boolean ignoreNoncriticalErrors) {
		this.ignoreNoncriticalErrors = ignoreNoncriticalErrors;
	}
	public String getChmod() {
		return chmod;
	}
	public void setChmod(String chmod) {
		this.chmod = chmod;
	}
	public List<FilesetElement> getFileset() {
		return fileset;
	}
	public void setFileset(List<FilesetElement> fileset) {
		this.fileset = fileset;
	}
    public boolean isActualTransfer() {
        return actualTransfer;
    }
    public void setActualTransfer(boolean actualTransfer) {
        this.actualTransfer = actualTransfer;
    }


	
	
}
