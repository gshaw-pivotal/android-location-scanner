package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.MainActivity
import gs.location_scanner.databinding.ViewDataFragmentBinding

class ViewDataFragment  : Fragment() {

    private var _fragmentBinding: ViewDataFragmentBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    companion object {
        const val TAG: String = "View Data Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = ViewDataFragmentBinding.inflate(inflater, container, false)

        fragmentBinding.closeViewData.setOnClickListener {
            (requireActivity() as MainActivity).navigateFromViewDataToMain()
        }

        return fragmentBinding.root
    }
}