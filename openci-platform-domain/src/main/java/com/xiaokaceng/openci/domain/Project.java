package com.xiaokaceng.openci.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dayatang.domain.QuerySettings;
import com.xiaokaceng.openci.EntityNullException;

@Entity
@Table(name = "projects")
public class Project extends TimeIntervalEntity {

	private static final long serialVersionUID = -1381157577442931544L;

	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_detail_id")
	private ProjectDetail projectDetail;

	@Enumerated(EnumType.STRING)
	@Column(name = "project_status")
	private ProjectStatus projectStatus;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.EAGER)
	private Set<ProjectDeveloper> developers = new HashSet<ProjectDeveloper>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
	private Set<Tool> tools = new HashSet<Tool>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "project_create_date")
	private Date projectCreateDate = new Date();

	Project() {
	}

	public Project(String name) {
		this.name = name;
	}

	public void addTool(Tool tool) {
		if (tool == null) {
			throw new EntityNullException();
		}
		tools.add(tool);
		save();
	}
	
	public void updateProjectStatus() {
		projectStatus = ProjectStatus.SUCCESS;
		for (Tool each : tools) {
			if (each.getStatus().equals(ToolIntegrationStatus.FAILURE)) {
				projectStatus = ProjectStatus.INTEGRATION_TOOL_FAILURE;
				break;
			}
			if (each.getStatus().equals(ToolIntegrationStatus.ONGOING)) {
				projectStatus = ProjectStatus.INTEGRATION_TOOL;
			}
		}
		save();
	}

	public static boolean isExixtByName(String name) {
		List<Project> projects = getRepository().find(QuerySettings.create(Project.class).eq("name", name));
		return !projects.isEmpty();
	}
	
	public String integrationProcess() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Tool each : tools) {
			for (ToolInterfaceImplement interfaceImplement : each.getToolInterfaceImplements()) {
				stringBuilder.append(interfaceImplement.getExecuteDate()).append(" ");
				stringBuilder.append(each.getToolConfiguration().toString()).append("正在执行");
				stringBuilder.append(interfaceImplement.getToolInterface().toString()).append("方法  ");
				stringBuilder.append("状态：").append(interfaceImplement.isSuccess()).append("<br>");
			}
		}
		if (getProjectStatus().equals(ProjectStatus.SUCCESS)) {
			stringBuilder.append("整合成功!");
		}
		if (getProjectStatus().equals(ProjectStatus.INTEGRATION_TOOL_FAILURE)) {
			stringBuilder.append("整合失败!");
		}
		return stringBuilder.toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDevelopers(Set<ProjectDeveloper> developers) {
		this.developers = developers;
	}

	public void setTools(Set<Tool> tools) {
		this.tools = tools;
	}

	public Set<ProjectDeveloper> getDevelopers() {
		return developers;
	}

	public Set<Tool> getTools() {
		return tools;
	}

	public String getProjectCreateDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(projectCreateDate);
	}

	public ProjectDetail getProjectDetail() {
		return projectDetail;
	}

	public void setProjectDetail(ProjectDetail projectDetail) {
		this.projectDetail = projectDetail;
	}

	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(ProjectStatus projectStatus) {
		this.projectStatus = projectStatus;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Project)) {
			return false;
		}
		Project that = (Project) other;
		return new EqualsBuilder().append(getName(), that.getName()).append(getCreateDate(), that.getCreateDate()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getName()).append(getCreateDate()).hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

}
