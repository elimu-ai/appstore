package ai.elimu.appstore.data.repository.source;

import android.support.annotation.NonNull;

import ai.elimu.appstore.data.repository.LicenseDataSource;
import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import ai.elimu.appstore.util.Const;
import ai.elimu.appstore.util.Preconditions;

public class LicenseRepository implements LicenseDataSource {

    private static LicenseRepository sLicenseRepository;

    private final LicenseDataSource licenseLocalDataSource;

    private final LicenseDataSource licenseRemoteDataSource;

    private LicenseRepository(@NonNull LicenseDataSource licenseLocalDataSource,
                              @NonNull LicenseDataSource licenseRemoteDataSource) {

        this.licenseLocalDataSource = Preconditions.checkNotNull(licenseLocalDataSource);
        this.licenseRemoteDataSource = Preconditions.checkNotNull(licenseRemoteDataSource);
    }

    public static LicenseRepository getInstance(@NonNull LicenseDataSource licenseLocalDataSource,
                                                @NonNull LicenseDataSource
                                                        licenseRemoteDataSource) {
        if (sLicenseRepository == null) {
            synchronized (LicenseRepository.class) {
                if (sLicenseRepository == null) {
                    sLicenseRepository = new LicenseRepository(licenseLocalDataSource,
                            licenseRemoteDataSource);
                }
            }
        }
        return sLicenseRepository;
    }

    @Override
    public void validateLicense(@NonNull String licenseEmail,
                                @NonNull String licenseNumber,
                                @NonNull final ValidateLicenseCallback validateLicenseCallback) {

        licenseRemoteDataSource.validateLicense(licenseEmail, licenseNumber,
                new ValidateLicenseCallback() {
                    @Override
                    public void onValidateLicenseSuccess(LicenseValidationResponse
                                                                 licenseValidationResponse) {

                        if (licenseValidationResponse != null
                                && Const.NET_REQ_SUCCESS.equals(licenseValidationResponse
                                .getResult())) {
                            validateLicenseCallback.onValidateLicenseSuccess
                                    (licenseValidationResponse);
                        } else {
                            validateLicenseCallback.onValidateLicenseFail(Const.ERR_FAILED);
                        }
                    }

                    @Override
                    public void onValidateLicenseFail(int errorCode) {
                        validateLicenseCallback.onValidateLicenseFail(errorCode);
                    }
                });
    }
}
