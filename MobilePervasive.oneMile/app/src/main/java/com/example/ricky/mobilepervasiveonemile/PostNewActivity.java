package com.example.ricky.mobilepervasiveonemile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class PostNewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_input);
        //Button send = (Button) findViewById(R.id.SendButton);
        FloatingActionButton send = (FloatingActionButton) findViewById(R.id.SendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.NewPostEditView);

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                intent.putExtra("input",input.getText().toString());
                System.out.println(input.getText().toString());
                startActivity(intent);
            }
        });
    }

}