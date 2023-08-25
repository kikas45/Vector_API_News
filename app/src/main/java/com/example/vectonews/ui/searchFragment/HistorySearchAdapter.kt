package com.example.vectonews.ui.searchFragment


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vectonews.R
import com.example.vectonews.databinding.CustomRowBinding
import com.example.vectonews.searchHistory.User

class HistorySearchAdapter(private val listener: OnItemClickListener, private val onLongListener: OnItemLongClickListener  ): RecyclerView.Adapter<HistorySearchAdapter.MyViewHolder>() {


    private var userList = emptyList<User>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CustomRowBinding.bind( LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))

        return MyViewHolder(binding)
    }

    interface OnItemClickListener{
        fun onItemClicked(photo: User)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(photo: User)
    }

    inner class MyViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root){
        val firstNameCO = binding.firstNameTxt

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = userList[position]
                    if (item != null) {
                        listener.onItemClicked(item)
                    }
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = userList[position]
                    if (item != null) {
                        onLongListener.onItemLongClicked(item)
                        return@setOnLongClickListener true
                    }
                }
                return@setOnLongClickListener false
            }


        }




    }


    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.firstNameCO.text = currentItem.firstName

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(user: List<User>){
        this.userList = user
        notifyDataSetChanged()
    }



}