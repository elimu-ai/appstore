package ai.elimu.appstore.data.repository.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import ai.elimu.appstore.data.repository.LicenseDataSource;
import ai.elimu.appstore.util.Preconditions;

public class LicenseLocalDataSource implements LicenseDataSource {

    private static LicenseLocalDataSource sLicenseLocalDataSource = null;

    private final Context context;

    private LicenseLocalDataSource(@NonNull Context context) {
        this.context = Preconditions.checkNotNull(context);
    }

    public static LicenseLocalDataSource getInstance(@NonNull Context context) {
        if (sLicenseLocalDataSource == null) {
            synchronized (LicenseLocalDataSource.class) {
                if (sLicenseLocalDataSource == null) {
                    sLicenseLocalDataSource = new LicenseLocalDataSource(context);
                }
            }
        }
        return sLicenseLocalDataSource;
    }

    @Override
    public void validateLicense(@NonNull String licenseEmail,
                                @NonNull String licenseNumber,
                                @NonNull ValidateLicenseCallback validateLicenseCallback) {

    }
}
