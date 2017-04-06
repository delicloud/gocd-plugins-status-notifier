package com.tw.go.plugin.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtils {


    public static void doPostWithServerID(String url, String data, String serverId) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.addHeader("content-type", "application/json");
        request.addHeader("gocd-uuid", serverId);
        request.setEntity(new StringEntity(data));
        client.execute(request);
    }

    public static HttpResponse doGetWithGoCDAuth(String url, String username, String password) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        byte[] plainCredsBytes = (username + ":" + password).getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        request.addHeader("Authorization", "Basic " + base64Creds);
        return client.execute(request);

    }
}
