package com.mosin.mynotes.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.mosin.mynotes.R
import com.mosin.mynotes.databinding.ActivityNoteBinding
import com.mosin.mynotes.model.note.Color
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.ui.base.BaseActivity
import com.mosin.mynotes.ui.main.format
import com.mosin.mynotes.ui.main.getColorInt
import com.mosin.mynotes.viewModel.NoteViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

private const val SAVE_DELAY = 1000L

class NoteActivity : BaseActivity<NoteViewState.Data, NoteViewState>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    private var note: Note? = null
    private var color: Color = Color.WHITE

    override val viewModel: NoteViewModel by viewModel()
    override val layoutRes: Int = R.layout.activity_note
    override val ui: ActivityNoteBinding
            by lazy { ActivityNoteBinding.inflate(layoutInflater) }

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            triggerSaveNote()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(ui.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        onClickSaveBtnBehavior()
        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        } ?: run {
            supportActionBar?.title = getString(R.string.new_note_title)
            ui.toolbar.setBackgroundColor(R.drawable.background_card_view)
        }

        ui.colorPicker.onColorClickListener = {
            color = it
            setToolBarColor(it)
            triggerSaveNote()
        }

        setEditListener()
    }

    private fun initViews() {
        note?.run {
            removeEditListener()
            ui.progressBar.visibility = View.INVISIBLE
            if (title != ui.titleEt.text.toString()) {
                ui.titleEt.setText(title)
            }
            if (note != ui.bodyEt.text.toString()) {
                ui.bodyEt.setText(note)
            }
            setEditListener()

            supportActionBar?.title = getString(R.string.text_changed) + " " + lastChange.format()
            setToolBarColor(color)
        }
    }

    private fun setToolBarColor(color: Color) {
        ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    override

    fun onCreateOptionsMenu(menu: Menu?): Boolean =
            menuInflater.inflate(R.menu.menu_note, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun togglePalette() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            ui.colorPicker.open()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog_message)
                .setNegativeButton(R.string.cancel_btn_title) { dialog, _ ->
                    dialog.dismiss()
                    setProgressBarInvisible()
                }
                .setPositiveButton(R.string.ok_bth_title) { _, _ ->
                    viewModel.deleteNote()
                    if (note?.id != null)  setProgressBarVisible()
                }
                .show()
    }

    private fun onClickSaveBtnBehavior() {
        ui.saveBtn.setOnClickListener(View.OnClickListener { super.onBackPressed() })
    }

    private fun createNewNote(): Note = Note(
            UUID.randomUUID().toString(),
            ui.titleEt.text.toString(),
            ui.bodyEt.text.toString(),
            color = color)

    private fun triggerSaveNote() {
        if (ui.titleEt.text == null || ui.titleEt.text!!.length < 3) return

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(
                    title = ui.titleEt.text.toString(),
                    note = ui.bodyEt.text.toString(),
                    color = color,
                    lastChange = Date()
            ) ?: createNewNote()

            note?.let { note ->
                viewModel.saveChanges(note)
            }
        }, SAVE_DELAY)
    }

    override fun renderData(data: NoteViewState.Data) {
        if (data.isDeleted) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initViews()
    }

    private fun setEditListener() {
        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    private fun removeEditListener() {
        ui.titleEt.removeTextChangedListener(textChangeListener)
        ui.bodyEt.removeTextChangedListener(textChangeListener)
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }

    private fun setProgressBarVisible() {
        ui.progressBar.visibility = View.VISIBLE
    }

    private fun setProgressBarInvisible() {
        ui.progressBar.visibility = View.INVISIBLE
    }

}
