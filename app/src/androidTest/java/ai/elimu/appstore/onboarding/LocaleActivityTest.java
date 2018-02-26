package ai.elimu.appstore.onboarding;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ai.elimu.appstore.R;
import ai.elimu.appstore.onboarding.LocaleActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocaleActivityTest {

    @Rule
    public ActivityTestRule<LocaleActivity> activityTestRule = new ActivityTestRule<LocaleActivity>(LocaleActivity.class);

//    @Rule
//    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Test
    public void testIsViewPresent() {
        onView(ViewMatchers.withId(R.id.textViewLocale)).check(matches(withText(R.string.select_the_students_native_language)));
    }
}
