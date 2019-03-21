package com.rent24.driver.components.snaps.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rent24.driver.R
import com.rent24.driver.databinding.FragmentCategoryListDialogBinding

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CategoryListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [CategoryListDialogFragment.Listener].
 */
class CategoryListDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCategoryListDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_category_list_dialog, container, false)
        binding = FragmentCategoryListDialogBinding.bind(layout)
        binding.lifecycleOwner = this
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
    }

    companion object {

        fun newInstance(): CategoryListDialogFragment = CategoryListDialogFragment()
    }
}
