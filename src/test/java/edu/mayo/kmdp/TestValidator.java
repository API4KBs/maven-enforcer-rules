package edu.mayo.kmdp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind;
import mock.MockMaven;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;


public class TestValidator {

  private MockMaven maven = MockMaven.newInstance("/");

  private VersionRangeRule rule = new VersionRangeRule();

  @Test
  public void testIllegalParentRange() {
    MavenProject p = maven.getProject("org.foo:inv_parentRange:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(4, issues.size());

    VersionRangeIssue issue = issues.getIssue("version", RelatedArtifactKind.PARENT)
        .orElse(null);
    assertNotNull(issue);

    assertEquals("(3.3.0,3.4.0)", issue.getValue());
  }


  @Test
  public void testIllegalVersionProperties() {
    MavenProject p = maven.getProject("org.foo:inv_versionVariable:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(3, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("third.party.versionRange", RelatedArtifactKind.SELF)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("(0.0.0,0.3.0)", issue1.getValue());

    VersionRangeIssue issue2 = issues.getIssue("kmdp.mock.version", RelatedArtifactKind.SELF)
        .orElse(null);
    assertNotNull(issue2);
    assertEquals("[0.0.0,)", issue2.getValue());

    VersionRangeIssue issue3 = issues.getIssue("kmdp.ill.version", RelatedArtifactKind.SELF)
        .orElse(null);
    assertNotNull(issue3);
    assertEquals("1.0", issue3.getValue());
  }

  @Test
  public void testRangeInDepManagement() {
    MavenProject p = maven.getProject("org.foo:barent:3.3.5:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(3, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("version", RelatedArtifactKind.MGM_DEPENDENCY)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("(0.0.0,0.2.0)", issue1.getValue());
    assertEquals("someDep", issue1.getArtifactId());
  }

  @Test
  public void testRangeInDependency() {
    MavenProject p = maven.getProject("org.mock:inv_dependencies:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(5, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("version", RelatedArtifactKind.MGM_DEPENDENCY)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("(0.0.0,0.2.0)", issue1.getValue());
    assertEquals("someDep", issue1.getArtifactId());

    VersionRangeIssue issue2 = issues
        .getIssue("edu.foo:someDep", "version", RelatedArtifactKind.DEPENDENCY)
        .orElse(null);
    assertNotNull(issue2);
    assertEquals("(0.0.0,0.2.0)", issue2.getValue());
    assertEquals("someDep", issue1.getArtifactId());

    VersionRangeIssue issue3 = issues
        .getIssue("edu.foo:otherDep", "version", RelatedArtifactKind.DEPENDENCY)
        .orElse(null);
    assertNotNull(issue3);
    assertEquals("(0.0.0,0.1.1)", issue3.getValue());
    assertEquals("otherDep", issue3.getArtifactId());
  }

  @Test
  public void testRangeInPluginManagement() {
    MavenProject p = maven.getProject("org.foo:barent:3.3.5:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(3, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("org.foo:fake_plugin",
        "version", RelatedArtifactKind.MGM_PLUGIN)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("fake_plugin", issue1.getArtifactId());

    VersionRangeIssue issue2 = issues.getIssue("org.foo:fake_plugin2",
        "version", RelatedArtifactKind.MGM_PLUGIN)
        .orElse(null);
    assertNotNull(issue2);
    assertEquals("fake_plugin2", issue2.getArtifactId());
  }

  @Test
  public void testRangeInPlugins() {
    MavenProject p = maven.getProject("org.mock:inv_plugins:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(7, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("org.foo:fake_plugin",
        "version", RelatedArtifactKind.PLUGIN)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("fake_plugin", issue1.getArtifactId());

    VersionRangeIssue issue2 = issues.getIssue("org.foo:fake_plugin2",
        "version", RelatedArtifactKind.PLUGIN)
        .orElse(null);
    assertNotNull(issue2);
    assertEquals("fake_plugin2", issue2.getArtifactId());

    VersionRangeIssue issue3 = issues.getIssue("org.foo:fake_plugin5",
        "version", RelatedArtifactKind.PLUGIN)
        .orElse(null);
    assertNotNull(issue3);
    assertEquals("fake_plugin5", issue3.getArtifactId());

    VersionRangeIssue issue4 = issues.getIssue("org.foo:fake_plugin6",
        "version", RelatedArtifactKind.PLUGIN)
        .orElse(null);
    assertNotNull(issue4);
    assertEquals("fake_plugin6", issue4.getArtifactId());
  }


  @Test
  public void testRangeInPluginDependencies() {
    MavenProject p = maven.getProject("org.mock:inv_complexPlugins:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(3, issues.size());

    VersionRangeIssue issue1 = issues.getIssue("org.mock:mockPluginDep2",
        "version", RelatedArtifactKind.PLUGIN_DEP)
        .orElse(null);
    assertNotNull(issue1);
    assertEquals("(1.0.0,1.0.4)", issue1.getValue());
    assertEquals("mockPluginDep2", issue1.getArtifactId());

    VersionRangeIssue issue2 = issues.getIssue("org.mock:mockPluginDep1",
        "version", RelatedArtifactKind.PLUGIN_DEP)
        .orElse(null);
    assertNotNull(issue2);
    assertEquals("(1.0.0,1.0.1)", issue2.getValue());
    assertEquals("mockPluginDep1", issue2.getArtifactId());
  }

  @Test
  public void testRangeInPluginDependencies2() {
    MavenProject p = maven.getProject("org.mock:inv_complexPlugins2:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    //System.out.println(issues);
    assertEquals(2, issues.size());
  }


  @Test
  public void testRangeInPluginDependencies3() {
    MavenProject p = maven.getProject("org.mock:inv_complexPlugins3:0.0.1:jar");

    VersionRangeReport issues = rule.validateProject(p);

    System.out.println(issues);
    assertEquals(1, issues.size());
  }
}