package com.toms.scm.core.deployment;

public interface Deployer {

	public abstract void deploy(String repositoryName, String issueNumber) throws Exception;

}