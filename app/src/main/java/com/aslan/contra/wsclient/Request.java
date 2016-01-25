package com.aslan.contra.wsclient;

import org.springframework.http.HttpMethod;

/**
 * A generic request used by ServiceConnector.
 *
 * @author gobinath
 * @see ServiceConnector
 */
public class Request<T> {
    private static final String[] EMPTY_ARRAY = new String[0];
    /**
     * Entity to be passed.
     */
    private T entity;

    /**
     * URL of the endpoint.
     */
    private String url;

    /**
     * Required HTTP method.
     */
    private HttpMethod httpMethod;

    /**
     * URL parameters. (If there are any).
     */
    private String[] urlVariables = EMPTY_ARRAY;

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String[] getUrlVariables() {
        return urlVariables;
    }

    public void setUrlVariables(String... urlVariables) {
        this.urlVariables = urlVariables;
    }
}