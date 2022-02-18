package com.toms.scm.build.svn.support;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;

import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.core.config.definition.SvnElement;

/**
 *
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class DefaultSvnRepositoryProvider implements SvnRepositoryProvider {

	private SvnElement svnElement;

	public DefaultSvnRepositoryProvider() {
		super();
		init();
	}

	public DefaultSvnRepositoryProvider(SvnElement svnElement) {
		super();
		this.svnElement = svnElement;
		init();
	}

	private void init() {
		/*
		 * For using over http:// and https://
		 */
		DAVRepositoryFactory.setup();
		/*
		 * For using over svn:// and svn+xxx://
		 */
		SVNRepositoryFactoryImpl.setup();
		/*
		 * For using over file:///
		 */
		// FSRepositoryFactory.setup();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.toms.scm.build.svn.support.SvnRepositoryProvider#createRepository(java
	 * .lang.String)
	 */
	@Override
	public SVNRepository createRepository(String appReporsitoryName)
			throws SVNException {
		String svnurl = svnElement.getRootUrl() + "/"
				+ svnElement.getSrcRootPath() + "/" + appReporsitoryName;
		return createRepository(SVNURL.parseURIEncoded(svnurl));
	}

	@Override
	public SVNRepository createRepository(SVNURL svnurl) throws SVNException {
		SVNRepository repository = SVNRepositoryFactory.create(svnurl);
		repository.setAuthenticationManager(this.createAuthManager());
		return repository;
	}

	public ISVNAuthenticationManager createAuthManager(String username,
			String passwd) {
		return SVNWCUtil.createDefaultAuthenticationManager(username, passwd);
	}

	public ISVNAuthenticationManager createAuthManager() {
		return SVNWCUtil.createDefaultAuthenticationManager(
				svnElement.getUserName(), svnElement.getPassword());
	}

	public SvnOperationFactory createOperationFactory() {
		SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(this.createAuthManager());
		return svnOperationFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.toms.scm.build.svn.support.SvnRepositoryProvider#getSvnEnvironmemt()
	 */
	@Override
	public SvnElement getSvnConfig() {
		return svnElement;
	}

	public void setSvnConfig(SvnElement svnElement) {
		this.svnElement = svnElement;
	}

}
