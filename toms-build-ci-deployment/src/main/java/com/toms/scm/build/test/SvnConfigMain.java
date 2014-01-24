package com.toms.scm.build.test;

import java.util.List;

import com.toms.scm.build.support.DefaultSvnBuilder;
import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.build.svn.support.DefaultSvnClient;
import com.toms.scm.build.svn.support.DefaultSvnRepositoryProvider;
import com.toms.scm.core.build.Builder;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.SvnElement;
import com.toms.scm.core.config.reader.SvnConfigurationReader;
import com.toms.scm.core.io.SvnEntryInfo;



public class SvnConfigMain {

	public static void main(String[] args) throws Exception {
		String term ="SS2-1";
		String appReporsitoryName = "xml-telegraph-spring";
		String configXmlLocation ="classpath:com/toms/scm/builder/test/svn-config-sample2.xml";
        ConfigurationReader<SvnElement> cr = new SvnConfigurationReader(configXmlLocation);
		
		SvnElement env = cr.getConfiguration();
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
