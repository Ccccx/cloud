/**
 * https://www.cnblogs.com/throwable/p/13053582.html
 * 内置的JavaFileObject标准实现SimpleJavaFileObject是面向类源码文件，由于动态编译时候输入的是类源码文件的内容字符串，需要自行实现JavaFileObject。
 * 内置的JavaFileManager是面向类路径下的Java源码文件进行加载，这里也需要自行实现JavaFileManager。
 * 需要自定义一个ClassLoader实例去加载编译出来的动态类。
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-10-27 15:49
 */
package com.example.factorydemo.dyjava;