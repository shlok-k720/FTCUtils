package com.rmsftc.utils.ftc;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.rmsftc.utils.ftc.daemons.Daemon;
import com.rmsftc.utils.ftc.daemons.FTCDaemon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.frozenmilk.sinister.Scanner;
import dev.frozenmilk.sinister.targeting.NarrowSearch;
import dev.frozenmilk.sinister.targeting.SearchTarget;
import dev.frozenmilk.util.graph.Graph;
import dev.frozenmilk.util.graph.rule.AdjacencyRule;

public final class DaemonManager {
    static final List<RegisteredDaemon> daemons = new ArrayList<>();
    static final Map<ClassLoader, List<RegisteredDaemon>> byLoader = new HashMap<>();

    public static List<RegisteredDaemon> unmodifiable() {
        return Collections.unmodifiableList(daemons);
    }

    public static RegisteredDaemon getProcedure(String id) {
        return daemons.stream()
                .filter(daemon -> daemon.id.equals(id))
                .findFirst()
                .orElse(null);
    }



    public static class RegisteredDaemon {
        public final String name;
        public final String id;
        public final Daemon daemon;
        public RegisteredDaemon(String name, Method method) {
            this.name = name;
            this.id = nanoId();
            this.daemon = () -> {
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        private static String nanoId() {
            return com.aventrix.jnanoid.jnanoid.NanoIdUtils.randomNanoId(
                    NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 6
            );
        }
    }


    public static class DaemonFinder implements Scanner {
        public static final DaemonFinder INSTANCE = new DaemonFinder();
        private final SearchTarget searchTarget = new NarrowSearch();
        @Override
        public AdjacencyRule<Scanner, Graph<Scanner>> getLoadAdjacencyRule() {
            return Scanner.INDEPENDENT();
        }

        @Override
        public AdjacencyRule<Scanner, Graph<Scanner>> getUnloadAdjacencyRule() {
            return Scanner.INDEPENDENT();
        }

        @Override
        public SearchTarget getTargets() {
            return searchTarget;
        }

        @Override
        public void scan(ClassLoader classLoader, Class<?> aClass) {
            List<Method> methods = Arrays.stream(aClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(FTCDaemon.class))
                    .collect(Collectors.toList());
            for (Method factory : methods) {
                if (!Modifier.isStatic(factory.getModifiers()))
                    throw new IllegalArgumentException(String.format("Method %s.%s is annotated with @FTCDaemon, but is not static.", aClass.getCanonicalName(), factory.getName()));
                if (factory.getParameterCount() != 0)
                    throw new IllegalArgumentException(String.format("Method %s.%s is annotated with @FTCDaemon, but has %d parameters instead of 0.", aClass.getCanonicalName(), factory.getName(), factory.getParameterCount()));
                if (factory.getGenericReturnType() != Void.class)
                    throw new IllegalArgumentException(String.format("Method %s.%s is annotated with @FTCDaemon, but returns %s instead of Void.", aClass.getCanonicalName(), factory.getName(), factory.getReturnType().getCanonicalName()));
                factory.setAccessible(true);
                FTCDaemon annotation = factory.getAnnotation(FTCDaemon.class);
                String name;
                if (!annotation.name().isEmpty()) {
                    name = annotation.name();
                } else {
                    name = factory.getName();
                }
                RegisteredDaemon registered = new RegisteredDaemon(name, factory);
                byLoader.computeIfAbsent(classLoader, (unused) -> new ArrayList<>()).add(registered);
                daemons.add(registered);
            }
        }

        @Override
        public void unload(ClassLoader classLoader, Class<?> aClass) {

        }


        @Override
        public void beforeUnload(ClassLoader loader) {
            List<RegisteredDaemon> registeredDaemons = byLoader.get(loader);
            if (registeredDaemons != null) {
                daemons.removeAll(registeredDaemons);
                byLoader.remove(loader);
            }
        }
    }
}
