package com.xiaokaceng.openci.pojo;

import org.openkoala.opencis.jenkins.JenkinsCISClient;
import org.openkoala.opencis.jenkins.KoalaGitConfig;
import org.openkoala.opencis.jenkins.KoalaScmConfig;
import org.openkoala.opencis.jenkins.KoalaSvnConfig;

import com.xiaokaceng.openci.domain.JenkinsConfiguration;
import com.xiaokaceng.openci.domain.ScmType;
import com.xiaokaceng.openci.domain.ToolConfiguration;
import com.xiaokaceng.openci.dto.ScmConfig;

public class JenkinsConfigurationPojo extends ToolConfigurationPojo {

	private ScmConfig scmConfig;

	@Override
	public void createCISClient(ToolConfiguration toolConfiguration) {
		if (toolConfiguration instanceof JenkinsConfiguration) {
			JenkinsCISClient jenkinsCISClient = new JenkinsCISClient(toolConfiguration.getServiceUrl(), toolConfiguration.getUsername(), toolConfiguration.getPassword());
			jenkinsCISClient.setKoalaScmConfig(createKoalaScmConfig());
			cisClient = jenkinsCISClient;
			isInstance = true;
		}
	}

	private KoalaScmConfig createKoalaScmConfig() {
		if (scmConfig == null) {
			return null;
		}
		if (scmConfig.getScmType().equals(ScmType.SVN)) {
			return new KoalaSvnConfig(scmConfig.getRepositoryUrl());
		}
		if (scmConfig.getScmType().equals(ScmType.GIT)) {
			return new KoalaGitConfig(scmConfig.getRepositoryUrl());
		}
		return null;
	}

	public ScmConfig getScmConfig() {
		return scmConfig;
	}

	public void setScmConfig(ScmConfig scmConfig) {
		this.scmConfig = scmConfig;
	}

}
