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
import com.yl.yuanlu.pocketresume.Model.Experience;
import com.yl.yuanlu.pocketresume.Utils.DateUtils;
import com.yl.yuanlu.pocketresume.Utils.ModelUtils;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LUYUAN on 6/4/2017.
 */

public class ExperienceEditActivity extends AppCompatActivity {

    @BindView(R.id.experience_edit_delete) TextView experienceDelete;
    @BindView(R.id.experience_edit_company) TextView experienceCompany;
    @BindView(R.id.experience_edit_title) TextView experienceTitle;
    @BindView(R.id.experience_edit_start_date) EditText experienceStartDate;
    @BindView(R.id.experience_edit_end_date) TextView experienceEndDate;
    @BindView(R.id.experience_edit_tasks) TextView experienceTasks;


    public static final String KEY_EXPERIENCE = "experience";
    public static final String KEY_EXPERIENCE_DELETE = "experience_delete";

    private Experience experience;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_edit);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Experience");

        experience = ModelUtils.toObject(getIntent().getStringExtra(KEY_EXPERIENCE), new TypeToken<Experience>(){});

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
        if(experience == null) {
            experienceDelete.setVisibility(View.GONE);
            experience = new Experience();
        }
        else {
            experienceDelete.setVisibility(View.VISIBLE);
            experienceCompany.setText(experience.company);
            experienceTitle.setText(experience.title);
            if(experience.startDate != null) experienceStartDate.setText(DateUtils.dateToString(experience.startDate));
            if(experience.endDate != null) experienceEndDate.setText(DateUtils.dateToString(experience.endDate));
            experienceTasks.setText(TextUtils.join("\n", experience.tasks));
            experienceDelete.setOnClickListener(new View.OnClickListener() {
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
        if(experience.startDate != null) {
            myCalendarStart.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(experience.startDate)));
            myCalendarStart.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(experience.startDate)));
        }

        if(experience.endDate != null) {
            myCalendarEnd.set(Calendar.YEAR, Integer.parseInt(DateUtils.getYear(experience.endDate)));
            myCalendarEnd.set(Calendar.MONTH, Integer.parseInt(DateUtils.getMonth(experience.endDate)));
        }

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                experienceStartDate.setText(DateUtils.dateToString(myCalendarStart.getTime()));
                experience.startDate = myCalendarStart.getTime();
            }
        };

        experienceStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExperienceEditActivity.this, startDateListener, myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH), 1).show();
            }
        });

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                experienceEndDate.setText(DateUtils.dateToString(myCalendarEnd.getTime()));
                experience.endDate = myCalendarEnd.getTime();
            }
        };

        experienceEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExperienceEditActivity.this, endDateListener, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), 1).show();
            }
        });
    }

    private void saveAndExit(boolean toDelete){
        experience.company = String.valueOf(experienceCompany.getText());
        experience.title = String.valueOf(experienceTitle.getText());
        experience.tasks = Arrays.asList(TextUtils.split(experienceTasks.getText().toString(), "\n"));
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_EXPERIENCE_DELETE, toDelete);
        resultIntent.putExtra(KEY_EXPERIENCE, ModelUtils.toString(experience, new TypeToken<Experience>(){}));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
