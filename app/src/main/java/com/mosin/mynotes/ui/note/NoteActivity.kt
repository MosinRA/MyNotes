package com.mosin.mynotes.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.mosin.mynotes.R
import com.mosin.mynotes.databinding.ActivityNoteBinding
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.ui.base.BaseActivity
import com.mosin.mynotes.ui.main.format
import com.mosin.mynotes.ui.main.getColorInt
import com.mosin.mynotes.viewModel.NoteViewModel
import java.util.*

private const val SAVE_DELAY = 1000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        const val EXTRA_NOTE = "NoteActivity.extra.NOTE"

        fun getStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    private var note: Note? = null
    override val viewModel: NoteViewModel by lazy { ViewModelProvider(this).get(NoteViewModel::class.java) }
    override val layoutRes: Int = R.layout.activity_note
    override val ui: ActivityNoteBinding
            by lazy { ActivityNoteBinding.inflate(layoutInflater) }

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            triggerSaveNote()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // do nothing
        }

        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(ui.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        } ?: kotlin.run {
            supportActionBar?.title = getString(R.string.new_note_title)
            ui.toolbar.setBackgroundColor(R.drawable.background_card_view)
        }

        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    private fun initViews() {
        note?.run {
            ui.toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
            ui.titleEt.setText(title)
            ui.bodyEt.setText(note)
            supportActionBar?.title = getString(R.string.text_changed) + " " + lastChange.format()
        }
        ui.titleEt.addTextChangedListener(textChangeListener)
        ui.bodyEt.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun createNewNote(): Note = Note(
            UUID.randomUUID().toString(),
            ui.titleEt.text.toString(),
            ui.bodyEt.text.toString())

    private fun triggerSaveNote() {
        if (ui.titleEt.text == null || ui.titleEt.text!!.length < 3) return

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(title = ui.titleEt.text.toString(),
                    note = ui.bodyEt.text.toString(),
                    lastChange = Date()
            ) ?: createNewNote()

            note?.let { note ->
                viewModel.saveChanges(note)
            }
        }, SAVE_DELAY)
    }

    override fun renderData(data: Note?) {
        this.note = data
        initViews()
    }
}
