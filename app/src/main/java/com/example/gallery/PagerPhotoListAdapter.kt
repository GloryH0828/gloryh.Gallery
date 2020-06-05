package com.example.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.pager_photo_view.view.*

class PagerPhotoListAdapter:ListAdapter<photoItem,PagerPhotoViewHolder>(DiffCallBack) {
    object DiffCallBack:DiffUtil.ItemCallback<photoItem>(){
        override fun areItemsTheSame(oldItem: photoItem, newItem: photoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: photoItem, newItem: photoItem): Boolean {
            return oldItem.photoId ==newItem.photoId
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
       LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent,false).apply {
           return  PagerPhotoViewHolder(this)
       }
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
       Glide.with(holder.itemView)
           .load(getItem(position).previewUrl)
           .placeholder(R.drawable.photo_palceholder)
           .into(holder.itemView.pagerPhoto)
    }
}
class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)