package com.cjz.webmvc.doc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-10 11:29
 */
@Slf4j
 class DocTest {

    @Test
    @SneakyThrows
    void t1() {
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

        Document parentPom = null;
        File parentFile = null;
        Element dependencyManagement = null;
        Element dependencies = null;
        String parentArtifactId = "new-platform";
        for (Path pom : poms) {
            final SAXReader saxReader = new SAXReader();
            final File file = pom.toFile();
            final Document document = saxReader.read(file);
            //获取整个文档
            Element rootElement = document.getRootElement();
            final Element groupId = rootElement.element("groupId");
            final Element artifactId = rootElement.element("artifactId");
            log.info("{} {}", groupId.getText(), artifactId.getText());
            // 父pom 创建依赖节点
            if (parentArtifactId.equals(artifactId.getText())) {
                parentPom = document;
                parentFile = file;
                dependencyManagement =  rootElement.element("dependencyManagement");
                dependencies = dependencyManagement.element("dependencies");
                continue;
            }
            Element parent = rootElement.element("parent");
            if (Objects.isNull(parent)) {
                // 创建 继承父pom节点
                final List<Element> elements = rootElement.elements();
                final Namespace namespace = rootElement.getNamespace();
                final QName qName = DocumentHelper.createQName("parent", namespace);
                parent = DocumentHelper.createElement(qName);
                addElement(parent, "groupId", "com.tiamaes.cloud.m1");
                addElement(parent, "artifactId", "new-platform");
                addElement(parent, "version", "1.1.6-SNAPSHOT");

                elements.add(0, parent);
                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
                writer.write(document);
                writer.close();

                // 在父pom添加依赖管理
                final Element dependency = dependencies.addElement("dependency");
                addElement(dependency, "groupId", groupId.getText());
                addElement(dependency, "artifactId", artifactId.getText());
                addElement(dependency, "version", "${project.version}");
            }

            log.info("-----------------------");
        }
        // 写入依赖关系
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileOutputStream(parentFile), format);
        writer.write(parentPom);
        writer.close();
    }

    public Element addElement(Element rootElement, String elementName, String elementText) {
        final Element element = rootElement.addElement(elementName);
        element.addText(elementText);
        return element;
    }
}
