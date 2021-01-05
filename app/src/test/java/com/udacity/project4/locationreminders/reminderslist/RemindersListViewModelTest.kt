package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineScopeRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private val testReminder1 = ReminderDTO(
            "title1",
            "description1",
            "location1",
            00.00,
            00.00
    )
    private val testReminder2 = ReminderDTO(
            "title2",
            "description2",
            "location2",
            00.00,
            00.00
    )
    private val testReminder3 = ReminderDTO(
            "title3",
            "description3",
            "location3",
            00.00,
            00.00
    )
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersListViewModel: RemindersListViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    private val fakeDataSource = FakeDataSource()

    @Before
    fun setupSaveReminderViewModel() {
        remindersListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun whenGetEmptyList_ExpectError() = mainCoroutineScopeRule.runBlockingTest {
        fakeDataSource.deleteAllReminders()
        fakeDataSource.setReturnError(true)

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`(FakeDataSource.REMINDERS_NOT_FOUND))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun whenSetReminders_ExpectSuccess() = mainCoroutineScopeRule.runBlockingTest {
        fakeDataSource.deleteAllReminders()
        fakeDataSource.saveReminder(testReminder1)
        fakeDataSource.saveReminder(testReminder2)
        fakeDataSource.saveReminder(testReminder3)

        remindersListViewModel.loadReminders()

        val remindersList = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(remindersList.size, `is`(3))
        assertThat(remindersList[0].title, `is`(testReminder1.title))
        assertThat(remindersList[2].title, `is`(testReminder3.title))
    }

    @Test
    fun loadReminders_checkLoading() {
        mainCoroutineScopeRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineScopeRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

}