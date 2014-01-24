package com.toms.scm.ci.test;

import com.toms.scm.ci.CiServerHttpConnector;
import com.toms.scm.ci.io.JobConfiguration;
import com.toms.scm.ci.support.ApacheCiServerHttpConnector;
import com.toms.scm.ci.support.JenkinsCiServerHttpAdapter;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.JenkinsElement;
import com.toms.scm.core.config.reader.JenkinsConfigurationReader;

public class JenkinsTest {

    public static void main(String[] args) throws Exception {
        
        String loc = "classpath:com/toms/scm/ci/test/jenkins-config-sample.xml";

        ConfigurationReader<JenkinsElement> reader = new JenkinsConfigurationReader(loc);
        // JenkinsElement config = reader.getConfiguration();

        CiServerHttpConnector connector = new ApacheCiServerHttpConnector();
        // connector.initHttpClient();
        JenkinsCiServerHttpAdapter adaptor = new JenkinsCiServerHttpAdapter();
        adaptor.setConnector(connector);
        adaptor.setCoonfigReader(reader);

        // 로그인 - 로그인은 항상 맨 먼저 호출되야 함.
        // adaptor.login();
        // job 정보
        // String readUrl = "http://localhost:8888/job/copy-build/api/xml";
        String jobName = "copy-build";
        JobConfiguration jobConfig = adaptor.readJobConfiguration(jobName);

        // 빌드
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("build.type", "L");
        // params.put("issue.number", "6");
        // params.put("app.dir.name", "xml-telegraph-spring");
        // String targetUrl =
        // "http://localhost:8888/job/copy-build/buildWithParameters";
        //
        // String response = adaptor.build(targetUrl,params);
        // //빌드시에는 빌드 정보가 리턴되지 않음..시간을 두고 빌드 log를 조회 해야함.
        // System.out.println("--------------------------------------");
        // System.out.println(response);

        // 빌드후 로그 확인
        String res = adaptor.readConsoleOutput(jobName, jobConfig.getLastBuildNumber());
        System.out.println(res);
    }
}
