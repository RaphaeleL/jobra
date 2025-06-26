package com.cobra.commands;

import com.cobra.core.Repository;
import java.io.IOException;

public class InitCommand {
    
    public static void run(String path) throws IOException {
        Repository.init(path);
        System.out.println("Initialized empty Jobra repository in " + path);
    }
} 
