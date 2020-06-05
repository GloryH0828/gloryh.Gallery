package com.example.gallery

import android.app.Application
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoListLive= MutableLiveData<List<photoItem>>()
    val photoListLive:LiveData<List<photoItem>>
    get() = _photoListLive
    //未作分页处理
    private val perPage=50
    private val keyWords=arrayOf("cat","dog","beauty","animal","computer","SCP")
    private var currentPage=1
    private var totalPage=1
    private var currentKey="cat"
    private var isNewQuery=true
    private var isLoading=false
    fun fetchData(){
        val stringRequest=StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                _photoListLive.value= Gson().fromJson(it,pixabay::class.java).hits.toList()
            },
            Response.ErrorListener {
                Log.e("error",it.toString())
            }
        )

        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=16238228-015814b6e69d91bd9c28cca28&q=${keyWords.random()}&per_page=100"
    }

}