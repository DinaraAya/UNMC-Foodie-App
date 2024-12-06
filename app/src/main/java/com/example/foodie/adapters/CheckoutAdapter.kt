package com.example.foodie.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.databinding.ItemCheckoutBinding
import com.example.foodie.models.CartItemData

/**
 * Adapter class for managing the list of items in the checkout screen.
 * It binds data from the cart to the RecyclerView for display.
 */
class CheckoutAdapter : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    // List to hold cart items, each pair contains the item name and its associated data.
    private val items = mutableListOf<Pair<String, CartItemData>>()

    /**
     * Submits a new list of items to the adapter.
     * Clears the current list, updates it with the new data, and refreshes the RecyclerView.
     * @param newItems - The new list of items to be displayed.
     */
    fun submitList(newItems: List<Pair<String, CartItemData>>) {
        Log.d("CheckoutAdapter", "Submitting new list to adapter: $newItems")
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Inflates the layout for individual items in the RecyclerView.
     * @param parent - The parent ViewGroup that holds the RecyclerView.
     * @param viewType - The type of view (not used here since we have a single view type).
     * @return A new instance of CheckoutViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    /**
     * Binds data to a specific item in the RecyclerView at the given position.
     * @param holder - The ViewHolder for the item.
     * @param position - The position of the item in the list.
     */
    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val (itemName, cartItemData) = items[position]
        Log.d("CheckoutAdapter", "Binding item at position $position: Name = $itemName, Data = $cartItemData")
        holder.bind(itemName, cartItemData)
    }

    /**
     * Returns the total number of items in the RecyclerView.
     * @return The count of items in the list.
     */
    override fun getItemCount(): Int {
        val count = items.size
        Log.d("CheckoutAdapter", "Item count in adapter: $count")
        return count
    }

    /**
     * ViewHolder class for managing the views of individual items in the RecyclerView.
     * It binds the data to the views in the layout.
     */
    class CheckoutViewHolder(private val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the item name, quantity, and total price to the respective views in the layout.
         * @param itemName - The name of the item.
         * @param cartItemData - The data associated with the item, including quantity and price.
         */
        fun bind(itemName: String, cartItemData: CartItemData) {
            // Setting the item's name.
            binding.itemName.text = itemName

            // Displaying the item's quantity in the format "x<quantity>".
            binding.itemQuantity.text = "x${cartItemData.quantity}"

            // Calculating and displaying the total price (price * quantity) formatted to two decimal places.
            binding.itemPrice.text = "$${String.format("%.2f", cartItemData.price * cartItemData.quantity)}"
            Log.d("CheckoutAdapter", "Bound item: Name = $itemName, Quantity = ${cartItemData.quantity}, Total Price = ${String.format("%.2f", cartItemData.price * cartItemData.quantity)}")
        }
    }
}
