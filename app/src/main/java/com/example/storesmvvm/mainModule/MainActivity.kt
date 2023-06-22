package com.example.storesmvvm.mainModule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storesmvvm.editModule.EditStoreFragment
import com.example.storesmvvm.common.entities.StoreEntity
import com.example.storesmvvm.editModule.viewModel.EditStoreViewModel
import com.example.storesmvvm.mainModule.adapter.OnClickListener
import com.example.storesmvvm.mainModule.adapter.StoreAdapter
import com.example.storesmvvm.mainModule.viewModel.MainViewModel
import com.examples.stores.R
import com.examples.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * View from MainActivity (MVVM)
 * This class controls what users can see and interact with it on mainActivity
 */
class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { launchEditFragment() }

        setupViewModel()
        setupRecyclerView()
    }


    /**
     * Connect the View with the ViewModel.
     */
    private fun setupViewModel() {

        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        //add an observer to the model, so when the model change, it will refresh
        mMainViewModel.getStores().observe(this) { storesList ->
            mAdapter.setStores(storesList)
        }
        //initialize the editStoreViewModel
        mEditStoreViewModel = ViewModelProvider(this)[EditStoreViewModel::class.java]
        //hide or show the fab
        mEditStoreViewModel.getshowFab().observe(this) { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }

        //notify the adapter when a store is added or updated
        mEditStoreViewModel.getStoreSelected().observe(this){store ->
            mAdapter.add(store)

        }


    }

    /**
     * @param storeEntity Store to show on fragment. If user select a store (clicking on the
     * recyclerView) pass it to the fragment, else create a new object without parameters.
     * Launch the EditStoreFragment.
     * Hide the FloatingActionButton
     */
    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {

        mEditStoreViewModel.setshowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }


    /**
     * Set up the recyclerView. Attach it to the adapter and give to it the layout
     */
    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        //getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }



    /*
    * OnClickListener
    * */
    override fun onClick(storeEntity: StoreEntity) {

        launchEditFragment(storeEntity)
    }

    /**
     * Change the favorite status
     */
    override fun onFavoriteStore(storeEntity: StoreEntity) {

        mMainViewModel.updateStore(storeEntity)

    }

    /**
     * Override the onDelete method from the interface
     */
    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        //show an alertdialog with multiple options
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)

                    1 -> dial(storeEntity.phone)

                    2 -> goToWebsite(storeEntity.website)
                }
            }
            .show()
    }

    /**
     * Show an alertDialog asking for delete a store.
     */
    private fun confirmDelete(storeEntity: StoreEntity) {

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mMainViewModel.deleteStore(storeEntity)

            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()

    }

    /**
     * Open the dial app from the device to call the store
     * @param the phone's store to call it
     */
    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        startIntent(callIntent)
    }

    /**
     * Open a browser to show the store's web
     * @param website the url from the store
     */
    private fun goToWebsite(website: String){
        if (website.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }

            startIntent(websiteIntent)
        }
    }


    /**
     * Start a intent to open other apps, (dial or browser)
     * if the device has not any app to open this intent, show a message
     * "There is not any application compatible with this option"
     * @param intent the intent to open
     */
    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }


}