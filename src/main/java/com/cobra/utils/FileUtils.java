package com.cobra.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FileUtils {
    
    public static boolean isIgnored(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.startsWith(".") || 
               fileName.equals(".cobra") || 
               fileName.equals("target") ||
               fileName.equals("build") ||
               fileName.equals("node_modules") ||
               fileName.endsWith(".class") ||
               fileName.endsWith(".jar");
    }
    
    public static Stream<Path> walkFiles(Path root) throws IOException {
        return Files.walk(root)
                   .filter(Files::isRegularFile)
                   .filter(path -> !isIgnored(path));
    }
    
    public static String getRelativePath(Path root, Path file) {
        return root.relativize(file).toString();
    }
    
    public static String getFileMode(Path path) throws IOException {
        if (Files.isExecutable(path)) {
            return "100755";
        } else {
            return "100644";
        }
    }
} 