package com.example.canvasdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.canvasdemo.bean.result;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private TextView tv;
    private List<result>list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        init();
    }

    private void init() {
        tv = findViewById(R.id.tv);

        list =(List<result>) getIntent().getSerializableExtra("list");
        tv.setText(list.toString());
    }
}