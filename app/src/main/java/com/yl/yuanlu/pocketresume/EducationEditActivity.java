package com.yl.yuanlu.pocketresume;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yl.yuanlu.pocketresume.Model.Education;
import com.yl.yuanlu.pocketresume.Utils.DateUtils;
import com.yl.yuanlu.pocketresume.Utils.ModelUtils;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LUYUAN on 5/29/2017.
 */

public class EducationEditActivity extends AppCompatActivity {

    @BindView(R.id.education_edit_delete) TextView educationDelete;
    @BindView(R.id.education_edit_school) EditText educationSchool;
    @BindView(R.id.education_edit_major) EditText educationMajor;
    @BindView(R.id.education_edit_start_date) EditText educationStartDate;
    @BindView(R.id.education_edit_end_date) EditText educationEndDate;
    @BindView(R.id.education_edit_courses) EditText educationCourses;


    public static final String KEY_EDUCATION = "education";
    public static final String KEY_EDUCATION_DELETE = "education_delete";

    private Education education;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_edit);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Education");

        education = ModelUtils.toObject(getIntent().getStringExtra(KEY_EDUCATION), new TypeToken<Education>(){});

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
        if(education == null) {
            educationDelete.setVisibility(View.GONE);
            education = new Education();
        }
        else {
            educationDelete.setVisibility(View.VISIBLE);
            educationSchool.setText(education.school);
            educationMajor.setText(education.major);
            if(education.startDate != null) educationStartDate.setText(DateUtils.dateToString(education.startDate));
            if(education.endDate != null) educationEndDate.setText(DateUtils.dateToString(education.endDate));
            educationCourses.setText(TextUtils.join("\n", education.courses));
            educationDelete.setOnClickListener(new View.OnClickListener() {
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
        if(education.startDate != null) {
            myCalendarStart.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(education.startDate)));
            myCalendarStart.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(education.startDate)));
        }

        if(education.endDate != null) {
            myCalendarEnd.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(education.endDate)));
            myCalendarEnd.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(education.endDate)));
        }

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                educationStartDate.setText(DateUtils.dateToString(myCalendarStart.getTime()));
                education.startDate = myCalendarStart.getTime();
            }
        };

        educationStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EducationEditActivity.this, startDateListener, myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH), 1).show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                educationEndDate.setText(DateUtils.dateToString(myCalendarEnd.getTime()));
                education.endDate = myCalendarEnd.getTime();
            }
        };

        educationEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EducationEditActivity.this, endDateListener, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), 1).show();
            }
        });
    }

    private void saveAndExit(boolean toDelete) {
        education.school = String.valueOf(educationSchool.getText());
        education.major = String.valueOf(educationMajor.getText());
        education.courses = Arrays.asList(TextUtils.split(educationCourses.getText().toString(), "\n"));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_EDUCATION_DELETE, toDelete);
        resultIntent.putExtra(KEY_EDUCATION, ModelUtils.toString(education, new TypeToken<Education>(){}));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
