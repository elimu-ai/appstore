package ai.elimu.appstore.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class RootHelper {

    public static boolean runAsRoot(String[] commands) {
        Timber.i("runAsRoot");

        boolean isSuccess = false;

        try {
            Process process = Runtime.getRuntime().exec("su");

            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                Timber.i("command: " + command);
                dataOutputStream.writeBytes(command + "\n");
            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();

            process.waitFor();
            int exitValue = process.exitValue();
            Timber.i("exitValue: " + exitValue);
            if (exitValue == 0) {
                isSuccess = true;
            }

            InputStream inputStreamSuccess = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamSuccess));
            String successMessage = bufferedReader.readLine();
            Timber.i("successMessage: " + successMessage);

            InputStream inputStreamError = process.getErrorStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStreamError));
            String errorMessage = bufferedReader.readLine();
            if (TextUtils.isEmpty(errorMessage)) {
                Timber.i("errorMessage: " + errorMessage);
            } else {
                Timber.e("errorMessage: " + errorMessage);
            }
        } catch (IOException | InterruptedException e) {
            Timber.e(e);
        }

        return isSuccess;
    }
}
