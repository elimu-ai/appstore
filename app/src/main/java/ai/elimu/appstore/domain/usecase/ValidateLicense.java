package ai.elimu.appstore.domain.usecase;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.NotNull;

import ai.elimu.appstore.data.repository.LicenseDataSource;
import ai.elimu.appstore.data.repository.source.LicenseRepository;
import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import ai.elimu.appstore.mvpcore.UseCase;
import ai.elimu.appstore.util.Preconditions;

public class ValidateLicense extends UseCase<ValidateLicense.RequestValue,
        ValidateLicense.ResponseValue> {

    private final LicenseRepository licenseRepository;

    public ValidateLicense(@NonNull LicenseRepository licenseRepository) {
        this.licenseRepository = Preconditions.checkNotNull(licenseRepository);
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        Preconditions.checkNotNull(requestValues);
        Preconditions.checkNotNull(licenseRepository);

        licenseRepository.validateLicense(requestValues.getLicenseEmail(),
                requestValues.getLicenseNumber(),
                new LicenseDataSource.ValidateLicenseCallback() {
                    @Override
                    public void onValidateLicenseSuccess(LicenseValidationResponse
                                                                 licenseValidationResponse) {
                        getUseCaseCallback().onSuccess(new ResponseValue
                                (licenseValidationResponse));
                    }

                    @Override
                    public void onValidateLicenseFail(int errorCode) {
                        getUseCaseCallback().onError(errorCode);
                    }
                });
    }

    public static final class RequestValue implements UseCase.RequestValues {

        private final String licenseEmail;

        private final String licenseNumber;

        public RequestValue(@NotNull String licenseEmail,
                            @NonNull String licenseNumber) {

            this.licenseEmail = Preconditions.checkNotNull(licenseEmail);
            this.licenseNumber = Preconditions.checkNotNull(licenseNumber);
        }

        public String getLicenseEmail() {
            return licenseEmail;
        }

        public String getLicenseNumber() {
            return licenseNumber;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final LicenseValidationResponse licenseValidationResponse;

        public ResponseValue(@NonNull LicenseValidationResponse licenseValidationResponse) {
            this.licenseValidationResponse = Preconditions.checkNotNull(licenseValidationResponse);
        }

        public LicenseValidationResponse getLicenseValidationResponse() {
            return licenseValidationResponse;
        }
    }

}
