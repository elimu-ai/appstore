package ai.elimu.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import ai.elimu.appstore.AppstoreApplication;
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.model.enums.admin.ApplicationStatus;
import ai.elimu.model.enums.content.LiteracySkill;
import ai.elimu.model.enums.content.NumeracySkill;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hide/show apps based on Student's current literacy/numeracy skills.
 */
public class StudentUpdatedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        ArrayList<String> availableLiteracySkillsStringArray = intent.getStringArrayListExtra("availableLiteracySkills");
        Log.i(getClass().getName(), "availableLiteracySkillsStringArray: " + availableLiteracySkillsStringArray);
        Set<LiteracySkill> availableLiteracySkills = new HashSet<>();
        for (String literacySkillAsString : availableLiteracySkillsStringArray) {
            LiteracySkill literacySkill = LiteracySkill.valueOf(literacySkillAsString);
            availableLiteracySkills.add(literacySkill);
        }
        Log.i(getClass().getName(), "availableLiteracySkills: " + availableLiteracySkills);

        ArrayList<String> availableNumeracySkillsStringArray = intent.getStringArrayListExtra("availableNumeracySkills");
        Log.i(getClass().getName(), "availableNumeracySkillsStringArray: " + availableNumeracySkillsStringArray);
        Set<NumeracySkill> availableNumeracySkills = new HashSet<>();
        for (String numeracySkillAsString : availableNumeracySkillsStringArray) {
            NumeracySkill numeracySkill = NumeracySkill.valueOf(numeracySkillAsString);
            availableNumeracySkills.add(numeracySkill);
        }
        Log.i(getClass().getName(), "availableNumeracySkills: " + availableNumeracySkills);

        if (!availableLiteracySkills.isEmpty() || !availableNumeracySkills.isEmpty()) {
            AppstoreApplication appstoreApplication = (AppstoreApplication) context.getApplicationContext();
            ApplicationDao applicationDao = appstoreApplication.getDaoSession().getApplicationDao();
            List<Application> applications = applicationDao.loadAll();
            for (Application application : applications) {
                Log.i(getClass().getName(), "packageName: " + application.getPackageName() + ", literacySkills: " + application.getLiteracySkills() + ", numeracySkills: " + application.getNumeracySkills());

                // Filter by LiteracySkill
                if (!application.getLiteracySkills().isEmpty() && !availableLiteracySkills.isEmpty()) {
                    // Hide the Application if it _only_ contains LiteracySkills not yet made available to the Student
                    boolean appContainsAtLeastOneAvailableLiteracySkill = false;
                    for (LiteracySkill literacySkill : application.getLiteracySkills()) {
                        if (availableLiteracySkills.contains(literacySkill)) {
                            appContainsAtLeastOneAvailableLiteracySkill = true;
                            break;
                        }
                    }
                    
                    if (!appContainsAtLeastOneAvailableLiteracySkill) {
                        // Hide Application
                        Log.w(getClass().getName(), "LiteracySkill(s) not yet available. Hiding Application: " + application.getPackageName() + ", literacySkills: " + application.getLiteracySkills());
                        try {
                            runAsRoot(new String[]{
                                    "pm disable " + application.getPackageName()
                            });
                        } catch (IOException | InterruptedException e) {
                            Log.e(getClass().getName(), null, e);
                        }
                    } else {
                        if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                            // Show Application
                            Log.i(getClass().getName(), "LiteracySkill(s) available. Showing Application: " + application.getPackageName() + ", literacySkills: " + application.getLiteracySkills());
                            try {
                                runAsRoot(new String[]{
                                        "pm enable " + application.getPackageName()
                                });
                            } catch (IOException | InterruptedException e) {
                                Log.e(getClass().getName(), null, e);
                            }
                        }
                    }
                }

                // Filter by NumeracySkill
                if (!application.getNumeracySkills().isEmpty() && !availableNumeracySkills.isEmpty()) {
                    // Hide the Application if it _only_ contains NumeracySkills not yet made available to the Student
                    boolean appContainsAtLeastOneAvailableNumeracySkill = false;
                    for (NumeracySkill numeracySkill : application.getNumeracySkills()) {
                        if (availableNumeracySkills.contains(numeracySkill)) {
                            appContainsAtLeastOneAvailableNumeracySkill = true;
                            break;
                        }
                    }

                    if (!appContainsAtLeastOneAvailableNumeracySkill) {
                        // Hide Application
                        Log.w(getClass().getName(), "NumeracySkill(s) not yet available. Hiding Application: " + application.getPackageName() + ", numeracySkills: " + application.getNumeracySkills());
                        try {
                            runAsRoot(new String[]{
                                    "pm disable " + application.getPackageName()
                            });
                        } catch (IOException | InterruptedException e) {
                            Log.e(getClass().getName(), null, e);
                        }
                    } else {
                        if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                            // Show Application
                            Log.i(getClass().getName(), "NumeracySkill(s) available. Showing Application: " + application.getPackageName() + ", numeracySkills: " + application.getNumeracySkills());
                            try {
                                runAsRoot(new String[]{
                                        "pm enable " + application.getPackageName()
                                });
                            } catch (IOException | InterruptedException e) {
                                Log.e(getClass().getName(), null, e);
                            }
                        }
                    }
                }

                // No filter
                if (application.getLiteracySkills().isEmpty() && application.getNumeracySkills().isEmpty()) {
                    if (application.getApplicationStatus() == ApplicationStatus.ACTIVE) {
                        // Show Application
                        Log.i(getClass().getName(), "No skills required. Showing Application: " + application.getPackageName() + ", numeracySkills: " + application.getNumeracySkills());
                        try {
                            runAsRoot(new String[]{
                                    "pm enable " + application.getPackageName()
                            });
                        } catch (IOException | InterruptedException e) {
                            Log.e(getClass().getName(), null, e);
                        }
                    }
                }
            }
        }
    }

    private void runAsRoot(String[] commands) throws IOException, InterruptedException {
        Log.i(getClass().getName(), "runAsRoot");

        Process process = Runtime.getRuntime().exec("su");

        DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
        for (String command : commands) {
            Log.i(getClass().getName(), "command: " + command);
            dataOutputStream.writeBytes(command + "\n");
        }
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();

        process.waitFor();
        int exitValue = process.exitValue();
        Log.i(getClass().getName(), "exitValue: " + exitValue);

        InputStream inputStreamSuccess = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
        String successMessage = bufferedReader.readLine();
        Log.i(getClass().getName(), "successMessage: " + successMessage);

        InputStream inputStreamError = process.getErrorStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
        String errorMessage = bufferedReader.readLine();
        if (TextUtils.isEmpty(errorMessage)) {
            Log.i(getClass().getName(), "errorMessage: " + errorMessage);
        } else {
            Log.e(getClass().getName(), "errorMessage: " + errorMessage);
        }
    }
}
