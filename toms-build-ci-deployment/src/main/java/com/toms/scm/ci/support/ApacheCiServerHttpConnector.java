
package com.toms.scm.ci.support;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toms.scm.ci.CiServerHttpConnector;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class ApacheCiServerHttpConnector implements CiServerHttpConnector {//implements InitializingBean{

	protected static Logger log = LoggerFactory.getLogger(ApacheCiServerHttpConnector.class);
	
	/** 기본 {@link #connectionTimeout} */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 0;

	/** 기본 {@link #socketTimeout} */
	public static final int DEFAULT_SOCKET_TIMEOUT = 0;

	/** 기본 {@link #maxConnectionCount} */
	public static final int DEFAULT_MAX_CONNECTION_COUNT = 20;

	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

	/** Connection Timeout */
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

	/** Socket Timeout */
	private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

	/** Max Connection Count */
	private int maxConnectionCount = DEFAULT_MAX_CONNECTION_COUNT;
	
	private int maxConnectionPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
	
	/** HTTP Client */
	private HttpClient httpClient;

	/**
	 * 생성자.
	 */
	public ApacheCiServerHttpConnector() {
		super();
		if(httpClient == null)
			this.initHttpClient();
	}
	
	/**
	 * setter of {@link #socketTimeout}.
	 * 
	 * @param socketTimeout
	 *            Socket Timeout.
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/**
	 * setter of {@link #connectionTimeout}.
	 * 
	 * @param connectionTimeout
	 *            Connection Timeout.
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * setter of {@link #maxConnectionCount}.
	 * 
	 * @param maxConnectionCount
	 *            Max Connection Count.
	 */
	public void setMaxConnectionCount(int maxConnectionCount) {
		this.maxConnectionCount = maxConnectionCount;
	}

	public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
		this.maxConnectionPerRoute = maxConnectionPerRoute;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * @throws IllegalArgumentException
	 *             {@link #targetUrl}이 <code>null</code>인 경우.
	 */
	public void afterPropertiesSet() throws Exception {
		//Assert.notNull(targetUrl, "Property 'targetUrl' must not be null.");
		if(httpClient == null)
			this.initHttpClient();
	}

	public void destroy() throws Exception {
		releaseHttpClient();
	}

	/**
	 * {@link #httpClient}를 초기화한다.
	 */
	protected void initHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				socketTimeout);
		httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				connectionTimeout);

//		ThreadSafeClientConnManager clientConnectionManager = new ThreadSafeClientConnManager();
//		clientConnectionManager.setMaxTotal(maxConnectionCount);
//		clientConnectionManager.setDefaultMaxPerRoute(maxConnectionCount);
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager clientConnectionManager = new PoolingClientConnectionManager(schemeRegistry);
		clientConnectionManager.setMaxTotal(maxConnectionCount);
		clientConnectionManager.setDefaultMaxPerRoute(maxConnectionPerRoute);
		

		httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
	}

	/**
	 * {@link #httpClient}를 해제한다.
	 */
	protected void releaseHttpClient() {
		httpClient.getConnectionManager().shutdown();
	}

	/**
	 * <code>bytes</code> 요청 전문을 전송하고 {@link HttpResponse}를 return한다.
	 * 
	 * @param bytes
	 *            요청 전문.
	 * @return {@link HttpResponse}.
	 * @throws IOException
	 *             송신 과정에서 오류가 발생한 경우.
	 */
    /**
     * 파라미터로 원격호출
     * @param params
     * @param targetUrl
     * @return
     * @throws IOException
     */
    public String doSend(Map<String,String> params,String targetUrl,boolean isPostMethod) throws IOException {
        if(log.isInfoEnabled()) {
            log.info(" connector target url=" + targetUrl);
        }
        HttpResponse httpResponse = this.doSend(targetUrl, params, isPostMethod);
        // Read Entity
        HttpEntity responseEntity = httpResponse.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (responseEntity != null) {
            responseEntity.writeTo(outputStream);
        }
        
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            //return ""+httpResponse.getStatusLine().getStatusCode();
            // Not '200 OK'
            throw new IOException("Fail to send HTTP Post : Status Code = "
                    + httpResponse.getStatusLine());
        }
        return outputStream.toString();
    }
    /**
     * 파라미터로 조립
     * @param params
     * @param targetUrl
     * @return
     * @throws IOException
     */
    private HttpUriRequest getHttpPost(Map<String,String> params,String targetUrl) throws IOException {
        if(log.isInfoEnabled()) {
            log.info(" getHttpPost target url=" + targetUrl);
        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        for(Map.Entry<String, String> entry:params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return httpPost;
    }
    /**
     * 파라미터로 조립
     * @param params
     * @param targetUrl
     * @return
     * @throws IOException
     */
    private HttpUriRequest getHttpGet(Map<String,String> params,String targetUrl) throws IOException {
        if(log.isInfoEnabled()) {
            log.info(" getHttpGet target url=" + targetUrl);
        }
        StringBuffer sb = new StringBuffer("?");
        for(Map.Entry<String, String> entry:params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        HttpGet httpGet = new HttpGet(targetUrl+sb.toString());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        
        return httpGet;
        
    }

    @Override
    public HttpResponse doSend(String targetUrl, Map<String, String> params, boolean isPostMethod)
            throws IOException {
        HttpResponse httpResponse ;
        if(isPostMethod) {
             httpResponse = httpClient.execute(this.getHttpPost(params, targetUrl));
        }else {
             httpResponse = httpClient.execute(this.getHttpGet(params, targetUrl));
        }
        return httpResponse;
    }
	

}
