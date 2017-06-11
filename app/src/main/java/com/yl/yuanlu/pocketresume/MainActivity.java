package com.yl.yuanlu.pocketresume;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yl.yuanlu.pocketresume.Model.BasicInfo;
import com.yl.yuanlu.pocketresume.Model.Education;
import com.yl.yuanlu.pocketresume.Model.Experience;
import com.yl.yuanlu.pocketresume.Model.Project;
import com.yl.yuanlu.pocketresume.Utils.DateUtils;
import com.yl.yuanlu.pocketresume.Utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edit_user_info) ImageButton editBasicInfoBtn;
    @BindView(R.id.education_list) LinearLayout educationListLayout;
    @BindView(R.id.experience_list) LinearLayout experienceListLayout;
    @BindView(R.id.project_list) LinearLayout projectListLayout;
    @BindView(R.id.add_education_btn) ImageButton addEducationBtn;
    @BindView(R.id.add_experience_btn) ImageButton addExperienceBtn;
    @BindView(R.id.add_project_btn) ImageButton addProjectBtn;
    @BindView(R.id.name) TextView userName;
    @BindView(R.id.email) TextView userEmail;
    @BindView(R.id.user_picture) ImageView userImage;


    public static final int REQ_CODE_BASICINFO_EDIT = 100;
    public static final int REQ_CODE_EDUCATION_EDIT = 200;
    public static final int REQ_CODE_EXPERIENCE_EDIT = 300;
    public static final int REQ_CODE_PROJECT_EDIT = 400;
    private static final String KEY_BASICINFOSP = "basicinfo_sp";
    private static final String KEY_EDUCATIONLISTSP = "education_list_sp";
    private static final String KEY_EXPERIENCELISTSP = "experience_list_sp";
    private static final String KEY_PROJECTLISTSP = "project_list_sp";
    private static final String SP_DATA = "sp_data";


    private BasicInfo basicInfo;
    private List<Education> educationList;
    private List<Experience> experienceList;
    private List<Project> projectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadData();
        setupUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean toDelete;
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_BASICINFO_EDIT :
                    basicInfo = ModelUtils.toObject(data.getStringExtra(BasicInfoEditActivity.KEY_BASICINFO), new TypeToken<BasicInfo>(){});
                    setupBasicInfo();
                    saveData(KEY_BASICINFOSP);
                    break;
                case REQ_CODE_EDUCATION_EDIT :
                    toDelete = data.getBooleanExtra(EducationEditActivity.KEY_EDUCATION_DELETE, false);
                    Education education = ModelUtils.toObject(data.getStringExtra(EducationEditActivity.KEY_EDUCATION), new TypeToken<Education>(){});
                    updateEducation(education, toDelete);
                    break;
                case REQ_CODE_EXPERIENCE_EDIT :
                    toDelete = data.getBooleanExtra(ExperienceEditActivity.KEY_EXPERIENCE_DELETE, false);
                    Experience experience = ModelUtils.toObject(data.getStringExtra(ExperienceEditActivity.KEY_EXPERIENCE), new TypeToken<Experience>(){});
                    updateExperience(experience, toDelete);
                    break;
                case REQ_CODE_PROJECT_EDIT :
                    toDelete = data.getBooleanExtra(ProjectEditActivity.KEY_PROJECT_DELETE, false);
                    Project project = ModelUtils.toObject(data.getStringExtra(ProjectEditActivity.KEY_PROJECT), new TypeToken<Project>(){});
                    updateProject(project, toDelete);
                    break;
            }
        }
    }

    private void loadData() {

        basicInfo = ModelUtils.toObject(ModelUtils.load_from_sp(SP_DATA, MainActivity.this, KEY_BASICINFOSP), new TypeToken<BasicInfo>(){});
        if(basicInfo == null) basicInfo = new BasicInfo();

        educationList = ModelUtils.toObject(ModelUtils.load_from_sp(SP_DATA, MainActivity.this, KEY_EDUCATIONLISTSP), new TypeToken<List<Education>>(){});
        if(educationList == null) educationList = new ArrayList<>();

        experienceList = ModelUtils.toObject(ModelUtils.load_from_sp(SP_DATA, MainActivity.this, KEY_EXPERIENCELISTSP), new TypeToken<List<Experience>>(){});
        if(experienceList == null) experienceList = new ArrayList<>();

        projectList = ModelUtils.toObject(ModelUtils.load_from_sp(SP_DATA, MainActivity.this, KEY_PROJECTLISTSP), new TypeToken<List<Project>>(){});
        if(projectList == null) projectList = new ArrayList<>();

    }

    private void saveData(String item) {
        switch(item) {
            case KEY_BASICINFOSP:
                ModelUtils.save_to_sp(SP_DATA, MainActivity.this, ModelUtils.toString(basicInfo, new TypeToken<BasicInfo>(){}), KEY_BASICINFOSP);
                break;
            case KEY_EDUCATIONLISTSP:
                ModelUtils.save_to_sp(SP_DATA, MainActivity.this, ModelUtils.toString(educationList, new TypeToken<List<Education>>(){}), KEY_EDUCATIONLISTSP);
                break;
            case KEY_EXPERIENCELISTSP:
                ModelUtils.save_to_sp(SP_DATA, MainActivity.this, ModelUtils.toString(experienceList, new TypeToken<List<Experience>>(){}), KEY_EXPERIENCELISTSP);
                break;
            case KEY_PROJECTLISTSP:
                ModelUtils.save_to_sp(SP_DATA, MainActivity.this, ModelUtils.toString(projectList, new TypeToken<List<Project>>(){}), KEY_PROJECTLISTSP);
                break;
        }
    }

    private void setupUI() {

        setupBasicInfo();
        setupEducation();
        setupExperience();
        setupProject();

        addEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationEditActivity.class);
                startActivityForResult(intent, REQ_CODE_EDUCATION_EDIT);
            }
        });

        addExperienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExperienceEditActivity.class);
                startActivityForResult(intent, REQ_CODE_EXPERIENCE_EDIT);
            }
        });

        addProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProjectEditActivity.class);
                startActivityForResult(intent, REQ_CODE_PROJECT_EDIT);
            }
        });

    }

    private void setupBasicInfo() {
        if(basicInfo.name != null) userName.setText(basicInfo.name);
        if(basicInfo.email != null) userEmail.setText(basicInfo.email);
        if(basicInfo.userPictureUri != null) userImage.setImageURI(basicInfo.userPictureUri);

        editBasicInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String basicInfoJString = ModelUtils.toString(basicInfo, new TypeToken<BasicInfo>(){});
                Intent intent = new Intent(MainActivity.this, BasicInfoEditActivity.class);
                intent.putExtra(BasicInfoEditActivity.KEY_BASICINFO, basicInfoJString);
                startActivityForResult(intent, REQ_CODE_BASICINFO_EDIT);
            }
        });

    }

    private void setupEducation() {
        educationListLayout.removeAllViews();
        for(Education education : educationList) {
            educationListLayout.addView(setupEducationItem(education));
        }
    }

    private View setupEducationItem(final Education education) {
        View educationView = getLayoutInflater().inflate(R.layout.education_item, null);
        ((TextView) educationView.findViewById(R.id.education_school)).setText(education.school);
        ((TextView) educationView.findViewById(R.id.education_major)).setText(education.major);
        String educationDate = "";
        if(education.startDate != null) educationDate = DateUtils.dateToString(education.startDate);
        educationDate = educationDate + " - ";
        if(education.endDate != null) educationDate = educationDate + DateUtils.dateToString(education.endDate);
        if(education.startDate != null || education.endDate != null) ((TextView) educationView.findViewById(R.id.education_date)).setText(educationDate);
        ((TextView) educationView.findViewById(R.id.education_courses)).setText(formatItems(education.courses));
        educationView.findViewById(R.id.edit_education_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String educationJString = ModelUtils.toString(education, new TypeToken<Education>(){});
                Intent intent = new Intent(MainActivity.this, EducationEditActivity.class);
                intent.putExtra(EducationEditActivity.KEY_EDUCATION, educationJString);
                startActivityForResult(intent, REQ_CODE_EDUCATION_EDIT);
            }
        });
        return educationView;
    }

    private void updateEducation(Education education, boolean toDelete) {
        String educationID = education.id;
        boolean newEntry = true;
        for(int i=0; i<educationList.size(); i++) {
            if(educationList.get(i).id.equals(educationID)) {
                if(toDelete) {
                    educationList.remove(i);
                }
                else {
                    educationList.set(i, education);
                }
                newEntry = false;
                break;
            }
        }
        if(newEntry) educationList.add(education);
        setupEducation();
        saveData(KEY_EDUCATIONLISTSP);
    }

    private void setupExperience() {
        experienceListLayout.removeAllViews();
        for(Experience experience : experienceList) {
            experienceListLayout.addView(setupExperienceItem(experience));
        }
    }

    private View setupExperienceItem(final Experience experience) {
        View experienceView = getLayoutInflater().inflate(R.layout.experience_item, null);
        ((TextView) experienceView.findViewById(R.id.experience_company)).setText(experience.company);
        ((TextView) experienceView.findViewById(R.id.experience_title)).setText(experience.title);
        String experienceDate = "";
        if(experience.startDate != null) experienceDate = DateUtils.dateToString(experience.startDate);
        experienceDate += " - ";
        if (experience.endDate != null) experienceDate += DateUtils.dateToString(experience.endDate);
        if(experience.startDate != null || experience.endDate != null) ((TextView) experienceView.findViewById(R.id.experience_date)).setText(experienceDate);
        ((TextView) experienceView.findViewById(R.id.experience_tasks)).setText(formatItems(experience.tasks));
        experienceView.findViewById(R.id.edit_experience_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String experienceJString = ModelUtils.toString(experience, new TypeToken<Experience>(){});
                Intent intent = new Intent(MainActivity.this, ExperienceEditActivity.class);
                intent.putExtra(ExperienceEditActivity.KEY_EXPERIENCE, experienceJString);
                startActivityForResult(intent, REQ_CODE_EXPERIENCE_EDIT);
            }
        });
        return experienceView;
    }

    private void updateExperience(Experience experience, boolean toDelete) {
        String experienceID = experience.id;
        boolean newEntry = true;
        for(int i=0; i<experienceList.size(); i++) {
            if(experienceList.get(i).id.equals(experienceID)) {
                if(toDelete) {
                    experienceList.remove(i);
                }
                else {
                    experienceList.set(i, experience);
                }
                newEntry = false;
                break;
            }
        }
        if(newEntry) experienceList.add(experience);
        setupExperience();
        saveData(KEY_EXPERIENCELISTSP);
    }

    private void setupProject() {
        projectListLayout.removeAllViews();
        for(Project project : projectList) {
            projectListLayout.addView(setupProjectItem(project));
        }
    }

    private View setupProjectItem(final Project project) {
        View projectView = getLayoutInflater().inflate(R.layout.project_item, null);
        ((TextView) projectView.findViewById(R.id.project_name)).setText(project.projectName);
        ((TextView) projectView.findViewById(R.id.project_description)).setText(project.description);
        String projectDate = "";
        if(project.startDate != null) projectDate = DateUtils.dateToString(project.startDate);
        projectDate += " - ";
        if(project.endDate != null) projectDate += DateUtils.dateToString(project.endDate);
        if(project.startDate != null || project.endDate != null) ((TextView) projectView.findViewById(R.id.project_date)).setText(projectDate);
        ((TextView) projectView.findViewById(R.id.project_details)).setText(formatItems(project.details));
        projectView.findViewById(R.id.edit_project_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectJString = ModelUtils.toString(project, new TypeToken<Project>(){});
                Intent intent = new Intent(MainActivity.this, ProjectEditActivity.class);
                intent.putExtra(ProjectEditActivity.KEY_PROJECT, projectJString);
                startActivityForResult(intent, REQ_CODE_PROJECT_EDIT);
            }
        });
        return projectView;
    }

    private void updateProject(Project project, boolean toDelete) {
        String projectID = project.id;
        boolean newEntry = true;
        for(int i=0; i<projectList.size(); i++) {
            if(projectList.get(i).id.equals(projectID)) {
                if(toDelete) {
                    projectList.remove(i);
                }
                else {
                    projectList.set(i, project);
                }
                newEntry = false;
                break;
            }
        }
        if(newEntry) projectList.add(project);
        setupProject();
        saveData(KEY_PROJECTLISTSP);
    }

    public static String formatItems(List<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item: items) {
            sb.append(' ').append('-').append(' ').append(item).append('\n');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
