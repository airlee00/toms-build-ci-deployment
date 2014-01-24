package com.toms.scm.ci;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
/**
 * Red Persimmon Software 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public interface CiServerHttpConnector {

	public HttpResponse doSend(String targetUrl,Map<String,String> params,boolean isPostMethod) throws IOException;
	
	//public String doSendS(String targetUrl,Map<String,String> params,boolean isPostMethod) throws IOException;
}