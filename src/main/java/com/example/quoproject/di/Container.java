package com.example.quoproject.di;

import com.example.quoproject.di.annotations.Bean;
import com.example.quoproject.di.annotations.Inject;
import com.example.quoproject.di.annotations.Named;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Container {
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<Class<?>, String> beanNames = new HashMap<>();

    public void addPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Bean.class);
        for (Class<?> clazz : annotatedClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                registerBean(clazz, instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create bean instance for " + clazz.getName(), e);
            }
        }
    }

    public void addBean(String name, Object instance) {
        if (beans.containsKey(name)) {
            throw new RuntimeException("Bean with name " + name + " already exists.");
        }
        beans.put(name, instance);
    }

    public void start() {
        beans.values().forEach(this::injectDependencies);
    }

    private void registerBean(Class<?> clazz, Object instance) {
        String beanName = clazz.getName();
        if (clazz.isAnnotationPresent(Named.class)) {
            Named named = clazz.getAnnotation(Named.class);
            beanName = named.value();
            if (beans.containsKey(beanName)) {
                throw new RuntimeException("Duplicate bean name: " + beanName);
            }
        }
        beans.put(beanName, instance);
        beanNames.put(clazz, beanName);
    }

    private void injectDependencies(Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    String name = getFieldBeanName(field);
                    Object dependency = beans.get(name);
                    if (dependency == null) {
                        throw new RuntimeException("No bean found for type: " + field.getType().getName());
                    }
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency", e);
                }
            }
        }
    }

    private String getFieldBeanName(Field field) {
        if (field.isAnnotationPresent(Named.class)) {
            Named named = field.getAnnotation(Named.class);
            return named.value();
        }
        return field.getType().getName();
    }

    public <T> T getBean(Class<T> type) {
        String beanName = beanNames.get(type);
        return (T) beans.get(beanName);
    }
}

