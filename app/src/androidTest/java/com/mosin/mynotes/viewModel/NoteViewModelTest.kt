package com.mosin.mynotes.viewModel

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.mosin.mynotes.R
import com.mosin.mynotes.model.note.Color
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.ui.main.getColorInt
import com.mosin.mynotes.ui.note.NoteActivity
import com.mosin.mynotes.ui.note.NoteViewState
import io.mockk.*
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class NoteViewModelTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val viewModel: NoteViewModel = spyk(NoteViewModel(mockk()))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note("333", "title", "body")

    @Before
    fun setUp() {
        startKoin {
            modules()
        }

        loadKoinModules(
                module {
                    viewModel { viewModel }
                })
        every { viewModel.getViewState() } returns viewStateLiveData
        every { viewModel.loadNote(any()) } just runs
        every { viewModel.saveChanges(any()) } just runs
        every { viewModel.deleteNote() } just runs

        activityTestRule.launchActivity(Intent().apply {
            putExtra("NoteActivity.extra.NOTE", testNote.id)
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun should_show_color_picker() {
        onView(withId(R.id.palette)).perform(click())

        onView(withId(R.id.color_picker)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun should_hide_color_picker() {
        onView(withId(R.id.palette)).perform(click()).perform(click())

        onView(withId(R.id.color_picker)).check(matches(not(isDisplayed())))
    }

    @Test
    fun should_set_toolbar_color() {
        onView(withId(R.id.palette)).perform(click())
        onView(withTagValue(`is`(Color.BLUE))).perform(click())

        val colorInt = Color.BLUE.getColorInt(activityTestRule.activity)

        onView(withId(R.id.toolbar)).check { view, _ ->
            assertTrue("toolbar background color does not match",
                    (view.background as? ColorDrawable)?.color == colorInt)
        }
    }

    @Test
    fun should_call_viewModel_loadNote() {
        verify(exactly = 1) { viewModel.loadNote(testNote.id) }
    }

    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(note = testNote)))

        onView(withId(R.id.titleEt)).check(matches(withText(testNote.title)))
        onView(withId(R.id.bodyEt)).check(matches(withText(testNote.note)))
    }

    @Test
    fun should_back_note() {
        activityTestRule.launchActivity(null)
        onView(withId(R.id.titleEt)).perform(typeText(testNote.title))
    }

    @Test
    fun should_call_saveNote() {
        onView(withId(R.id.titleEt)).perform(typeText(testNote.title))
        verify(timeout = 1000) { viewModel.saveChanges(any()) }
    }

    @Test
    fun should_call_deleteNote() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.delete_menu_title)).perform(click())
        onView(withText(R.string.ok_bth_title)).perform(click())
        verify { viewModel.deleteNote() }
    }

    @Test
    fun should_save_note() {
        activityTestRule.launchActivity(null)
        onView(withId(R.id.palette)).perform(click())
        onView(withTagValue(`is`(Color.BLUE))).perform(click())
        val colorInt = Color.BLUE.getColorInt(activityTestRule.activity)
        onView(withId(R.id.toolbar)).check { view, _ ->
            assertTrue("toolbar background color does not match",
                    (view.background as? ColorDrawable)?.color == colorInt)}
        onView(withId(R.id.titleEt)).perform(typeText(testNote.title))
        onView(withText(R.string.text_btn_save)).perform(click())
    }
}