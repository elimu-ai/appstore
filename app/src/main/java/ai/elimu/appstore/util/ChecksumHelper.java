package ai.elimu.appstore.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ai.elimu.appstore.LicenseNumberActivity;
import timber.log.Timber;

public class ChecksumHelper {

    public static String getChecksum(Context context) {
        String checksum = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String licenseNumber = sharedPreferences.getString(LicenseNumberActivity.PREF_LICENSE_NUMBER, null);
        if (TextUtils.isEmpty(licenseNumber)) {
            throw new RuntimeException("License number needs to be set before calling this method");
        } else {
            String deviceId = DeviceInfoHelper.getDeviceId(context);
            String input = deviceId + licenseNumber;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] md5AsBytes = messageDigest.digest(input.getBytes("UTF-8"));
                checksum = new BigInteger(1, md5AsBytes).toString(16);
            } catch (NoSuchAlgorithmException e) {
                Timber.e(e);
            } catch (UnsupportedEncodingException e) {
                Timber.e(e);
            }
        }

        return checksum;
    }
}
