package ai.elimu.appstore.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ai.elimu.appstore.BuildConfig;
import timber.log.Timber;

public class ChecksumHelper {

    public static String getChecksum(Context context) {
        String checksum = null;

        String appstoreSecret = BuildConfig.APPSTORE_SECRET;
        if (TextUtils.isEmpty(appstoreSecret)) {
            throw new RuntimeException("License number needs to be set before calling this method");
        } else {
            String deviceId = DeviceInfoHelper.getDeviceId(context);
            String input = deviceId + appstoreSecret;
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
