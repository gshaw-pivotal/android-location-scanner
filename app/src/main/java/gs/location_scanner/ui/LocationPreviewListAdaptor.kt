package gs.location_scanner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gs.location_scanner.data.LocationPreview
import gs.location_scanner.databinding.LocationPreviewDetailsListItemFragmentBinding

class LocationPreviewListAdaptor: ListAdapter<LocationPreview, RecyclerView.ViewHolder>(LocationPreviewDiffCallback()) {

    class LocationPreviewViewHolder(
        private val locationPreviewDetailsListItemFragmentBinding: LocationPreviewDetailsListItemFragmentBinding
    ): RecyclerView.ViewHolder(locationPreviewDetailsListItemFragmentBinding.root) {

        fun bind(item: LocationPreview) {
            locationPreviewDetailsListItemFragmentBinding.apply {
                locationPreviewLatValue.text = item.latitude
                locationPreviewLongValue.text = item.longitude
                locationPreviewNetworkCountValue.text = item.wifiNetworkCount.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return LocationPreviewViewHolder(
            LocationPreviewDetailsListItemFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as LocationPreviewViewHolder).bind(getItem(position))
    }
}

private class LocationPreviewDiffCallback : DiffUtil.ItemCallback<LocationPreview>() {
    override fun areItemsTheSame(oldItem: LocationPreview, newItem: LocationPreview): Boolean {
        return oldItem.latitude ==
                newItem.latitude &&
                oldItem.longitude == newItem.longitude &&
                oldItem.wifiNetworkCount == newItem.wifiNetworkCount
    }

    override fun areContentsTheSame(oldItem: LocationPreview, newItem: LocationPreview): Boolean {
        return oldItem == newItem
    }

}