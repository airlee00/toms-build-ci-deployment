package com.toms.scm.build.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.core.build.Builder;
import com.toms.scm.core.config.definition.SvnElement;
import com.toms.scm.core.io.SvnEntryInfo;

/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class DefaultSvnBuilder extends AbstractSvnBuilder implements Builder {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	public DefaultSvnBuilder() {
		super();
	}
	
	public DefaultSvnBuilder(SvnClient svnClient) {
		super(svnClient);
		// TODO Auto-generated constructor stub
	}
	

	protected void copyInternal(String path,long revision,String commitMessage) throws SVNException {
		String _path = svnClient.getSvnRepositoryProvider().getSvnConfig().getRootUrl() + "/" + path;
		if(this.isFile(_path)) {
			
			//svnCopyClient.setSvnRepositoryProvider(this.getSvnRepositoryProvider());
			commitMessage = commitMessage +" -org revision:" + revision;
			SVNCommitInfo commitInfo = svnClient.copy(path, SVNRevision.create(revision), commitMessage);
			if(log.isInfoEnabled()) {
				log(log,3,"[Copy  ]" + commitInfo +",path="+_path);
			}
		}else {
			if(log.isInfoEnabled()) {
				log(log,3,"[Copy  ] Not exists or file =" +_path);
			}
		}
	}
	
	public void delete(String srcPath) throws Exception {
		SvnElement env = svnClient.getSvnRepositoryProvider().getSvnConfig();
		String _targetPath = env.getRootUrl() +"/" +srcPath.replace(env.getSrcRootPath(),env.getTargetRootPath()); 
		if(this.isFile(_targetPath )) {
			SVNURL targetFile = SVNURL.parseURIEncoded(_targetPath);
			
			SVNCommitInfo commitInfo = svnClient.delete(targetFile);
			if(log.isInfoEnabled()) {
				log(log,3,"[Delete]" + commitInfo +",Path =" +_targetPath);
			}
		}else {
			if(log.isInfoEnabled()) {
				log(log,3,"[Delete] Not exists or file =" +_targetPath);
			}
		}
	}
	
	//String term ="IssueNumber #4";
	public void copy(String appReporsitoryName,String searchTerm,boolean realCopy) throws Exception {
		if(log.isInfoEnabled()) {
			log.info("[SVN Start] copy term ( " + searchTerm +" )");
		}
		//issue#에 해당하는 entry를 찾아온다. 
		Collection<SVNLogEntry> svnLogEntrys = this.searchSvnLogEntry(appReporsitoryName,searchTerm);
		
		List<String> executedList = new ArrayList<String>();

		int index = 0;
		
		for (SVNLogEntry logEntry : svnLogEntrys) {
			if(log.isInfoEnabled()) {
				log(log,1,"[--------------- " + (index) + " START ---------------------------");
				logLogEntry(log,logEntry);
			}
			Map<String, SVNLogEntryPath> paths = logEntry.getChangedPaths();
			//int innerIndex = 0;
			for (Map.Entry<String, SVNLogEntryPath> entry : paths.entrySet()) {
				/*if(log.isInfoEnabled()) {
					log(log,2,"<-----------[" + (innerIndex) + "] INNER BEGIN------------");
				}*/
				SVNLogEntryPath svnPath = entry.getValue();
				if(log.isInfoEnabled()) {
					this.logLogEntryPath(log, svnPath);
				}
				if(executedList.contains(entry.getKey())){
					if(log.isInfoEnabled()) {
						log(log,2, "[Skip] Already executed, so skipped ");
					}
				}else {
					
					if(realCopy) {
						//기존파일이 target에 존재하면 삭제를 먼저함.
						delete( svnPath.getPath());
						//target에 해당 revision파일을 복사함.
						copyInternal(svnPath.getPath(),logEntry.getRevision(),logEntry.getMessage());
					}
					//실행결과에 등록
					executedList.add(entry.getKey());
				}
				if(log.isInfoEnabled()) {
				    log(log, 2, svnPath.getPath());
					//log(log,2,"------------[" + (innerIndex++) + "] INNER END ------------->");
				}
			}
			if(log.isInfoEnabled()) {
				log(log,1,"---------------- " + (index++) + " END -----------------------------]\n");
			}
		}
		
		if(log.isInfoEnabled()) {
			log.info("[SVN END] copy term ( "+ searchTerm +" )\n\n------------------------------------");
			log.info("[Result] copied target file list ");
			for(String name : executedList) {
				log(log,1,name);
			}
			log.info("[TARGET] targetPath=[" + svnClient.getSvnRepositoryProvider().getSvnConfig().getTargetRootPath() + "/" + appReporsitoryName +"]");
		}
		
	}

	public List<SvnEntryInfo> log(String appReporsitoryName,String searchTerm) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("[SVN Start] search term ( " + searchTerm + " )");
		}
		// issue#에 해당하는 entry를 찾아온다.
		Collection<SVNLogEntry> svnLogEntrys = this.searchSvnLogEntry(appReporsitoryName,searchTerm);

		List<String> dupCheckList = new ArrayList<String>();
		List<SvnEntryInfo> executedList = new ArrayList<SvnEntryInfo>();

		int index = 0;

		for (SVNLogEntry logEntry : svnLogEntrys) {
			if (log.isInfoEnabled()) {
				log(log, 1, "[--------------- " + (index)+ " START ---------------------------");
				logLogEntry(log, logEntry);
			}
			Map<String, SVNLogEntryPath> paths = logEntry.getChangedPaths();
			//int innerIndex = 0;
			for (Map.Entry<String, SVNLogEntryPath> entry : paths.entrySet()) {
				/*if (log.isDebugEnabled()) {
					log(log, 2, "<-----------[" + (innerIndex)+ "] INNER BEGIN------------");
				}*/
				SVNLogEntryPath svnPath = entry.getValue();
				SvnEntryInfo vo = new SvnEntryInfo();
				vo.setPath(svnPath.getPath());
				vo.setRevision(logEntry.getRevision());
				vo.setKind(svnPath.getKind().toString());
				vo.setMessage(logEntry.getMessage());
				vo.setType(svnPath.getType());
				vo.setDestPath(svnClient.getSvnRepositoryProvider().getSvnConfig().getTargetRootPath() + "/" + appReporsitoryName);

				if (dupCheckList.contains(entry.getKey())) {
					if (log.isInfoEnabled()) {
						log(log, 2, "[Skip] Already executed, so skipped ");
					}
					vo.setSkip(true);
				} else {
					vo.setSkip(false);

					// 실행결과에 등록
					dupCheckList.add(entry.getKey());
					executedList.add(vo);
				}
				if (log.isInfoEnabled()) {
					log(log, 2, svnPath.getPath());
					//log(log, 2, "------------[" + (innerIndex++)+ "] INNER END ------------->");
				}
			}
			if (log.isInfoEnabled()) {
				log(log, 1, "---------------- " + (index++)+ " END -----------------------------]\n");
			}
		}
        if (log.isInfoEnabled()) {
            log(log, 1, "[list] = " +executedList);
            log.info("[SVN End] search term ( " + searchTerm + " )");
        }
		return executedList;
	}
	
	public String diff(String srcPath,long srcRevision) throws Exception {
		return svnClient.diff(srcPath, SVNRevision.create(srcRevision));
	}
}
