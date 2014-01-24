package com.toms.scm.deployment.remoting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toms.scm.core.BuildException;
import com.toms.scm.core.config.definition.FtpElement;
import com.toms.util.FileUtils;

/**
 * FTP client
 * 
 * @author apache ant 1.3
 * @author airlee00@gmail.com
 * @version 1.0
 */
public class FtpClientSupport {

    private Logger     logger      = LoggerFactory.getLogger(FtpClientSupport.class);

    private FtpElement config      = null;

    private int        skipped     = 0;
    private int        transferred = 0;

    private Set<File>  dirCache    = new HashSet<File>();

    /**
     * constructor
     * 
     * @param ftpElement
     */
    public FtpClientSupport(FtpElement config) {
        this.config = config;
    }

    /**
     * resolve file separator "\" -> "/"
     * 
     * @param file
     * @return
     */
    protected String resolveFile(String file) {
        return file.replace(System.getProperty("file.separator").charAt(0), config
                .getRemoteFileSeparator().charAt(0));
    }

    /**
     * sending file
     * 
     * @param ftpClient
     * @param sourceFilename
     *            local file name
     * @param targetFilename
     *            remote target file name
     * @throws IOException
     * @throws BuildException
     */
    public void sendFile(FTPClient ftpClient, String sourceFilename, String targetFilename)
            throws IOException, BuildException {
        InputStream instream = null;
        try {
            String resolvedSourceFilename = resolveFile(sourceFilename);

            String resolvedTargetFilename = resolveFile(targetFilename);

            File file = new File(resolvedSourceFilename);

            if (config.isVerbose()) {
                logger.info("transferring " + file.getAbsolutePath());
            }

            instream = new BufferedInputStream(new FileInputStream(file));

            createParents(ftpClient, resolvedTargetFilename);

            ftpClient.storeFile(resolvedTargetFilename, instream);

            boolean success = FTPReply.isPositiveCompletion(ftpClient.getReplyCode());

            if (!success) {
                String s = "could not put file: " + ftpClient.getReplyString();

                if (config.isSkipFailedTransfers()) {
                    logger.warn(s);
                    skipped++;
                } else {
                    throw new BuildException(s);
                }

            } else {
                // see if we should issue a chmod command
                if (config.getChmod() != null) {
                    doSiteCommand(ftpClient, "chmod " + config.getChmod() + " "
                            + resolvedTargetFilename);
                }
                logger.debug("File " + file.getAbsolutePath() + " copied to "
                        + this.getConfig().getHost());
                transferred++;
            }
        } finally {
            FileUtils.close(instream);
        }
    }

    /**
     * Sends a site command to the ftp server
     * 
     * @param ftpClient
     *            ftp client
     * @param theCMD
     *            command to execute
     * @throws IOException
     *             in unknown circumstances
     * @throws BuildException
     *             in unknown circumstances
     */
    protected void doSiteCommand(FTPClient ftpClient, String theCMD) throws IOException,
            BuildException {
        boolean rc;
        String[] myReply = null;

        logger.debug("Doing Site Command: ");

        rc = ftpClient.sendSiteCommand(theCMD);

        if (!rc) {
            logger.warn("Failed to issue Site Command: " + theCMD);
        } else {

            myReply = ftpClient.getReplyStrings();

            for (int x = 0; x < myReply.length; x++) {
                if (myReply[x] != null && myReply[x].indexOf("200") == -1) {
                    logger.warn(myReply[x]);
                }
            }
        }
    }

    /**
     * create parent dir
     * 
     * @param ftpClient
     * @param filename
     * @throws IOException
     * @throws BuildException
     */
    protected void createParents(FTPClient ftpClient, String filename) throws IOException,
            BuildException {

        File dir = new File(filename);
        if (dirCache.contains(dir)) {
            return;
        }

        Vector<File> parents = new Vector<File>();
        String dirname;

        while ((dirname = dir.getParent()) != null) {
            File checkDir = new File(dirname);
            if (dirCache.contains(checkDir)) {
                break;
            }
            dir = checkDir;
            parents.addElement(dir);
        }

        // find first non cached dir
        int i = parents.size() - 1;

        if (i >= 0) {
            String cwd = ftpClient.printWorkingDirectory();
            String parent = dir.getParent();
            if (parent != null) {
                if (!ftpClient.changeWorkingDirectory(resolveFile(parent))) {
                    throw new BuildException("could not change to " + "directory: "
                            + ftpClient.getReplyString());
                }
            }

            while (i >= 0) {
                dir = (File) parents.elementAt(i--);
                String path = resolveFile(dir.getPath());
                // check if dir exists by trying to change into it.
                if (!ftpClient.changeWorkingDirectory(path)) {
                    // could not change to it - try to create it
                    logger.debug("creating remote directory " + resolveFile(dir.getPath()));
                    if (!ftpClient.makeDirectory(dir.getName())) {
                        handleMkDirFailure(ftpClient);
                    }
                    if (!ftpClient.changeWorkingDirectory(path)) {
                        throw new BuildException("could not change to " + "directory: "
                                + ftpClient.getReplyString());
                    }
                }
                dirCache.add(dir);
            }
            ftpClient.changeWorkingDirectory(cwd);
        }
    }

