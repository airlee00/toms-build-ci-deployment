package com.toms.scm.ci;

import java.io.IOException;
import java.util.Map;

import com.toms.scm.ci.io.JobConfiguration;


/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public interface CiServerHttpAdapter {

	public abstract void login() throws Exception;

	public abstract String build(String targetUri, Map<String, String> params)
			throws Exception;

	public abstract JobConfiguration readJobConfiguration(String targetUri) throws Exception;
	
	public String readConsoleOutput(String jobName,String buildNumber) throws Exception;

}