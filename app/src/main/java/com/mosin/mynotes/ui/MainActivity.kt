package com.mosin.mynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mosin.mynotes.databinding.ActivityMainBinding
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    lateinit var ui: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var mainAdapter: ViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainAdapter = ViewAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteScreen(note)
            }

        })
        ui.mainRecycler.adapter = mainAdapter

        viewModel.viewState().observe(this, Observer<MainViewState> { state ->
            state?.let { mainAdapter.notes = state.notes }
        })

        ui.fab.setOnClickListener { openNoteScreen() }
    }

    private fun openNoteScreen(note: Note? = null){
        startActivity(NoteActivity.getStartIntent(this, note))
    }
}