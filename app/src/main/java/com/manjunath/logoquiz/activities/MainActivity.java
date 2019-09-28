package com.manjunath.logoquiz.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manjunath.logoquiz.R;
import com.manjunath.logoquiz.contracts.PresenterContract;
import com.manjunath.logoquiz.presenters.Presenter;
import com.manjunath.logoquiz.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PresenterContract.view, View.OnClickListener {

    private static final int NEGATIVE_POINTS = -2;
    private static final int MAX_TIME_MILI_SEC = 10;
    List<String> inputText;
    List<String> inputLogoUrl;
    Presenter presenter;
    TextView timerTextView;
    ImageView logoImageView;
    Button pauseGameButton;
    Button nextButton;
    EditText answerTextView;
    boolean isGamePaused = false;
    private CountDownTimer countDownTimer;
    private int currentSource = 0;
    private int finalScore  = 0;
    ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        //getIntentData();
        presenter = new Presenter(this);
        presenter.loadJSONValue();
    }

    private void initializeViews() {
        answerTextView = findViewById(R.id.answer);
        timerTextView = findViewById(R.id.timer);
        logoImageView = findViewById(R.id.logo);
        pauseGameButton = findViewById(R.id.pauseGame);
        pauseGameButton.setOnClickListener(this);
        nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(this);
        mainLayout = findViewById(R.id.mainLayout);
    }

    @Override
    public void onInputJSONReceived(String input) {
        parseInput(input);
        startGame();
    }

    @Override
    public void onImageFetched(Bitmap imageBitmap) {
        logoImageView.setImageBitmap(imageBitmap);
        answerTextView.setText("");
        jumbleAnswer();
        startTimer();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.next:
                verifyAnswerAndAddScore();
                break;
            case R.id.pauseGame:
                handleTimerAndText();
                break;
        }
    }

    private void parseInput(String input) {
        inputText = new ArrayList<>();
        inputLogoUrl = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(input);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    inputText.add(jsonObject.getString("name"));
                    inputLogoUrl.add(jsonObject.getString("imgUrl"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        presenter.fetchImageForUrl(inputLogoUrl.get(currentSource));
    }

    private void continueGame() {
        if (currentSource < inputLogoUrl.size()) {
            currentSource++;
            startGame();
        } else {
            endGame();
        }
    }

    private void jumbleAnswer() {
        char[] array = inputText.get(currentSource).toCharArray();
        Arrays.sort(array);
        String requiredAnswer = new String(array);
        for (int i=0; i< requiredAnswer.length();i++) {
            Button button = new Button(this);
            button.setText(requiredAnswer.charAt(i));
            button.setId(i);
            mainLayout.addView(button);
        }
        // Handle UI correctly
    }

    private void verifyAnswerAndAddScore() {
        String answer = answerTextView.getText().toString();
        if (TextUtils.isEmpty(answer)) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.emptyAnswer),Toast.LENGTH_SHORT).show();
        }
        if (answer.equals(inputText.get(currentSource))) {
            String timerSec = timerTextView.getText().toString();
            finalScore += MAX_TIME_MILI_SEC - Integer.valueOf(timerSec);
        } else {
            finalScore += NEGATIVE_POINTS;
        }
        continueGame();
    }

    private void endGame() {
        showResponseDialog();
    }

    private void showResponseDialog() {
       new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.result))
                .setMessage(getResources().getString(R.string.finalScore) + finalScore)
                .setPositiveButton(getResources().getString(R.string.finish), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       startInitActivity();
                    }
                })
                .show();
    }

    private void handleTimerAndText() {
        if (isGamePaused) {
            startTimer();
            isGamePaused = false;
            pauseGameButton.setText(getResources().getString(R.string.pause));
        } else {
            stopTimer();
            isGamePaused = true;
            pauseGameButton.setText(getResources().getString(R.string.resume));
        }
    }

    private void stopTimer() {
        countDownTimer.cancel();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(Constants.MAX_TIME, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished));
            }

            public void onFinish() {
                verifyAnswerAndAddScore();
            }
        }.start();
    }

    private void startInitActivity() {
        Intent intent = new Intent(getApplicationContext(),InitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Add extras if required
        startActivity(intent);
    }
}
