package com.example.gallery


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.GridLayoutAnimationController
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {

    private lateinit var ReViewModel:RetrofitViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.swpie_refresh ->  {
               swipeLayoutGallery.isRefreshing=true
              // Handler().postDelayed({galleryViewModel.fetchData()},1000)
               Handler().postDelayed({ReViewModel.resetQuery()},1000)
           }
       }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        ReViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(RetrofitViewModel::class.java)
            val galleryAdapter=Adapter(ReViewModel)
        recyclerView.apply {
            adapter=galleryAdapter
            layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }



        //galleryViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)

       // galleryViewModel.photoListLive.observe(this, Observer {
          //  galleryAdapter.submitList(it)
         //   swipeLayoutGallery.isRefreshing = false
       // })



        ReViewModel.photoListLive.observe(this, Observer {
            if (ReViewModel.needToScrollToTop){
                recyclerView.scrollToPosition(0)
                ReViewModel.needToScrollToTop=false
            }
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing=false
        })

        ReViewModel.dataStatusLive.observe(this, Observer {
            galleryAdapter.footerViewStatus=it
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount-1)
            if (it== DATA_STATUS_NETWORK_ERROR){
                swipeLayoutGallery.isRefreshing=false

            }
        })




        //RetrofitViewModel中已经通过init进行初始化处理
        //galleryViewModel.photoListLive.value?:galleryViewModel.fetchData()
        //ReViewModel.photoListLive.value?:ReViewModel.getPhoto()
        swipeLayoutGallery.setOnRefreshListener {
           // galleryViewModel.fetchData()
            ReViewModel.resetQuery()
        }
        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy<0)return
                val layoutManager=recyclerView.layoutManager as StaggeredGridLayoutManager
                val intArray=IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray)
                if (intArray[0]==galleryAdapter.itemCount-1){
                    ReViewModel.getPhoto()
                }
            }
        })
    }

}











