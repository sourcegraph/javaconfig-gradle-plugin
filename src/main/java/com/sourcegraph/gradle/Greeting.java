package com.sourcegraph.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import groovy.json.JsonOutput;

public class Greeting extends DefaultTask {
    /**
     * The root project
     */
    private Project rootProject;

    public Project getProject() {
        return rootProject;
    }

    public void setProject(Project project) {
        this.rootProject = project;
    }

    private final static HashSet<String> configWhitelist = new HashSet<>();
    static {
        configWhitelist.add("androidTestApi");
        configWhitelist.add("androidTestCompile");
        configWhitelist.add("androidTestCompileOnly");
        configWhitelist.add("debugApi");
        configWhitelist.add("debugCompile");
        configWhitelist.add("debugCompileOnly");
        configWhitelist.add("debugImplementation");
        configWhitelist.add("releaseApi");
        configWhitelist.add("releaseCompile");
        configWhitelist.add("releaseCompileOnly");
        configWhitelist.add("releaseImplementation");
        configWhitelist.add("testApi");
        configWhitelist.add("testCompile");
        configWhitelist.add("testCompileOnly");
        configWhitelist.add("testImplementation");
        configWhitelist.add("testCompileClasspath");
        configWhitelist.add("api");
        configWhitelist.add("implementation");
        configWhitelist.add("compile");
        configWhitelist.add("compileOnly");
        configWhitelist.add("compileClasspath");
    }

    @TaskAction
    void printProjectHierarchy() {
        Map<String, Object> pconfig = new HashMap<>();
        getProject().allprojects(project -> {
            Map<String, Object> info = new HashMap<>();

            info.put("artifactId", project.getName());
            info.put("groupId", project.getGroup());
            info.put("version",  project.getVersion());

            List<Map<String, Object>> deps = project.getConfigurations().stream()
                    .filter(c -> configWhitelist.contains(c.getName()))
                    .flatMap(c -> c.getAllDependencies().stream().map(d -> {
                        HashMap<String, Object> depInfo = new HashMap<>();
                        depInfo.put("group", d.getGroup());
                        depInfo.put("name", d.getName());
                        depInfo.put("version", d.getVersion());
                        return depInfo;
                    })).distinct().collect(Collectors.toList());
            info.put("dependencies", deps);

            SourceSetContainer sourceSetContainer = (SourceSetContainer)project.getProperties().get("sourceSets");
            if (sourceSetContainer != null) {
                File projectDir = project.getProjectDir();
                try {
                    Set<File> srcDirs = sourceSetContainer.getByName("main").getJava().getSrcDirs();
                    info.put("sourceDirectories", srcDirs.stream().map(srcDir -> projectDir.toPath().relativize(srcDir.toPath()).toString()).collect(Collectors.toList()));
                } catch (Exception e) {/* exception thrown if sourceset not found */}
                try {
                    Set<File> testSrcDirs = sourceSetContainer.getByName("test").getJava().getSrcDirs();
                    info.put("testSourceDirectories", testSrcDirs.stream().map(srcDir -> projectDir.toPath().relativize(srcDir.toPath()).toString()).collect(Collectors.toList()));
                } catch (Exception e) {/* exception thrown if sourceset not found */}
            }
            if (!info.containsKey("sourceDirectories")) {
                List<String> dirs = new ArrayList<>();
                dirs.add("src/main/java");
                info.put("sourceDirectories", dirs);
            }
            if (!info.containsKey("testSourceDirectories")) {
                List<String> dirs = new ArrayList<>();
                dirs.add("src/test/java");
                info.put("testSourceDirectories", dirs);
            }

            String projectDir = "/" + project.getRootDir().toPath().relativize(project.getProjectDir().toPath()).toString();
            pconfig.put(projectDir, info);
        });

        getProject().getGradle().buildFinished(__ -> {
            Map<String, Object> javaconfig = new HashMap<>();
            javaconfig.put("projects", pconfig);
            System.out.println(JsonOutput.prettyPrint(JsonOutput.toJson(javaconfig)));
        });
    }

}
