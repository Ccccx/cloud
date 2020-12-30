package com.example.factorydemo.aop;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author chengjz
 * @version 1.0
 * @date 2020-04-20 10:51
 */
public class AopTest {
    @Test
    public void t1() {
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        //ajexp.setExpression("execution(* com.tiamaes.cloud.bigscreen.*.controller.*.*(..))");
        ajexp.setExpression("target(org.springframework.beans.factory.BeanFactory)");
        System.out.println(ajexp.matches(DefaultListableBeanFactory.class));

    }
}
