package com.mosin.mynotes.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.mosin.mynotes.R
import com.mosin.mynotes.databinding.ActivityMainBinding
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.viewModel.MainViewModel


class MainActivity : BaseActivity<List<Note>?, MainViewState, ActivityMainBinding>(ActivityMainBinding::inflate) {

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override val layoutRes: Int = R.layout.activity_main
    private lateinit var adapter: ViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        adapter = ViewAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }
        })
        binding.mainRecycler.adapter = adapter
        binding.fab.setOnClickListener { openNoteScreen(null) }
    }

    override fun renderData(data: List<Note>?) {
        if (data == null) return
        adapter.notes = data
    }

    private fun openNoteScreen(note: Note?) {
        val intent = NoteActivity.getStartIntent(this, note?.id)
        startActivity(intent)
    }
}
