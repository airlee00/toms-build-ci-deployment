package com.toms.scm.deployment.test;

import com.toms.scm.core.deployment.Deployer;
import com.toms.scm.deployment.support.FtpDeployerImpl;

public class DeployerTest {

	public static void main(String[] args) throws Exception {
		String loc = "classpath:com/toms/scm/deployment/test/build-config-sample.xml";
		String issueNumber ="PM1-2";
		String repositoryName = "xml-telegraph-spring";
		Deployer dh = new FtpDeployerImpl(loc);
		dh.deploy(repositoryName, issueNumber);
		
	}
}
