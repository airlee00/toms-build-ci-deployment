package com.toms.scm.build.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCommit;
import org.tmatesoft.svn.core.wc2.SvnCopySource;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRemoteCopy;
import org.tmatesoft.svn.core.wc2.SvnRemoteDelete;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class SVNTest1 {

	private static ISVNAuthenticationManager authManager;
	private static String srcUrl = "svn://localhost/test/trunk/xml-telegraph-spring";
	private static String targetUrl= "svn://localhost/test/tag/1.0/xml-telegraph-spring";
	private static  SVNRepository srcRepository = null;
	private static  SVNRepository targetRepository = null;
	private static Logger log = LoggerFactory.getLogger(SVNTest1.class);
	static {
		DAVRepositoryFactory.setup();
		authManager = SVNWCUtil.createDefaultAuthenticationManager( "airlee",	"1111");
		try {
			srcRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(srcUrl));
			srcRepository.setAuthenticationManager(authManager);
			targetRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(targetUrl));
			targetRepository.setAuthenticationManager(authManager);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static Collection<SVNLogEntry> searchSVN(String url, String name,
			String password, long startRevision, long endRevision,
			final String searchTerm, final String svnUser) throws Exception {
		
		// changed the config folder to avoid conflicting with anthill svn use
//		ISVNAuthenticationManager authManager = SVNWCUtil
//				.createDefaultAuthenticationManager( name,	password);
						//createDefaultAuthenticationManager(new File("/tmp"), name,	password, false);
		//Collection<SVNLogEntry> resultLogEntries = new LinkedList();
/*		Collection<SVNLogEntry> logEntries = repository.log(
				new String[] { "" }, null, startRevision, endRevision, true,
				true);*/
		final Collection<SVNLogEntry> logEntries = new LinkedList<SVNLogEntry>();
			
			srcRepository.log(new String[] { "" }, startRevision, endRevision, true
				, true, 0, false, null, new ISVNLogEntryHandler() {
				//, true, 0, false, new String[] { "--search","#0012" }, new ISVNLogEntryHandler() {
		            public void handleLogEntry(SVNLogEntry logEntry) {
		            	if(logEntry == null || logEntry.getMessage() ==null)
		            		return;
			    		if (logEntry.getMessage().indexOf(searchTerm) > -1) {
							if ((svnUser == null || svnUser.equals(""))
									|| logEntry.getAuthor().equals(svnUser)) {
								logEntries.add(logEntry);
							}
						}
		            }        
		        });

		return logEntries;
	}
	public static void copy(SVNURL sourceFile,SVNURL targetFile,SVNRevision srcRevision,String commitMessage) throws SVNException {
		System.out.println("src:" + sourceFile);
		System.out.println("target:" + targetFile);
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		 svnOperationFactory.setAuthenticationManager(authManager);
//			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( name,	password);
//			//repository.setAuthenticationManager(authManager);
//			System.out.println("---------");
//			
//			//SVNClientManager clientManager = SVNClientManager.newInstance();
//			//clientManager.setAuthenticationManager(authManager);
//			SVNCopyClient client= new  SVNCopyClient(authManager,SVNWCUtil.createDefaultOptions(true) ) ;//clientManager.getCopyClient();
//			boolean isMove = false;
//			boolean failWhenDstExists = false;
//			boolean makeParents = true;
//			String commitMessage = term;
//			client.doCopy(css, dst, isMove, makeParents, failWhenDstExists, commitMessage, null);
        
        final SvnRemoteCopy remoteCopy = svnOperationFactory.createRemoteCopy();
        remoteCopy.addCopySource(SvnCopySource.create(SvnTarget.fromURL(sourceFile, srcRevision), SVNRevision.HEAD));
        remoteCopy.setSingleTarget(SvnTarget.fromURL(targetFile,srcRevision));
        remoteCopy.setMove(false);
        remoteCopy.setFailWhenDstExists(false);
        remoteCopy.setMakeParents(true);
        remoteCopy.setCommitMessage(commitMessage);
        //cp.setRevisionProperties(revisionProperties);
       // cp.setExternalsHandler(SvnCodec.externalsHandler(getExternalsHandler()));
        //cp.setCommitHandler(SvnCodec.commitHandler(getCommitHandler()));
       // cp.setDisableLocalModifications(disableLocalModifications);
        //cp.setCommitParameters(SvnCodec.commitParameters(getCommitParameters()));
        
        
        final SVNCommitInfo commitInfo = remoteCopy.run();
        System.out.println(commitInfo);
	}

	public static void delete(SVNURL targetFile) throws SVNException {
		try {
			System.out.println("target:" + targetFile);
			final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
			svnOperationFactory.setAuthenticationManager(authManager);
			final SvnRemoteDelete remote = svnOperationFactory
					.createRemoteDelete();
			remote.addTarget(SvnTarget.fromURL(targetFile));

			final SVNCommitInfo commitInfo = remote.run();
			System.out.println(commitInfo);
		} catch (Exception e) {
			System.err.println(" Error target :" + targetFile);
		}
	}
	public static void commit(SVNURL targetFile) throws SVNException {
		try {
			System.out.println("target:" + targetFile);
			final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
			svnOperationFactory.setAuthenticationManager(authManager);
			
            final String commitMessage = "Commit message with " + "\r\n" + "CRLF";

            final SvnCommit commit = svnOperationFactory.createCommit();
            commit.setCommitMessage(commitMessage);
            commit.setSingleTarget(SvnTarget.fromURL(targetFile));
            final SVNCommitInfo commitInfo = commit.run();
            System.out.println("new revision : " + commitInfo.getNewRevision());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(" Error target :" + targetFile);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String url = "svn://localhost/test/tag/1.0/xml-telegraph-spring/src/main/java/com/lotte/integration/telegraph/MessageConverter.java";
		SVNURL src = SVNURL.parseURIEncoded(url);
		commit(src);
/*		
		String rootUrl ="svn://localhost/test";
		
		String name ="airlee";
		String password="1111";
		long start = 0;
		long end = -1;
		String term ="IssueNumber #4";
		Collection<SVNLogEntry> e1 = searchSVN(srcUrl,name,password,start,end,term,null);

		for (SVNLogEntry e : e1) {
			log.info("----------------------");
			System.out.println("##auth:" +e.getAuthor());
			System.out.println("##message:" +e.getMessage());
			System.out.println("##revision:"+e.getRevision());
			System.out.println("##date:"+e.getDate());
			log.info("##path:" + e.getChangedPaths());
			Map<String, SVNLogEntryPath> paths = e.getChangedPaths();
			for (Map.Entry<String, SVNLogEntryPath> entry : paths.entrySet()) {
				System.out.println("===========================================");
				SVNLogEntryPath p = entry.getValue();
				System.out.println("Key : " + entry.getKey() 
						+ "\n Value : "
					+ p.getPath() +"," + p.getCopyRevision() +"," + p.getType() + "," + p.getKind());
				
//				SVNURL src = SVNURL.parseURIEncoded(rootUrl+p.getPath());
//				SVNURL target = SVNURL.parseURIEncoded(targetUrl+p.getPath().replace("/trunk/xml-telegraph-spring",""));
//				
//				SVNNodeKind srcNodeKind = srcRepository.checkPath(p.getPath(), -1);
//				System.out.println(">>:"+srcNodeKind);
//
//				SVNNodeKind nodeKind = targetRepository.checkPath(p.getPath().replace("/trunk/xml-telegraph-spring","/tag/1.0/xml-telegraph-spring"), -1);
//			
				
//				if( nodeKind  != SVNNodeKind.NONE )
//					delete(target);
//				if(srcNodeKind != SVNNodeKind.NONE)
//				copy(src,target,SVNRevision.create(e.getRevision()),e.getMessage());
				
			}
		}
*/
	}
}
