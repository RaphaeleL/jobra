package com.cobra.commands;

import com.cobra.core.Repository;
import com.cobra.core.GitObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogCommand {
    
    private static final Pattern COMMIT_PATTERN = Pattern.compile(
        "tree ([a-f0-9]+)\\s*\\n" +
        "(?:parent ([a-f0-9]+)\\s*\\n)?" +
        "author .*\\s*\\n" +
        "committer .*\\s*\\n\\s*\\n" +
        "(.+)", 
        Pattern.DOTALL
    );
    
    public static void run() throws IOException {
        // Find repository
        Repository repo = findRepository();
        
        // Get current HEAD
        String currentHash = repo.getRefStore().getHead();
        
        if (currentHash == null || currentHash.isEmpty()) {
            System.out.println("No commits yet");
            return;
        }
        
        // Walk through commit history
        String commitHash = currentHash;
        while (commitHash != null && !commitHash.isEmpty()) {
            printCommit(repo, commitHash);
            
            // Get parent commit
            GitObject commitObj = repo.readObject(commitHash);
            Matcher matcher = COMMIT_PATTERN.matcher(commitObj.getContent());
            if (matcher.matches()) {
                commitHash = matcher.group(2); // parent hash
            } else {
                commitHash = null;
            }
        }
    }
    
    private static void printCommit(Repository repo, String commitHash) throws IOException {
        GitObject commitObj = repo.readObject(commitHash);
        Matcher matcher = COMMIT_PATTERN.matcher(commitObj.getContent());
        
        if (matcher.matches()) {
            String treeHash = matcher.group(1);
            String parentHash = matcher.group(2);
            String message = matcher.group(3).trim();
            
            System.out.println("commit " + commitHash);
            System.out.println("tree " + treeHash);
            if (parentHash != null) {
                System.out.println("parent " + parentHash);
            }
            System.out.println();
            System.out.println("    " + message);
            System.out.println();
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