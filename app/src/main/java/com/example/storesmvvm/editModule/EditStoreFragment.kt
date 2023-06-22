package com.example.storesmvvm.editModule

import com.examples.stores.R
import com.examples.stores.databinding.FragmentEditStoreBinding
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.storesmvvm.common.entities.StoreEntity
import com.example.storesmvvm.editModule.viewModel.EditStoreViewModel
import com.example.storesmvvm.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout


/**
 * View from EditStoreFragment (MVVM)
 * This class controls what users can see and interact with it
 */
class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //MVVM
        setupViewModel()

        setupTextFields()
    }

    /**
     * Connect the View with the ViewModel.
     * Check if the view comes from the add button or from the recyclerView
     * If comes from a click on recyclerView, would add the store data into the textfields
     */
    private fun setupViewModel() {

        //Observe the store from the ViewModel
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) { store ->
            mStoreEntity = store
            //if id is not 0, means that storeEntity is a created store and want to update
            if (store.id != 0L) {
                mIsEditMode = true
                setUiStore(store)
            } else { // else means that it is creating a store
                mIsEditMode = false

            }

            setupActionBar()
        }

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner){result ->

            hideKeyboard()

            when(result){
                //result = Long means is the id when a store is created
                is Long -> {

                    mStoreEntity.id = result
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)

                    Toast.makeText(mActivity, "Store added successfully", Toast.LENGTH_SHORT).show()

                    //close this activity and back to the last activity (main activity in this case)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                //result = store means is the store to update
                is StoreEntity -> {

                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(mActivity, "Store updated successfully", Toast.LENGTH_SHORT).show()

                }
            }

        }

    }


    /**
     *Configure the ActionNavBar, add a title according to what want to do
     */
    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_store_title_edit)
                                            else getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    /**
     * If isEditingMode, add the store's data to the textfields
     */
    private fun setupTextFields() {
        with(mBinding) {
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    /**
     * Load the store's image from the url
     */
    private fun loadImage(url: String){

        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)

    }



    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Configure the buttons from the NavBar
     **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            //if save button is pressed, check if the required fields are filled and are valid
            R.id.action_save -> {
                if(validateFields(mBinding.tilPhotoUrl,mBinding.tilPhone, mBinding.tilName)) {

                    with(mStoreEntity){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if(mIsEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)


                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Validate if a textField is not empty.
     * If so, show a error message
     */
    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }

        if (!isValid) Snackbar.make(mBinding.root,
                R.string.edit_store_message_valid,
                Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }

        if (mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }

        if (mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    /**
     * Hide the keyboard
     */
    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setshowFab(true)
        mEditStoreViewModel.setResult(Any())

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}