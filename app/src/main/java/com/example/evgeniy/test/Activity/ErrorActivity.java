package com.example.evgeniy.test.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.evgeniy.test.Objects.Data;
import com.example.evgeniy.test.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvError;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error);
        Intent intent = getIntent();
        tvError = findViewById(R.id.tvError);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        Data input = intent.getParcelableExtra("err");
        tvError.setText(input.err);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
