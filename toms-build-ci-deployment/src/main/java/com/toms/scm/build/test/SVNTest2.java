/*
 * ====================================================================
 * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://svnkit.com/license.html.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package com.toms.scm.build.test;

import java.util.Collection;
import java.util.LinkedList;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * @version 1.3
 * @author TMate Software Ltd.
 */
public class SVNTest2 {


    public static void main(String[] args) throws SVNException {
        DAVRepositoryFactory.setup();
        FSRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        
        
        SVNURL url = SVNURL.parseURIEncoded("svn://localhost/test/trunk/xml-telegraph-spring");
        SVNRepository repository = SVNRepositoryFactory.create(url);
        final Collection logs = new LinkedList();
        long fromRevision = -1;
        long toRevision = 0;
        String path = "";
        repository.log(new String[] {path}, fromRevision, toRevision, true, true, 2 /* limit */, 
                false, null, new ISVNLogEntryHandler() {
            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                logs.add(logEntry);
            }
        });
        System.out.println(logs);
    }
}

