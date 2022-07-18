package dgroomes;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

import java.util.Comparator;
import java.util.List;

/**
 * Inspecting the Java runtime environment.
 */
public class JavaRuntimeUtil {

    /**
     * Find all classes on the classpath.
     */
    public static List<? extends Class<?>> findClasses() {
        try (var scanResult = new ClassGraph()
                //                .acceptPackages(javaPackage.packageName())
                .scan()) {
            return scanResult.getAllClasses()
                    .stream()
                    .map(ClassInfo::loadClass)
                    .sorted(Comparator.comparing(Class::getName))
                    .toList();
        }
    }
}
