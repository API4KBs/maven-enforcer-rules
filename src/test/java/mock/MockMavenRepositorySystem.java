package mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.ArtifactDoesNotExistException;
import org.apache.maven.repository.ArtifactTransferFailedException;
import org.apache.maven.repository.ArtifactTransferListener;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.eclipse.aether.RepositorySystemSession;

public class MockMavenRepositorySystem implements RepositorySystem {

  @Override
  public ArtifactRepository buildArtifactRepository(Repository repository)
      throws InvalidRepositoryException {
    return new MavenArtifactRepository();
  }

  @Override
  public Artifact createArtifact(String groupId, String artifactId, String version,
      String packaging) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Artifact createArtifact(String groupId, String artifactId, String version, String scope,
      String type) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Artifact createProjectArtifact(String groupId, String artifactId, String version) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Artifact createArtifactWithClassifier(String groupId, String artifactId, String version,
      String type, String classifier) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Artifact createPluginArtifact(Plugin plugin) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Artifact createDependencyArtifact(Dependency dependency) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public ArtifactRepository createDefaultRemoteRepository() throws InvalidRepositoryException {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public ArtifactRepository createDefaultLocalRepository() throws InvalidRepositoryException {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public ArtifactRepository createLocalRepository(File localRepository)
      throws InvalidRepositoryException {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public ArtifactRepository createArtifactRepository(String id, String url,
      ArtifactRepositoryLayout repositoryLayout, ArtifactRepositoryPolicy snapshots,
      ArtifactRepositoryPolicy releases) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public List<ArtifactRepository> getEffectiveRepositories(List<ArtifactRepository> repositories) {
    return Collections.emptyList();
  }

  @Override
  public Mirror getMirror(ArtifactRepository repository, List<Mirror> mirrors) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void injectMirror(List<ArtifactRepository> repositories, List<Mirror> mirrors) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void injectProxy(List<ArtifactRepository> repositories, List<Proxy> proxies) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void injectAuthentication(List<ArtifactRepository> repositories, List<Server> servers) {
  }

  @Override
  public void injectMirror(RepositorySystemSession session, List<ArtifactRepository> repositories) {

  }

  @Override
  public void injectProxy(RepositorySystemSession session, List<ArtifactRepository> repositories) {
  }

  @Override
  public void injectAuthentication(RepositorySystemSession session,
      List<ArtifactRepository> repositories) {
  }

  @Override
  public ArtifactResolutionResult resolve(ArtifactResolutionRequest request) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void publish(ArtifactRepository repository, File source, String remotePath,
      ArtifactTransferListener transferListener) throws ArtifactTransferFailedException {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void retrieve(ArtifactRepository repository, File destination, String remotePath,
      ArtifactTransferListener transferListener)
      throws ArtifactTransferFailedException, ArtifactDoesNotExistException {
    throw new UnsupportedOperationException("Not implemented");
  }
}
