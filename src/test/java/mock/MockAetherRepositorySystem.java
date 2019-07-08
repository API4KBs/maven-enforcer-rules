package mock;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

public class MockAetherRepositorySystem extends org.eclipse.aether.internal.impl.DefaultRepositorySystem {

  @Override
  public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request) {
    ArtifactResult result = new ArtifactResult(request);

    Artifact resolved = request.getArtifact();
    resolved = resolved.setFile(getFile(resolved));

    result.setArtifact(resolved);
    return result;
  }

  private File getFile(Artifact resolved) {
    try {
      URL url = MockAetherRepositorySystem.class.getResource("/" + resolved.getArtifactId() + ".pom.xml");
      if (url == null) {
        throw new FileNotFoundException("Unable to find POM file for " + resolved.getArtifactId()
            + ", expected /" + resolved.getArtifactId() + ".pom.xml");
      }
      return new File(url.toURI());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    return null;
  }


}
