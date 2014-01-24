package com.toms.scm.core.config;


public interface ConfigurationReader<T> {

    public T getConfiguration() throws Exception;

}
