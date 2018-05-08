package com.sourcegraph.gradle;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class ProjectConfig {

  private String artifactId;

  private String groupId;

  private String version;

  // List of source directories relative to the project root dir
  private Collection<String> sourceDirectories;

  // List of test source directories, relative to the project root dir
  private Collection<String> testSourceDirectories;

  // List of additional javac compiler options
  private List<CompilerOption> compilerOptions;

  private List<ProjectDep> deps;

  public String getArtifactId() {
    return artifactId == null ? "" : artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return groupId == null ? "" : groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getVersion() {
    return version == null ? "" : version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  // Returns source directories of a project (relative to the project root)
  public Collection<String> getSourceDirectories() {
    return sourceDirectories == null ? new ArrayList<>() : sourceDirectories;
  }

  // These should be relative to the project root
  public void setSourceDirectories(Collection<String> sourceDirectories) {
    this.sourceDirectories = sourceDirectories;
  }

  // Returns test directories of a project (relative to the project root).
  public Collection<String> getTestSourceDirectories() {
    return testSourceDirectories == null ? new ArrayList<>() : testSourceDirectories;
  }

  // These should be relative to the project root
  public void setTestSourceDirectories(Collection<String> testSourceDirectories) {
    this.testSourceDirectories = testSourceDirectories;
  }

  public List<ProjectDep> getDeps() {
    return deps;
  }

  public void setDeps(List<ProjectDep> deps) {
    this.deps = deps;
  }
}
