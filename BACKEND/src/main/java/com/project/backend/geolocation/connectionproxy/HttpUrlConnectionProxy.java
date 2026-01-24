package com.project.backend.geolocation.connectionproxy;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.project.backend.geolocation.utils.Constants.MAX_CACHE_SIZE;

public class HttpUrlConnectionProxy extends HttpURLConnection {
    HttpURLConnection realConnection;
    private static final Map<String, String> cache = new LinkedHashMap<String,String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    private String cachedResponse;
    public HttpUrlConnectionProxy(URL u) throws IOException {
        super(u);
        this.realConnection = (HttpURLConnection) url.openConnection();
    }

    @Override
    public void disconnect() {
        realConnection.disconnect();
    }

    @Override
    public boolean usingProxy() {
        return realConnection.usingProxy();
    }
    @Override
    public InputStream getInputStream() throws IOException {
        //System.out.println("Fetching URL: " + url);
        synchronized (cache) {
            cachedResponse = cache.get(url.toString());
        }

        if (cachedResponse != null) {
            //System.out.println("Cache hit for URL: " + url);
            return new ByteArrayInputStream(cachedResponse.getBytes());
        }

        InputStream is = realConnection.getInputStream();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        cachedResponse = sb.toString();

        synchronized (cache) {
            cache.put(url.toString(), cachedResponse);
        }

        return new ByteArrayInputStream(cachedResponse.getBytes());
    }
    @Override
    public void connect() throws IOException {
        synchronized (cache) {
            cachedResponse = cache.get(url.toString());
        }
        if (cachedResponse == null) {
            realConnection.setRequestMethod(getRequestMethod());
            realConnection.setRequestProperty("User-Agent", "Java App");
            realConnection.connect();
        }
    }
}
