package com.example.quoproject;

import com.example.quoproject.di.Container;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainerTest {
    @Test
    public void testInjectWithNamedInjection() {
        Integer beanA = 1;
        Integer beanB = 0;
        Container container = new Container();
        container.addPackage("com.example.quoproject");
        container.addBean("a", beanA);
        container.addBean("b", beanB);
        container.start();

        InjectionTargetNamed injectionTargetNamed = container.getBean(InjectionTargetNamed.class);
        assertEquals(beanA, injectionTargetNamed.valueA);
        assertEquals(beanB, injectionTargetNamed.valueB);
    }
}

