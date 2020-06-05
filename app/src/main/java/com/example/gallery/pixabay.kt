package com.example.gallery

import android.os.Parcelable
import android.text.style.LineHeightSpan
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class pixabay (
    val totalHits:Int,
    val hits:Array<photoItem>,
    val total:Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as pixabay

        if (totalHits != other.totalHits) return false
        if (!hits.contentEquals(other.hits)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = totalHits
        result = 31 * result + hits.contentHashCode()
        result = 31 * result + total
        return result
    }
}


@Parcelize data class photoItem (
    @SerializedName("webformatURL") val previewUrl:String,
    @SerializedName("id")  val photoId:Int,
    @SerializedName("largeImageURL") val fullUrl:String,
    @SerializedName("likes") val photoLikes:Int,
    @SerializedName("user") val photoUser:String,
    @SerializedName("favorites") val photoFavorites:Int,
    @SerializedName("webformatHeight") val photoHeight:Int
):Parcelable