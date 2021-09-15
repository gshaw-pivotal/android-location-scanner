package gs.location_scanner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gs.location_scanner.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _fragmentBinding: MainFragmentBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBinding = MainFragmentBinding.inflate(inflater, container, false)

        fragmentBinding.fetchGpsData.setOnClickListener {
        }

        fragmentBinding.fetchWifiData.setOnClickListener {
        }

        fragmentBinding.saveDataPoint.setOnClickListener {
        }

        return fragmentBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentBinding = null
    }
}