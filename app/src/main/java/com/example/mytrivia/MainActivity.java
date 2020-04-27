package com.example.mytrivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytrivia.data.AnswerListAsyncResponse;
import com.example.mytrivia.data.QuestionBank;
import com.example.mytrivia.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String BEST_SCORE = "best_score";
    private static int CORRECT_ANSWER_PRICE = 100;

    private TextView counterText, questionText, bestscoreResultText, currentScoreText;
    private Button trueButton, falseButton;
    private ImageButton nextButton, prevButton;

    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    private int currentScore = 0;
    private int sharedBestScore = 0;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        sharedBestScore = sharedPreferences.getInt(BEST_SCORE, currentScore);

        bestscoreResultText = findViewById(R.id.bestscore_result);
        bestscoreResultText.setText(String.valueOf(sharedBestScore));


        counterText = findViewById(R.id.counter_text);
        questionText = findViewById(R.id.question_text);

        currentScoreText = findViewById(R.id.current_score_text);

        trueButton = findViewById(R.id.button_true);
        falseButton = findViewById(R.id.button_false);

        nextButton = findViewById(R.id.button_next);
        prevButton = findViewById(R.id.prev_button);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionText.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                counterText.setText((currentQuestionIndex + 1) + " out of " + questionArrayList.size());
            }
        });
    }

    @Override
    protected void onPause() {
        if (currentScore > sharedBestScore) {
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
            sharedEditor.putInt(BEST_SCORE, currentScore);
            sharedEditor.apply();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_true:
                checkAnswer(true);
                updateCurrentScoreText();
                updateQuestionForCard();
                break;
            case R.id.button_false:
                checkAnswer(false);
                updateCurrentScoreText();
                updateQuestionForCard();
                break;
            case R.id.button_next:
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                }
                updateQuestionForCard();
                break;
            case R.id.prev_button:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                }
                updateQuestionForCard();
                break;
        }
    }

    private void checkAnswer(boolean userAnswers) {
        int toestMessageId = 0;
        boolean realAnswer = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (realAnswer == userAnswers) {
            toestMessageId = R.string.correct_text;
            fadeCardView();
            currentScore += CORRECT_ANSWER_PRICE;
        } else {
            toestMessageId = R.string.wrong_text;
            shakeCardView();
            if (currentScore > 0) {
                currentScore -= CORRECT_ANSWER_PRICE;
            }
        }
        Toast.makeText(MainActivity.this, toestMessageId, Toast.LENGTH_SHORT).show();

    }

    private void updateCurrentScoreText() {
        currentScoreText.setText("Current score: " + currentScore);
    }

    private void updateQuestionForCard() {
        questionText.setText(questionList.get(currentQuestionIndex).getAnswer());
        counterText.setText((currentQuestionIndex + 1) + " out of " + questionList.size());
    }

    private void shakeCardView() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.cardview_wrong_background));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                }
                updateQuestionForCard();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeCardView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(150);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.cardview_correct_background));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                }
                updateQuestionForCard();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
