package com.toms.scm.build.support;

import java.util.List;

import com.toms.scm.build.svn.SvnClient;
import com.toms.scm.build.svn.SvnRepositoryProvider;
import com.toms.scm.build.svn.support.DefaultSvnClient;
import com.toms.scm.build.svn.support.DefaultSvnRepositoryProvider;
import com.toms.scm.core.build.Builder;
import com.toms.scm.core.config.ConfigurationReader;
import com.toms.scm.core.config.definition.SvnElement;
import com.toms.scm.core.config.reader.SvnConfigurationReader;
import com.toms.scm.core.io.SvnEntryInfo;



/**
 * 
 * @author airlee@naver.com
 * @since 2013-10-04
 */
public class SvnBuildExecutor {

	public static void main(String[] args) throws Exception {
		if(args.length < 4) {
			System.out.println("[Usage] SvnBuildExecutor [configXmlLocation] [C|L|P] [issue#] [appReposioryName]");
			System.exit(1);
		}
		//ex) configXmlLocation=>"classpath:com/toms/scm/builder/test/svn-config-sample2.xml"
		String configXmlLocation = args[0];
		String executeMode  = args[1];
		String issueNumber = args[2] ; //"IssueNumber #" + args[6];
		String appReporsitoryName = args[3];//kr.ng.co.co 와 같은 svn repository명 
		
	    ConfigurationReader<SvnElement> cr = new SvnConfigurationReader(configXmlLocation);
	        
	    SvnElement env = cr.getConfiguration();
	        
		SvnRepositoryProvider p = new DefaultSvnRepositoryProvider(env);
		SvnClient client = new DefaultSvnClient(p);
		Builder c = new DefaultSvnBuilder(client);
		
		if("C".equals(executeMode)) {
			c.copy(appReporsitoryName, issueNumber, true);
		}else if("P".equals(executeMode)) {
			c.copy(appReporsitoryName, issueNumber, false);
		}else if("L".equals(executeMode)) {
			List<SvnEntryInfo> vo = c.log(appReporsitoryName, issueNumber);
			System.out.println(vo);
		}else {
			System.out.println("[Error] mode code is not [C|L|P] ");
			System.exit(1);
		}
	}
}
