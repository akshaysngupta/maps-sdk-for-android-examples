/*
 * Copyright (c) 2015-2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.tomtom.online.sdk.samples.ktx.cases

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tomtom.online.sdk.common.location.LatLng
import com.tomtom.online.sdk.map.MapConstants
import com.tomtom.online.sdk.samples.IdlingResourceHelper
import com.tomtom.online.sdk.samples.ktx.MainViewModel
import com.tomtom.online.sdk.samples.ktx.MapAction
import com.tomtom.online.sdk.samples.ktx.dialogs.ProgressFragment
import com.tomtom.online.sdk.samples.ktx.utils.routes.Locations

abstract class ExampleFragment : Fragment(), ExampleLifecycle, OnBackPressedCallback {

    lateinit var mainViewModel: MainViewModel
    lateinit var exampleViewModel: ExampleViewModel

    private val idlingResourceHelper = IdlingResourceHelper(ProgressFragment.PROGRESS_FRAGMENT_TAG)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Handle back press for proper navigation
        activity!!.addOnBackPressedCallback(this)

        //Example view model
        exampleViewModel = ViewModelProviders.of(this).get(ExampleViewModel::class.java)

        //Shared view model
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mainViewModel.applyAboutButtonVisibility(false)

        //Restore if required
        if (!isRestored()) {
            onExampleStarted()
        }
    }

    override fun onDestroyView() {
        activity!!.removeOnBackPressedCallback(this)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        exampleViewModel.isRestored = true
    }

    override fun handleOnBackPressed(): Boolean {
        mainViewModel.applyAboutButtonVisibility(true)
        onExampleEnded()
        return false
    }

    private fun isRestored(): Boolean {
        return exampleViewModel.isRestored
    }

    override fun onExampleStarted() {
    }

    override fun onExampleEnded() {
    }

    fun centerOnLocation(location: LatLng = Locations.AMSTERDAM,
                         zoomLevel: Double = DEFAULT_MAP_ZOOM_LEVEL_FOR_EXAMPLE,
                         bearing: Int = MapConstants.ORIENTATION_NORTH) {
        mainViewModel.applyOnMap(MapAction {
            let { tomtomMap ->
                //tag::doc_map_center_on_amsterdam[]
                tomtomMap.centerOn(location.latitude,
                    location.longitude,
                    zoomLevel,
                    bearing)
                //end::doc_map_center_on_amsterdam[]
            }
        })
    }

    open fun showLoading() {
        idlingResourceHelper.startIdling()
        fragmentManager?.let { fm ->
            ProgressFragment.show(fm)
        }
    }

    open fun hideLoading() {
        fragmentManager?.let { fm ->
            ProgressFragment.hide(fm)
        }
        idlingResourceHelper.stopIdling()
    }

    open fun showError(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val DEFAULT_MAP_ZOOM_LEVEL_FOR_EXAMPLE = 10.0
    }

}