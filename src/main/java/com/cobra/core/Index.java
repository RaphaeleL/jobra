package com.cobra.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Index {
    private List<IndexEntry> entries;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Index() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(IndexEntry entry) {
        // Remove existing entry with same path if exists
        entries.removeIf(e -> e.getPath().equals(entry.getPath()));
        entries.add(entry);
    }

    public void removeEntry(String path) {
        entries.removeIf(e -> e.getPath().equals(path));
    }

    public List<IndexEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public IndexEntry getEntry(String path) {
        return entries.stream()
                .filter(e -> e.getPath().equals(path))
                .findFirst()
                .orElse(null);
    }

    public boolean hasEntry(String path) {
        return entries.stream().anyMatch(e -> e.getPath().equals(path));
    }

    public void writeToFile(Path indexPath) throws IOException {
        String json = objectMapper.writeValueAsString(entries);
        Files.write(indexPath, json.getBytes());
    }

    public static Index load(Repository repo) throws IOException {
        Path indexPath = repo.getJobraDir().resolve("index");
        if (!Files.exists(indexPath)) {
            return new Index();
        }

        String json = new String(Files.readAllBytes(indexPath));
        List<IndexEntry> entries = objectMapper.readValue(json, new TypeReference<List<IndexEntry>>() {});
        Index index = new Index();
        index.entries = entries;
        return index;
    }

    public void clear() {
        entries.clear();
    }
} 
