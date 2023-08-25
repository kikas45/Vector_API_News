package com.example.vectonews.comments


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vectonews.R
import com.example.vectonews.databinding.ItemComentLayoutBinding
import java.text.SimpleDateFormat


class AdapterComment(
    private val listenerCo: OnItemClickListener,
    private val onItemClickDelete: OnItemDeleteListener,
) : RecyclerView.Adapter<AdapterComment.MyViewHolder>() {


    private var userList = emptyList<ModelComment>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemComentLayoutBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_coment_layout, parent, false)
        )

        return MyViewHolder(binding)
    }

    interface OnItemClickListener {
        fun onItemClicked(photo: ModelComment)
    }

    interface OnItemDeleteListener {
        fun onItemDeleteListner(photo: ModelComment)
    }

    inner class MyViewHolder(private val binding: ItemComentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = userList[position]
                    if (item != null) {
                        listenerCo.onItemClicked(item)
                    }
                }
            }




            binding.optionImage.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = userList[position]
                    if (item != null) {
                        onItemClickDelete.onItemDeleteListner(item)
                    }
                }
            }


        }


        @SuppressLint("SetTextI18n")
        fun bind(photo: ModelComment) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.userImage.toString())
                    .centerCrop()
                    .into(imageProfiler)


                profilename.text = photo.name.toString()
                textdescription.text = photo.desc.toString()
               textView43.text = photo.postDate.toString()

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
    fun setData(user: List<ModelComment>) {
        this.userList = user
        notifyDataSetChanged()
    }


}