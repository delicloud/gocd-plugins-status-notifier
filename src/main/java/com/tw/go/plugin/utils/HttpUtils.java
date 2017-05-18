package com.tw.go.plugin.utils;

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
}
