package com.example.mybatis.rest.utils;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.springframework.util.ReflectionUtils;

import javax.tools.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 16:02
 */
@Slf4j
@SuppressWarnings("all")
public class DynamicCompilerUtils {
    /**
     * 获取系统编译器实例
     */
    public static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    /**
     * 初始化自定义类加载器
     */
    public static final JdkDynamicCompileClassLoader CLASSLOADER = new JdkDynamicCompileClassLoader(Thread.currentThread().getContextClassLoader());

    /**
     * 编译诊断收集器
     */
    public static final DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();
    /**
     * 获取标准的Java文件管理器实例
     */
    public static final StandardJavaFileManager MANAGER = COMPILER.getStandardFileManager(DIAGNOSTIC_COLLECTOR, null, null);

    public static final SpringClassFinder FINDER = new SpringClassFinder(Thread.currentThread().getContextClassLoader());
    /**
     * 初始化自定义Java文件管理器实例
     */
    public static final JdkDynamicCompileJavaFileManager FILE_MANAGER = new JdkDynamicCompileJavaFileManager(MANAGER, CLASSLOADER, FINDER);

    /**
     * 设置编译参数
     */
    protected static final List<String> OPTIONS = new ArrayList<>();

    static {
        OPTIONS.add("-source");
        OPTIONS.add("1.8");
        OPTIONS.add("-target");
        OPTIONS.add("1.8");
        boolean addDependencies = false;

        // 将自定义的类加载器加入到Mybatis封装的Wrapper中
        Resources.setDefaultClassLoader(CLASSLOADER);

        FINDER.init();
    }


    public static Class<?> compile(String packageName,
                                   String className,
                                   String sourceCode) throws Exception {

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
        log.debug("【{}】编译【{}】", qualifiedName, result ? "完成" : "失败");
        if (BooleanUtils.isFalse(result) && log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(StringUtils.repeat("-", 50)).append("\n");
            final List<Diagnostic<? extends JavaFileObject>> diagnostics = DIAGNOSTIC_COLLECTOR.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                sb.append(diagnostic.toString()).append("\n");
            }
            sb.append(StringUtils.repeat("-", 50)).append("\n");
            log.debug(sb.toString());
        }
        //        增强类
        //        final Class<?> loadClass = CLASSLOADER.loadClass(qualifiedName)
        //        return injectAppClassLoad(loadClass)
        clearReport();
        return CLASSLOADER.loadClass(qualifiedName);
    }

    private static void clearReport() {
        final Field diagnostics = ReflectionUtils.findField(DiagnosticCollector.class, "diagnostics");
        diagnostics.setAccessible(true);
        ReflectionUtils.setField(diagnostics, DIAGNOSTIC_COLLECTOR, Collections.synchronizedList(new ArrayList<JavaFileObject>()));
    }

    public static Class<?> injectAppClassLoad(Class<?> clz) {
        final Unloaded<?> mapperMake = new ByteBuddy()
                .redefine(clz)
                .name(clz.getCanonicalName())
                .make();
        return mapperMake.load(DynamicCompilerUtils.class.getClassLoader(), Default.INJECTION).getLoaded();
    }

}
