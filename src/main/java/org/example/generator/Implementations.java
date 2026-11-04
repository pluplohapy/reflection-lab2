package org.example.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class Implementations {

    private static List<Class<?>> cached;

    private Implementations() {
    }

    static List<Class<?>> findGeneratableClasses() {
        if (cached != null) {
            return cached;
        }
        List<Class<?>> found = new ArrayList<>();
        String classpath = System.getProperty("java.class.path", "");
        for (String entry : classpath.split(File.pathSeparator)) {
            File root = new File(entry);
            if (root.isDirectory()) {
                scanDirectory(root, root, found);
            }
        }
        cached = found;
        return found;
    }

    private static void scanDirectory(File root, File directory, List<Class<?>> found) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(root, file, found);
                continue;
            }
            String name = file.getName();
            if (!name.endsWith(".class")) {
                continue;
            }

            String relative = root.toPath().relativize(file.toPath()).toString();
            String className = relative.replace(File.separatorChar, '.')
                    .substring(0, relative.length() - ".class".length());
            if (className.endsWith("module-info") || className.endsWith("package-info")) {
                continue;
            }
            try {

                Class<?> clazz = Class.forName(className, false, classLoader());

                if (clazz.isAnnotationPresent(Generatable.class) && Types.isConcrete(clazz)) {
                    found.add(clazz);
                }
            } catch (Throwable ignored) {

            }
        }
    }

    private static ClassLoader classLoader() {
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        return context != null ? context : Generator.class.getClassLoader();
    }
}
