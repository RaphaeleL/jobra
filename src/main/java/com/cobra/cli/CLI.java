package com.cobra.cli;

import com.cobra.commands.*;

import java.io.IOException;

public class CLI {
    public static void run(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
            return;
        }
        
        String command = args[0];
        
        try {
            switch (command) {
                case "init":
                    handleInit(args);
                    break;
                case "add":
                    handleAdd(args);
                    break;
                case "commit":
                    handleCommit(args);
                    break;
                case "log":
                    handleLog(args);
                    break;
                case "status":
                    handleStatus(args);
                    break;
                case "branch":
                    handleBranch(args);
                    break;
                case "stash":
                    handleStash(args);
                    break;
                case "help":
                case "-h":
                case "--help":
                    printHelp();
                    break;
                case "-v":
                case "--version":
                    System.out.println("Jobra version 1.0.0");
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    printHelp();
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void handleInit(String[] args) throws IOException {
        String path = args.length > 1 ? args[1] : ".";
        InitCommand.run(path);
    }
    
    private static void handleAdd(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Error: file argument required");
            System.exit(1);
        }
        String file = args[1];
        AddCommand.run(file);
    }
    
    private static void handleCommit(String[] args) throws IOException {
        String message = null;
        
        // Look for -m flag
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("-m") && i + 1 < args.length) {
                message = args[i + 1];
                break;
            } else if (args[i].startsWith("--message=")) {
                message = args[i].substring("--message=".length());
                break;
            }
        }
        
        if (message == null) {
            System.err.println("Error: commit message required (-m or --message)");
            System.err.println("Example: cobra commit -m \"your message\"");
            System.exit(1);
        }
        
        CommitCommand.run(message);
    }
    
    private static void handleLog(String[] args) throws IOException {
        LogCommand.run();
    }
    
    private static void handleStatus(String[] args) throws IOException {
        StatusCommand.run();
    }
    
    private static void handleBranch(String[] args) throws IOException {
        if (args.length < 2) {
            BranchCommand.list();
            return;
        }
        
        String subCommand = args[1];
        switch (subCommand) {
            case "list":
            case "ls":
                BranchCommand.list();
                break;
            case "create":
                if (args.length < 3) {
                    System.err.println("Error: branch name required");
                    System.exit(1);
                }
                BranchCommand.create(args[2]);
                break;
            case "checkout":
                if (args.length < 3) {
                    System.err.println("Error: branch name required");
                    System.exit(1);
                }
                BranchCommand.checkout(args[2]);
                break;
            case "delete":
                if (args.length < 3) {
                    System.err.println("Error: branch name required");
                    System.exit(1);
                }
                BranchCommand.delete(args[2]);
                break;
            case "merge":
                if (args.length < 3) {
                    System.err.println("Error: branch name required");
                    System.exit(1);
                }
                BranchCommand.merge(args[2]);
                break;
            case "rebase":
                if (args.length < 3) {
                    System.err.println("Error: branch name required");
                    System.exit(1);
                }
                BranchCommand.rebase(args[2]);
                break;
            default:
                System.out.println("Unknown branch command: " + subCommand);
                System.out.println("Available commands: list, create, checkout, delete, merge, rebase");
                break;
        }
    }
    
    private static void handleStash(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("No stash subcommand was used");
            return;
        }
        
        String subCommand = args[1];
        switch (subCommand) {
            case "push":
                String message = null;
                for (int i = 2; i < args.length; i++) {
                    if (args[i].equals("-m") && i + 1 < args.length) {
                        message = args[i + 1];
                        break;
                    } else if (args[i].startsWith("--message=")) {
                        message = args[i].substring("--message=".length());
                        break;
                    }
                }
                StashCommand.push(message);
                break;
            case "list":
                StashCommand.list();
                break;
            case "show":
                String stash = args.length > 2 ? args[2] : "stash@{0}";
                StashCommand.show(stash);
                break;
            case "apply":
                stash = args.length > 2 ? args[2] : "stash@{0}";
                StashCommand.apply(stash);
                break;
            case "drop":
                stash = args.length > 2 ? args[2] : "stash@{0}";
                StashCommand.drop(stash);
                break;
            default:
                System.out.println("Unknown stash command: " + subCommand);
                System.out.println("Available commands: push, list, show, apply, drop");
                break;
        }
    }
    
    private static void printHelp() {
        System.out.println("Jobra - A Git-like version control system");
        System.out.println();
        System.out.println("Usage: java -jar cobra.jar [COMMAND]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  init [path]           Initialize a new repository");
        System.out.println("  add <file>            Add file contents to the index");
        System.out.println("  commit -m <message>   Record changes to the repository");
        System.out.println("  log                   Show commit logs");
        System.out.println("  status                Show the working tree status");
        System.out.println("  branch                List, create, or delete branches");
        System.out.println("  stash                 Stash changes in a dirty working directory");
        System.out.println("  help                  Print this message");
        System.out.println("  -v, --version         Print version");
        System.out.println();
        System.out.println("Branch subcommands:");
        System.out.println("  list, ls              List all branches");
        System.out.println("  create <name>         Create a new branch");
        System.out.println("  checkout <name>       Switch to a branch");
        System.out.println("  delete <name>         Delete a branch");
        System.out.println("  merge <name>          Merge a branch into the current branch");
        System.out.println("  rebase <branch>       Reapply commits on top of another base tip");
        System.out.println();
        System.out.println("Stash subcommands:");
        System.out.println("  push [-m <message>]   Save your local modifications to a new stash");
        System.out.println("  list                  List all stashes");
        System.out.println("  show [stash]          Show the contents of a stash");
        System.out.println("  apply [stash]         Apply a stash to the working directory");
        System.out.println("  drop [stash]          Remove a stash from the stash list");
    }
} 
