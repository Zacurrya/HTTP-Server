package com.zacurrya;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion; // literal from the request
    private HttpVersion bestCompatibleVersion;
    private HashMap<String, String> headers = new HashMap<>();

    HttpRequest() {}

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return this.bestCompatibleVersion;
    }

    public String getOriginalHttpVersion() {
        return this.originalHttpVersion;
    }
    void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().equals(methodName)) {
                this.method = method;
                return;
            }
        }
        throw new HttpParsingException(
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeader(String headerName) {
        return headers.get(headerName.toLowerCase());
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public void setHttpVersion(String httpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = httpVersion;
        this.bestCompatibleVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestCompatibleVersion == null) { // No compatible version found
            throw new HttpParsingException(
                    HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED
            );
        }
    }

    void addHeader(String headerName, String headerField) {
        headers.put(headerName.toLowerCase(), headerField);
    }

}
