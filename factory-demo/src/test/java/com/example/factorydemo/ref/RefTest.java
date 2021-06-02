package com.example.factorydemo.ref;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.ref.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-28 14:34
 */
@Slf4j
 class RefTest {

    /**
     * 强引用
     */
    @Test
     void t1() {
        final Object o = new Object();
        log.info("强引用: {}", o);
    }

    /**
     * 软引用在java中有个专门的SoftReference类型，软引用的意思是只有在内存不足的情况下，被引用的对象才会被回收。
     */
    @Test
     void t2() {
        Object obj = new Object();
        SoftReference<Object> soft = new SoftReference<>(obj);
        obj = null;
        log.info("{}", soft.get());
        System.gc();
        log.info("{}", soft.get());
    }

    /**
     * 弱引用 weakReference和softReference很类似，不同的是weekReference引用的对象只要垃圾回收执行，就会被回收，而不管是否内存不足。
     */
    @Test
     void t3() {
        Object obj = new Object();
        WeakReference<Object> weak = new WeakReference<>(obj);
        obj = null;
        log.info("{}", weak.get());
        System.gc();
        log.info("{}", weak.get());
    }

    /**
     * 虚引用 PhantomReference的作用是跟踪垃圾回收器收集对象的活动，在GC的过程中，如果发现有PhantomReference，GC则会将引用放到ReferenceQueue中，由程序员自己处理，当程序员调用ReferenceQueue.pull()方法，将引用出ReferenceQueue移除之后，Reference对象会变成Inactive状态，意味着被引用的对象可以被回收了。
     */
    @Test
     void t4() {
        ReferenceQueue<Object> rq = new ReferenceQueue<>();
        Object obj = new Object();
        PhantomReference<Object> phantomReference = new PhantomReference<>(obj, rq);
        obj = null;
        log.info("{}", phantomReference.get());
        System.gc();
        Reference<Object> r = (Reference<Object>) rq.poll();
        log.info("{}", r);
    }

}
