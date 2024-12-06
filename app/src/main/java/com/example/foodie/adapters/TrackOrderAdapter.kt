package com.example.foodie.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.databinding.ItemTrackBinding
import com.example.foodie.models.Order

/**
 * Adapter for tracking the user's orders. Displays order details and status messages,
 * with an arrow icon for navigating to a detailed status page.
 *
 * @param onArrowClick Lambda function triggered when the arrow icon is clicked.
 * Provides the order number and status for navigation or further actions.
 */
class TrackOrderAdapter(
    private val onArrowClick: (Int, String) -> Unit // Add a lambda to handle arrow clicks
) : ListAdapter<Order, TrackOrderAdapter.TrackOrderViewHolder>(OrderDiffCallback()) {

    /**
     * ViewHolder for a single order tracking item. Handles binding order data
     * to the layout and setting up interactions.
     */
    inner class TrackOrderViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an order's details and status message to the corresponding views.
         *
         * @param order The order to display.
         */
        fun bind(order: Order) {
            // Set stall name
            binding.stallName.text = order.stallName

            // Display an appropriate message based on the order's status
            binding.orderStatusMessage.text = when (order.status) {
                "Order Placed" -> "Your order was successfully placed! We will notify you once it's in the kitchen."
                "In the Kitchen" -> "Your order is in the kitchen. We will notify you when it's ready for collection."
                "Order Ready" -> "Your order is ready! You can collect it at the stall counter."
                else -> ""
            }

            // Set click listener for arrow to navigate to StatusFragment
            binding.arrowView.setOnClickListener {
                onArrowClick(order.orderNumber, order.status)
            }
        }
    }

    /**
     * Inflates the item layout and creates a new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackOrderViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackOrderViewHolder(binding)
    }

    /**
     * Binds the order data to the ViewHolder at the given position.
     */
    override fun onBindViewHolder(holder: TrackOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Callback for calculating the difference between two lists of orders.
     * Ensures only changed items are updated.
     */
    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Compare based on unique order numbers
            return oldItem.orderNumber == newItem.orderNumber
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Compare entire objects for content equality
            return oldItem == newItem
        }
    }
}

