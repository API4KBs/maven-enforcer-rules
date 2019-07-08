package mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.handler.manager.DefaultArtifactHandlerManager;
import org.apache.maven.bridge.MavenRepositorySystem;
import org.apache.maven.classrealm.DefaultClassRealmManager;
import org.apache.maven.extension.internal.CoreExportsProvider;
import org.apache.maven.lifecycle.DefaultLifecycles;
import org.apache.maven.lifecycle.Lifecycle;
import org.apache.maven.lifecycle.internal.DefaultLifecyclePluginAnalyzer;
import org.apache.maven.lifecycle.mapping.DefaultLifecycleMapping;
import org.apache.maven.lifecycle.mapping.LifecycleMapping;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.DefaultModelProcessor;
import org.apache.maven.model.composition.DefaultDependencyManagementImporter;
import org.apache.maven.model.inheritance.DefaultInheritanceAssembler;
import org.apache.maven.model.interpolation.StringSearchModelInterpolator;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.management.DefaultDependencyManagementInjector;
import org.apache.maven.model.management.DefaultPluginManagementInjector;
import org.apache.maven.model.normalization.DefaultModelNormalizer;
import org.apache.maven.model.path.DefaultModelPathTranslator;
import org.apache.maven.model.path.DefaultModelUrlNormalizer;
import org.apache.maven.model.path.DefaultPathTranslator;
import org.apache.maven.model.path.DefaultUrlNormalizer;
import org.apache.maven.model.plugin.DefaultLifecycleBindingsInjector;
import org.apache.maven.model.plugin.DefaultPluginConfigurationExpander;
import org.apache.maven.model.plugin.DefaultReportConfigurationExpander;
import org.apache.maven.model.plugin.DefaultReportingConverter;
import org.apache.maven.model.profile.DefaultProfileSelector;
import org.apache.maven.model.superpom.DefaultSuperPomProvider;
import org.apache.maven.model.validation.DefaultModelValidator;
import org.apache.maven.project.DefaultProjectBuilder;
import org.apache.maven.project.DefaultProjectBuildingHelper;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest.RepositoryMerging;
import org.apache.maven.project.ProjectModelResolver;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.internal.impl.DefaultArtifactResolver;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultMetadataResolver;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositoryEventDispatcher;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultSyncContextFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

public class MockMaven {

  DefaultSyncContextFactory syncFactory;

  DefaultLocalRepositoryProvider repoProvider;

  DefaultRepositorySystemSession repoSession;

  DefaultMetadataResolver metadataResolver;

  DefaultVersionRangeResolver rangeResolver;

  DefaultRepositorySystem repoSystem;

  ProjectModelResolver projectModelResolver;

  DefaultProjectBuilder projectBuilder;

  DefaultModelBuilder modelBuilder;

  DefaultModelReader modelReader;

  DefaultModelProcessor modelProcessor;

  DefaultSuperPomProvider superPomProvider;

  public DefaultArtifactResolver getArtifactResolver() {
    return artifactResolver;
  }

  DefaultArtifactResolver artifactResolver;

  public static MockMaven newInstance(String repoFolder) {
    try {
      return new MockMaven(repoFolder);
    } catch (NoLocalRepositoryManagerException | NoSuchFieldException | IllegalAccessException | PlexusContainerException e) {
      e.printStackTrace();
      fail(e.getMessage());
      return null;
    }
  }

