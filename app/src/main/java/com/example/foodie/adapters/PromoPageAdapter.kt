package com.example.foodie.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodie.databinding.PromoItemBinding

/**
 * Adapter for displaying a list of promotional images with a main image
 * and two supporting images (left and right).
 *
 * @param promoImageUrls List of image URLs to be displayed in the promo pager.
 */
class PromoPagerAdapter(private val promoImageUrls: List<String>) :
    RecyclerView.Adapter<PromoPagerAdapter.PromoViewHolder>() {

    /**
     * Creates a new ViewHolder instance for a promo item.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The type of the new View (unused here as there is only one view type).
     * @return A new instance of PromoViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = PromoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromoViewHolder(binding)
    }

    /**
     * Binds the appropriate images to the ViewHolder at the specified position.
     * Uses modular arithmetic to create a circular carousel effect.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position in the adapter.
     */
    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        // Calculate indices for the main, left, and right images to enable seamless looping.
        val mainImageIndex = position % promoImageUrls.size
        val leftImageIndex = (mainImageIndex + promoImageUrls.size - 1) % promoImageUrls.size
        val rightImageIndex = (mainImageIndex + 1) % promoImageUrls.size

        // Bind the images to the ViewHolder.
        holder.bind(
            promoImageUrls[mainImageIndex],
            promoImageUrls[leftImageIndex],
            promoImageUrls[rightImageIndex]
        )
    }

    /**
     * Returns the number of items in the adapter.
     *
     * @return The total number of promotional images.
     */
    override fun getItemCount(): Int {
        return promoImageUrls.size
    }

    /**
     * ViewHolder for displaying a single promotional item.
     *
     * @param binding The binding object for the promo item layout (PromoItemBinding).
     */
    class PromoViewHolder(private val binding: PromoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds image URLs to the corresponding ImageView elements in the layout.
         *
         * @param mainImageUrl URL of the main promotional image.
         * @param leftImageUrl URL of the left supporting image.
         * @param rightImageUrl URL of the right supporting image.
         */
        fun bind(mainImageUrl: String, leftImageUrl: String, rightImageUrl: String) {
            // Load the main image into the central ImageView.
            Glide.with(binding.promoImage.context)
                .load(mainImageUrl)
                .into(binding.promoImage)

            // Load the left supporting image.
            Glide.with(binding.promoImageLeft.context)
                .load(leftImageUrl)
                .into(binding.promoImageLeft)

            // Load the right supporting image.
            Glide.with(binding.promoImageRight.context)
                .load(rightImageUrl)
                .into(binding.promoImageRight)
        }
    }
}


