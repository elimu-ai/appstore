package ai.elimu.appstore;

import android.Manifest;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LicenseOptionActivityTest {

    @Rule
    public ActivityTestRule<LicenseOptionActivity> activityTestRule = new ActivityTestRule<LicenseOptionActivity>(LicenseOptionActivity.class);

//    @Rule
//    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Test
    public void testAskForWritePermission() {
        onView(withId(R.id.textViewLicenseQuestion)).check(matches(withText(R.string.do_you_have_a_license_number)));
    }
}
