package com.example;

import com.google.inject.Injector;
import com.example.config.BurlesqueModule;

import static com.google.inject.Guice.createInjector;

public class Main {
    public static void main(String[] args) {
        Injector injector = createInjector(new BurlesqueModule());
        injector.getInstance(BurlesqueREST.class);
    }
}
