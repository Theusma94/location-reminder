package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                0.0,
                0.0)

        database.reminderDao().saveReminder(reminder)

        val reminderFromDb = database.reminderDao().getReminderById(reminder.id)
        assertThat(reminderFromDb, not(nullValue()))
        assertThat(reminderFromDb?.title, `is`(reminder.title))
        assertThat(reminderFromDb?.description, `is`(reminder.description))
        assertThat(reminderFromDb?.location, `is`(reminder.location))
        assertThat(reminderFromDb?.latitude, `is`(reminder.latitude))
        assertThat(reminderFromDb?.longitude, `is`(reminder.longitude))
    }

    @Test
    fun insertRemindersAndGetAll() = runBlockingTest {
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

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, not(nullValue()))
        assertThat(remindersList.size, `is`(3))
        assertThat(remindersList[0], `is`(reminder1))
        assertThat(remindersList[1], `is`(reminder2))
        assertThat(remindersList[2], `is`(reminder3))
    }
}