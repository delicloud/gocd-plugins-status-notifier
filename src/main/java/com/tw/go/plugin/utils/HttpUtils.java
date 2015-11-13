package com.tw.go.plugin.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtils {

    public static void doPost(String url, String data) throws Exception {

        CloseableHttpClient client = null;

        try {
            client = HttpClients.createDefault();

            HttpPost request = new HttpPost(url);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(data));
            HttpResponse httpResponse = client.execute(request);

        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

    }

}
