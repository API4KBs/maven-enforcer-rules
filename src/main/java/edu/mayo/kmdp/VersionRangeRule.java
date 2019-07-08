/**
 * Copyright © 2018 Mayo Clinic (RSTKNOWLEDGEMGMT@mayo.edu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.mayo.kmdp;

import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.DEPENDENCY;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.MGM_DEPENDENCY;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.MGM_PLUGIN;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.PARENT;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.PLUGIN;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.PLUGIN_ARG;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.PLUGIN_DEP;
import static edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind.SELF;

import edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind;
import java.util.Arrays;
import java.util.UUID;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class VersionRangeRule
    implements EnforcerRule {

  private static final String PATTERN = "\\d+\\.\\d+\\.\\d+(.+)?";

  public void execute(EnforcerRuleHelper helper)
      throws EnforcerRuleException {

//    Log log = helper.getLog();

    try {
      // get the various expressions out of the helper.
      MavenProject project = (MavenProject) helper.evaluate("${project}");
//      MavenSession session = (MavenSession) helper.evaluate("${session}");

      VersionRangeReport issues = validateProject(project);

      if (!issues.isValid()) {
        throw new EnforcerRuleException(issues.toString());
      }

    } catch (ExpressionEvaluationException e) {
      throw new EnforcerRuleException(
          "Unable to lookup an expression " + e.getLocalizedMessage(),
          e);
    }
  }

  protected VersionRangeReport validateProject(MavenProject project) {
    VersionRangeReport issues = new VersionRangeReport(
        project.getGroupId(),
        project.getArtifactId(),
        project.getVersion());
    validateProject(project, issues);
    return issues;
  }

  protected void validateProject(MavenProject project, VersionRangeReport issues) {
    validateArtifact(project.getArtifact(), SELF, issues);

    if (project.getParentArtifact() != null) {
      validateArtifact(
          project.getParentArtifact(), PARENT, issues);
    }

    project.getProperties().keySet().stream()
        .filter((k) -> k.toString().contains(".version"))
        .map(Object::toString)
        .forEach((p) ->
            validateVersionProperty(project.getArtifactId(), p,
                project.getProperties().getProperty(p), SELF, issues));

    project.getDependencies().forEach((dep) ->
        validateDependency(dep, DEPENDENCY, issues));

    if (project.getDependencyManagement() != null) {
      project.getDependencyManagement().getDependencies().forEach((dep) ->
          validateDependency(dep, MGM_DEPENDENCY, issues));
    }

    if (project.getPluginManagement() != null) {
      project.getPluginManagement().getPlugins()
          .forEach((mgPlugin) -> validatePlugin(mgPlugin, MGM_PLUGIN, issues));
    }

    if (project.getBuildPlugins() != null) {
      project.getBuildPlugins()
          .forEach((plugin) -> validatePlugin(plugin, PLUGIN, issues));
    }

    // plugin arguments
    // semper stuff

  }

  private VersionRangeReport validateVersionProperty(
      String artifactId,
      String property,
      String propertyValue,
      RelatedArtifactKind kind,
      VersionRangeReport localIssues) {

    if (property.contains("kmdp") || property.contains("knew")) {
      if (!propertyValue.matches(PATTERN)) {
        localIssues.add(
            new VersionRangeIssue(kind,
                artifactId,
                property,
                propertyValue,
                "KMD Property " + property + " sets version " + propertyValue));
      }
    } else if (propertyValue.contains(",")) {
      localIssues.add(
          new VersionRangeIssue(kind,
              artifactId,
              property,
              propertyValue,
              "Property " + property + " sets version " + propertyValue));
    }
    return localIssues;
  }

  private VersionRangeReport validateDependency(
      Dependency dependency,
      RelatedArtifactKind kind,
      VersionRangeReport localIssues) {
    return validateGAV(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
        kind, localIssues);
  }


  private VersionRangeReport validatePlugin(
      Plugin plugin,
      RelatedArtifactKind kind,
      VersionRangeReport localIssues) {

    if (plugin.getDependencies() != null) {
      plugin.getDependencies().forEach((dep) -> validateDependency(dep, PLUGIN_DEP, localIssues));
    }

    validateConfiguration(plugin.getConfiguration(),plugin,localIssues);
    if (plugin.getExecutions() != null) {
      plugin.getExecutions().forEach((exec) -> validateConfiguration(exec.getConfiguration(),plugin,localIssues));
    }

    if (plugin.getVersion() != null) {
      return validateGAV(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(),
          kind, localIssues);
    } else {
      localIssues.add(new VersionRangeIssue(kind,
          plugin.getArtifactId(),
          "version",
          "LATEST (implicit)",
          "Unable to detect version of plugin " + kind + " "
              + plugin.getGroupId() + ":" + plugin.getArtifactId()
      ));
      return localIssues;
    }
  }

  private void validateConfiguration(Object configuration, Plugin plugin,
      VersionRangeReport localIssues) {
    if (configuration instanceof Xpp3Dom) {
      validateDom((Xpp3Dom) configuration, plugin, localIssues);
    }
  }

  private void validateDom(Xpp3Dom dom, Plugin plugin, VersionRangeReport localIssues) {
    if ("version".equals(dom.getName())) {
      validateVersionProperty(plugin.getArtifactId(), dom.getName(), dom.getValue(), PLUGIN_ARG,
          localIssues);
    }
    if (dom.getValue() != null) {
      validateConfigVersionProperty(dom.getName(),dom.getValue(),plugin,localIssues);
    } else {
      Arrays.stream(dom.getChildren()).forEach((child) -> validateDom(child, plugin, localIssues));
    }
  }

  private void validateConfigVersionProperty(String key, String value, Plugin plugin,
      VersionRangeReport localIssues) {
    if (value.contains("$") && value.contains(".version")) {
      localIssues.add(new VersionRangeIssue(
          PLUGIN_ARG,
          plugin.getArtifactId(),
          key,
          value,
          "Unresolved configuration property : " + key + " = " + value
      ));
    }
  }

  private VersionRangeReport validateArtifact(
      Artifact artifact,
      RelatedArtifactKind kind,
      VersionRangeReport localIssues) {

    if (artifact == null) {
      return localIssues;
    }

    if (artifact.getVersionRange() == null
        || artifact.getVersionRange().getRecommendedVersion() == null) {
      localIssues.add(
          new VersionRangeIssue(kind,
              artifact.getArtifactId(),
              "version",
              artifact.getVersion(),
              "Unable to detect version of " + kind + " " + artifact.getGroupId() + ":" + artifact
                  .getArtifactId() + ":" + artifact.getVersion()));
    }
    validateGAV(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), kind,
        localIssues);
    return localIssues;
  }

  private VersionRangeReport validateGAV(String groupId, String artifactId, String version,
      RelatedArtifactKind kind, VersionRangeReport localIssues) {
    if (version == null) {
      localIssues.add(
          new VersionRangeIssue(kind,
              artifactId,
              "version",
              "",
              "Version of GAV " + groupId + ":" + artifactId + ":"
                  + version
                  + " is missing"));
      return localIssues;
    }

    if (version.contains(",")) {
      localIssues.add(
          new VersionRangeIssue(kind,
              artifactId,
              "version",
              version,
              "Version of GAV " + groupId + ":" + artifactId + ":"
                  + version
                  + " should not be range-based"));
    }
    if (groupId.startsWith("edu.mayo")) {
      if (!version.matches(PATTERN)) {

        localIssues.add(
            new VersionRangeIssue(kind,
                artifactId,
                "version",
                version,
                "Policies require MAJOR.MINOR.PATCH versions - found " + version));
      }
    }
    return localIssues;
  }

  public String getCacheId() {
    return UUID.randomUUID().toString();
  }

  public boolean isCacheable() {
    return false;
  }

  public boolean isResultValid(EnforcerRule rule) {
    return false;
  }
}