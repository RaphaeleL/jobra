package com.cobra.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class RefStore {
    private Path cobraDir;

    public RefStore(Path cobraDir) {
        this.cobraDir = cobraDir;
    }

    public void createInitialRefs() throws IOException {
        // Create main branch
        createBranch("main", null);
        
        // Set HEAD to main
        setHead("refs/heads/main");
    }

    public void createBranch(String name, String commitHash) throws IOException {
        Path branchPath = cobraDir.resolve("refs/heads").resolve(name);
        Files.createDirectories(branchPath.getParent());
        
        if (commitHash != null) {
            Files.write(branchPath, commitHash.getBytes());
        } else {
            Files.write(branchPath, "".getBytes());
        }
    }

    public void deleteBranch(String name) throws IOException {
        Path branchPath = cobraDir.resolve("refs/heads").resolve(name);
        if (Files.exists(branchPath)) {
            Files.delete(branchPath);
        }
    }

    public String getBranchHead(String name) throws IOException {
        Path branchPath = cobraDir.resolve("refs/heads").resolve(name);
        if (Files.exists(branchPath)) {
            return new String(Files.readAllBytes(branchPath)).trim();
        }
        return null;
    }

    public void setBranchHead(String name, String commitHash) throws IOException {
        Path branchPath = cobraDir.resolve("refs/heads").resolve(name);
        Files.createDirectories(branchPath.getParent());
        Files.write(branchPath, commitHash.getBytes());
    }

    public String getHead() throws IOException {
        Path headPath = cobraDir.resolve("HEAD");
        if (Files.exists(headPath)) {
            String head = new String(Files.readAllBytes(headPath)).trim();
            if (head.startsWith("ref: ")) {
                String ref = head.substring(5);
                return getRef(ref);
            }
            return head;
        }
        return null;
    }

    public void setHead(String ref) throws IOException {
        Path headPath = cobraDir.resolve("HEAD");
        Files.write(headPath, ("ref: " + ref + "\n").getBytes());
    }

    public void setHeadCommit(String commitHash) throws IOException {
        Path headPath = cobraDir.resolve("HEAD");
        Files.write(headPath, commitHash.getBytes());
    }

    public String getRef(String ref) throws IOException {
        Path refPath = cobraDir.resolve(ref);
        if (Files.exists(refPath)) {
            return new String(Files.readAllBytes(refPath)).trim();
        }
        return null;
    }

    public List<String> listBranches() throws IOException {
        Path headsDir = cobraDir.resolve("refs/heads");
        if (!Files.exists(headsDir)) {
            return new ArrayList<>();
        }

        List<String> branches = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(headsDir)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    branches.add(path.getFileName().toString());
                }
            }
        }
        return branches;
    }

    public String getCurrentBranch() throws IOException {
        String head = getHead();
        if (head != null && head.startsWith("refs/heads/")) {
            return head.substring("refs/heads/".length());
        }
        return null;
    }
} 