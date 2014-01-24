package com.toms.scm.core.config.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("project")
public class ProjectElement implements Serializable{
	@XStreamOmitField
	private static final long serialVersionUID = 1L;
	@XStreamAsAttribute
	private String name;
	
	@XStreamAlias("default")
	@XStreamAsAttribute
	private String defaultTarget;
	
	@XStreamAlias("basedir")
	@XStreamAsAttribute
	private String basedir;
	
	@XStreamImplicit
	private List<TargetElement> target = new ArrayList<TargetElement>();
	
	@XStreamAlias("svn")
	private SvnElement svnElement;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultTarget() {
		return defaultTarget;
	}

	public void setDefaultTarget(String defaultTarget) {
		this.defaultTarget = defaultTarget;
	}

	public String getBasedir() {
		return basedir;
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public List<TargetElement> getTarget() {
		return target;
	}

	public SvnElement getSvnConfig() {
		return svnElement;
	}

	public void setSvnConfig(SvnElement svnElement) {
		this.svnElement = svnElement;
	}

	public void setTarget(List<TargetElement> target) {
		this.target = target;
	}

	public static void main(String[] args) {
		
		ExcludeElement e = new ExcludeElement();
		e.setName("**/*.jar");
		
		IncludeElement i = new IncludeElement();
		i.setName("**/*.class");
		
		ReplaceElement con = new ReplaceElement();
		con.setFrom("java");
		con.setTo("class");
		
		ReplaceElement con2 = new ReplaceElement();
		con2.setFrom("/trunk/xml");
		con2.setTo("/data/dev");		
		
		FilesetElement f = new FilesetElement();
		f.setDir("/WEB-INF/*");
		f.getExclude().add(e);
		f.getInclude().add(i);
		f.getReplace().add(con2);
		f.getReplace().add(con);
		
		FtpElement c = new FtpElement();
		c.setHost("host");
		c.setPassword("password");
		c.setUsername("deploy");
		c.setPort(21);
		c.setRootPath("/data/dev");
		c.getFileset().add(f);
		
		TargetElement t = new TargetElement();
		t.setName("fffff");
		t.getFtpConfig().add(c);
		
		SvnElement config = new SvnElement();
		
		ProjectElement p = new ProjectElement();
		p.setBasedir(".");
		p.setDefaultTarget("svn");
		p.getTarget().add(t);
		p.setSvnConfig(config);
		
		XStream xstream = new XStream();
		//xstream.setMode(XStream.NO_REFERENCES);
		xstream.processAnnotations(ProjectElement.class);
		String aa = xstream.toXML(p);
		System.out.println(aa);
//		
//		XStream xstream = new XStream();
//		xstream.processAnnotations(ProjectElement.class);
//		ProjectElement p2 = (ProjectElement) xstream.fromXML(new java.io.File("D:/FrameFIT_v2.1.0/workspace-inni/jenkins-svn-deploy/src/main/java/com/toms/deploy/config/build.xml"));
//		System.out.println(p2);
	}
}
