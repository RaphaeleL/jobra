package com.cobra.commands;

import com.cobra.core.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BranchCommand {
    
    public static void list() throws IOException {
        Repository repo = findRepository();
        List<String> branches = repo.getRefStore().listBranches();
        String currentBranch = repo.getRefStore().getCurrentBranch();
        
        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                System.out.println("* " + branch);
            } else {
                System.out.println("  " + branch);
            }
        }
    }
    
    public static void create(String name) throws IOException {
        Repository repo = findRepository();
        String currentHead = repo.getRefStore().getHead();
        
        repo.getRefStore().createBranch(name, currentHead);
        System.out.println("Created branch '" + name + "'");
    }
    
    public static void checkout(String name) throws IOException {
        Repository repo = findRepository();
        
        // Check if branch exists
        String branchHead = repo.getRefStore().getBranchHead(name);
        if (branchHead == null) {
            throw new IOException("Branch '" + name + "' not found");
        }
        
        // Switch to branch
        repo.getRefStore().setHead("refs/heads/" + name);
        System.out.println("Switched to branch '" + name + "'");
    }
    
    public static void delete(String name) throws IOException {
        Repository repo = findRepository();
        String currentBranch = repo.getRefStore().getCurrentBranch();
        
        if (name.equals(currentBranch)) {
            throw new IOException("Cannot delete the branch you're currently on");
        }
        
        repo.getRefStore().deleteBranch(name);
        System.out.println("Deleted branch '" + name + "'");
    }
    
    public static void merge(String name) throws IOException {
        Repository repo = findRepository();
        
        // Check if branch exists
        String branchHead = repo.getRefStore().getBranchHead(name);
        if (branchHead == null) {
            throw new IOException("Branch '" + name + "' not found");
        }
        
        // For now, just create a merge commit (simplified)
        String currentHead = repo.getRefStore().getHead();
        String treeHash = "tree-hash"; // This would be computed from the merged tree
        
        String mergeHash = repo.createCommit("Merge branch '" + name + "'", treeHash, currentHead);
        repo.getRefStore().setBranchHead(repo.getRefStore().getCurrentBranch(), mergeHash);
        
        System.out.println("Merged branch '" + name + "'");
    }
    
    public static void rebase(String name) throws IOException {
        Repository repo = findRepository();
        
        // Check if branch exists
        String branchHead = repo.getRefStore().getBranchHead(name);
        if (branchHead == null) {
            throw new IOException("Branch '" + name + "' not found");
        }
        
        // For now, just print a message (rebase is complex)
        System.out.println("Rebase not yet fully implemented");
        System.out.println("Would rebase current branch onto '" + name + "'");
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