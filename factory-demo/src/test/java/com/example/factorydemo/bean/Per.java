package com.example.factorydemo.bean;


/**
 * @author chengjz
 * @version 1.0
 * @since 2020-12-30 9:42
 */
public class Per {
    public static void newInstance() {
        Per per = new Per();
        per.logClassLoader();
    }

    public void logClassLoader() {
        System.out.println("Bar: " + this.getClass().getClassLoader());
        final Foo foo = new Foo("Ccx", true, 25);
        foo.logClassLoader();
    }
}
