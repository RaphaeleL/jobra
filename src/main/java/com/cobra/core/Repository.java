package com.cobra.core;

import com.cobra.utils.HashUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Repository {
    private Path rootPath;
    private Path cobraDir;
    private Index index;
    private RefStore refStore;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Repository(Path rootPath) {
        this.rootPath = rootPath;
        this.cobraDir = rootPath.resolve(".cobra");
        this.index = new Index();
        this.refStore = new RefStore(cobraDir);
    }

    public static Repository init(String path) throws IOException {
        Path rootPath = Paths.get(path);
        Path cobraDir = rootPath.resolve(".cobra");
        
        // Create .cobra directory and its subdirectories
        Files.createDirectories(cobraDir);
        Files.createDirectories(cobraDir.resolve("objects"));
        Files.createDirectories(cobraDir.resolve("refs/heads"));

        // Create HEAD file pointing to refs/heads/main
        Files.write(
            cobraDir.resolve("HEAD"),
            "ref: refs/heads/main\n".getBytes()
        );

        Repository repo = new Repository(rootPath);
        
        // Initialize refs
        repo.refStore.createInitialRefs();
        
        // Save empty index
        repo.saveIndex();
        
        return repo;
    }

    public static boolean exists(String path) {
        Path cobraDir = Paths.get(path).resolve(".cobra");
        return Files.exists(cobraDir) && Files.isDirectory(cobraDir);
    }

    public static Repository open(String path) throws IOException {
        Path rootPath = Paths.get(path);
        Path cobraDir = rootPath.resolve(".cobra");

        if (!Files.isDirectory(cobraDir)) {
            throw new IOException("Not a cobra repository (or any of the parent directories)");
        }

        Repository repo = new Repository(rootPath);
        
        // Try to load existing index
        repo.index = Index.load(repo);
        
        return repo;
    }

    public void addToIndex(IndexEntry entry) throws IOException {
        index.addEntry(entry);
        saveIndex();
    }

    public void saveIndex() throws IOException {
        Path indexPath = cobraDir.resolve("index");
        index.writeToFile(indexPath);
    }

    public String writeObject(GitObject object) throws IOException {
        // Hash the full content (header + content)
        String fullContent = object.getFullContent();
        String hash = HashUtils.sha256(fullContent);
        Path objectPath = cobraDir.resolve("objects").resolve(hash.substring(0, 2));
        Files.createDirectories(objectPath);
        
        Path objectFile = objectPath.resolve(hash.substring(2));
        Files.write(objectFile, fullContent.getBytes());
        
        return hash;
    }

    public GitObject readObject(String hash) throws IOException {
        Path objectPath = cobraDir.resolve("objects").resolve(hash.substring(0, 2)).resolve(hash.substring(2));
        if (!Files.exists(objectPath)) {
            throw new IOException("Object not found: " + hash);
        }
        
        String content = new String(Files.readAllBytes(objectPath));
        
        // Try to parse as Git object format first (new format)
        try {
            return GitObject.fromContent(content);
        } catch (IllegalArgumentException e) {
            // If that fails, treat as raw content (old format)
            // We need to determine the object type from the content
            String type = determineObjectType(content);
            return GitObject.fromRawContent(type, content);
        }
    }
    
    private String determineObjectType(String content) {
        // Simple heuristics to determine object type
        if (content.startsWith("tree ")) {
            return "commit";
        } else if (content.contains("\0")) {
            return "tree";
        } else {
            return "blob";
        }
    }

    public String createCommit(String message, String treeHash, String parentHash) throws IOException {
        StringBuilder commitContent = new StringBuilder();
        commitContent.append("tree ").append(treeHash).append("\n");
        
        if (parentHash != null && !parentHash.isEmpty()) {
            commitContent.append("parent ").append(parentHash).append("\n");
        }
        
        commitContent.append("author Jobra <cobra@example.com> ")
                    .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .append("\n");
        commitContent.append("committer Jobra <cobra@example.com> ")
                    .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .append("\n\n");
        commitContent.append(message).append("\n");

        GitObject commit = new GitObject("commit", commitContent.toString());
        return writeObject(commit);
    }

    public String createTree(Tree tree) throws IOException {
        GitObject treeObj = new GitObject("tree", tree.toContent());
        return writeObject(treeObj);
    }

    public String createBlob(Path filePath) throws IOException {
        String content = new String(Files.readAllBytes(filePath));
        GitObject blob = new GitObject("blob", content);
        return writeObject(blob);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public Path getJobraDir() {
        return cobraDir;
    }

    public Index getIndex() {
        return index;
    }

    public RefStore getRefStore() {
        return refStore;
    }
} 
