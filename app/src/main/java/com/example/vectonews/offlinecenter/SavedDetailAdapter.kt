package com.example.vectonews.offlinecenter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vectonews.R
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.CustomSavedRowBinding

class SavedDetailAdapter(private val listener:OnItemClickListenerDetails, private val onLongListener:OnItemLongClickListenerSaved): RecyclerView.Adapter<SavedDetailAdapter.MyViewHolder>() {


    private var userList = emptyList<UnsplashPhoto>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CustomSavedRowBinding.bind( LayoutInflater.from(parent.context).inflate(R.layout.custom_saved_row, parent, false))

        return MyViewHolder(binding)
    }

    interface OnItemClickListenerDetails {
        fun onclickDetailsItem(photo: UnsplashPhoto)

    }

    interface OnItemLongClickListenerSaved {
        fun onItemLongClickedSaved(photo: UnsplashPhoto)
    }

    inner class MyViewHolder(private val binding: CustomSavedRowBinding) :
        RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val item = userList[position]
                    if (item != null){
                        listener.onclickDetailsItem(item)
                    }
                }

            }


            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = userList[position]
                    if (item != null) {
                        onLongListener.onItemLongClickedSaved(item)
                        return@setOnLongClickListener true
                    }
                }
                return@setOnLongClickListener false
            }

        }




        fun bind(photo: UnsplashPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urlToImage)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_launcher_background)
                    .into(imageTV)

                textDescription.text = photo.title
            }
        }

    }



    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        if (currentItem != null) {
            holder.bind(currentItem)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<UnsplashPhoto>){
        this.userList = user
        notifyDataSetChanged()

    }



}