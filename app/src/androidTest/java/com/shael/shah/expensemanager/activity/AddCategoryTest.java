package com.shael.shah.expensemanager.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.shael.shah.expensemanager.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddCategoryTest {

    @Rule
    public ActivityTestRule<LandingActivity> mActivityTestRule = new ActivityTestRule<>(LandingActivity.class);

    @Test
    public void addCategoryTest() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.add_expense), withContentDescription("Add Expense"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.addCategoryTextView), withText("Add Category...")));
        textView.perform(scrollTo(), click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.categoryNameEditText), isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.categoryNameEditText), isDisplayed()));
        editText2.perform(replaceText("Food"), closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(android.R.id.button1), withText("Add")));
        button.perform(scrollTo(), click());

        ViewInteraction radioButton = onView(
                allOf(withId(R.id.categoryRadioButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scrollLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        radioButton.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withText("Cancel"),
                        withParent(withId(R.id.toolbarLinearLayout)),
                        isDisplayed()));
        button2.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.add_expense), withContentDescription("Add Expense"), isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction radioButton2 = onView(
                allOf(withId(R.id.categoryRadioButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scrollLinearLayout),
                                        0),
                                1),
                        isDisplayed()));
        radioButton2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
