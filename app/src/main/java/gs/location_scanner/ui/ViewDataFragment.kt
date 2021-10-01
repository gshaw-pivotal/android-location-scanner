package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.MainActivity
import gs.location_scanner.R
import gs.location_scanner.databinding.ViewDataFragmentBinding
import gs.location_scanner.service.FileService

class ViewDataFragment  : Fragment() {

    private val locationPreviewListAdaptor = LocationPreviewListAdaptor()

    private val fileService = FileService()

    companion object {
        const val TAG: String = "View Data Fragment"
    }

    private var _fragmentBinding: ViewDataFragmentBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = ViewDataFragmentBinding.inflate(inflater, container, false)

        fragmentBinding.viewDataLocationList.adapter = locationPreviewListAdaptor

        fileService.setup(requireContext())

        setupClickListeners()
        setupObservers()

        fragmentBinding.viewDataContentCount.text = getString(
            R.string.total_number_of_stored_data_points,
            getString(R.string.na)
        )

        fileService.viewLocationsData()

        return fragmentBinding.root
    }

    private fun setupClickListeners() {
        fragmentBinding.closeViewData.setOnClickListener {
            (requireActivity() as MainActivity).navigateFromViewDataToMain()
        }
    }

    private fun setupObservers() {
        fileService.locationDataPointCount.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.viewDataContentCount.text = getString(
                    R.string.total_number_of_stored_data_points,
                    it.toString()
                )
            }
        })

        fileService.locationPreviewList.observe(viewLifecycleOwner, {
            if (it != null) {
                locationPreviewListAdaptor.submitList(it)
            }
        })
    }
}