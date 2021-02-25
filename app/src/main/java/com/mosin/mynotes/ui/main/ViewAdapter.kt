package com.mosin.mynotes.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mosin.mynotes.R
import com.mosin.mynotes.databinding.ItemNoteBinding
import com.mosin.mynotes.model.note.Note

interface OnItemClickListener {
    fun onItemClick(note: Note)
}

class ViewAdapter(private val OnItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<ViewAdapter.NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ui: ItemNoteBinding = ItemNoteBinding.bind(itemView)

        fun bind(note: Note) {
            ui.title.text = note.title
            ui.body.text = note.note

            ui.cardViewContainer.setCardBackgroundColor(note.color.getColorInt(itemView.context))
            itemView.setOnClickListener { (OnItemClickListener.onItemClick(note)) }
        }
    }
}