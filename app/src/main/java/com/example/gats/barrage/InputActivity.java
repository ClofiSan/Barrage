package com.example.gats.barrage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    private Button inputButton;
    private EditText inputEdit;
    private String inputString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        inputButton = (Button) findViewById(R.id.inputButton);
        inputEdit = (EditText) findViewById(R.id.inputEdit);

        inputButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==inputButton){
            //设置一个intent传递相关的信息
        }
    }
}
