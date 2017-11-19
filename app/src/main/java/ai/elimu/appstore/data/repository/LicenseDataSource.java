package ai.elimu.appstore.data.repository;

import android.support.annotation.NonNull;

import ai.elimu.appstore.domain.model.LicenseValidationResponse;

public interface LicenseDataSource {

    void validateLicense(@NonNull String licenseEmail,
                         @NonNull String licenseNumber,
                         @NonNull ValidateLicenseCallback validateLicenseCallback);

    interface ValidateLicenseCallback {

        void onValidateLicenseSuccess(LicenseValidationResponse licenseValidationResponse);

        void onValidateLicenseFail(int errorCode);

    }

}
