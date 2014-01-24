package com.toms.scm.build.svn;


import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.toms.scm.core.config.definition.SvnElement;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public interface SvnRepositoryProvider {

	public abstract SVNRepository createRepository(String appReporsitoryName)
			throws SVNException;

	public abstract SvnElement getSvnConfig();
	
	public ISVNAuthenticationManager createAuthManager();
	
	public ISVNAuthenticationManager createAuthManager(String username, String passwd);

	SVNRepository createRepository(SVNURL svnurl) throws SVNException;

}