package ai.elimu.appstore.data.repository.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import ai.elimu.appstore.data.network.ApiManager;
import ai.elimu.appstore.data.repository.LicenseDataSource;
import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import ai.elimu.appstore.util.Const;
import ai.elimu.appstore.util.Preconditions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LicenseRemoteDataSource implements LicenseDataSource {

    private static LicenseRemoteDataSource sLicenseRemoteDataSource = null;

    private final Context context;

    private LicenseRemoteDataSource(@NonNull Context context) {
        this.context = Preconditions.checkNotNull(context);
    }

    public static LicenseRemoteDataSource getInstance(@NonNull Context context) {
        if (sLicenseRemoteDataSource == null) {
            synchronized (LicenseRemoteDataSource.class) {
                if (sLicenseRemoteDataSource == null) {
                    sLicenseRemoteDataSource = new LicenseRemoteDataSource(context);
                }
            }
        }
        return sLicenseRemoteDataSource;
    }

    @Override
    public void validateLicense(@NonNull String licenseEmail,
                                @NonNull String licenseNumber,
                                @NonNull final ValidateLicenseCallback validateLicenseCallback) {

        ApiManager.validateLicense(context, licenseEmail, licenseNumber,
                new Callback<LicenseValidationResponse>() {
                    @Override
                    public void onResponse(Call<LicenseValidationResponse> call,
                                           Response<LicenseValidationResponse> response) {
                        if (response != null && response.isSuccessful()
                                && response.body() != null && Const.NET_REQ_SUCCESS.equals
                                (response.body().getResult())) {
                            validateLicenseCallback.onValidateLicenseSuccess(response.body());
                        } else {
                            validateLicenseCallback.onValidateLicenseFail(Const.ERR_FAILED);
                        }
                    }

                    @Override
                    public void onFailure(Call<LicenseValidationResponse> call, Throwable t) {
                        validateLicenseCallback.onValidateLicenseFail(Const.ERR_UNKNOWN);
                        t.printStackTrace();
                    }
                });
    }
}
