package gs.location_scanner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gs.location_scanner.data.WifiNetworkStats
import gs.location_scanner.databinding.WifiDetailsListItemFragmentBinding

class WifiDetailsListAdapter: ListAdapter<WifiNetworkStats, RecyclerView.ViewHolder>(WifiDetailsDiffCallback()) {

    class WifiDetailsViewHolder(
        private val wifiDetailsListItemFragmentBinding: WifiDetailsListItemFragmentBinding
    ): RecyclerView.ViewHolder(wifiDetailsListItemFragmentBinding.root) {

        fun bind(item: WifiNetworkStats) {
            wifiDetailsListItemFragmentBinding.apply {
                wifiSsid.text = item.ssid
                wifiSignalLevel.text = "${item.signalLevelIndBm}dBm"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return WifiDetailsViewHolder(
            WifiDetailsListItemFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as WifiDetailsViewHolder).bind(getItem(position))
    }
}

private class WifiDetailsDiffCallback : DiffUtil.ItemCallback<WifiNetworkStats>() {
    override fun areItemsTheSame(oldItem: WifiNetworkStats, newItem: WifiNetworkStats): Boolean {
        return oldItem.ssid == newItem.ssid
    }

    override fun areContentsTheSame(oldItem: WifiNetworkStats, newItem: WifiNetworkStats): Boolean {
        return oldItem == newItem
    }

}