package com.example.takenotesapp.listnotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotesapp.database.Note
import com.example.takenotesapp.databinding.RecyclerRowNoteBinding

class ListNoteAdapter(val clickListener: NoteListener): ListAdapter<Note,ListNoteAdapter.ViewHolder>(NoteDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(val binding: RecyclerRowNoteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Note, clickListener: NoteListener){
            binding.note = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerRowNoteBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class NoteDiffCallback : DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}

class NoteListener(val clickListener: (noteId: Long) -> Unit){
    fun onClick(note: Note) = clickListener(note.noteId)
}



