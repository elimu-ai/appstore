package ai.elimu.appstore.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Check MD5 hash value of a file if it's valid compared to provided MD5 string
     *
     * @param md5        Provided MD5 string
     * @param updateFile The file whose's MD5 value needs to be checked
     * @return true if file is valid, false if invalid
     *
     * Reference: https://github.com/CyanogenMod/android_packages_apps_CMUpdater/blob/cm-10.2/src/com/cyanogenmod/updater/utils/MD5.java
     */
    public static boolean checkMd5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Timber.e("MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMd5(updateFile);
        if (calculatedDigest == null) {
            Timber.e("calculatedDigest null");
            return false;
        }

        Timber.v("Calculated digest: " + calculatedDigest);
        Timber.v("Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static boolean checkMd5(String md5, InputStream inputStream) {
        if (TextUtils.isEmpty(md5) || inputStream == null) {
            Timber.e("MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMd5(inputStream, "MD5");
        if (calculatedDigest == null) {
            Timber.e("calculatedDigest null");
            return false;
        }

        Timber.v("Calculated digest: " + calculatedDigest);
        Timber.v("Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    /**
     * Get MD5 hash value of a file
     *
     * @param updateFile The file whose's MD5 hash value needs to be calculated
     * @return The MD5 hash value of input file
     */
    public static String calculateMd5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
            return "";
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Timber.e(e);
            return "";
        }

        byte[] buffer = new byte[8192]; //8x1024 = 8MB
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    /**
     * Calculate checksum of an InputStream
     *
     * @param stream            The InputStream whose checksum needs to be calculated
     * @param checksumAlgorithm Checksum algorithm
     * @return The checksum string of the InputStream
     * @throws Exception Reference: https://stackoverflow.com/a/21710105/3682198
     */
    public static String calculateMd5(InputStream stream, String checksumAlgorithm) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(checksumAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
            return "";
        }

        InputStream input = null;
        StringBuffer sb = new StringBuffer();
        try {
            input = stream;
            byte[] buffer = new byte[8192];   //8x1024 = 8MB
            do {
                int read = input.read(buffer);
                if (read <= 0)
                    break;
                digest.update(buffer, 0, read);
            } while (true);
            byte[] sum = digest.digest();

            for (int i = 0; i < sum.length; i++) {
                sb.append(Integer.toString((sum[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }

        return sb.toString();
    }
}
