package com.example.foodie.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodie.databinding.ItemCategoryHeaderBinding
import com.example.foodie.databinding.ItemMenuBinding
import com.example.foodie.models.MenuCategory
import com.example.foodie.models.MyMenuItem

/**
 * Adapter class for displaying menu items grouped by categories.
 * Includes functionality to manage quantities and interact with the cart.
 *
 * @param categoriesWithItems List of menu categories and their associated menu items.
 * @param initialCartData Initial quantities of items in the cart, mapped by item name.
 * @param onCartUpdated Callback triggered whenever the cart is updated.
 */
class MenuAdapter(
    private val categoriesWithItems: List<Pair<MenuCategory, List<MyMenuItem>>>,
    private val initialCartData: Map<String, Int>,
    private val onCartUpdated: (MyMenuItem, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Constants for distinguishing between header and item view types.
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
    // Stores current quantities of items in the cart.
    private val quantities = initialCartData.toMutableMap()

    // Flattened list combining headers and items for the RecyclerView.
    private val displayItems = mutableListOf<Pair<Int, Any>>()

    // Prepares the display items by flattening the categories and their items.
    init {
        prepareDisplayItems()
    }

    /**
     * Returns the type of view (header or item) for a given position.
     * @param position Position in the list.
     */
    override fun getItemViewType(position: Int): Int = displayItems[position].first

    /**
     * Inflates the appropriate view (header or item) based on the view type.
     * @param parent The parent ViewGroup.
     * @param viewType The type of view to be created.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> CategoryViewHolder(
                ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            TYPE_ITEM -> ItemViewHolder(
                ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    /**
     * Binds data to the appropriate view holder based on the position.
     * @param holder The view holder.
     * @param position Position of the item in the list.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.bind(displayItems[position].second as String)
            is ItemViewHolder -> holder.bind(displayItems[position].second as MyMenuItem)
        }
    }

    /**
     * Returns the total number of items (headers + items) in the RecyclerView.
     */
    override fun getItemCount(): Int = displayItems.size

    /**
     * Flattens the categories and their items into a single list of display items.
     * Adds a header for each category followed by its associated items.
     */
    private fun prepareDisplayItems() {
        displayItems.clear()
        categoriesWithItems.forEach { (category, items) ->
            displayItems.add(TYPE_HEADER to category.categoryName)
            items.forEach { item ->
                displayItems.add(TYPE_ITEM to item)
            }
        }
    }

    /**
     * ViewHolder for category headers.
     */
    inner class CategoryViewHolder(private val binding: ItemCategoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the category name to the header view.
         * @param categoryName The name of the category.
         */
        fun bind(categoryName: String) {
            binding.categoryName.text = categoryName
        }
    }

    /**
     * ViewHolder for individual menu items.
     */
    inner class ItemViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a menu item to the view and sets up click listeners for cart interactions.
         * @param menuItem The menu item to be displayed.
         */
        fun bind(menuItem: MyMenuItem) {
            binding.menuItemName.text = menuItem.itemName
            binding.menuItemPrice.text = "$${String.format("%.2f", menuItem.itemPrice.toDoubleOrNull() ?: 0.0)}"

            // Load the item image using Glide if an image URL is available.
            if (!menuItem.imageUrl2.isNullOrEmpty()) {
                Glide.with(binding.root.context).load(menuItem.imageUrl2).into(binding.menuItemImage)
            }

            // Get the initial quantity from the cart data.
            val quantityInCart = quantities[menuItem.itemName] ?: 0
            Log.d("MenuAdapter", "Binding ${menuItem.itemName} with quantity from cart: $quantityInCart")

            // Update UI based on the current quantity in the cart.
            binding.tvQuantity.text = quantityInCart.toString()
            if (quantityInCart > 0) {
                binding.btnAdd.visibility = View.GONE
                binding.quantitySelector.visibility = View.VISIBLE
            } else {
                binding.btnAdd.visibility = View.VISIBLE
                binding.quantitySelector.visibility = View.GONE
            }

            // Set up click listeners for add, plus, and minus buttons.
            binding.btnAdd.setOnClickListener {
                updateCart(menuItem, 1)
            }

            binding.btnPlus.setOnClickListener {
                val currentQuantity = quantities[menuItem.itemName] ?: 0
                updateCart(menuItem, currentQuantity + 1)
            }

            binding.btnMinus.setOnClickListener {
                val currentQuantity = quantities[menuItem.itemName] ?: 0
                if (currentQuantity > 1) {
                    updateCart(menuItem, currentQuantity - 1)
                } else {
                    // Hide the quantity selector and reset to the add button when quantity is 0.
                    binding.btnAdd.visibility = View.VISIBLE
                    binding.quantitySelector.visibility = View.GONE
                    updateCart(menuItem, 0)
                }
            }
        }

        /**
         * Updates the cart with the new quantity for a menu item.
         * Updates the UI and triggers the cart update callback.
         * @param menuItem The menu item to update.
         * @param quantity The new quantity for the item.
         */
        private fun updateCart(menuItem: MyMenuItem, quantity: Int) {
            if (quantity > 0) {
                quantities[menuItem.itemName] = quantity
                binding.tvQuantity.text = quantity.toString()
                binding.btnAdd.visibility = View.GONE
                binding.quantitySelector.visibility = View.VISIBLE
            } else {
                quantities.remove(menuItem.itemName)
                binding.tvQuantity.text = "0"
                binding.btnAdd.visibility = View.VISIBLE
                binding.quantitySelector.visibility = View.GONE
            }
            // Trigger the callback to inform about cart updates.
            onCartUpdated(menuItem, quantity)
        }
    }
}
