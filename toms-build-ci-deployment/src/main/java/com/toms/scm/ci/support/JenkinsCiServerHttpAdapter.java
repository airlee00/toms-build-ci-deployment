
package com.toms.scm.ci.support;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toms.scm.ci.CiServerHttpAdapter;
import com.toms.scm.ci.CiServerHttpConnector;
import com.toms.scm.ci.io.JobConfiguration;
import com.toms.scm.ci.io.JobStatus;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.JenkinsElement;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class JenkinsCiServerHttpAdapter implements CiServerHttpAdapter {//implements InitializingBean{

	protected static Logger logger = LoggerFactory.getLogger(JenkinsCiServerHttpAdapter.class);
	
	private CiServerHttpConnector connector;
	
	private ConfigurationReader<JenkinsElement> coonfigReader ;
	
	
	public void setConnector(CiServerHttpConnector connector) {
		this.connector = connector;
	}
	
	public void setCoonfigReader(ConfigurationReader<JenkinsElement> coonfigReader) {
        this.coonfigReader = coonfigReader;
    }

    /* (non-Javadoc)
	 * @see com.toms.scm.ci.jenkins.JenkinsHttpAdapter#login(java.lang.String)
	 */
	@Override
	public void login() throws Exception {
		String targetUrl = coonfigReader.getConfiguration().getRootUrl() + "/j_acegi_security_check";
		Map<String, String> params = new HashMap<String, String>();
		params.put("j_username", coonfigReader.getConfiguration().getUsername());
		params.put("j_password", coonfigReader.getConfiguration().getPassword());
		params.put("action", "login");
		HttpResponse httpResponse = connector.doSend(targetUrl, params,true);
		
		if(logger.isDebugEnabled()) {
			org.apache.http.Header[] headers=  httpResponse.getAllHeaders();
	        for(org.apache.http.Header h : headers)
	        	logger.debug("[HEADER]name:" + h.getName() + ",value=" + h.getValue());
		}
		HttpEntity responseEntity = httpResponse.getEntity();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (responseEntity != null) {
			responseEntity.writeTo(outputStream);
		}
		int statusCode  = httpResponse.getStatusLine().getStatusCode() ;
		if(logger.isInfoEnabled()) {
			logger.info("[statusCode]=" +statusCode + "\n");
		}
		if (statusCode == 200 || statusCode == 302) {
			//do nothing..
		}else{
			throw new IOException("Fail to send HTTP Post : Status Code = "
					+ httpResponse.getStatusLine());
		}
		if(logger.isInfoEnabled()) {
			logger.info("##returnStr=" + outputStream.toString() + "\n");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.toms.scm.ci.jenkins.JenkinsHttpAdapter#build(java.lang.String, java.util.Map)
	 */
	@Override
	public  String build(String jobName,Map<String, String> params) throws Exception {
		
		this.login();
		
		String targetUrl = coonfigReader.getConfiguration().getRootUrl() + "/job/" + jobName + "/buildWithParameters";
		HttpResponse httpResponse = connector.doSend(targetUrl,params,true);
		HttpEntity responseEntity = httpResponse.getEntity();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (responseEntity != null) {
			responseEntity.writeTo(outputStream);
		}
		int statusCode  = httpResponse.getStatusLine().getStatusCode() ;
		if(logger.isInfoEnabled()) {
			logger.info("[statusCode]=" +statusCode + "\n");
		}
		if (statusCode == 200 || statusCode == 302) {
			//do nothing..
		}else{
			throw new IOException("Fail to send HTTP Post : Status Code = "
					+ httpResponse.getStatusLine());
		}
		if(logger.isInfoEnabled()) {
			logger.info("##returnStr=" + outputStream.toString() + "\n");
		}
		return outputStream.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.toms.scm.ci.jenkins.JenkinsHttpAdapter#readJobStatus(java.lang.String)
	 */
	@Override
	public JobConfiguration readJobConfiguration(String jobName) throws Exception {
		
		this.login();
		
		String targetUrl = coonfigReader.getConfiguration().getRootUrl() + "/job/" + jobName + "/api/xml";
		HttpResponse httpResponse = connector.doSend(targetUrl,null,true);
		HttpEntity responseEntity = httpResponse.getEntity();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (responseEntity != null) {
			responseEntity.writeTo(outputStream);
		}
		Document dom = new SAXReader().read(new ByteArrayInputStream(outputStream.toByteArray()));
		JobConfiguration jobConfig = new JobConfiguration();
		
		Element root = dom.getRootElement();
		Element job = root.element("lastBuild");
		jobConfig.setLastBuildNumber(job.elementText("number"));
		jobConfig.setLastBuildUrl(job.elementText("url"));
		
		job = root.element("nextBuildNumber");
		jobConfig.setNextBuildNumber(job.getText());
		
		job  = root.element("color");
		jobConfig.setJobStatus(JobStatus.create(job.getText()));
		
		return jobConfig;
	}
	
	public String readConsoleOutput(String jobName,String buildNumber) throws Exception {
		
		this.login();
		
		String targetUrl = coonfigReader.getConfiguration().getRootUrl() + "/job/" + jobName + "/" + buildNumber + "/consoleText";
		HttpResponse httpResponse = connector.doSend(targetUrl,null,true);
		HttpEntity responseEntity = httpResponse.getEntity();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (responseEntity != null) {
			responseEntity.writeTo(outputStream);
		}
		return outputStream.toString();
	}
	
//	public static void main(String[] args) throws Exception {
//		CiServerHttpConnector connector= new ApacheCiServerHttpConnector();
//		//connector.initHttpClient();
//		JenkinsCiServerHttpAdapter adaptor = new JenkinsCiServerHttpAdapter();
//		adaptor.setConnector(connector);
//		adaptor.setUserName("airlee");
//		adaptor.setPassword("123qwe");
//		adaptor.setTargetRootUrl("http://localhost:8888");
//		
//		//로그인 - 로그인은 항상 맨 먼저 호출되야 함.
//		//adaptor.login();
//		//job 정보
//		//String readUrl = "http://localhost:8888/job/copy-build/api/xml";
//		String jobName = "copy-build";
//		JobConfiguration jobConfig = adaptor.readJobConfiguration(jobName);
//
//		//빌드
////		Map<String, String> params = new HashMap<String, String>();
////		params.put("build.type", "L");
////		params.put("issue.number", "6");
////		params.put("app.dir.name", "xml-telegraph-spring");
////		String targetUrl = "http://localhost:8888/job/copy-build/buildWithParameters";
////		
////		String response = adaptor.build(targetUrl,params);
////		//빌드시에는 빌드 정보가 리턴되지 않음..시간을 두고 빌드 log를 조회 해야함. 
////		System.out.println("--------------------------------------");
////		System.out.println(response);
//		
//		//빌드후 로그 확인
//		String res = adaptor.readConsoleOutput(jobName, jobConfig.getLastBuildNumber());
//		System.out.println(res);
//
//    }

}
