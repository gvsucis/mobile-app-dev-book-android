package edu.gvsu.cis.traxy.view

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import edu.gvsu.cis.traxy.R
import edu.gvsu.cis.traxy.viewmodel.UserDataViewModel
import kotlinx.android.synthetic.main.fragment_trip_maps.*

class TripMapsFragment : Fragment() {

    private val viewmodel by activityViewModels<UserDataViewModel>()
    private var gMap: GoogleMap? = null
    private var allMarkers = mutableListOf<MarkerOptions>()
    private var mapBound = LatLngBounds.Builder()

    private val callback = OnMapReadyCallback {
        gMap = it
        with(gMap!!.uiSettings) {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isScrollGesturesEnabledDuringRotateOrZoom = true
        }
        if (allMarkers.size > 0)
            refreshMarkers(gMap!!, allMarkers)
        gMap!!.setOnInfoWindowClickListener {
            val action1 = JournalPagerFragmentDirections.actionEditJournal(it.title)
            val ctrl = Navigation.findNavController(requireView())
            ctrl.navigate(action1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_trip_maps, container, false)
    }

    private fun refreshMarkers(theMap: GoogleMap, theJournal: List<MarkerOptions>) {
        theMap.clear()
        theJournal.forEach {
            mapBound.include(it.position)
            theMap.addMarker(it)
        }

        val screenWidth = requireActivity().resources.displayMetrics.widthPixels
        val screenHeight = requireActivity().resources.displayMetrics.heightPixels
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBound.build(),
            screenWidth, screenHeight, 128)
        theMap.animateCamera(cameraUpdate)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewmodel.remoteJournals.observe(viewLifecycleOwner, Observer {
            it.forEach {
                val pos = LatLng(it.lat, it.lng)
                allMarkers.add(MarkerOptions().position(pos).title(it.name))
            }
            if (gMap != null)
                refreshMarkers(gMap!!, allMarkers)

        })
    }
}