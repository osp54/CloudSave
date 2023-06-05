package com.ospx.cloudsavemod;

import arc.Core;
import arc.util.serialization.Base64Coder;
import org.eclipse.jetty.client.*;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MultiPart;
import org.eclipse.jetty.util.Fields;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RestClient {
    public static String API_SERVER_URL = "http://localhost:3000";

    private final HttpClient http = new HttpClient();

    public String credentials;


    public RestClient(String credentials) throws Exception {
        this.credentials = credentials;

        http.getProtocolHandlers().remove(WWWAuthenticationProtocolHandler.NAME);
        http.start();
    }

    public ContentResponse register(String email, String password) throws ExecutionException, InterruptedException, TimeoutException {
        Fields fields = new Fields();

        fields.add("email", email);
        fields.add("password", password);

        return http.POST(API_SERVER_URL + "/register")
                .body(new FormRequestContent(fields))
                .send();
    }

    public ContentResponse getSavesList() throws ExecutionException, InterruptedException, TimeoutException {
        return http.newRequest(API_SERVER_URL + "/saves/list")
                .method(HttpMethod.GET)
                .headers(this::addAuthHeader)
                .send();
    }

    public ContentResponse uploadSave(Path path) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        MultiPartRequestContent multiPart = new MultiPartRequestContent();
        multiPart.addPart(new MultiPart.PathPart("save", "save.zip", HttpFields.EMPTY, path));
        multiPart.close();

        return http.POST(API_SERVER_URL + "/saves/upload/")
                .body(multiPart)
                .headers(this::addAuthHeader)
                .send();
    }

    public ContentResponse downloadSave(String id) throws ExecutionException, InterruptedException, TimeoutException {
        return http.newRequest(API_SERVER_URL + "/saves/" + id + "/download")
                .method(HttpMethod.GET)
                .headers(this::addAuthHeader)
                .send();
    }

    private void addAuthHeader(HttpFields.Mutable h) {
        h.add("Authorization", generateBasicAuth());
    }

    private String generateBasicAuth() {
        return "Basic " + credentials;
    }

    public void updateCredentials(String credentials) {
        this.credentials = credentials;
    }
}
