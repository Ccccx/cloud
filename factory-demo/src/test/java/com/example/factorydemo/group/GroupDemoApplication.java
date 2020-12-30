package com.example.factorydemo.group;

import com.example.factorydemo.bean.Foo;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * // 分组功能测试
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 14:16
 */
public class GroupDemoApplication {

    /**
     * 常规bean注入
     */
    @Autowired
    private List<Foo> simpleList;

    @Autowired
    private ObjectFactory<List<Foo>> simpleBFList;

    @Autowired
    private ObjectProvider<List<Foo>> simpleOPList;

    /**
     * 限定bean注入
     */
    @Autowired
    @Qualifier
    private List<Foo> qualifierList;

    /**
     * 分组bean注入
     */
    @Autowired
    @FooGroup
    private List<Foo> groupList;


    public static void main(String[] args) throws Exception {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(GroupDemoApplication.class);
        context.refresh();
        final GroupDemoApplication contextBean = context.getBean(GroupDemoApplication.class);
        final DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        System.out.println("contextBean.simpleList :" + contextBean.simpleList);
        System.out.println("contextBean.qualifierList :" + contextBean.qualifierList);
        System.out.println("contextBean.groupList :" + contextBean.groupList);
        System.out.println("--------------------");
        contextBean.simpleBFList.getObject().forEach(System.out::println);
        System.out.println("--------------------");
        contextBean.simpleOPList.getObject().forEach(System.out::println);
        context.close();
    }

    @Bean
    public Foo foo1() {
        return createInstance("foo1");
    }

    @Bean
    public Foo foo2() {
        return createInstance("foo2");
    }

    @Bean
    @Qualifier
    public Foo foo3() {
        return createInstance("foo3");
    }

    @Bean
    @Qualifier
    public Foo foo4() {
        return createInstance("foo4");
    }


    @Bean
    @FooGroup
    public Foo foo5() {
        return createInstance("foo5");
    }

    @Bean
    @FooGroup
    public Foo foo6() {
        return createInstance("foo6");
    }


    public Foo createInstance(String name) {
        final Foo foo = new Foo();
        foo.setName(name);
        return foo;
    }


}
