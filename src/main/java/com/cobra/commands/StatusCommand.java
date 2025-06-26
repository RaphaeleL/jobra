package com.cobra.commands;

import com.cobra.core.Repository;
import com.cobra.core.IndexEntry;
import com.cobra.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class StatusCommand {
    
    public static void run() throws IOException {
        // Find repository
        Repository repo = findRepository();
        
        // Get current branch
        String currentBranch = repo.getRefStore().getCurrentBranch();
        if (currentBranch == null) {
            currentBranch = "HEAD";
        }
        
        System.out.println("On branch " + currentBranch);
        System.out.println();
        
        // Get staged files
        List<IndexEntry> stagedFiles = repo.getIndex().getEntries();
        if (!stagedFiles.isEmpty()) {
            System.out.println("Changes to be committed:");
            System.out.println("  (use \"cobra reset HEAD <file>\" to unstage)");
            System.out.println();
            
            for (IndexEntry entry : stagedFiles) {
                System.out.println("\tmodified: " + entry.getPath());
            }
            System.out.println();
        }
        
        // Get unstaged files (simplified - just check if files exist and are not in index)
        List<Path> unstagedFiles = FileUtils.walkFiles(repo.getRootPath())
            .filter(path -> !repo.getIndex().hasEntry(repo.getRootPath().relativize(path).toString()))
            .collect(Collectors.toList());
        
        if (!unstagedFiles.isEmpty()) {
            System.out.println("Untracked files:");
            System.out.println("  (use \"cobra add <file>\" to include in what will be committed)");
            System.out.println();
            
            for (Path file : unstagedFiles) {
                String relativePath = repo.getRootPath().relativize(file).toString();
                System.out.println("\t" + relativePath);
            }
            System.out.println();
        }
        
        if (stagedFiles.isEmpty() && unstagedFiles.isEmpty()) {
            System.out.println("nothing to commit, working tree clean");
        }
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