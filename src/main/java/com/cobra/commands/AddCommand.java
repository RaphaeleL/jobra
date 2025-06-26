package com.cobra.commands;

import com.cobra.core.Repository;
import com.cobra.core.IndexEntry;
import com.cobra.utils.HashUtils;
import com.cobra.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddCommand {
    
    public static void run(String file) throws IOException {
        // Find repository
        Repository repo = findRepository();
        
        // Get absolute path of file
        Path filePath = Paths.get(file).toAbsolutePath();
        Path repoRoot = repo.getRootPath();
        
        // Check if file exists
        if (!filePath.toFile().exists()) {
            throw new IOException("File not found: " + file);
        }
        
        // Get relative path from repository root
        String relativePath = repoRoot.relativize(filePath).toString();
        
        // Create blob object
        String blobHash = repo.createBlob(filePath);
        
        // Get file mode
        String mode = FileUtils.getFileMode(filePath);
        
        // Get file size
        long size = filePath.toFile().length();
        
        // Create index entry
        IndexEntry entry = new IndexEntry(relativePath, blobHash, mode, size);
        
        // Add to index
        repo.addToIndex(entry);
        
        System.out.println("Added '" + relativePath + "' to staging area");
    }
    
    private static Repository findRepository() throws IOException {
        Path currentDir = Paths.get(".").toAbsolutePath();
        
        // Walk up the directory tree to find .cobra directory
        while (currentDir != null) {
            if (Repository.exists(currentDir.toString())) {
                return Repository.open(currentDir.toString());
            }
            currentDir = currentDir.getParent();
        }
        
        throw new IOException("Not a cobra repository (or any of the parent directories)");
    }
} 