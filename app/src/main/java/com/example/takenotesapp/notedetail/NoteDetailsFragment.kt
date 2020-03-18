package com.example.takenotesapp.notedetail


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.takenotesapp.R
import com.example.takenotesapp.database.NoteDatabase
import com.example.takenotesapp.databinding.FragmentNoteDetailsBinding
import com.example.takenotesapp.listnotes.ListNoteAdapter

/**
 * A simple [Fragment] subclass.
 */
class NoteDetailsFragment : Fragment() {
    lateinit var args: NoteDetailsFragmentArgs
    lateinit var binding: FragmentNoteDetailsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentNoteDetailsBinding>(inflater,
            R.layout.fragment_note_details,container,false)
        arguments?.let {
                args = NoteDetailsFragmentArgs.fromBundle(it)
        }
        val key = args.noteKey
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = viewLifecycleOwner
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val viewModelFactory = NoteDetailsVMFactory(dataSource,application,key)
        val viewModel = ViewModelProviders.of(this,viewModelFactory).get(NoteDetailVM::class.java)
        binding.noteDetailsViewModel = viewModel

        viewModel.navigate.observe(viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(
                    NoteDetailsFragmentDirections
                        .actionNoteDetailsFragmentToListNotesFragment())
                viewModel.onDoneNavigating()
            }
        })

        viewModel.enable.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.fabNoteDetails.setImageDrawable(resources.getDrawable(R.drawable.ic_check_black_24dp))
            }
            else
            {
                binding.fabNoteDetails.setImageDrawable(resources.getDrawable(R.drawable.ic_mode_edit_black_24dp))
            }
        })

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
        binding.toolbar.title = "Note details"
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_note_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> binding.noteDetailsViewModel?.onDelete()
        }
        return true
    }

}
