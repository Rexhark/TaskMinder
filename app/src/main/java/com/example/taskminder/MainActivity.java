package com.example.taskminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.taskminder.Adapter.TaskAdapter;
import com.example.taskminder.Model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView tasksRV;
    private DatabaseHelper dbHelper;
    private List<Task> tasksList;
    private TaskAdapter adapter;
    private TextView noTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tasksRV = findViewById(R.id.tasksRV);
        FloatingActionButton addTaskBtn = findViewById(R.id.buttonAdd);
        noTasks = findViewById(R.id.noTasks);
        dbHelper = new DatabaseHelper(this);
        tasksList = new ArrayList<>();
        adapter = new TaskAdapter(dbHelper, this);

        if(dbHelper.getAllTasks().size() == 0){
            noTasks.setVisibility(View.VISIBLE);
            tasksRV.setVisibility(View.GONE);
        } else {
            noTasks.setVisibility(View.GONE);
            tasksRV.setVisibility(View.VISIBLE);
        }

        tasksRV.setHasFixedSize(true);
        tasksRV.setLayoutManager(new LinearLayoutManager(this));
        tasksRV.setAdapter(adapter);

        addTaskBtn.setOnClickListener(view -> {
            AddTask.newInstance().show(getSupportFragmentManager(), AddTask.TAG);
        });

    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        if(dbHelper.getAllTasks().size() == 0){
            noTasks.setVisibility(View.VISIBLE);
            tasksRV.setVisibility(View.GONE);
        } else {
            noTasks.setVisibility(View.GONE);
            tasksRV.setVisibility(View.VISIBLE);
        }
        tasksList = dbHelper.getAllTasks();
        adapter.setTasks(tasksList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}