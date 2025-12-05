package com.zacurrya.httpserver.core;

import com.zacurrya.httpserver.core.io.ReadFileException;
import com.zacurrya.httpserver.core.io.WebRootNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {
    private File webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = new File(webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundException("Webroot provided doesn't exist or isn't a folder");
        }
    }

    private boolean checkIfEndsWithSlash(String relativePath) {
        return relativePath.endsWith("/");
    }

    private boolean checkIfProvidedRelativePathExists(String relativePath) {
        File file = new File(webRoot, relativePath);

        if (!file.exists()) {
            return false;
        }

        // Security check: ensure the file is within webRoot
        try {
            String canonicalFilePath = file.getCanonicalPath();
            String canonicalWebRootPath = webRoot.getCanonicalPath();
            return canonicalFilePath.startsWith(canonicalWebRootPath);
        } catch (IOException e) {
            // If we can't get canonical path, just check if file exists and is a file
            return file.exists() && file.isFile();
        }
    }

    String normalizePath(String relativePath) throws FileNotFoundException {
        // Remove leading slash if present (e.g., "/" becomes "", "/page.html" becomes
        // "page.html")
        if (relativePath != null && relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        // Handle empty string or paths ending with slash by defaulting to index.html
        if (relativePath == null || relativePath.isEmpty() || checkIfEndsWithSlash(relativePath)) {
            if (relativePath == null || relativePath.isEmpty()) {
                relativePath = "index.html";
            } else {
                relativePath += "index.html"; // by default serve index.html
            }
        }

        if (!checkIfProvidedRelativePathExists(relativePath)) {
            throw new FileNotFoundException("File not found: " + relativePath);
        }

        return relativePath;
    }

    // TODO For large files a new strategy might be necessary
    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        relativePath = normalizePath(relativePath);

        File file = new File(webRoot, relativePath);

        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if (mimeType == null) {
            return "application/octet-stream";
        }
        return mimeType;
    }

    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        relativePath = normalizePath(relativePath);

        File file = new File(webRoot, relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
        return fileBytes;
    }

}
