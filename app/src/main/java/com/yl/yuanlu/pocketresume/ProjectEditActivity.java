package com.yl.yuanlu.pocketresume;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yl.yuanlu.pocketresume.Model.Project;
import com.yl.yuanlu.pocketresume.Utils.DateUtils;
import com.yl.yuanlu.pocketresume.Utils.ModelUtils;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LUYUAN on 6/4/2017.
 */

public class ProjectEditActivity extends AppCompatActivity {

    @BindView(R.id.project_edit_delete) TextView projectDelete;
    @BindView(R.id.project_edit_project_name) EditText projectName;
    @BindView(R.id.project_edit_description) EditText projectDescription;
    @BindView(R.id.project_edit_start_date) EditText projectStartDate;
    @BindView(R.id.project_edit_end_date) EditText projectEndDate;
    @BindView(R.id.project_edit_details) EditText projectDetails;


    public static final String KEY_PROJECT = "project";
    public static final String KEY_PROJECT_DELETE = "project_delete";

    private Project project;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_edit);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Project");

        project = ModelUtils.toObject(getIntent().getStringExtra(KEY_PROJECT), new TypeToken<Project>(){});

        setupUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.edit_save) {
            saveAndExit(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        if(project == null) {
            projectDelete.setVisibility(View.GONE);
            project = new Project();
        }
        else {
            projectDelete.setVisibility(View.VISIBLE);
            projectName.setText(project.projectName);
            projectDescription.setText(project.description);
            if(project.startDate != null) projectStartDate.setText(DateUtils.dateToString(project.startDate));
            if(project.endDate != null) projectEndDate.setText(DateUtils.dateToString(project.endDate));
            projectDetails.setText(TextUtils.join("\n", project.details));
            projectDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAndExit(true);
                }
            });
        }

        setupDate();

    }

    private void setupDate() {
        final Calendar myCalendarStart = Calendar.getInstance();
        final Calendar myCalendarEnd = Calendar.getInstance();
        if(project.startDate != null) {
            myCalendarStart.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(project.startDate)));
            myCalendarStart.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(project.startDate)));
        }

        if(project.endDate != null) {
            myCalendarEnd.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(project.endDate)));
            myCalendarEnd.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(project.endDate)));
        }

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                projectStartDate.setText(DateUtils.dateToString(myCalendarStart.getTime()));
                project.startDate = myCalendarStart.getTime();
            }
        };

        projectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProjectEditActivity.this, startDateListener, myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH), 1).show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                projectEndDate.setText(DateUtils.dateToString(myCalendarEnd.getTime()));
                project.endDate = myCalendarEnd.getTime();
            }
        };

        projectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProjectEditActivity.this, endDateListener, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), 1).show();
            }
        });
    }

    private void saveAndExit(boolean toDelete){
        project.projectName = String.valueOf(projectName.getText());
        project.description = String.valueOf(projectDescription.getText());
        project.details = Arrays.asList(TextUtils.split(projectDetails.getText().toString(), "\n"));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_PROJECT_DELETE, toDelete);
        resultIntent.putExtra(KEY_PROJECT, ModelUtils.toString(project, new TypeToken<Project>(){}));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
