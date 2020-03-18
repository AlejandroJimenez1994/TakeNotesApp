package com.example.takenotesapp.listnotes



import android.app.Application
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.takenotesapp.database.NoteDatabase
import com.example.takenotesapp.databinding.FragmentListNotesBinding

import com.example.takenotesapp.R
import com.example.takenotesapp.database.Note

/**
 * A simple [Fragment] subclass.
 */
class ListNotesFragment : Fragment() {
    lateinit var binding: FragmentListNotesBinding
    lateinit var application: Application
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_list_notes,container,false)

        application = requireNotNull(this.activity).application
        binding.lifecycleOwner = this
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val viewModelFactory = ListNotesViewModelFactory(dataSource)

        val listNotesVM = ViewModelProvider(this,viewModelFactory).get(ListNotesVM::class.java)
        binding.listNotesViewModel = listNotesVM

        val adapter = ListNoteAdapter(NoteListener { noteId ->
            listNotesVM.onNoteClicked(noteId)
        })
        var mDivider = DividerItemDecoration(application, 1)
        binding.recyclerNotesMain.addItemDecoration(mDivider)
        binding.recyclerNotesMain.adapter = adapter

        listNotesVM.changeLayout.observe(viewLifecycleOwner, Observer {
            setGrid(it)
        })

        listNotesVM.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(listNotesVM.filter.value){
                    Filter.ALL -> adapter.submitList(listNotesVM.notes.value?.sortedWith(compareBy(Note::lastEdited))?.reversed())
                    Filter.CREATED -> adapter.submitList(it.sortedWith(compareBy(Note::dateCreated)))
                    Filter.ALPHABETICAL -> adapter.submitList(it.sortedWith(compareBy(Note::title)))
                }
            }
        })

        listNotesVM.filter.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    Filter.ALL -> adapter.submitList(listNotesVM.notes.value?.sortedWith(compareBy(Note::lastEdited))?.reversed())
                    Filter.CREATED -> adapter.submitList(listNotesVM.notes.value?.sortedWith(compareBy(Note::dateCreated)))
                    Filter.ALPHABETICAL -> adapter.submitList(listNotesVM.notes.value?.sortedWith(compareBy(Note::title)))
                }
            }
        })

        listNotesVM.search.observe(viewLifecycleOwner, Observer {
            listNotesVM.onSearch()
        })

        listNotesVM.queryNotes.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        listNotesVM.navigateToNoteDetail.observe(viewLifecycleOwner, Observer { note ->
            note?.let {
                this.findNavController().navigate(
                    ListNotesFragmentDirections
                        .actionListNotesFragmentToNoteDetailsFragment(it))
                listNotesVM.onNoteDetailNavigated()
            }
        })

        binding.fabAddNoteMain.setOnClickListener {
            findNavController().navigate(
                ListNotesFragmentDirections
                    .actionListNotesFragmentToAddNoteFragment()
            )
        }


        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchMenu = menu.findItem(R.id.action_search)
        val searchView = searchMenu.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.listNotesViewModel?.loadQuery(newText)
                return false
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> binding.listNotesViewModel?.onClear()
                R.id.action_viewChange -> binding.listNotesViewModel?.onLayoutChange()
                R.id.action_filter -> setFilter(item)
            }
        if(binding.listNotesViewModel?.changeLayout?.value == Direction.GRID && item.itemId == R.id.action_viewChange){

            item.icon = resources.getDrawable(R.drawable.ic_view_list_black_24dp)
        }
        if(binding.listNotesViewModel?.changeLayout?.value == Direction.LIST && item.itemId == R.id.action_viewChange){

            item.icon = resources.getDrawable(R.drawable.ic_grid_on_black_24dp)
        }
        if(item.itemId == R.id.action_filter)
        when(binding.listNotesViewModel?.filter?.value){
            Filter.ALL -> item.title = "Filter: Last edited"
            Filter.CREATED -> item.title = "Filter: Created"
            Filter.ALPHABETICAL -> item.title = "Filter: Alphabetical"
        }

        return true
    }

    private fun setGrid(direction: Direction){
        if(direction == Direction.GRID){
            val manager = GridLayoutManager(activity, 3)
            binding.recyclerNotesMain.layoutManager = manager
        }
        else
        {
            val manager = LinearLayoutManager(activity)
            binding.recyclerNotesMain.layoutManager = manager
        }
    }

    private fun setFilter(item: MenuItem) {
        binding.listNotesViewModel?.onFilter()
        when(binding.listNotesViewModel?.filter?.value){
            Filter.ALL -> item.title = "Filter: Last edited"
            Filter.CREATED -> item.title = "Filter: Created"
            Filter.ALPHABETICAL -> item.title = "Filter: Alphabetical"
        }
    }


}


