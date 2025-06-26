package com.cobra.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitObject {
    private String type;
    private String content;
    private static final Pattern HEADER_PATTERN = Pattern.compile("^([a-z]+)\\s+(\\d+)\\x00(.+)$", Pattern.DOTALL);

    public GitObject(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getFullContent() {
        return type + " " + content.length() + "\0" + content;
    }

    public static GitObject fromContent(String fullContent) {
        Matcher matcher = HEADER_PATTERN.matcher(fullContent);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String content = matcher.group(3);
            return new GitObject(type, content);
        }
        throw new IllegalArgumentException("Invalid object format");
    }

    public static GitObject fromRawContent(String type, String content) {
        return new GitObject(type, content);
    }
} 