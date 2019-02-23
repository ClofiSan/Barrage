package com.example.gats.barrage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    private Button inputButton;
    private EditText inputEdit;
    private String inputString;
    private String TAG = "InputActivity";
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
            //提取editext中的String
            //利用intent返回
            Intent intent = new Intent();
            inputString = inputEdit.getText().toString();
            intent.putExtra("input_word",inputString);
            Log.d(TAG, "onClick: "+inputString);
            this.finish();
        }
    }
}
