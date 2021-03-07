package com.example.minci;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private List<TestModel> testModelList;

    public TestAdapter(List<TestModel> testModelList) {
        this.testModelList = testModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(testModelList.get(position).getName(), testModelList.get(position).getSets());
    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView test_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            test_view = itemView.findViewById(R.id.test_view);
        }

        private void setData(final String test1, int sets){

            test_view.setText(test1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(itemView.getContext(), SetActivity.class);
                    setIntent.putExtra("title", test1);
                    setIntent.putExtra("sets",sets);
                    itemView.getContext().startActivity(setIntent);
                }
            });
        }
    }
}