  protected MockMaven(String repoFolder)
      throws NoLocalRepositoryManagerException, NoSuchFieldException, IllegalAccessException, PlexusContainerException {
    PlexusContainer container = new DefaultPlexusContainer();

    syncFactory = new DefaultSyncContextFactory();

    repoProvider = new DefaultLocalRepositoryProvider();
    repoProvider.addLocalRepositoryManagerFactory(new SimpleLocalRepositoryManagerFactory());

    repoSession = new DefaultRepositorySystemSession();
    repoSession.setLocalRepositoryManager(repoProvider
        .newLocalRepositoryManager(repoSession,
            new LocalRepository(repoFolder)));

    metadataResolver = new DefaultMetadataResolver();
    metadataResolver.setSyncContextFactory(getSyncFactory());
    metadataResolver.setRepositoryEventDispatcher(new DefaultRepositoryEventDispatcher());

    rangeResolver = new DefaultVersionRangeResolver();
    rangeResolver.setMetadataResolver(metadataResolver);

    artifactResolver = new DefaultArtifactResolver();
    artifactResolver.setSyncContextFactory(getSyncFactory());
    artifactResolver.setRepositoryEventDispatcher(new DefaultRepositoryEventDispatcher());
    artifactResolver.setVersionResolver(new DefaultVersionResolver());

    repoSystem = new MockAetherRepositorySystem();
    repoSystem.setVersionRangeResolver(rangeResolver);
    repoSystem.setVersionResolver(new DefaultVersionResolver());
    repoSystem.setLocalRepositoryProvider(new DefaultLocalRepositoryProvider());
    repoSystem.setRemoteRepositoryManager(new DefaultRemoteRepositoryManager());
    repoSystem.setMetadataResolver(metadataResolver);
    repoSystem.setArtifactResolver(artifactResolver);

    modelReader = new DefaultModelReader();

    modelProcessor = new DefaultModelProcessor();
    modelProcessor.setModelReader(getModelReader());

    superPomProvider = new DefaultSuperPomProvider();
    superPomProvider.setModelProcessor(getModelProcessor());

    DefaultModelUrlNormalizer urlN = new DefaultModelUrlNormalizer();
    urlN.setUrlNormalizer(new DefaultUrlNormalizer());

    DefaultLifecyclePluginAnalyzer lifecycleAnalyzer = new DefaultLifecyclePluginAnalyzer();
    set(lifecycleAnalyzer,"logger", new SilentLog());
    Map<String,LifecycleMapping> mappings = new HashMap<>();
    mappings.put("jar", new DefaultLifecycleMapping());
    mappings.put("pom", new DefaultLifecycleMapping());
    set(lifecycleAnalyzer,"lifecycleMappings", mappings);
    DefaultLifecycles cycles = new DefaultLifecycles();
    set(cycles,"logger", new SilentLog());

    Map<String, Lifecycle> m = new HashMap<>();
    Arrays.stream(DefaultLifecycles.STANDARD_LIFECYCLES).forEach((c)-> m.put(c,new Lifecycle(c,Collections.emptyList(),Collections.emptyMap())));
    set(cycles,"lifecycles", m);
    set(lifecycleAnalyzer,"defaultLifeCycles", cycles);


    DefaultLifecycleBindingsInjector lifecycleBindingsInjector = new DefaultLifecycleBindingsInjector();
    set(lifecycleBindingsInjector,"lifecycle",lifecycleAnalyzer);

    StringSearchModelInterpolator interpolator = new StringSearchModelInterpolator();
    interpolator.setPathTranslator(new DefaultPathTranslator());
    DefaultModelPathTranslator modelPathTranslator = new DefaultModelPathTranslator();
    modelPathTranslator.setPathTranslator(new DefaultPathTranslator());

    modelBuilder = new DefaultModelBuilder();
    set(modelBuilder,"profileSelector", new DefaultProfileSelector());
    modelBuilder.setModelProcessor(getModelProcessor());
    modelBuilder.setModelValidator(new DefaultModelValidator());
    modelBuilder.setSuperPomProvider(getSuperPomProvider());
    modelBuilder.setModelNormalizer(new DefaultModelNormalizer());
    modelBuilder.setInheritanceAssembler(new DefaultInheritanceAssembler());
    modelBuilder.setModelInterpolator(interpolator);
    modelBuilder.setModelUrlNormalizer(urlN);
    modelBuilder.setModelPathTranslator(modelPathTranslator);
    modelBuilder.setPluginManagementInjector(new DefaultPluginManagementInjector());
    modelBuilder.setLifecycleBindingsInjector(lifecycleBindingsInjector);
    modelBuilder.setDependencyManagementImporter(new DefaultDependencyManagementImporter());
    modelBuilder.setDependencyManagementInjector(new DefaultDependencyManagementInjector());
    modelBuilder.setReportConfigurationExpander(new DefaultReportConfigurationExpander());
    modelBuilder.setReportingConverter(new DefaultReportingConverter());
    modelBuilder.setPluginConfigurationExpander(new DefaultPluginConfigurationExpander());


    projectModelResolver = new ProjectModelResolver(
        repoSession,
        new RequestTrace(null),
        repoSystem,
        new DefaultRemoteRepositoryManager(),
        Collections.emptyList(),
        RepositoryMerging.POM_DOMINANT,
        null);

    projectBuilder = new DefaultProjectBuilder();

    DefaultProjectBuildingHelper helper = new DefaultProjectBuildingHelper();
    set(helper,"logger", new SilentLog());
    set(helper,"classRealmManager", new DefaultClassRealmManager(new SilentLog(),
        container,
        Collections.emptyList(),
        new CoreExportsProvider(container, null)));
    set(helper,"repositorySystem", new MockMavenRepositorySystem());

    DefaultArtifactHandlerManager handlerManager = new DefaultArtifactHandlerManager();
    set(handlerManager,"artifactHandlers",new HashMap<>());

    MavenRepositorySystem mavenRepoSystem = new MavenRepositorySystem();
    set(mavenRepoSystem,"artifactHandlerManager", handlerManager);


    set(projectBuilder,"repoSystem",getRepoSystem());
    set(projectBuilder,"projectBuildingHelper", helper);
    set(projectBuilder,"modelBuilder", getModelBuilder());
    set(projectBuilder,"repositoryManager",new DefaultRemoteRepositoryManager());
    set(projectBuilder,"repositorySystem",mavenRepoSystem);
    set(projectBuilder,"logger",new SilentLog());

//    set(projectBuilder,"artifactResolver",getArtifactResolver());

  }


