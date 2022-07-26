package org.demakov.mongoinitializer.generator;

import org.apache.commons.io.FilenameUtils;

import javax.tools.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

public class YamlClassCompilator {

    public static List<Class> compile(File directory) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Class> result = new ArrayList<>();
        /** Compilation Requirements *********************************************************************************************/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        // This sets up the class path that the compiler will use.
        // I've added the .jar file that contains the DoStuff interface within in it...
        List<String> optionList = new ArrayList<String>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

        Iterable<? extends JavaFileObject> compilationUnit
                = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(Objects.requireNonNull(filesInDirectory(directory, ".java"))));
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                optionList,
                null,
                compilationUnit);
        /********************************************************************************************* Compilation Requirements **/
        if (task.call()) {
            /** Load and execute *************************************************************************************************/
            // Create a new custom class loader, pointing to the directory that contains the compiled
            // classes, this should point to the top of the package structure!
            URLClassLoader classLoader = new URLClassLoader(new URL[]{directory.toURI().toURL()});

            System.out.println(Arrays
                    .stream(filesInDirectory(directory, "class"))
                    .map(file -> FilenameUtils.removeExtension(file.getName()))
                    .collect(Collectors.toList()));

            // Load the class from the classloader by name....
            result.addAll(
                    Arrays
                    .stream(filesInDirectory(directory, "class"))
                    .map(file -> {
                        try {
                            return classLoader.loadClass(FilenameUtils.removeExtension(file.getName()));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()));
            /************************************************************************************************* Load and execute **/
        }
        fileManager.close();
        return result;
    }

    public interface DoStuff {

        public void doStuff();
    }

    private static File[] filesInDirectory(File directory, String extension) {
        File [] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(extension);
            }
        });

        return files;
    }

}



