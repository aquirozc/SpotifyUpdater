package com.aquirozc.updater.helper;

import android.util.Base64;

import com.aquirozc.updater.data.ApiResponse;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadHelper{

    private static final String DEFAULT_USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0";
    private static final Gson gson = new Gson();

    public static long getFileSize(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("Referer","apkmody.com");
        connection.connect();

        return connection.getHeaderFieldLong("Content-Length",-1);
    }

    public static String getLatestURLFromAndroforever() throws IOException {
        return Jsoup.connect("https://androforever.com/spotify-premium-apk/download/")
                .get()
                .select("#list-downloadlinks li a")
                .attr("href")
                .replace(" ","%20");
    }

    public static String getLatestURLFromApkmody() throws IOException{

        String target = Jsoup.connect("https://apkmody.com/apps/spotify-2/download/0")
                            .header("User-Agent",DEFAULT_USERAGENT)
                            .get()
                            .select("#d-button")
                            .attr("data-href")
                            .replace(" ","%20");

        String hostname= Jsoup.connect("https://apkmody.com/api/download")
                            .ignoreContentType(true)
                            .method(Connection.Method.GET)
                            .execute().body();

        hostname = gson.fromJson(hostname, ApiResponse.class).getHostname();

        target = new String(Base64.decode(target,Base64.DEFAULT));

        if(target.contains("/v2/")){
            target = "https://" +  hostname + new URL(target).getPath();
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
        connection.setRequestProperty("Referer",hostname);
        connection.setRequestMethod("HEAD");
        connection.setInstanceFollowRedirects(false);
        connection.connect();

        return connection.getHeaderField("Location");
    }
}
