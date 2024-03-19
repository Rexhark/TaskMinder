package com.example.taskminder;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskminder.Adapter.TaskAdapter;
import com.example.taskminder.Model.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddTask";
    private EditText titleEt, descriptionEt;
    private Button addBtn;
    private DatabaseHelper db;

    public static AddTask newInstance() {
        return new AddTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.add_task_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskAdapter adapter = new TaskAdapter(new DatabaseHelper(getActivity()), (MainActivity) getActivity());

        titleEt = view.findViewById(R.id.newTaskTitle);

        descriptionEt = view.findViewById(R.id.newTaskDescription);
        descriptionEt.setVisibility(View.GONE);

        ImageButton btnDescription = view.findViewById(R.id.btn_description);
        ImageButton btnDelete = view.findViewById(R.id.btn_delete);

        btnDescription.setOnClickListener(view1 -> {
            if (descriptionEt.getVisibility() == View.VISIBLE){
                descriptionEt.setVisibility(View.GONE);
            } else {
                descriptionEt.setVisibility(View.VISIBLE);
            }
        });

        addBtn = view.findViewById(R.id.btn_save);

        db = new DatabaseHelper(getActivity());

        Bundle bundle = getArguments();
        if(bundle != null) {
            String title = bundle.getString("title");
            String description = bundle.getString("description");
            titleEt.setText(title);
            btnDelete.setVisibility(View.VISIBLE);
            if (description != null && !description.equals("")){
                descriptionEt.setVisibility(View.VISIBLE);
                descriptionEt.setText(description);
            }
        }

        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addBtn.setEnabled(!charSequence.toString().equals(""));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addBtn.setOnClickListener(view1 -> {
            if (titleEt.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Task task = new Task();
            task.setTitle(titleEt.getText().toString());
            task.setDescription(descriptionEt.getText().toString());
            if (bundle != null) {
                task.setStatus(bundle.getInt("status"));
                db.updateTask(bundle.getInt("id"), task);
            }
            else {
                task.setStatus(0);
                db.addTask(task);
            }
            dismiss();
        });

        btnDelete.setOnClickListener(view1 -> {
           if (bundle != null) {
               adapter.deleteTask(bundle.getInt("id"));
               dismiss();
           }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
