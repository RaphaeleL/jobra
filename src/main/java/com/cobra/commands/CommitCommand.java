package com.cobra.commands;

import com.cobra.core.Repository;
import com.cobra.core.Tree;
import com.cobra.core.IndexEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommitCommand {
    
    public static void run(String message) throws IOException {
        // Find repository
        Repository repo = findRepository();
        
        // Get current HEAD
        String parentHash = repo.getRefStore().getHead();
        
        // Create tree from index
        String treeHash = createTreeFromIndex(repo);
        
        // Create commit
        String commitHash = repo.createCommit(message, treeHash, parentHash);
        
        // Update HEAD to point to new commit
        String currentBranch = repo.getRefStore().getCurrentBranch();
        if (currentBranch != null) {
            repo.getRefStore().setBranchHead(currentBranch, commitHash);
        } else {
            repo.getRefStore().setHeadCommit(commitHash);
        }
        
        System.out.println("[" + commitHash.substring(0, 8) + "] " + message);
        System.out.println("  " + repo.getIndex().getEntries().size() + " files changed");
    }
    
    private static String createTreeFromIndex(Repository repo) throws IOException {
        Tree tree = new Tree();
        List<IndexEntry> entries = repo.getIndex().getEntries();
        
        for (IndexEntry entry : entries) {
            String[] pathParts = entry.getPath().split("/");
            String fileName = pathParts[pathParts.length - 1];
            
            Tree.TreeEntry treeEntry = new Tree.TreeEntry(
                entry.getMode(),
                fileName,
                entry.getHash()
            );
            tree.addEntry(treeEntry);
        }
        
        return repo.createTree(tree);
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