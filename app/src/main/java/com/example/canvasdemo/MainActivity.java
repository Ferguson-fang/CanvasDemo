package com.example.canvasdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.example.canvasdemo.bean.result;
import com.example.canvasdemo.netrequest.GetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button single_request;
    private MHandler mHandler;
    private List<result>list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        single_request = findViewById(R.id.single_request);


        single_request.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.single_request:
                GetRequest.getInstance().sendGetNetRequest("https:q//wanandroid.com/wxarticle/chapters/json",mHandler);
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("list",(Serializable)list);
                startActivity(intent);
                break;
        }
    }

    private class MHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String responseData = msg.obj.toString();

            try{
                JSONObject jsonObject = new JSONObject(responseData);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObjecti = jsonArray.getJSONObject(i);
                    String name = jsonObjecti.getString("name");
                    list.add(new result(name));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}