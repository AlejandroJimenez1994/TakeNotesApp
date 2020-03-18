package com.example.takenotesapp.addnote


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_INTENT
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.takenotesapp.BuildConfig
import com.example.takenotesapp.R
import com.example.takenotesapp.database.NoteDatabase
import com.example.takenotesapp.databinding.FragmentAddNoteBinding
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class AddNoteFragment : Fragment() {
    lateinit var binding: FragmentAddNoteBinding
    lateinit var application : Application
    private var selectedPhotoPath: Uri? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentAddNoteBinding>(inflater,
            R.layout.fragment_add_note,container,false)

        application = requireNotNull(this.activity).application
        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val viewModelFactory = AddNoteViewModelFactory(dataSource,application)
        val viewModelAddNote = ViewModelProviders.of(this,viewModelFactory).get(AddNoteVM::class.java)
        binding.addNoteViewModel = viewModelAddNote
        binding.lifecycleOwner = this
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(R.string.add_note)
        setHasOptionsMenu(true)

        viewModelAddNote.navigateToNoteList.observe(viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(
                    AddNoteFragmentDirections
                        .actionAddNoteFragmentToListNotesFragment()
                )
                /// Hide the keyboard.
                val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                viewModelAddNote.doneNavigating()
            }
            else
            {
                /// Hide the keyboard.
                val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        })

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
        binding.toolbar.title = "Add a note!"
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_photo -> selectImageInAlbum()
        }
        return true
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Select a Picture"), REQUEST_PICK_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK){
            binding.addNoteViewModel?.setPhoto(data?.data)
            Log.e("URI",binding.addNoteViewModel?.getPhoto()?.value.toString())
        }
    }
    companion object {
        private const val REQUEST_PICK_PHOTO = 1
    }

}
