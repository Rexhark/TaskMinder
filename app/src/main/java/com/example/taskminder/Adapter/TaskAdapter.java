package com.example.taskminder.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskminder.AddTask;
import com.example.taskminder.DatabaseHelper;
import com.example.taskminder.MainActivity;
import com.example.taskminder.Model.Task;
import com.example.taskminder.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private List<Task> tasks;
    private final MainActivity activity;
    private final DatabaseHelper db;

    public TaskAdapter(DatabaseHelper db, MainActivity activity) {
        this.activity = activity;
        this.db = db;
        tasks = db.getAllTasks();
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.getStatus() == 1);
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                db.updateStatus(task.getId(), 1);
                holder.card.setAlpha(0.5f);
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                db.updateStatus(task.getId(), 0);
                holder.card.setAlpha(1);
                holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.description.setPaintFlags(holder.description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        holder.card.setOnClickListener(view -> editTask(position));

        if (holder.checkBox.isChecked()) {
            holder.card.setAlpha(0.5f);
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.card.setAlpha(1);
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.description.setPaintFlags(holder.description.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (task.getDescription().isEmpty()){
            holder.description.setVisibility(View.GONE);
        }
        else {
            holder.description.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks (List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        Task task = tasks.get(position);
        db.deleteTask(task.getId());
        tasks.remove(task);
        notifyItemRemoved(position);
    }

    public void editTask(int position) {
        Task task = tasks.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", task.getId());
        bundle.putString("title", task.getTitle());
        bundle.putString("description", task.getDescription());
        bundle.putInt("status", task.getStatus());

        AddTask addTask = new AddTask();
        addTask.setArguments(bundle);
        addTask.show(activity.getSupportFragmentManager(), addTask.getTag());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        TextView title, description;
        MaterialCardView card;
        public TaskViewHolder(@NonNull View v) {
            super(v);

            checkBox = v.findViewById(R.id.taskCheckBox);
            title = v.findViewById(R.id.taskTitle);
            description = v.findViewById(R.id.taskDetail);
            card = v.findViewById(R.id.card);

        }
    }
}
