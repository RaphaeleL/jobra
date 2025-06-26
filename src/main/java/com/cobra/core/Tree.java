package com.cobra.core;

import java.util.*;

public class Tree {
    private List<TreeEntry> entries;

    public Tree() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(TreeEntry entry) {
        entries.add(entry);
    }

    public List<TreeEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public String toContent() {
        StringBuilder content = new StringBuilder();
        for (TreeEntry entry : entries) {
            content.append(entry.getMode()).append(" ")
                   .append(entry.getName()).append("\0")
                   .append(entry.getHash());
        }
        return content.toString();
    }

    public static Tree fromContent(String content) {
        Tree tree = new Tree();
        // Parse tree content (simplified implementation)
        // In a real implementation, you'd need to parse the binary format
        return tree;
    }

    public static class TreeEntry {
        private String mode;
        private String name;
        private String hash;

        public TreeEntry(String mode, String name, String hash) {
            this.mode = mode;
            this.name = name;
            this.hash = hash;
        }

        public String getMode() {
            return mode;
        }

        public String getName() {
            return name;
        }

        public String getHash() {
            return hash;
        }
    }
} 