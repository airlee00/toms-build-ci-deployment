package com.toms.scm.build.support;

import java.util.Collection;


import org.slf4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;

import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.utils.SvnRepositoryUtils;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public abstract class AbstractSvnBuilder {
	
	protected SvnClient svnClient;
	
	
	public AbstractSvnBuilder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AbstractSvnBuilder(SvnClient svnClient) {
		super();
		//this.svnRepositoryProvider = svnRepositoryProvider;
		this.svnClient = svnClient;
	}

	protected Collection<SVNLogEntry> searchSvnLogEntry(String appReporsitoryName,String searchTerm) throws Exception {
		return svnClient.log(appReporsitoryName,searchTerm,null);
	}
	
	//source path에 대한 파일 여부 체크 
	protected boolean isFile(String path) throws SVNException {
		//String dstUrl = getSvnEnvironmemt().getRootUrl() + "/" + getSvnEnvironmemt().getSrcRootPath();
		return SvnRepositoryUtils.isFile(SVNURL.parseURIEncoded(path), "");
	}
	
	protected void logLogEntry(Logger logger, SVNLogEntry logEntry) {
		log(logger,1,"*auth/revision/date => " +logEntry.getAuthor() + " / " +logEntry.getRevision()+" / "+logEntry.getDate() );
	}

	protected void logLogEntryPath(Logger logger, SVNLogEntryPath svnPath) {
		log(logger,2,"*Key(path) : " +svnPath.getPath() );
		log(logger,2,"*CopyRevision/Type/Kind => "+svnPath.getCopyRevision() + " / "+svnPath.getType() + " / " + svnPath.getKind());
	}
	
	protected void log(Logger logger,int tabCount,String message) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<tabCount;i++)
			sb.append("\t");
		sb.append(message);
		logger.info(sb.toString());
	}

	/**  getter and setter **/
	public SvnClient getSvnClient() {
		return svnClient;
	}

	public void setSvnClient(SvnClient svnClient) {
		this.svnClient = svnClient;
	}

}
