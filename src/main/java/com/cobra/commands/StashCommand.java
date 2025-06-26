package com.cobra.commands;

import com.cobra.core.Repository;
import com.cobra.core.IndexEntry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StashCommand {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void push(String message) throws IOException {
        Repository repo = findRepository();
        
        // Get current index entries
        List<IndexEntry> entries = repo.getIndex().getEntries();
        
        if (entries.isEmpty()) {
            System.out.println("No changes to stash");
            return;
        }
        
        // Create stash entry
        StashEntry stash = new StashEntry();
        stash.setMessage(message != null ? message : "WIP on " + repo.getRefStore().getCurrentBranch());
        stash.setEntries(entries);
        stash.setBranch(repo.getRefStore().getCurrentBranch());
        
        // Save stash
        List<StashEntry> stashes = loadStashes(repo);
        stashes.add(0, stash);
        saveStashes(repo, stashes);
        
        // Clear index
        repo.getIndex().clear();
        repo.saveIndex();
        
        System.out.println("Saved working directory and index state " + stash.getMessage());
    }
    
    public static void list() throws IOException {
        Repository repo = findRepository();
        List<StashEntry> stashes = loadStashes(repo);
        
        if (stashes.isEmpty()) {
            System.out.println("No stashes found");
            return;
        }
        
        for (int i = 0; i < stashes.size(); i++) {
            StashEntry stash = stashes.get(i);
            System.out.println("stash@{" + i + "}: " + stash.getMessage());
        }
    }
    
    public static void show(String stashRef) throws IOException {
        Repository repo = findRepository();
        StashEntry stash = getStashByRef(repo, stashRef);
        
        if (stash == null) {
            throw new IOException("Stash not found: " + stashRef);
        }
        
        System.out.println("diff --git a/stash b/stash");
        System.out.println("index " + stash.getEntries().size() + " files");
        System.out.println("--- a/stash");
        System.out.println("+++ b/stash");
        System.out.println("@@ -0,0 +1," + stash.getEntries().size() + " @@");
        
        for (IndexEntry entry : stash.getEntries()) {
            System.out.println("+" + entry.getPath());
        }
    }
    
    public static void apply(String stashRef) throws IOException {
        Repository repo = findRepository();
        StashEntry stash = getStashByRef(repo, stashRef);
        
        if (stash == null) {
            throw new IOException("Stash not found: " + stashRef);
        }
        
        // Apply stash entries to index
        for (IndexEntry entry : stash.getEntries()) {
            repo.addToIndex(entry);
        }
        
        System.out.println("Applied stash " + stashRef);
    }
    
    public static void drop(String stashRef) throws IOException {
        Repository repo = findRepository();
        List<StashEntry> stashes = loadStashes(repo);
        
        int index = getStashIndex(stashRef);
        if (index < 0 || index >= stashes.size()) {
            throw new IOException("Stash not found: " + stashRef);
        }
        
        StashEntry removed = stashes.remove(index);
        saveStashes(repo, stashes);
        
        System.out.println("Dropped stash " + stashRef + " (" + removed.getMessage() + ")");
    }
    
    private static List<StashEntry> loadStashes(Repository repo) throws IOException {
        Path stashFile = repo.getJobraDir().resolve("stash");
        if (!stashFile.toFile().exists()) {
            return new ArrayList<>();
        }
        
        String json = new String(java.nio.file.Files.readAllBytes(stashFile));
        return objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, StashEntry.class));
    }
    
    private static void saveStashes(Repository repo, List<StashEntry> stashes) throws IOException {
        Path stashFile = repo.getJobraDir().resolve("stash");
        String json = objectMapper.writeValueAsString(stashes);
        java.nio.file.Files.write(stashFile, json.getBytes());
    }
    
    private static StashEntry getStashByRef(Repository repo, String stashRef) throws IOException {
        List<StashEntry> stashes = loadStashes(repo);
        int index = getStashIndex(stashRef);
        
        if (index >= 0 && index < stashes.size()) {
            return stashes.get(index);
        }
        return null;
    }
    
    private static int getStashIndex(String stashRef) {
        if (stashRef.startsWith("stash@{") && stashRef.endsWith("}")) {
            try {
                return Integer.parseInt(stashRef.substring(7, stashRef.length() - 1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
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
    
    public static class StashEntry {
        private String message;
        private List<IndexEntry> entries;
        private String branch;
        
        // Getters and setters for Jackson
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<IndexEntry> getEntries() { return entries; }
        public void setEntries(List<IndexEntry> entries) { this.entries = entries; }
        
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
    }
} 
