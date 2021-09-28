package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.MainActivity
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

        fragmentBinding.viewDataContent.text = fileService.viewLocationsData()

        return fragmentBinding.root
    }
}