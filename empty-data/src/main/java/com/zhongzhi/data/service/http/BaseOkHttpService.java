package com.zhongzhi.data.service.http;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public abstract class BaseOkHttpService {

    /***
     * ok http client
     */
    protected OkHttpClient client;

    /**
     * init OKHttpClient object
     */
    @PostConstruct
    public void initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60000, TimeUnit.MILLISECONDS).writeTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS).followRedirects(true)
                .followSslRedirects(true)
                .connectionPool(new ConnectionPool(1000, 10 * 60 * 1000, TimeUnit.MILLISECONDS))
                .pingInterval(10 * 1000, TimeUnit.MILLISECONDS).retryOnConnectionFailure(false);
        this.client = builder.build();
        this.client.dispatcher().setMaxRequests(1000);
        this.client.dispatcher().setMaxRequestsPerHost(1000);
    }

    @Override
    public void finalize() {
        this.client.connectionPool().evictAll();
    }
}
