package com.toms.scm.ci.io;

import java.io.Serializable;
/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class JobConfiguration implements Serializable {

	private static final long serialVersionUID = -3334888334529056354L;
	private String lastBuildNumber;
	private String nextBuildNumber;
	private String lastBuildUrl;
	private JobStatus jobStatus;
	
	public String getLastBuildNumber() {
		return lastBuildNumber;
	}
	public void setLastBuildNumber(String lastBuildNumber) {
		this.lastBuildNumber = lastBuildNumber;
	}
	public String getNextBuildNumber() {
		return nextBuildNumber;
	}
	public void setNextBuildNumber(String nextBuildNumber) {
		this.nextBuildNumber = nextBuildNumber;
	}
	public String getLastBuildUrl() {
		return lastBuildUrl;
	}
	public void setLastBuildUrl(String lastBuildUrl) {
		this.lastBuildUrl = lastBuildUrl;
	}
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	
	
	
}
