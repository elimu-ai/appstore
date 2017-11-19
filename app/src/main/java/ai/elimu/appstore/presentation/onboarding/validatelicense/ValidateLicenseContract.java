package ai.elimu.appstore.presentation.onboarding.validatelicense;

import android.support.annotation.NonNull;

import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import ai.elimu.appstore.presentation.common.BasePresenter;
import ai.elimu.appstore.presentation.common.BaseView;

public interface ValidateLicenseContract {

    interface Presenter extends BasePresenter {

        void validateLicense(@NonNull String licenseEmail,
                             @NonNull String licenseNumber);

    }

    interface View extends BaseView<Presenter> {

        void setLoading(boolean isActive);

        void showValidateLicenseSuccess(@NonNull LicenseValidationResponse
                                                licenseValidationResponse);

        void showValidateLicenseFail(int errorCode);

    }

}
