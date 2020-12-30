package com.example.factorydemo.dyjava;

import org.assertj.core.util.Lists;

import javax.tools.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 16:02
 */
public class Client {

    static String SOURCE_CODE = "package com.example.factorydemo.dyjava.impl;\n" +
            "\n" +
            "import com.example.factorydemo.dyjava.IHelloService;\n" +
            "\n" +
            "/**\n" +
            " * @author chengjz\n" +
            " * @version 1.0\n" +
            " * @since 2020-10-27 15:45\n" +
            " */\n" +
            "public class HelloServiceImpl implements IHelloService{\n" +
            "\t@Override\n" +
            "\tpublic void sayHello(String name) {\n" +
            "\t\tSystem.out.println(String.format(\"%s say hello [by default]\", name));\n" +
            "\t}\n" +
            "}\n";

    /**
     * 编译诊断收集器
     */
    static DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();

    public static void main(String[] args) throws Exception {
        String packageName = "com.example.factorydemo.dyjava.impl";
        String className = "HelloServiceImpl";
        IHelloService instance = compile(packageName, className, SOURCE_CODE, null, null);
        instance.sayHello("Cheng Jinzhou");
    }

    @SuppressWarnings("unchecked")
    public static <T> T compile(String packageName,
                                String className,
                                String sourceCode,
                                Class<?>[] constructorParamTypes,
                                Object[] constructorParams) throws Exception {
        // 获取系统编译器实例
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 设置编译参数
        List<String> options = new ArrayList<>();
        options.add("-source");
        options.add("1.6");
        options.add("-target");
        options.add("1.6");
        // 获取标准的Java文件管理器实例
        StandardJavaFileManager manager = compiler.getStandardFileManager(DIAGNOSTIC_COLLECTOR, null, null);
        // 初始化自定义类加载器
        JdkDynamicCompileClassLoader classLoader = new JdkDynamicCompileClassLoader(Thread.currentThread().getContextClassLoader());
        // 初始化自定义Java文件管理器实例
        JdkDynamicCompileJavaFileManager fileManager = new JdkDynamicCompileJavaFileManager(manager, classLoader);
        String qualifiedName = packageName + "." + className;
        // 构建Java源文件实例
        CharSequenceJavaFileObject javaFileObject = new CharSequenceJavaFileObject(className, sourceCode);
        // 添加Java源文件实例到自定义Java文件管理器实例中
        fileManager.addJavaFileObject(
                StandardLocation.SOURCE_PATH,
                packageName,
                className + CharSequenceJavaFileObject.JAVA_EXTENSION,
                javaFileObject
        );
        // 初始化一个编译任务实例
        JavaCompiler.CompilationTask compilationTask = compiler.getTask(
                null,
                fileManager,
                DIAGNOSTIC_COLLECTOR,
                options,
                null,
                Lists.newArrayList(javaFileObject)
        );
        Boolean result = compilationTask.call();
        System.out.println(String.format("编译[%s]结果:%s", qualifiedName, result));
        Class<?> klass = classLoader.loadClass(qualifiedName);
        return (T) klass.getDeclaredConstructor(constructorParamTypes).newInstance(constructorParams);
    }
}
