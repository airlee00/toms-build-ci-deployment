package com.toms.scm.build.svn;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public interface SvnClient {

	public SVNCommitInfo copy(String path, 
			SVNRevision srcRevision,
			String commitMessage) throws SVNException;

	// public SVNCommitInfo copy(SvnOperationFactory svnOperationFactory,
	// SVNURL sourceFile,
	// SVNURL targetFile,
	// SVNRevision srcRevision,
	// String commitMessage) throws SVNException ;

	public SVNCommitInfo delete(SVNURL targetFile) throws SVNException;

	// public abstract Collection<SVNLogEntry> searchSVN(
	// SVNRepository repository,
	// long startRevision,
	// long endRevision,
	// final String searchTerm,
	// final String logAuthor) throws Exception;

	public Collection<SVNLogEntry> log(SVNRepository repository,
			long startRevision, 
			long endRevision, 
			final String searchTerm,
			final String logAuthor) throws Exception;

	public Collection<SVNLogEntry> log(
			final String appReporsitoryName,
			final String searchTerm, 
			final String logAuthor) throws Exception;
	
	public Collection<SVNLogEntry> getPreviousLogEntrys(
			SVNRepository repository, 
			final long startRevision,
			final long endRevision, 
			long limit) throws Exception;

	public SVNLogEntry getPreviousLogEntry(SVNRepository repository,
			final long startRevision, 
			final long endRevision) throws Exception;

	public String diff(String path, SVNRevision srcRevision) throws Exception;

	public SvnRepositoryProvider getSvnRepositoryProvider();
}