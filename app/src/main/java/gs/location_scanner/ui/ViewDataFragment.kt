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

        fileService.setup(requireContext())

        fragmentBinding.closeViewData.setOnClickListener {
            (requireActivity() as MainActivity).navigateFromViewDataToMain()
        }

        fileService.locationDataPointContent.observe(viewLifecycleOwner, {
            if (it != null) {
                fragmentBinding.viewDataContent.text = it
            } else {
                fragmentBinding.viewDataContent.text = ""
            }
        })

        fragmentBinding.viewDataContent.text = ""

        fileService.viewLocationsData()

        return fragmentBinding.root
    }
}