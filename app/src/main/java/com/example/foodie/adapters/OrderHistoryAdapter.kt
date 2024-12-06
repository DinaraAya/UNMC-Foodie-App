package com.example.foodie.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.databinding.ItemHistoryBinding
import com.example.foodie.models.Order
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying a list of past orders in a RecyclerView.
 *
 * @param orderList List of orders to display in the history.
 */
class OrderHistoryAdapter (private val orderList: List<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
        /**
         * ViewHolder class for displaying individual order details.
         *
         * @param binding Binding object for the item layout (ItemHistoryBinding).
         */
        inner class OrderViewHolder(private val binding: ItemHistoryBinding) :
                    RecyclerView.ViewHolder(binding.root) {
                        // Date formatter for displaying order timestamps in a user-friendly format.
                        private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                        /**
                         * Binds order data to the item view.
                         *
                         * @param order The Order object containing details to display.
                         */
                        fun bind(order: Order) {
                            // Set the stall name.
                            binding.textViewStallName.text = order.stallName

                            // Format and display the total cost.
                            binding.textViewTotalPrice.text = String.format("RM %.2f", order.totalCost)

                            // Display the total number of items in the order.
                            binding.textViewItemCount.text = "${order.totalItems} items"

                            // Format and display the order timestamp.
                            binding.textViewOrderDate.text = dateFormatter.format(order.timestamp.toDate())
                        }

                    }

    /**
     * Inflates the layout for an individual order item and creates a ViewHolder for it.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new OrderViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    /**
     * Binds the data for an order at the specified position to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the order in the list.
     */
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    /**
     * Returns the total number of orders in the list.
     *
     * @return The size of the order list.
     */
    override fun getItemCount(): Int = orderList.size
    }