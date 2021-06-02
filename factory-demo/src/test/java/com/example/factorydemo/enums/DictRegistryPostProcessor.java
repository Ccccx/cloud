package com.example.factorydemo.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.factorydemo.enums.DictHolders.MAPPER_HOLDERS;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-12 16:21
 */
@Slf4j
public class DictRegistryPostProcessor   implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false) {
            @Override
            protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
                Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
                if (beanDefinitions.isEmpty()) {
                    log.warn( "没有在 {} 找到相关类", basePackages);
                } else {
                    for (BeanDefinitionHolder holder : beanDefinitions) {
                        final GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
                        final String beanClassName = genericBeanDefinition.getBeanClassName();
                        final Class<?> aClass;
                        try {
                            aClass = Class.forName(beanClassName);
                            if (aClass.isEnum()) {
                                final DictMapper annotation = AnnotationUtils.findAnnotation(aClass, DictMapper.class);
                                final String  dictName = annotation.dictName();
                                final String  dictDesc = annotation.dictDesc();
                                final String key = annotation.key();
                                final String val = annotation.val();
                                final DictHolder holders = MAPPER_HOLDERS.getOrDefault(dictName, new DictHolder(dictName, dictDesc));
                                final Method values = aClass.getMethod("values");
                                final Object[] invoke = (Object[]) values.invoke(null);
                                final Field keyField = ReflectionUtils.findField(aClass, key);
                                keyField.setAccessible(true);
                                final Field valField = ReflectionUtils.findField(aClass, val);
                                valField.setAccessible(true);
                                for (Object obj : invoke) {
                                    final DictOption dictOption = new DictOption(keyField.get(obj), valField.get(obj));
                                    log.info("{}", dictOption);
                                    holders.addDictOption(dictOption);
                                }
                                MAPPER_HOLDERS.put(key, holders);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return new LinkedHashSet<>();
            }

            @Override
            protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
                // 不需要注册到容器里
            }
        };


        // List<String> packages = AutoConfigurationPackages.get(context);
        List<String> packages = new ArrayList<>();
        packages.add("com.example.factorydemo.enums");
        scanner.setIncludeAnnotationConfig(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(DictMapper.class));
        scanner.scan(packages.toArray(new String[0]));
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EnumDictFactory) {
            final EnumDictFactory dictFactory = (EnumDictFactory) bean;
            final DictHolder dictHolder = dictFactory.getDictHolder();
            MAPPER_HOLDERS.put(dictHolder.getDictName(), dictHolder);
            log.info("process {} success.", bean.getClass());
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        return;
    }
}
