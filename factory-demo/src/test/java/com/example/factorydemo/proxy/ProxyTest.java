package com.example.factorydemo.proxy;

import com.example.factorydemo.bean.Foo;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import java.lang.reflect.Proxy;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-28 11:45
 */
@Slf4j
public class ProxyTest {

    /**
     * java原生方法创建代理对象
     */
    @Test
    public void test() {
        //    我们要代理的真实对象
        Boy boy = new Boy();
        //    我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
        final JdkProxy jdkProxy = new JdkProxy(boy);
        /*
         * 通过Proxy的newProxyInstance方法来创建我们的代理对象，我们来看看其三个参数
         * 第一个参数 handler.getClass().getClassLoader() ，我们这里使用handler这个类的ClassLoader对象来加载我们的代理对象
         * 第二个参数realSubject.getClass().getInterfaces()，我们这里为代理对象提供的接口是真实对象所实行的接口，表示我要代理的是该真实对象，这样我就能调用这组接口中的方法了
         * 第三个参数handler， 我们这里将这个代理对象关联到了上方的 InvocationHandler 这个对象上
         */
        final IPeople IPeople = (IPeople) Proxy.newProxyInstance(boy.getClass().getClassLoader(), boy.getClass().getInterfaces(), jdkProxy);
        final Class<? extends IPeople> clz = IPeople.getClass();
        System.err.println("是否是接口： " + clz.isInterface());
        System.err.println("是否是代理对象： " + Proxy.isProxyClass(clz));
        System.err.println("代理接口： " + clz.getInterfaces());
        final String result = IPeople.say("Ccxxxx");
        System.err.println(result);
    }

    @Test
    public void test1() {
        final String name = "cjz";
        MethodBeforeAdvice before = (method, args, target) -> {
            log.info("before  method: {} args: {}", method, args);
        };
        MethodInterceptor interceptor = (invocation -> {
            log.info("interceptor: method {} args: {}", invocation.getMethod(), invocation.getArguments());
            return name;
        });
        AfterReturningAdvice after = (returnValue, method, args, target) -> {
            log.info("returnValue: {} method: {}", returnValue, method);
            returnValue = "123456";
        };
        //MethodBeforeAdviceInterceptor beforeAdviceInterceptor = new MethodBeforeAdviceInterceptor(before);
        final AdvisedSupport sp = new AdvisedSupport(IPeople.class);
        sp.addAdvice(before);
        sp.addAdvice(after);
        sp.addAdvice(interceptor);
        final DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        final AopProxy aopProxy = proxyFactory.createAopProxy(sp);
        final IPeople people = (IPeople) aopProxy.getProxy();
        System.out.println(people.say("Hello"));
    }

    @Test
    public void test2() {
        final Foo foo = new Foo("cx", false, 10);
        final AdvisedSupport sp = new AdvisedSupport(Lockable.class);
        sp.addAdvisor(new LockMixinAdvisor());
        sp.setTarget(foo);
        final DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        final AopProxy aopProxy = proxyFactory.createAopProxy(sp);
        final Lockable lockable = (Lockable) aopProxy.getProxy();
        lockable.locked();
        System.out.println(foo.name);
        foo.setName("test");
        System.out.println(foo.name);
    }


    public interface Lockable {
        void lock();

        void unlock();

        boolean locked();
    }


    public static class LockMixin extends DelegatingIntroductionInterceptor implements Lockable {
        private boolean locked;

        public void lock() {
            this.locked = true;
        }

        public void unlock() {
            this.locked = false;
        }

        public boolean locked() {
            return this.locked;
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (locked() && invocation.getMethod().getName().indexOf("set") == 0) {
                throw new Exception("已被锁定,不允许操作set方法");
            }
            return super.invoke(invocation);
        }
    }

    public static class LockMixinAdvisor extends DefaultIntroductionAdvisor {

        public LockMixinAdvisor() {
            super(new LockMixin(), Lockable.class);
        }
    }
}
