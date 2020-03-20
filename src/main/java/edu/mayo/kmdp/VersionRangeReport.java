package edu.mayo.kmdp;

import edu.mayo.kmdp.VersionRangeIssue.RelatedArtifactKind;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class VersionRangeReport {

  private String groupId;
  private String artifactId;
  private String version;

  private Set<VersionRangeIssue> issues;

  public VersionRangeReport(final String groupId, final String artifactId,
      final String version) {
    this.artifactId = artifactId;
    this.groupId = groupId;
    this.version = version;

    issues = new HashSet<>();
  }

  public void add(VersionRangeIssue issue) {
    issues.add(issue);
  }

  public int size() {
    return issues.size();
  }

  public boolean isValid() {
    return issues.isEmpty();
  }

  public Optional<VersionRangeIssue> getIssue(
      final String variable,
      final RelatedArtifactKind kind) {
    return issues.stream()
        .filter(issue -> variable.equals(issue.getVariable()) && kind == issue.getKind())
        .findFirst();
  }

  public Optional<VersionRangeIssue> getIssue(
      final String artifactQualifiedName,
      final String variable,
      final RelatedArtifactKind kind) {

    StringTokenizer tok = new StringTokenizer(artifactQualifiedName,":");
    boolean qualified = tok.countTokens() > 1;
    final String groupFilter = qualified ? tok.nextToken() : null;
    final String nameFilter = tok.nextToken();

    return issues.stream()
        .filter(issue -> variable.equals(issue.getVariable())
            && kind == issue.getKind()
            && (nameFilter == null || nameFilter.equals(issue.getArtifactId()))
        ).findFirst();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VersionRangeReport)) {
      return false;
    }

    VersionRangeReport that = (VersionRangeReport) o;

    if (!groupId.equals(that.groupId)) {
      return false;
    }
    if (!artifactId.equals(that.artifactId)) {
      return false;
    }
    if (!version.equals(that.version)) {
      return false;
    }
    return issues.equals(that.issues);
  }

  @Override
  public int hashCode() {
    int result = groupId.hashCode();
    result = 31 * result + artifactId.hashCode();
    result = 31 * result + version.hashCode();
    result = 31 * result + issues.hashCode();
    return result;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder()
        .append("Version ranges validation check >> ")
        .append(groupId).append(":")
        .append(artifactId).append(":")
        .append(version).append("\n");
    if (isValid()) {
      sb.append("No issues detected");
    } else {
      sb.append(issues.stream()
          .map(VersionRangeIssue::toString)
          .collect(Collectors.joining()));
    }
    return sb.toString();
  }
}
