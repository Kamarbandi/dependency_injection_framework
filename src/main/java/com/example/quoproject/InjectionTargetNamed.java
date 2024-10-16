package com.example.quoproject;


import com.example.quoproject.di.annotations.Bean;
import com.example.quoproject.di.annotations.Inject;
import com.example.quoproject.di.annotations.Named;

@Bean
public class InjectionTargetNamed {
    @Inject
    @Named("a")
    public Integer valueA;

    @Inject
    @Named("b")
    public Integer valueB;
}
