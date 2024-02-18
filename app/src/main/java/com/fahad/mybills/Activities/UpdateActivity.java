package com.fahad.mybills.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fahad.mybills.R;

public class UpdateActivity extends AppCompatActivity {

    Button updateBtn, skipBtn;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateBtn = findViewById(R.id.updateBtn);
        skipBtn = findViewById(R.id.skipBtn);

        message = findViewById(R.id.message);


        Intent intent = getIntent();
        String text = intent.getStringExtra("message");
        String link = intent.getStringExtra("link");
        boolean skipable = intent.getBooleanExtra("skipable", false);

        if (text.isEmpty()) {
            text = "A new update is available";
        }

        message.setText(text);
        if (!skipable) {
            skipBtn.setVisibility(View.GONE);
        }
        else {
            skipBtn.setOnClickListener(v->{
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("check", false);
                startActivity(mainIntent);
                finish();
            });
        }

        updateBtn.setOnClickListener(v-> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        });
    }
}