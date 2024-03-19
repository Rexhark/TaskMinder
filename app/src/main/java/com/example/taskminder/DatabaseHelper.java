package com.example.taskminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.taskminder.Model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "taskminder.db";
    private static final String TABLE_NAME = "tasks";
    private static final String COL_1 = "id";
    private static final String COL_2 = "title";
    private static final String COL_3 = "description";
    private static final String COL_4 = "status";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, status INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addTask(Task task) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, task.getTitle());
        values.put(COL_3, task.getDescription());
        values.put(COL_4, task.getStatus());
        db.insert(TABLE_NAME, null, values);
        updateIds();
    }

    public void updateTask(int id, Task task) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, task.getTitle());
        values.put(COL_3, task.getDescription());
        db.update(TABLE_NAME, values, "id = ?", new String[] {String.valueOf(id)});
        updateIds();
    }

    public void updateStatus(int id, int status) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_4, status);
        db.update(TABLE_NAME, values, "id = ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] {String.valueOf(id)});
        updateIds();
    }

    public void deleteAllTasks() {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    @SuppressLint("Range")
    public List<Task> getAllTasks() {

        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<Task> tasks = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Task task = new Task();
                        task.setId(cursor.getInt(cursor.getColumnIndex(COL_1)));
                        task.setTitle(cursor.getString(cursor.getColumnIndex(COL_2)));
                        task.setDescription(cursor.getString(cursor.getColumnIndex(COL_3)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(COL_4)));
                        tasks.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }
        return tasks;
    }

    public void updateIds() {
        db = this.getWritableDatabase();
        List<Task> tasks = getAllTasks();

        // Looping pada semua data dan update id-nya
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            ContentValues values = new ContentValues();
            values.put(COL_1, i);
            db.update(TABLE_NAME, values, " id = ? ", new String[]{String.valueOf(task.getId())});
        }
    }

}
