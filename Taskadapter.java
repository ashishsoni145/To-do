package com.example.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<TaskModel> tasks;
    private SharedPreferences sharedPreferences;

    public TaskAdapter(Context context, List<TaskModel> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.sharedPreferences = context.getSharedPreferences("TaskData", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TaskModel task = tasks.get(position);
        CheckBox checkBox = convertView.findViewById(R.id.taskCheckbox);
        EditText taskText = convertView.findViewById(R.id.taskText);

        taskText.setText(task.getText());
        checkBox.setChecked(task.isCompleted());
        taskText.setEnabled(!task.isCompleted());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            taskText.setEnabled(!isChecked);
            saveTasks();
        });

        taskText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                task.setText(taskText.getText().toString());
                saveTasks();
            }
        });

        return convertView;
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder data = new StringBuilder();
        for (TaskModel task : tasks) {
            data.append(task.getText()).append("|").append(task.isCompleted()).append("\n");
        }
        editor.putString("tasks", data.toString());
        editor.apply();
    }

    public void loadTasks() {
        String savedData = sharedPreferences.getString("tasks", "");
        if (!savedData.isEmpty()) {
            tasks.clear();
            String[] taskLines = savedData.split("\n");
            for (String line : taskLines) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    tasks.add(new TaskModel(parts[0], Boolean.parseBoolean(parts[1])));
                }
            }
            notifyDataSetChanged();
        }
    }
}
