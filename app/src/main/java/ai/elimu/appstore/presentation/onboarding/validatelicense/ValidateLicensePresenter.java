package ai.elimu.appstore.presentation.onboarding.validatelicense;

import android.support.annotation.NonNull;

import ai.elimu.appstore.domain.usecase.ValidateLicense;
import ai.elimu.appstore.mvpcore.UseCase;
import ai.elimu.appstore.mvpcore.UseCaseHandler;
import ai.elimu.appstore.util.Preconditions;

public class ValidateLicensePresenter implements ValidateLicenseContract.Presenter {

    private final ValidateLicenseContract.View view;

    private final UseCaseHandler useCaseHandler;

    private final ValidateLicense validateLicense;

    public ValidateLicensePresenter(@NonNull ValidateLicenseContract.View view,
                                    @NonNull UseCaseHandler useCaseHandler,
                                    @NonNull ValidateLicense validateLicense) {

        this.view = Preconditions.checkNotNull(view);
        this.useCaseHandler = Preconditions.checkNotNull(useCaseHandler);
        this.validateLicense = Preconditions.checkNotNull(validateLicense);
        this.view.setPresenter(this);
    }

    @Override
    public void validateLicense(@NonNull String licenseEmail, @NonNull String licenseNumber) {
        view.setLoading(true);
        useCaseHandler.execute(validateLicense, new ValidateLicense.RequestValue(licenseEmail,
                licenseNumber), new UseCase.UseCaseCallback<ValidateLicense.ResponseValue>() {
            @Override
            public void onSuccess(ValidateLicense.ResponseValue response) {
                view.showValidateLicenseSuccess(response.getLicenseValidationResponse());
            }

            @Override
            public void onError(int errorCode) {
                view.showValidateLicenseFail(errorCode);
            }
        });
    }

    @Override
    public void start() {

    }
}
