package com.example.minci;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME = "MINCI";
    public static final String KEY_NAME = "QUESTION";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private TextView question, noIndicator;
    private FloatingActionButton bookmarkBtn;
    private RecyclerView recyclerView;
    private Button nextBtn, potvrdiBtn;
    private int count = 0;
    private List<QuestionModel> listQuestions;
    private int position = 0;
    private int score = 0;
    private String category;
    private int setNo;
    private Dialog loadingDialog;

    private List<QuestionModel> bookmarksList;

    private AnswersAdapter answersAdapter;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;
    private List<AnswerModel> listAnswers;
    private List<AnswerModel> allAnswers;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmark_btn);

        listAnswers = new ArrayList<>();
        allAnswers = new ArrayList<>();
        recyclerView = findViewById(R.id.options_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        answersAdapter = new AnswersAdapter(listAnswers);
        recyclerView.setAdapter(answersAdapter);

        nextBtn = findViewById(R.id.next_btn);
        potvrdiBtn = findViewById(R.id.potvrdi_btn);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelMatch()){
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else{
                    bookmarksList.add(listQuestions.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });
        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 0);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        listQuestions = new ArrayList<>();

        loadingDialog.show();

        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    listQuestions.add(snapshot1.getValue(QuestionModel.class));
                    Log.i("info1", "" + listQuestions.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        });



        myRef.child("SETS").child(category).child("answers").addListenerForSingleValueEvent(new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            for(DataSnapshot testModel : snapshot.getChildren()){
                allAnswers.add(testModel.getValue(AnswerModel.class));
            }

            if(listQuestions.size() != 0 && allAnswers.size() != 0){

                for(int j = 0; j < allAnswers.size(); j++ ){
                    if(allAnswers.get(j).getIdQ() == listQuestions.get(position).getIdQ()){
                        listAnswers.add(new AnswerModel(allAnswers.get(j).getAnswer(),allAnswers.get(j).getCorrect(), allAnswers.get(j).getIdQ()));
                    }
                }

                answersAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
                playAnim(question, 0, listQuestions.get(position).getQuestion());

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        nextBtn.setEnabled(false);
                        nextBtn.setAlpha(0.7f);

                        enableOption(false);

                        potvrdiBtn.setEnabled(true);
                        potvrdiBtn.setAlpha(1f);

                        if(answersAdapter.isCorrectAnswer()){
                            score++;
                        }
                        position++;
                        if (position == listQuestions.size()) {
                            // score activity
                            Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                            scoreIntent.putExtra("score", score);
                            scoreIntent.putExtra("total", listQuestions.size());
                            startActivity(scoreIntent);
                            finish();
                            return;
                        }
                        listAnswers.clear();
                        for(int j = 0; j < allAnswers.size(); j++ ){
                            if(allAnswers.get(j).getIdQ() == listQuestions.get(position).getIdQ()){
                                listAnswers.add(new AnswerModel(allAnswers.get(j).getAnswer(),allAnswers.get(j).getCorrect(), allAnswers.get(j).getIdQ()));
                            }
                        }



                        count = 0;
                        playAnim(question, 0, listQuestions.get(position).getQuestion());
                        answersAdapter.notifyDataSetChanged();
                    }
                });

                potvrdiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        potvrdiBtn.setEnabled(false);
                        potvrdiBtn.setAlpha(0.7f);
                        enableOption(true);
                        nextBtn.setEnabled(true);
                        nextBtn.setAlpha(1f);
                    }
                });



            } else {
                finish();
                Toast.makeText(QuestionsActivity.this, "Nema pitanja!", Toast.LENGTH_SHORT );
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            answersAdapter.notifyDataSetChanged();

            loadingDialog.dismiss();
            finish();
        }
        });


 //       loadingDialog.show();
 /*       myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    list.add(snapshot1.getValue(QuestionModel.class));
                }
                if(list.size() > 0){

                    for(int i = 0; i < 4; i++){
                        recyclerView.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button)v);
                            }
                        });
                    }

                    playAnim(question, 0, list.get(position).getQuestion());

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;
                            if(position == list.size()){
                                // score activity
                                Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                scoreIntent.putExtra("score", score);
                                scoreIntent.putExtra("total", list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(question, 0, list.get(position).getQuestion());
                        }
                    });
                } else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "Nema pitanja!", Toast.LENGTH_SHORT );
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(QuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT );
                loadingDialog.dismiss();
                finish();
            }
        });
*/
        noIndicator.setText(position+1+"/"+listQuestions.size());


    }

    private void playAnim(View view, int value, final String data){

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(value == 0 && count < listAnswers.size()){
                    String option = "";
                    option = listAnswers.get(count).getAnswer();
                    playAnim(recyclerView.getChildAt(count), 0, option);
                    count++;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animation) {
                if(value == 0){
                    try {
                        ((TextView)view).setText(data);
                        noIndicator.setText(position+1+"/"+listQuestions.size());
                        if(modelMatch()){
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else{
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    }catch (ClassCastException ex){
//                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void checkAnswer(Button selectedOption){
//        enableOption(false);
//        nextBtn.setEnabled(true);
//        nextBtn.setAlpha(1);
//        if(selectedOption.getText().toString().equals(listQuestions.get(position).getCorrectANS())){
//            score++;
//            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
//        }else{
//            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
//            Button correctoption = (Button) recyclerView.findViewWithTag(listQuestions.get(position).getCorrectANS());
//            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableOption(boolean enable ){
        answersAdapter.setPotvrdi(enable);
        for(int i = 0; i < listAnswers.size(); i++) {
            answersAdapter.notifyItemChanged(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void getBookmarks(){
        String json = preferences.getString(KEY_NAME, "");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json, type);

        if(bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch(){
        boolean matched = false;
        int i = 0;
        for(QuestionModel model : bookmarksList){
            if(model.getQuestion().equals(listQuestions.get(position).getQuestion())
            && model.getSetNo() == listQuestions.get(position).getSetNo()){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }

        return matched;
    }

    private void storeBookmarks(){
        String json = gson.toJson(bookmarksList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }
}