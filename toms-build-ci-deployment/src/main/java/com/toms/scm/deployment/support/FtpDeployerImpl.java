package com.toms.scm.deployment.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.toms.scm.build.support.DefaultSvnBuilder;
import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.build.svn.support.DefaultSvnClient;
import com.toms.scm.build.svn.support.DefaultSvnRepositoryProvider;
import com.toms.scm.core.BuildException;
import com.toms.scm.core.build.Builder;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.ExcludeElement;
import com.toms.scm.core.config.definition.FilesetElement;
import com.toms.scm.core.config.definition.FtpElement;
import com.toms.scm.core.config.definition.IncludeElement;
import com.toms.scm.core.config.definition.ProjectElement;
import com.toms.scm.core.config.definition.ReplaceElement;
import com.toms.scm.core.config.definition.SvnElement;
import com.toms.scm.core.config.definition.TargetElement;
import com.toms.scm.core.config.reader.ProjectConfigurationReader;
import com.toms.scm.core.deployment.Deployer;
import com.toms.scm.core.io.SvnEntryInfo;
import com.toms.scm.deployment.remoting.FtpClientSupport;

public class FtpDeployerImpl implements Deployer {

    protected Logger log = LoggerFactory.getLogger(FtpDeployerImpl.class);

    /** project information */
    private ConfigurationReader<ProjectElement> configReader;
    
    private ProjectElement projectDefinition;
    
    /** svn entry files for issueNumber **/
    private List<SvnEntryInfo> buildFiles;
    
    /** pattern matcher **/
    private PathMatcher pathMather = new AntPathMatcher();
    
    /**
     * constructor
     * @param configXmlLocation file:/path/to/xml , classpath:..
     * @throws Exception
     */
    public FtpDeployerImpl(String configXmlLocation) throws Exception {
        this.configReader = new ProjectConfigurationReader(configXmlLocation);
        this.projectDefinition = this.configReader.getConfiguration();
    }

    /**
     * start deployment
     * @param repositoryName
     * @param issueNumber
     */
    public void deploy( String repositoryName, String issueNumber)
            throws Exception {
        if(log.isInfoEnabled()) {
            log.info("[Deploy] main start....");
        }
        //search the list of files That corresponds to the issues# 
        buildFiles = this.searchBuildFiles(projectDefinition.getSvnConfig(), repositoryName, issueNumber);
        
        if (buildFiles.size() < 1) {
            log.warn("[deploy] Build files not found...");
            throw new BuildException("Build files not found");
        } else {
            log.info("[deploy] Build target files=\n" + buildFiles);
        }
        //project tag->target tag
        for (TargetElement target : projectDefinition.getTarget()) {
            deploy(target);
        }
        if(log.isInfoEnabled()) {
            log.info("[Deploy] main end....");
        }
    }
    /**
     * deploy with target config tag
     * @param target
     * @throws Exception
     */
    private void deploy(TargetElement target) throws Exception {
        if(log.isInfoEnabled()) {
            log.info("[Deploy target start] target=" + target.getName() +"...");
        }
        for (FtpElement ftp : target.getFtpConfig()) {
            deploy(ftp);
        }
        if(log.isInfoEnabled()) {
            log.info("[Deploy target end] target=" + target.getName() +"...");
        }
    }
    /**
     * deploy with ftp config tag
     * @param ftpConfig
     * @throws Exception
     */
    private void deploy(FtpElement ftpConfig) throws Exception {
        if(log.isInfoEnabled()) {
            log.info("[Deploy-ftpConfig]  start...." + ftpConfig.getHost());
        }
        // ftp connect
        FtpClientSupport support = new FtpClientSupport(ftpConfig);
        FTPClient client = null;
        try {
            if(ftpConfig.isActualTransfer()){
                client = support.connect();
            }
            for (FilesetElement fileset : ftpConfig.getFileset()) {
                List<String> convertedList = compareAndConvert(fileset);

                for (String filename : convertedList) {
                    String srcFilename = projectDefinition.getBasedir() + fileset.getDir() + filename;
                    String remoteFilename = ftpConfig.getRootPath() + filename;
                    if (log.isInfoEnabled()) {
                        log.info("[transfer] Source=" + srcFilename + "\n\t-->Remote="
                                + remoteFilename);
                    }
                    if(ftpConfig.isActualTransfer()){
                        support.sendFile(client, srcFilename,remoteFilename);
                    }
                }//end for
            }//end for
        } finally {
            if (client != null)
                support.disconnect(client);
        }
        if(log.isInfoEnabled()) {
            log.info("[Deploy-ftpConfig] end...." + ftpConfig.getHost());
        }
    }
    /**
     * Compare the list of changed files and fileset tag
     * 
     * @param fileset filesettag
     */
    
    private List<String> compareAndConvert(FilesetElement fileset) {
        String basePath = fileset.getDir();
        if (log.isInfoEnabled()) {
            log.info("[Fileset start....] basePath=" + basePath);
        }
        List<String> convertedList = new ArrayList<String>();
        for (SvnEntryInfo info : buildFiles) {
            // log
            String path = info.getPath();
            if (log.isInfoEnabled()) {
                log.info("\t[fileset] orginal path=" + path);
            }
            for (ReplaceElement replace : fileset.getReplace()) {
                path = path.replace(replace.getFrom(), replace.getTo());
            }
            if (log.isInfoEnabled()) {
                log.info("\t\t<replace> renamed=" + path);
            }
            if (fileset.getInclude().size() > 0) {
                for (IncludeElement include : fileset.getInclude()) {
                    boolean match = pathMather.match(include.getName(), path);
                    if (log.isInfoEnabled()) {
                        log.info("\t\t<include> match=" + match + ",include.getName()="
                                + include.getName());
                    }
                    if (match) {
                        convertedList.add(path);
                    }

                }
            } else {
                convertedList.add(path);
            }
            if (log.isInfoEnabled()) {
                log.info("\t\tbefore exclude=" + convertedList);
            }
            for (ExcludeElement exclude : fileset.getExclude()) {
                boolean match = pathMather.match(exclude.getName(), path);
                if (log.isDebugEnabled()) {
                    log.debug("\t\t<exclude> match=" + match + ",exclude.getName()="
                            + exclude.getName());
                }
                if (match) {
                    convertedList.remove(path);
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("[Fileset end..] After converted=\n" + convertedList);
        }
        return convertedList;
    }
    /**
     * search the list of files That corresponds to the issues# 
     * @param svnElement
     * @param repositoryName
     * @param issueNumber
     * @return
     * @throws Exception
     */
    protected List<SvnEntryInfo> searchBuildFiles(SvnElement svnElement,
            String repositoryName, String issueNumber) throws Exception {

        SvnRepositoryProvider p = new DefaultSvnRepositoryProvider(svnElement);
        SvnClient c = new DefaultSvnClient(p);

        Builder cmd = new DefaultSvnBuilder(c);
        return cmd.log(repositoryName, issueNumber);
    }
}
