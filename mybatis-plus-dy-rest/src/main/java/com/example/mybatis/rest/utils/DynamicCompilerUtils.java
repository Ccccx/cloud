package com.example.mybatis.rest.utils;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default;

import javax.tools.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 16:02
 */
@Slf4j
public class DynamicCompilerUtils {
    /** 获取系统编译器实例 */
    public static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
    /**
     * 编译诊断收集器
     */
    public static final DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();
    /** 获取标准的Java文件管理器实例 */
    public static final StandardJavaFileManager MANAGER = COMPILER.getStandardFileManager(DIAGNOSTIC_COLLECTOR, null, null);
    /**初始化自定义类加载器 */
    public static final JdkDynamicCompileClassLoader CLASSLOADER = new JdkDynamicCompileClassLoader(Thread.currentThread().getContextClassLoader());
    /** 初始化自定义Java文件管理器实例 */
    public static final JdkDynamicCompileJavaFileManager FILE_MANAGER = new JdkDynamicCompileJavaFileManager(MANAGER, CLASSLOADER);

    /**
     * 设置编译参数
     */
    protected static final   List<String> OPTIONS = new ArrayList<>();
    static {
        OPTIONS.add("-source");
        OPTIONS.add("1.8");
        OPTIONS.add("-target");
        OPTIONS.add("1.8");
    }

    @SuppressWarnings("unchecked")
    public static  Class<?> compile(String packageName,
                                String className,
                                String sourceCode ) throws Exception {

        String qualifiedName = packageName + "." + className;
        // 构建Java源文件实例
        CharSequenceJavaFileObject javaFileObject = new CharSequenceJavaFileObject(className, sourceCode);

        // 添加Java源文件实例到自定义Java文件管理器实例中
        FILE_MANAGER.addJavaFileObject(
                StandardLocation.SOURCE_PATH,
                packageName,
                className + CharSequenceJavaFileObject.JAVA_EXTENSION,
                javaFileObject
        );
        // 初始化一个编译任务实例
        JavaCompiler.CompilationTask compilationTask = COMPILER.getTask(
                null,
                FILE_MANAGER,
                DIAGNOSTIC_COLLECTOR,
                OPTIONS,
                null,
                Lists.newArrayList(javaFileObject)
        );
        Boolean result = compilationTask.call();
        log.info("编译[{}}] 结果: {}", qualifiedName, result);
//        final Class<?> loadClass = CLASSLOADER.loadClass(qualifiedName);
//        return injectAppClassLoad(loadClass);
        return CLASSLOADER.loadClass(qualifiedName);
    }

    public static Class<?> injectAppClassLoad(Class<?> clz) {
        final Unloaded<?> mapperMake = new ByteBuddy()
                .redefine(clz)
                .name(clz.getCanonicalName())
                .make();
       return mapperMake.load(DynamicCompilerUtils.class.getClassLoader(), Default.INJECTION).getLoaded();
    }

}
