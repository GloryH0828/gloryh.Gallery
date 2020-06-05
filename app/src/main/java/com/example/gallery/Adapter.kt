package com.example.gallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

class Adapter (val retrofitViewModel: RetrofitViewModel): ListAdapter<photoItem, MyViewHolder>(Adapter.DIFFCALLBACK) {
    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }
    var footerViewStatus= DATA_STATUS_CAN_LOAD_MORE
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            holder = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false))
            //点击进入第二个页面
            holder.itemView.setOnClickListener {
                Bundle().apply {
                    //putParcelable("PHOTO",getItem(holder.adapterPosition))
                    // holder.itemView.findNavController().navigate(R.id.action_galleryFragment_to_photoFragment,this)
                    putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                    putInt("PHOTO_POSITION", holder.adapterPosition)
                    holder.itemView.findNavController()
                        .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)

                }
            }
        } else {
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.gallery_footer,
                    parent,
                    false
                ).also {
                    (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan=true
                    it.setOnClickListener { it ->
                        it.progressBar.visibility=View.VISIBLE
                        it.textView.text="正在加载"
                        retrofitViewModel.getPhoto()
                    }
                }
            )
        }


        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == itemCount - 1) {
            with(holder.itemView){
                when(footerViewStatus){
                    DATA_STATUS_CAN_LOAD_MORE->{
                        progressBar.visibility=View.VISIBLE
                        textView.text="正在加载"
                        isClickable=false
                    }
                    DATA_STATUS_NO_MORE->{
                        progressBar.visibility=View.GONE
                        textView.text="全部加载完毕"
                        isClickable=false
                    }
                    DATA_STATUS_NETWORK_ERROR->{
                        progressBar.visibility=View.GONE
                        textView.text="网络故障，请点击重试"
                        isClickable=true
                    }
                }
            }
            return
        }
        val phItem = getItem(position)
        with(holder.itemView) {
            shimmerGalleryLayout.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            tv_user.text = phItem.photoUser
            tv_likes.text = phItem.photoLikes.toString()
            tv_favorites.text = phItem.photoFavorites.toString()
            imageView.layoutParams.height = getItem(position).photoHeight
        }

        Glide.with(holder.itemView)
            .load(getItem(position).previewUrl)
            .placeholder(R.drawable.photo_palceholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerGalleryLayout?.stopShimmerAnimation() }
                }

            })
            .into(holder.itemView.imageView)

    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<photoItem>() {
        override fun areItemsTheSame(oldItem: photoItem, newItem: photoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: photoItem, newItem: photoItem): Boolean {
            return oldItem == newItem
        }
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)