    /**
     * look at the response for a failed mkdir action, decide whether it matters
     * or not. If it does, we throw an exception
     * 
     * @param ftpClient
     *            current ftp connection
     * @throws BuildException
     *             if this is an error to signal
     */
    private void handleMkDirFailure(FTPClient ftpClient) throws BuildException {
        int rc = ftpClient.getReplyCode();
        if (!(config.isIgnoreNoncriticalErrors() && (rc == 550 || rc == 553 || rc == 521))) {
            throw new BuildException("could not create directory: " + ftpClient.getReplyString());
        }
    }

    /**
     * get remote file
     * 
     * @param filename
     *            대상 파일명
     * @param os
     *            파일을 담아낼 OutputStream
     * @return
     */
    public void getFile(FTPClient ftpClient, String filename, OutputStream outstream)
            throws IOException, BuildException {
        try {
            String resolvedFilename = this.resolveFile(filename);

            File file = new File(resolvedFilename);

            if (config.isVerbose()) {
                logger.info("transferring " + resolvedFilename + " to " + file.getAbsolutePath());
            }

            File pdir = file.getParentFile();

            if (!pdir.exists()) {
                pdir.mkdirs();
            }
            if (outstream == null)
                outstream = new BufferedOutputStream(new FileOutputStream(file));

            ftpClient.retrieveFile(resolvedFilename, outstream);

            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                String s = "could not get file: " + ftpClient.getReplyString();

                if (config.isSkipFailedTransfers()) {
                    logger.warn(s);
                    skipped++;
                } else {
                    throw new BuildException(s);
                }

            } else {
                logger.debug("File " + file.getAbsolutePath() + " copied from "
                        + this.getConfig().getHost());
                transferred++;
            }
        } finally {
            FileUtils.close(outstream);
        }
    }

    /**
     * delete remote file
     * 
     * @param filename
     *            remote target filename
     * @return
     */
    public void delFile(FTPClient ftpClient, String filename) throws IOException, BuildException {
        if (config.isVerbose()) {
            logger.info("deleting " + filename);
        }

        if (!ftpClient.deleteFile(resolveFile(filename))) {
            String s = "could not delete file: " + ftpClient.getReplyString();

            if (config.isSkipFailedTransfers()) {
                logger.warn(s);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        } else {
            logger.debug("File " + filename + " deleted from " + this.getConfig().getHost());
            transferred++;
        }
    }

    /**
     * delete remote dir
     * 
     * @param ftpClient
     * @param dirname
     * @throws IOException
     * @throws BuildException
     */
    public void rmDir(FTPClient ftpClient, String dirname) throws IOException, BuildException {
        if (config.isVerbose()) {
            logger.info("removing " + dirname);
        }

        if (!ftpClient.removeDirectory(resolveFile(dirname))) {
            String s = "could not remove directory: " + ftpClient.getReplyString();

            if (config.isSkipFailedTransfers()) {
                logger.warn(s);
                skipped++;
            } else {
                throw new BuildException(s);
            }
        } else {
            logger.warn("Directory " + dirname + " removed from " + this.getConfig().getHost());
            transferred++;
        }
    }

    /**
     * connect ftp
     * 
     * @return ftpClient
     */
    public FTPClient connect() throws Exception {
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            ftp.configure(conf);

            ftp.connect(config.getHost(), config.getPort());

            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.error("FTP server refused connection.");
                throw new BuildException("FTP connection failed: " + ftp.getReplyString());
            }
            // boolean isLogin = ftp.login(config.getUsername(),
            // JasyptEncryptUtils.decrypt(pw));
            boolean isLogin = ftp.login(config.getUsername(), config.getPassword());
            if (!isLogin) {
                logger.error("login error");
                throw new BuildException("Could not login to FTP server");
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            if (config.getRootPath() != null)
                ftp.changeWorkingDirectory(config.getRootPath());

            return ftp;
        } catch (Exception e) {
            throw new BuildException("error during FTP transfer: " + e, e);
        }
    }

    /**
     * disconnect ftp
     * 
     * @param ftpClient
     */
    public void disconnect(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                logger.warn("disconnecting");
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                // ignore it
            }
        }
    }

    // getter , setter
    public FtpElement getConfig() {
        return config;
    }

    public void setConfig(FtpElement config) {
        this.config = config;
    }

}