  private void set(Object src, String name, Object value)
      throws IllegalAccessException, NoSuchFieldException {
    Field fld = src.getClass().getDeclaredField(name);
    fld.setAccessible(true);
    fld.set(src,value);
  }

  public MavenProject getProject(String GAV) {
    StringTokenizer tok = new StringTokenizer(GAV,":");
    assertEquals(4, tok.countTokens());

    String G = tok.nextToken();
    String A = tok.nextToken();
    String V = tok.nextToken();
    String type = tok.nextToken();

    ProjectBuildingRequest configuration = new DefaultProjectBuildingRequest();
    configuration.setRepositorySession(getRepoSession());
    Artifact artifact = new DefaultArtifact(
        G,
        A,
        V,
        "compile",
        type,
        "",
        new DefaultArtifactHandler());
    try {
      return getProjectBuilder().build(artifact,configuration).getProject();
    } catch (ProjectBuildingException e) {
      e.printStackTrace();
      fail(e.getMessage());
      return null;
    }
  }

  public DefaultLocalRepositoryProvider getRepoProvider() {
    return repoProvider;
  }

  public DefaultRepositorySystemSession getRepoSession() {
    return repoSession;
  }

  public DefaultMetadataResolver getMetadataResolver() {
    return metadataResolver;
  }

  public DefaultVersionRangeResolver getRangeResolver() {
    return rangeResolver;
  }

  public DefaultRepositorySystem getRepoSystem() {
    return repoSystem;
  }

  private SyncContextFactory getSyncFactory() {
    return syncFactory;
  }

  public ProjectModelResolver getProjectModelResolver() {
    return projectModelResolver;
  }

  public ProjectBuilder getProjectBuilder() {
    return projectBuilder;
  }

  public DefaultModelBuilder getModelBuilder() {
    return modelBuilder;
  }

  public DefaultModelReader getModelReader() {
    return modelReader;
  }

  public DefaultModelProcessor getModelProcessor() {
    return modelProcessor;
  }

  public DefaultSuperPomProvider getSuperPomProvider() {
    return superPomProvider;
  }
}
