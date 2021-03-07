package com.example.minci;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.Viewholder> {

    private List<AnswerModel> list;
    private boolean[] clicked;
    private boolean potvrdi;
    private boolean correctAnswer;

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isPotvrdi() {
        return potvrdi;
    }

    public void setPotvrdi(boolean potvrdi) {
        this.potvrdi = potvrdi;
    }

    public AnswersAdapter(List<AnswerModel> list) {
        this.list = list;
        clicked = new boolean[10]; // max 10 odgovora
        correctAnswer = true;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);

        return new AnswersAdapter.Viewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setData(list.get(position).getAnswer(), position);
        if(potvrdi){
            if(clicked[position] && list.get(position).getCorrect() == 1){
                holder.answerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                Log.i("info2", "tacno1");
            } else if(!clicked[position] && list.get(position).getCorrect() == 1){
                holder.answerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4B62DD")));
                Log.i("info2", "nije tacno kliknuto");
                correctAnswer = false;
            }else if(clicked[position] && list.get(position).getCorrect() == 0){

                holder.answerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                Log.i("info2", "pogresan klik");
                correctAnswer = false;
            }else {
                Log.i("info2", "ostalo");
                holder.answerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            }
            holder.answerBtn.setEnabled(false);
        }else {
            for(int i = 0; i < clicked.length; i++){
                clicked[i] = false;
            }
            correctAnswer = true;
            holder.answerBtn.setEnabled(true);
            holder.answerBtn.setAlpha(0.5f);
            holder.answerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void enableOption(){

    }

    class Viewholder extends RecyclerView.ViewHolder{

        private Button answerBtn;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            answerBtn = itemView.findViewById(R.id.answer_btn);
        }

        private void setData(String answer, int position){

            answerBtn.setText(answer);
            answerBtn.setAlpha(0.5f);
            answerBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    if(clicked[position]){
                        answerBtn.setAlpha(0.5f);
                    }else{
                        answerBtn.setAlpha(1f);
                    }
                    clicked[position] = !clicked[position];

                }
            });
        }

        public void enableOption(boolean enable){
            answerBtn.setEnabled(enable);
            if(enable){
                answerBtn.setAlpha(0.5f);
            }
        }
    }
}
