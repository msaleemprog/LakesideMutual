package analysis;

import org.contextmapper.discovery.ContextMapDiscoverer;
import org.contextmapper.discovery.boundarycontexts.spring.SpringBootBoundedContextDiscoveryStrategy;
import org.contextmapper.discovery.relationships.dockercompose.DockerComposeRelationshipDiscoveryStrategy;
import org.contextmapper.discovery.serialization.ContextMapSerializer;
import org.contextmapper.discovery.names.DefaultBoundedContextNameMappingStrategy;
import org.contextmapper.discovery.names.SeparatorToCamelCaseBoundedContextNameMappingStrategy;

import org.contextmapper.dsl.contextMappingDSL.ContextMap;

import java.io.File;
import java.nio.file.Path;

public class DiscoverLakeside {

  public static void main(String[] args) throws Exception {
    // repo root (.../LakesideMutual)
    Path repoRoot = Path.of("..", "..", "..", "..", "..").normalize().toAbsolutePath();

    File sourceRootDir = repoRoot.toFile();                 // contains docker-compose.yml
    File outDir = repoRoot.resolve("analysis")
                          .resolve("phase1")
                          .resolve("context-map")
                          .toFile();
    outDir.mkdirs();

    File outCml = new File(outDir, "lakeside-discovery.cml");

    // TODO: adjust this root package (see step 3 below)
    String rootPackage = "com.lakesidemutual";

    ContextMapDiscoverer discoverer = new ContextMapDiscoverer()
        .usingBoundedContextDiscoveryStrategies(
            new SpringBootBoundedContextDiscoveryStrategy(rootPackage)
        )
        .usingRelationshipDiscoveryStrategies(
            new DockerComposeRelationshipDiscoveryStrategy(sourceRootDir)
        )
        .usingBoundedContextNameMappingStrategies(
            new DefaultBoundedContextNameMappingStrategy(),
            new SeparatorToCamelCaseBoundedContextNameMappingStrategy("-")
        );

    ContextMap contextMap = discoverer.discoverContextMap();

    new ContextMapSerializer().serializeContextMap(contextMap, outCml);

    System.out.println("✅ Generated CML: " + outCml.getAbsolutePath());
  }
}
