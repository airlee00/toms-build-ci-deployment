package com.toms.scm.build.svn.support;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnCopySource;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRemoteCopy;
import org.tmatesoft.svn.core.wc2.SvnRemoteDelete;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.core.config.definition.SvnElement;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class DefaultSvnClient implements SvnClient {

	private Logger log = LoggerFactory.getLogger(getClass());

	protected SvnRepositoryProvider svnRepositoryProvider;
	
	public DefaultSvnClient() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DefaultSvnClient(SvnRepositoryProvider svnRepositoryProvider) {
		super();
		this.svnRepositoryProvider = svnRepositoryProvider;
	}
	/* (non-Javadoc)
	 * @see com.toms.scm.build.svn.support.SvnCopyClient#copy(org.tmatesoft.svn.core.SVNURL, org.tmatesoft.svn.core.SVNURL, org.tmatesoft.svn.core.wc.SVNRevision, java.lang.String)
	 */
	
	public SVNCommitInfo copy(SvnOperationFactory svnOperationFactory,
							  SVNURL sourceFile,
							  SVNURL targetFile,
							  SVNRevision srcRevision,
							  String commitMessage) throws SVNException {
		try {
	        final SvnRemoteCopy remoteCopy = svnOperationFactory.createRemoteCopy();
	        remoteCopy.addCopySource(SvnCopySource.create(SvnTarget.fromURL(sourceFile, srcRevision), srcRevision));
	        remoteCopy.setSingleTarget(SvnTarget.fromURL(targetFile,srcRevision));
	        remoteCopy.setMove(false);
	        remoteCopy.setFailWhenDstExists(false);
	        remoteCopy.setMakeParents(true);
	        remoteCopy.setCommitMessage(commitMessage);
//	        cp.setRevisionProperties(revisionProperties);
//	        cp.setExternalsHandler(SvnCodec.externalsHandler(getExternalsHandler()));
//	        cp.setCommitHandler(SvnCodec.commitHandler(getCommitHandler()));
//	        cp.setDisableLocalModifications(disableLocalModifications);
//	        cp.setCommitParameters(SvnCodec.commitParameters(getCommitParameters()));
	        final SVNCommitInfo commitInfo = remoteCopy.run();
	        if(log.isDebugEnabled()) {
	        	log.debug(""+commitInfo);
	        }
	        return commitInfo;
		}finally {
			svnOperationFactory.dispose();
		}
	}
	public SVNCommitInfo copy(String path,
							  SVNRevision srcRevision,
							  String commitMessage) throws SVNException {
		
		SvnElement env = svnRepositoryProvider.getSvnConfig();
		SVNURL sourceFile = SVNURL.parseURIEncoded(env.getRootUrl() + "/" + path);
		
		String target = env.getRootUrl() + "/" + path.replace(env.getSrcRootPath(),env.getTargetRootPath()); 
		SVNURL targetFile = SVNURL.parseURIEncoded(target);
		
		if(log.isDebugEnabled()) {
			log.debug("src:" + sourceFile +",target:" + targetFile);
		}
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(svnRepositoryProvider.createAuthManager());
		return copy(svnOperationFactory,sourceFile,targetFile,srcRevision,commitMessage);
	}
	
	/* (non-Javadoc)
	 * @see com.toms.scm.build.svn.support.SvnCopyClient#delete(org.tmatesoft.svn.core.SVNURL)
	 */
	@Override
	public SVNCommitInfo delete(SVNURL targetFile) throws SVNException {
		if(log.isDebugEnabled()) {
			log.debug("delete target:" + targetFile);
		}
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		try {
			svnOperationFactory.setAuthenticationManager(svnRepositoryProvider.createAuthManager());
			final SvnRemoteDelete remote = svnOperationFactory
					.createRemoteDelete();
			remote.addTarget(SvnTarget.fromURL(targetFile));

			final SVNCommitInfo commitInfo = remote.run();
	        if(log.isDebugEnabled()) {
	        	log.debug(""+commitInfo);
	        }
	        return commitInfo;
		} finally {
			svnOperationFactory.dispose();
		}
	}
	
	public Collection<SVNLogEntry> log(SVNRepository repository,
										long startRevision, 
										long endRevision, 
										final String searchTerm,
										final String logAuthor) throws Exception {

		final Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		try {
			repository.log(new String[] { "" }, startRevision, endRevision,
					true, true, 0, false, null, new ISVNLogEntryHandler() {
						public void handleLogEntry(SVNLogEntry logEntry) {
							if (logEntry == null || logEntry.getMessage() == null)
								return;
							if (logEntry.getMessage().indexOf(searchTerm) > -1) {
								if ((logAuthor == null || logAuthor.equals(""))
										|| logEntry.getAuthor().equals(	logAuthor)) {
									logEntries.add(logEntry);
								}
							}
						}
					});
		} finally {
			repository.closeSession();
		}
		return logEntries;
	}

	
	public Collection<SVNLogEntry> log(
			final String appReporsitoryName,
			final String searchTerm, 
			final String logAuthor) throws Exception {
		
		SVNRepository repository = svnRepositoryProvider.createRepository(appReporsitoryName);
		SvnElement svnEnvironmemt = svnRepositoryProvider.getSvnConfig();
		
		return this.log(repository, svnEnvironmemt.getStartRevision(),
				svnEnvironmemt.getEndRevision(), searchTerm, logAuthor);
	}

	//다건
	public Collection<SVNLogEntry> getPreviousLogEntrys(SVNRepository repository,
			final long startRevision,
			final long endRevision,
			long limit) throws Exception {
		
		final Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
		try {
			repository.log(new String[] { "" }, startRevision, endRevision,
					true, true, limit, false, null, new ISVNLogEntryHandler() {
				public void handleLogEntry(SVNLogEntry logEntry) {
					if (logEntry == null)
						return;
					if (logEntry.getRevision() != startRevision) {
							logEntries.add(logEntry);
					}
				}
			});
		} finally {
			repository.closeSession();
		}
		return logEntries;
	}
	//단건
	public SVNLogEntry getPreviousLogEntry(SVNRepository repository,
			final long startRevision,
			final long endRevision) throws Exception {
		
		Collection<SVNLogEntry> logEntries = getPreviousLogEntrys (repository,startRevision, endRevision,2);
		if(logEntries.size() > 0) {
			for(SVNLogEntry entry : logEntries) {
				return entry;
			}
		}
		return null;
	}


	public void setSvnRepositoryProvider(SvnRepositoryProvider svnRepositoryProvider) {
		this.svnRepositoryProvider = svnRepositoryProvider;
	}

	public SvnRepositoryProvider getSvnRepositoryProvider() {
		return svnRepositoryProvider;
	}
	
	public String diff(
			  String path,
			  SVNRevision srcRevision) throws Exception {
		
		SvnElement env = svnRepositoryProvider.getSvnConfig();
		SVNURL sourceFile = SVNURL.parseURIEncoded(env.getRootUrl() + "/" + path);
		SVNRepository repository = svnRepositoryProvider.createRepository(sourceFile);
		
		if(log.isDebugEnabled()) {
			log.debug("##diff src:" + sourceFile );
		}
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(svnRepositoryProvider.createAuthManager());
		try {
			SVNLogEntry preLog =  this.getPreviousLogEntry(repository, srcRevision.getNumber(),env.getEndRevision());
		    if(preLog != null)
		    	return runDiff(svnOperationFactory, sourceFile, srcRevision, SVNRevision.create(preLog.getRevision()));
		    else
		    	return runDiff(svnOperationFactory, sourceFile, srcRevision, SVNRevision.create(1));
		}finally {
			svnOperationFactory.dispose();
		}
	}
	
    public String runDiff(SvnOperationFactory svnOperationFactory, SVNURL srcUrl, SVNRevision srcRevision, SVNRevision previousRevision) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
        final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
        
        final SvnDiff diff = svnOperationFactory.createDiff();
        diff.setSource(SvnTarget.fromURL(srcUrl, srcRevision), previousRevision,srcRevision);//SvnTarget.fromURL(url2, svnRevision2));
        diff.setOutput(byteArrayOutputStream);
        diff.setDiffGenerator(diffGenerator);
        diff.run();

        return new String(byteArrayOutputStream.toByteArray()).replace(System.getProperty("line.separator"), "\n");
    }
	
}
