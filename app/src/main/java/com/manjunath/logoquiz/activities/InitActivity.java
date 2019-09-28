package com.manjunath.logoquiz.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.manjunath.logoquiz.R;

public class InitActivity extends AppCompatActivity implements View.OnClickListener {

    Button startGameButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        initializeViews();
    }

    private void initializeViews() {
        startGameButton = findViewById(R.id.startGame);
        startGameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.startGame: startGameActivity();
        }
    }

    private void startGameActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        // Add extras if required for changing difficulty or for any other requirements
        startActivity(intent);
    }
}
