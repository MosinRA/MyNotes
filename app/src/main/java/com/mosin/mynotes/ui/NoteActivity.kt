package com.mosin.mynotes.ui

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
import com.mosin.mynotes.model.Color
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.viewModel.NoteViewModel
import java.util.*

private const val SAVE_DELAY = 1000L

class NoteActivity : BaseActivity<Note?, NoteViewState, ActivityNoteBinding>(ActivityNoteBinding::inflate) {

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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        }

        if (noteId == null) {
            supportActionBar?.title = getString(R.string.new_note_title)
            initViews()
        } else {
            //не получилось разобраться с подтягиванием даты =(
            supportActionBar?.title = getString(R.string.text_changed)
            initViews()
        }
    }

    private fun initViews() {
        binding.titleEt.setText(note?.title ?: "")
        binding.bodyEt.setText(note?.note ?: "")

        val color = when (note?.color) {
            Color.WHITE -> R.color.color_white
            Color.VIOLET -> R.color.color_violet
            Color.YELLOW -> R.color.color_yellow
            Color.RED -> R.color.color_red
            Color.PINK -> R.color.color_pink
            Color.GREEN -> R.color.color_green
            Color.BLUE -> R.color.color_blue
            else -> R.color.color_white
        }
        binding.toolbar.setBackgroundColor(resources.getColor(color))
        binding.titleEt.addTextChangedListener(textChangeListener)
        binding.bodyEt.addTextChangedListener(textChangeListener)
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
            binding.titleEt.text.toString(),
            binding.bodyEt.text.toString())

    private fun triggerSaveNote() {
        if (binding.titleEt.text == null || binding.titleEt.text!!.length < 3) return

        Handler(Looper.getMainLooper()).postDelayed({
            note = note?.copy(title = binding.titleEt.text.toString(),
                    note = binding.bodyEt.text.toString(),
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
