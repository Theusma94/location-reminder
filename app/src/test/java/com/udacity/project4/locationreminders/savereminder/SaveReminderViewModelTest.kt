package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineScopeRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    private val fakeDataSource = FakeDataSource()

    @Before
    fun setupSaveReminderViewModel() {
        saveReminderViewModel = SaveReminderViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun whenSaveReminder_ExpectSuccess() = mainCoroutineScopeRule.runBlockingTest {
        val reminderDataItem = ReminderDataItem(
                null,
                null,
                null,
                null,
                null,
                "reminder_id"
        )
        saveReminderViewModel.saveReminder(reminderDataItem)

        val resultReminderFromDataSource = fakeDataSource.getReminder(reminderDataItem.id) as Result.Success
        val resultData = resultReminderFromDataSource.data

        assertThat(resultData.id, `is`(reminderDataItem.id))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

    @Test
    fun saveReminder_loading() {
        mainCoroutineScopeRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(ReminderDataItem(null, null, null, null, null))

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineScopeRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun whenReminderItemMissingTitle_ExcepectError() {
        val reminderToTest = ReminderDataItem(
                null,
                "description",
                "location",
                -00.00,
                -00.00
        )

        val check = saveReminderViewModel.validateEnteredData(reminderToTest)

        assertThat(check, `is`(false))
    }

    @Test
    fun whenReminderItemMissingLocation_ExcepectError() {
        val reminderToTest = ReminderDataItem(
                "title",
                "description",
                null,
                -00.00,
                -00.00
        )

        val check = saveReminderViewModel.validateEnteredData(reminderToTest)

        assertThat(check, `is`(false))
    }

    @Test
    fun whenReminderItemWithMandatory_ExcepectSuccess() {
        val reminderToTest = ReminderDataItem(
                "title",
                "description",
                "location",
                -00.00,
                -00.00
        )

        val check = saveReminderViewModel.validateEnteredData(reminderToTest)

        assertThat(check, `is`(true))
    }

}