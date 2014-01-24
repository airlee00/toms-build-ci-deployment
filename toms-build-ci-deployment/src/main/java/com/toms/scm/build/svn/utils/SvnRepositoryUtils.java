package com.toms.scm.build.svn.utils;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class SvnRepositoryUtils {

    public static SVNNodeKind getNodeKind(SVNURL url, String path) throws SVNException {
        final SVNRepository svnRepository = SVNRepositoryFactory.create(url);
        try {
            return svnRepository.checkPath(path, SVNRepository.INVALID_REVISION);
        } finally {
            svnRepository.closeSession();
        }
    }
    
    public static boolean isFile(SVNURL url, String path) throws SVNException {
        final SVNNodeKind nodeKind = getNodeKind(url, path);
        return nodeKind == SVNNodeKind.FILE;
    }
    
    public static boolean exists(SVNURL url, String path) throws SVNException {
        final SVNNodeKind nodeKind = getNodeKind(url, path);
        return nodeKind == SVNNodeKind.DIR || nodeKind == SVNNodeKind.FILE;
    }
    
}
