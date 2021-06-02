package com.cjz.webmvc.neo4j;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testng.util.Strings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.neo4j.driver.Values.parameters;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-07 14:15
 */
@Slf4j
 class T4MavenHelper {
    private static final Set<String> NODES = new LinkedHashSet<>();

    private final String   CREATE = "create ($artifactId:$group {name: \"$artifactId\"})";
    //private String   CREATE = "create (%s:%s {name: \"%s\"}) return %s";
   //  private final String   CREATE_RELATIONSHIP = "match ($fArtifactId:$fGroupId), ($tArtifactId:$tGroupId) create ($fArtifactId)-[dep:mvn]->($tArtifactId) ";
    private final String   CREATE_RELATIONSHIP = "match (a:$fGroup), (b:$tGroup) where a.name=\"$fArtifactId\" and b.name = \"$tArtifactId\" create (a)-[:dep]->(b) ";
    private  Driver driver;
    private  Session session;
    @Test
     @SneakyThrows
    void t1() {
        driver = GraphDatabase.driver("bolt://127.0.0.1:7687", AuthTokens.basic("neo4j", "123456"));
         session = driver.session();
         final Path root = Paths.get("E:\\IDEA\\a-tm\\gitlab\\new-platform");
         final Stream<Path> walk = Files.walk(root, 1);
         final List<Path> paths = walk.collect(Collectors.toList());
         final List<Path> poms = paths.stream()
                 .filter(path -> {
                     if (path.toFile().isDirectory()) {
                         final Path pom = path.resolve("pom.xml");
                         return pom.toFile().exists();
                     }
                     return false;
                 })
                 .map(path -> path.resolve("pom.xml"))
                //  .peek(path -> log.info("pom : {}", path))
                 .collect(Collectors.toList());

         try {
             for (Path pom : poms) {
                 final SAXReader saxReader = new SAXReader();
                 final Document document = saxReader.read(pom.toFile());
                 //获取整个文档
                 Element rootElement = document.getRootElement();
                 final Element groupId = rootElement.element("groupId");
                 final Element artifactId = rootElement.element("artifactId");
                 log.info("{} {}", groupId.getText(), artifactId.getText());
                 createNode(groupId.getText(), artifactId.getText());
                 final Element dependencies = rootElement.element("dependencies");
                 if (Objects.nonNull(dependencies)) {
                     final List<Element> dependency = dependencies.elements("dependency");
                     for (Element element : dependency) {
                         final Element dpGroupId = element.element("groupId");
                         final Element dpArtifactId = element.element("artifactId");
                         log.info("\t{} {}", dpGroupId.getText(), dpArtifactId.getText());
                         createNode(dpGroupId.getText(), dpArtifactId.getText());
                         createRelationship(groupId.getText(), artifactId.getText(), dpGroupId.getText(), dpArtifactId.getText());
                     }
                 }
                 log.info("-----------------------");
             }
         } finally {
             session.close();
             driver.close();
         }

     }

    private void createRelationship(String fGroup, String fArtifactId, String tGroup, String tArtifactId) {
        exec(replaceEnv(CREATE_RELATIONSHIP,
                "fGroup", replaceIgStr(fGroup), "fArtifactId", replaceIgStr(fArtifactId),
                "tGroup", replaceIgStr(tGroup), "tArtifactId", replaceIgStr(tArtifactId)));
    }


    @Test
    @SneakyThrows
    void t2() {
        driver = GraphDatabase.driver("bolt://127.0.0.1:7687", AuthTokens.basic("neo4j", "123456"));
        session = driver.session();
        final String message = "Greeting";
        String greeting = session.writeTransaction(
                new TransactionWork<String>(){
                    public String execute(Transaction tx ) {
                        Result result = tx.run("CREATE (a:Greeting) " +
                                        "SET a.message = $message " +
                                        "RETURN a.message + ', from node ' + id(a)",
                                parameters( "message", message));
                        return result.single().get( 0 ).asString();
                    }
                }
        );
        log.info(greeting);
        log.info("OK");
        driver.close();
    }

     private   void createNode( String groupId, String artifactId) {
         groupId = replaceIgStr(groupId);
         artifactId = replaceIgStr(artifactId);
         String key = artifactId + ":" + groupId;
        if (NODES.contains(key))  {
            return;
        }
        NODES.add(key);
        // exec(String.format(CREATE, artifactId, groupId, artifactId, artifactId));
         exec(replaceEnv(CREATE, "group", groupId, "artifactId", artifactId));
         // session.run(CREATE, parameters( "groupId", groupId, "artifactId", artifactId));
     }

     private   void exec(String sql) {
         log.info(sql);
         session.run(sql);
     }

     private String  replaceIgStr(String str) {
         return str.replace(".", "_").replace("-", "_");
     }

     private String replaceEnv(String str, String ... params) {
         for (int i = 0; i < params.length; i+=2) {
             str = str.replace("$" + params[i],  params[i+1]);
         }
         return str;
     }
}
