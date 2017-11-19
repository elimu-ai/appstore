/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.elimu.appstore.mvpcore;

import android.content.Context;
import android.support.annotation.NonNull;

import ai.elimu.appstore.data.repository.source.LicenseRepository;
import ai.elimu.appstore.data.repository.source.local.LicenseLocalDataSource;
import ai.elimu.appstore.data.repository.source.remote.LicenseRemoteDataSource;
import ai.elimu.appstore.domain.usecase.ValidateLicense;

/**
 * Enables injection of mock implementations for some data sources. This is useful for
 * testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    /**
     * Provide an access point to the UseCaseHandler
     *
     * @return a UseCaseHandler instance
     */
    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    /**
     * Provide reference to LicenseRepository where local/remote access can be executed
     *
     * @param context The context where access to the repository is needed
     * @return A reference to LicenseRepository
     */
    private static LicenseRepository provideLicenseRepository(@NonNull Context context) {
        return LicenseRepository.getInstance(LicenseLocalDataSource.getInstance(context),
                LicenseRemoteDataSource.getInstance(context));
    }

    /**
     * Provide ValidateLicense use case instance
     *
     * @param context The context where use case is executed
     * @return A use case referencing {@link ValidateLicense}
     */
    public static ValidateLicense provideValidateLicense(@NonNull Context context) {
        return new ValidateLicense(provideLicenseRepository(context));
    }

}
