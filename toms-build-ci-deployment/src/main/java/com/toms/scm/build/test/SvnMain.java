package com.toms.scm.build.test;

import java.util.List;

import com.toms.scm.build.support.DefaultSvnBuilder;
import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.build.svn.support.DefaultSvnClient;
import com.toms.scm.build.svn.support.DefaultSvnRepositoryProvider;
import com.toms.scm.core.build.Builder;
import com.toms.scm.core.config.definition.SvnElement;
import com.toms.scm.core.io.SvnEntryInfo;



public class SvnMain {

	public static void main(String[] args) throws Exception {
		String term ="테스트입니다";
		String appReporsitoryName = "hone-integration-zookeeper";

		 String rootUrl = "svn://localhost:3690/test_repo";
		 String srcRootPath = "/trunk";
		 String targetRootPath = "/tag/1.0";
		 String userName = "test";
		 String password = "test1234";


		SvnElement env = new SvnElement(rootUrl, srcRootPath, targetRootPath, userName, password);
		SvnRepositoryProvider p = new DefaultSvnRepositoryProvider(env);
		SvnClient c = new DefaultSvnClient(p);

		Builder cmd = new DefaultSvnBuilder(c);
		List<SvnEntryInfo> list  = cmd.log(appReporsitoryName,term);
		for(SvnEntryInfo vo : list) {
			String aa = cmd.diff(vo.getPath(),vo.getRevision());
			System.out.println("========REV:" + vo.getRevision() + "," + vo.getPath() +"," + aa);
		}

		//cmd.copy(appReporsitoryName, term,true);

	}
}
