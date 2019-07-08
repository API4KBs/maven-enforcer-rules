package edu.mayo.kmdp;

public class VersionRangeIssue {

  public enum RelatedArtifactKind {
    SELF, PARENT, DEPENDENCY, MGM_DEPENDENCY, PLUGIN, MGM_PLUGIN, PLUGIN_DEP, PLUGIN_ARG, PROPERTY
  }

  private String artifactId;
  private String variable;
  private String value;
  private RelatedArtifactKind kind;
  private String msg;

  public VersionRangeIssue(RelatedArtifactKind kind, String artifactId, String variable, String value, String msg) {
    this.artifactId = artifactId;
    this.variable = variable;
    this.value = value;
    this.kind = kind;
    this.msg = msg;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVariable() {
    return variable;
  }

  public String getValue() {
    return value;
  }

  public RelatedArtifactKind getKind() {
    return kind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VersionRangeIssue)) {
      return false;
    }

    VersionRangeIssue that = (VersionRangeIssue) o;

    if (!artifactId.equals(that.artifactId)) {
      return false;
    }
    if (!variable.equals(that.variable)) {
      return false;
    }
    if (!value.equals(that.value)) {
      return false;
    }
    return kind == that.kind;
  }

  @Override
  public int hashCode() {
    int result = artifactId.hashCode();
    result = 31 * result + variable.hashCode();
    result = 31 * result + value.hashCode();
    result = 31 * result + kind.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "- Issue: " +
        "(" + kind + ") \n" +
        "\t" + artifactId + " : " +
        variable + '=' +
        value + "\n" +
        "\t\t" + msg + '\n';
  }
}
