package com.example.factorydemo.life;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-09 15:29
 */
@Configuration
public class LifeDemoApplication {
    public static void main(String[] args) throws InterruptedException {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.example.factorydemo.life");
        applicationContext.register(DemoFactoryPostProcessor.class);
        applicationContext.register(LifeDemoApplication.class);
        applicationContext.refresh();
        System.out.println("应用启动完成");
        final LifeDemo lifeDemo = applicationContext.getBean(LifeDemo.class);
        System.out.println(lifeDemo);
        applicationContext.close();
        System.out.println("应用关闭完成");
    }

    @Bean(name = {"demo", "demoLife"}, initMethod = "initMethod", destroyMethod = "destroyMethod")
    public LifeDemo lifeDemo() {
        return new LifeDemo("生命周期测试", "测试生命周期的不同回调");
    }
}
