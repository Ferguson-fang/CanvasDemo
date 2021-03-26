package com.example.canvasdemo.netrequest;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest {
    private volatile static GetRequest instance;
    private GetRequest(){}
    public static GetRequest getInstance(){
        if(instance == null){
            synchronized (GetRequest.class){
                if(instance == null){
                    instance = new GetRequest();
                }
            }
        }
        return instance;
    }

    public void sendGetNetRequest(String mUrl, Handler handler) {
        new Thread(() -> {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.connect();
                InputStream in = connection.getInputStream();
                String responseData = StreamToString(in);
                Message message = new Message();
                message.obj = responseData;
                handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public String StreamToString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        String oneline;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((oneline = reader.readLine()) != null) {
                sb.append(oneline).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
