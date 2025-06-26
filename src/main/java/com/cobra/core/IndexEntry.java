package com.cobra.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexEntry {
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("hash")
    private String hash;
    
    @JsonProperty("mode")
    private String mode;
    
    @JsonProperty("size")
    private long size;

    public IndexEntry() {
        // Default constructor for Jackson
    }

    public IndexEntry(String path, String hash, String mode, long size) {
        this.path = path;
        this.hash = hash;
        this.mode = mode;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "IndexEntry{" +
                "path='" + path + '\'' +
                ", hash='" + hash + '\'' +
                ", mode='" + mode + '\'' +
                ", size=" + size +
                '}';
    }
} 