package com.example.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.ceil
const val DATA_STATUS_CAN_LOAD_MORE=0
const val DATA_STATUS_NO_MORE=1
const val DATA_STATUS_NETWORK_ERROR=2
class RetrofitViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataStatusLive= MutableLiveData<Int>()
    val dataStatusLive:LiveData<Int>get()=_dataStatusLive
    private val _photoListLive= MutableLiveData<List<photoItem>>()
    val photoListLive: LiveData<List<photoItem>>
        get() = _photoListLive

    var needToScrollToTop=true
    private val perPage=100
    private val keyWords=arrayOf("cat","dog","beauty","animal","computer","SCP")
    private var currentPage=1
    private var totalPage=1
    private var currentKey="cat"
    private var isNewQuery=true
    private var isLoading=false
    init {
        resetQuery()
    }
    fun resetQuery(){
        currentPage=1
        totalPage=1
        currentKey=keyWords.random()
        isNewQuery=true
        needToScrollToTop=true
        getPhoto()
    }
    fun getPhoto(){
        if (isLoading)return

        if (currentPage>totalPage){
            _dataStatusLive.value= DATA_STATUS_NO_MORE
            return

        }
        isLoading = true
        val retrofit= Retrofit.Builder()
            .baseUrl("https://pixabay.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api=retrofit.create(PhotoApi::class.java)
        api.PixSearch(key="16238228-015814b6e69d91bd9c28cca28",q=currentKey,per_page = perPage,page = currentPage)
            .enqueue(object :Callback<pixabay>{
                override fun onFailure(call: Call<pixabay>?, t: Throwable?) {
                    _dataStatusLive.value= DATA_STATUS_NETWORK_ERROR
                    Log.d("error","error")
                    isLoading=false
                }

                override fun onResponse(call: Call<pixabay>?, response: Response<pixabay>?) {
                    response?.let { it ->
                        it.body()?.let {
                            totalPage= ceil(it.totalHits.toDouble()/perPage).toInt()
                            if (isNewQuery){
                                _photoListLive.value= it.hits.toList()
                            }else{
                                _photoListLive.value= arrayListOf(_photoListLive.value!!,it.hits.toList()).flatten()
                            }
                            _dataStatusLive.value= DATA_STATUS_CAN_LOAD_MORE
                            isLoading=false
                            isNewQuery = false
                            currentPage++


                            Log.e("ddd",it.total.toString())
                        }
                    }
                }

            })
    }

    interface PhotoApi{
        @GET(".")
        fun PixSearch(@Query("key")key:String,
                      @Query("q")q:String,
                      @Query("per_page")per_page:Int,
                      @Query("page")page:Int):Call<pixabay>
    }

}