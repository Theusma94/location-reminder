package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()

        localDataSource =
                RemindersLocalRepository(
                        database.reminderDao(),
                        Dispatchers.Main
                )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrieverReminder() = runBlocking {
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                0.0,
                0.0)

        localDataSource.saveReminder(reminder)

        val reminderFromDataSource = localDataSource.getReminder(reminder.id)

        assertThat(reminderFromDataSource, not(nullValue()))
        reminderFromDataSource as Result.Success
        assertThat(reminderFromDataSource.data.title, `is`(reminder.title))
        assertThat(reminderFromDataSource.data.description, `is`(reminder.description))
        assertThat(reminderFromDataSource.data.location, `is`(reminder.location))
        assertThat(reminderFromDataSource.data.latitude, `is`(reminder.latitude))
        assertThat(reminderFromDataSource.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveReminders_retrieveAll() = runBlocking {
        val reminder1 = ReminderDTO(
                "title1",
                "description1",
                "location1",
                0.0,
                0.0)
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                0.0,
                0.0)
        val reminder3 = ReminderDTO(
                "title3",
                "description3",
                "location3",
                0.0,
                0.0)

        localDataSource.saveReminder(reminder1)
        localDataSource.saveReminder(reminder2)
        localDataSource.saveReminder(reminder3)

        val reminders = localDataSource.getReminders() as Result.Success
        assertThat(reminders, not(nullValue()))
        assertThat(reminders.data.size, `is`(3))
        assertThat(reminders.data[0], `is`(reminder1))
        assertThat(reminders.data[1], `is`(reminder2))
        assertThat(reminders.data[2], `is`(reminder3))
    }

    @Test
    fun deleteAllRemindersAfterInsert() = runBlocking {
        val reminder1 = ReminderDTO(
                "title1",
                "description1",
                "location1",
                0.0,
                0.0)
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                0.0,
                0.0)
        val reminder3 = ReminderDTO(
                "title3",
                "description3",
                "location3",
                0.0,
                0.0)

        localDataSource.saveReminder(reminder1)
        localDataSource.saveReminder(reminder2)
        localDataSource.saveReminder(reminder3)

        val reminders = localDataSource.getReminders() as Result.Success
        assertThat(reminders, not(nullValue()))
        assertThat(reminders.data.size, `is`(3))
        assertThat(reminders.data[0], `is`(reminder1))
        assertThat(reminders.data[1], `is`(reminder2))
        assertThat(reminders.data[2], `is`(reminder3))

        localDataSource.deleteAllReminders()
        val remindersAfterDelete = localDataSource.getReminders() as Result.Success
        assertThat(remindersAfterDelete, not(nullValue()))
        assertThat(remindersAfterDelete.data.size, `is`(0))
    }

}