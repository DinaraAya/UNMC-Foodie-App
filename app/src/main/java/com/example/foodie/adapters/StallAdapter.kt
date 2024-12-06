package com.example.foodie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodie.databinding.ItemStallBinding
import com.example.foodie.models.Stalls

/**
 * Adapter for displaying a list of food stalls in a RecyclerView.
 * Each stall item displays the stall's name, image, operating days, hours, and closed days.
 * Clicking on an item triggers a callback to handle the action.
 *
 * @param stalls List of stalls to display.
 * @param onClick Callback function to handle clicks on stall items.
 */
class StallAdapter(
    private val stalls: List<Stalls>,
    private val onClick: (Stalls) -> Unit
) : RecyclerView.Adapter<StallAdapter.StallViewHolder>() {
    /**
     * Creates a ViewHolder for a stall item.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The type of the new View (unused here as there is only one view type).
     * @return A new instance of StallViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StallViewHolder {
        val binding = ItemStallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StallViewHolder(binding)
    }

    /**
     * Binds a stall item to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the adapter.
     */
    override fun onBindViewHolder(holder: StallViewHolder, position: Int) {
        val stall = stalls[position]
        holder.bind(stall)
    }

    /**
     * Returns the number of stalls in the list.
     *
     * @return The size of the stalls list.
     */
    override fun getItemCount(): Int = stalls.size

    /**
     * ViewHolder for displaying a single stall item.
     *
     * @param binding The binding object for the stall item layout (ItemStallBinding).
     */
    inner class StallViewHolder(private val binding: ItemStallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds a stall's data to the UI elements in the layout.
         *
         * @param stall The stall to bind to this ViewHolder.
         */
        fun bind(stall: Stalls) {
            // Set stall name
            binding.stallName.text = stall.name

            // Load stall image using Glide
            Glide.with(binding.root.context).load(stall.imageUrl).into(binding.stallImage)

            // Set operating and closed days/hours
            binding.operatingDays.text = stall.operatingDays
            binding.operatingHrs.text = stall.operatingHours
            binding.closedDays.text = stall.closedDays

            // Set click listener for the entire item
            binding.root.setOnClickListener {
                onClick(stall) // Trigger the onClick callback with the stall data
            }
        }
    }
}
