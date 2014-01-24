package com.toms.scm.build.test;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNTest3 {

	public static void main(String[] args) throws SVNException {
		String repositoryUrl = "http://127.0.0.1:9880/repo1";
		String username = "taro";
		String password = "taro";
		Map<Character, String> actionTypeMap = new HashMap<Character, String>();
		actionTypeMap.put(SVNLogEntryPath.TYPE_ADDED, "Added");
		actionTypeMap.put(SVNLogEntryPath.TYPE_DELETED, "Deleted");
		actionTypeMap.put(SVNLogEntryPath.TYPE_MODIFIED, "Modified");
		actionTypeMap.put(SVNLogEntryPath.TYPE_REPLACED, "Replaced");

		// This line is necessary when using svnkit 1.3.5
		// DAVRepositoryFactory.setup();

		SVNURL repositorySVNURL = SVNURL.parseURIDecoded(repositoryUrl);
		SVNRepository repository = SVNRepositoryFactory
				.create(repositorySVNURL);
		ISVNAuthenticationManager authenticationManager = SVNWCUtil
				.createDefaultAuthenticationManager(username, password);
		repository.setAuthenticationManager(authenticationManager);

		long startRevision = 2;
		long endRevision = -1; // HEAD (the latest) revision

		Collection<SVNLogEntry> logEntries = repository.log(
				new String[] { "" }, null, startRevision, endRevision, true,
				true);

		for (SVNLogEntry logEntry : logEntries) {
			System.out.println("———————————————");
			System.out.println("revision        : " + logEntry.getRevision());
			System.out.println("author          : " + logEntry.getAuthor());
			System.out.println("date            : " + logEntry.getDate());
			System.out.println("log message     : " + logEntry.getMessage());
			System.out.println("commited files  : ");

			Map<String, SVNLogEntryPath> logEntryPathMap = logEntry
					.getChangedPaths();

			if (logEntryPathMap.size() > 0) {
				Set<String> logEntryPathNames = logEntryPathMap.keySet();

				for (String logEntryPathName : logEntryPathNames) {
					SVNLogEntryPath logEntryPath = logEntryPathMap
							.get(logEntryPathName);

					if (logEntryPath.getKind() == SVNNodeKind.FILE) {
						String actionString = actionTypeMap.get(logEntryPath
								.getType());
						System.out.println("   Action       : " + actionString);
						System.out.println("   File name    : "
								+ logEntryPath.getPath());
						System.out.println("   File content :");

						if (!actionString.equals("Deleted")) {
							SVNProperties fileProperties = new SVNProperties();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();

							repository.getFile(logEntryPathName,
									logEntry.getRevision(), fileProperties,
									baos);

							String fileContentString = baos.toString();
							System.out.println(fileContentString);
						}
					}
				}
			}
		}

	}
}
