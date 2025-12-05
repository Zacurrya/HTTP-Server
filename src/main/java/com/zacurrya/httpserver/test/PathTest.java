package com.zacurrya.httpserver.test;

import java.io.File;

public class PathTest {
    public static void main(String[] args) {
        String webRootPath = "Webroot";
        File webRoot = new File(webRootPath);

        System.out.println("WebRoot path: " + webRootPath);
        System.out.println("WebRoot absolute path: " + webRoot.getAbsolutePath());
        System.out.println("WebRoot exists: " + webRoot.exists());
        System.out.println("WebRoot is directory: " + webRoot.isDirectory());

        String relativePath = "index.html";
        File file = new File(webRoot, relativePath);

        System.out.println("\nFile path: " + relativePath);
        System.out.println("File absolute path: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());
        System.out.println("File is file: " + file.isFile());
    }
}
