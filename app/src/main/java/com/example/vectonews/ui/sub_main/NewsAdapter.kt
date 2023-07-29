package com.example.vectonews.ui.sub_main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.ItemUnsplashPhotoBinding


class NewsAdapter(private val listener: OnItemClickListenerMe, private val shortListner: OnShortClickedAddItem) :
    PagingDataAdapter<UnsplashPhoto, NewsAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    interface OnItemClickListenerMe{
        fun onItemClickedMe(photo: UnsplashPhoto)
    }

    interface OnShortClickedAddItem {
        fun onITemShortAdded(photo: UnsplashPhoto)
    }



    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if (item != null){
                        listener.onItemClickedMe(item)
                    }
                }

            }

            binding.imageViewBookmark.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if (item != null){
                        shortListner.onITemShortAdded(item)
                    }
                }

            }

        }



        fun bind(photo: UnsplashPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urlToImage + "")
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)


                textViewTitle.text = photo.title.toString()
            }
        }
    }



    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.title.toString() == newItem.title.toString()

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
        }
    }
}