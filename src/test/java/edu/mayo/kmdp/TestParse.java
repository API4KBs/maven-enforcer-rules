package edu.mayo.kmdp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mock.MockMaven;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

/**
 * Test class for the RequireSnapshotVersion rule.
 */
public class TestParse {

  private MockMaven maven = MockMaven.newInstance("/");

  @Test
  public void testProjectParse() {
    MavenProject p = maven.getProject("org.mock:inv_parentRange:0.0.1:jar" );

    assertEquals("org.mock", p.getGroupId());
    assertEquals("inv_parentRange", p.getArtifactId());

    assertEquals("barent",p.getParent().getArtifactId());

    assertEquals("3.3.5",p.getParent().getVersion());
    assertEquals("(3.3.0,3.4.0)",p.getParentArtifact().getVersion());
    assertEquals("(3.3.0,3.4.0)",p.getParentArtifact().getBaseVersion());

    assertEquals("pom", p.getParent().getPackaging());
  }

  @Test
  public void testProjectParseWithSnapshot() {
    MavenProject p = maven.getProject("org.foo:snapParent:1.0.0-SNAPSHOT:pom" );

    assertEquals("org.foo", p.getGroupId());
    assertEquals("snapParent", p.getArtifactId());
    assertEquals("1.0.0-SNAPSHOT", p.getVersion());

    assertEquals(2, p.getDependencies().size());
    assertEquals(1, p.getDependencyManagement().getDependencies().size());
    assertTrue(p.getPluginManagement().getPlugins().stream()
        .anyMatch(plugin -> plugin.getArtifactId().equalsIgnoreCase("fake_plugin3")));
    assertEquals(1, p.getBuild().getPlugins().size());
  }



}