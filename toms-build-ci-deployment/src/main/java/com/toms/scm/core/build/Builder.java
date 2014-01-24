package com.toms.scm.core.build;

import java.util.List;

import com.toms.scm.core.io.SvnEntryInfo;


/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public interface Builder {

	public abstract List<SvnEntryInfo> log(String appReporsitoryName,
			String searchTerm) throws Exception;
	
	public abstract void copy(String appReporsitoryName,
			String searchTerm,boolean onlyForLogging) throws Exception;
	
	public void delete(String srcPath) throws Exception;

	public String diff(String srcPath,long srcRevision) throws Exception ;
}