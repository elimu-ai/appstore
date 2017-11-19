package ai.elimu.appstore.data.network;

import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ApiEndpointInterface {

    @GET("project/licenses")
    Call<LicenseValidationResponse> validateLicense(@Query("licenseEmail") String licenseEmail,
                                                    @Query("licenseNumber") String licenseNumber);

}
