package com.narsha2018.usicmusic.util

import android.content.Context
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.narsha2018.usicmusic.activity.*
import com.narsha2018.usicmusic.model.LoginResponse
import com.narsha2018.usicmusic.model.MusicResponse
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.progressDialog
import org.json.JSONArray

class FuelUtils (private val c: Context){
    init {
        Toasty.Config.reset()
        FuelManager.instance.basePath = "http://10.80.162.221:3000/api"
    }
    fun postData(url : String, data : Any) {
        val gson = Gson()
        var json : String = gson.toJson(data)
        var resultJson : Any
        resultJson = if(url.contains("auth"))
            LoginResponse(600, "", "","")
        else
            LoginResponse(600, "", "","")
        url.httpPost().body(json, Charsets.UTF_8).header("Content-Type" to "application/json").responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    if(url.contains("login")) {
                        (c as LoginActivity).notifyFinish(gson.toJson(resultJson))
                    }
                    else if(url.contains("register")) {
                        (c as RegisterActivity).notifyFinish(gson.toJson(resultJson))
                    }
                }
                is Result.Success -> {
                    if(url.contains("auth")) {
                        //
                        if (url.contains("login")) {
                            (c as LoginActivity).notifyFinish(result.get().content)
                        }
                        if (url.contains("register")) {
                            (c as RegisterActivity).notifyFinish(result.get().content)
                        }
                    }

                }
            }
        }

    }
    fun postFavorite(id : String) {
        ("/music/$id/rate").httpPost().body("{ \"username\":\""+ PreferencesUtils(c).getData("id") +"\" }", Charsets.UTF_8).header("Content-Type" to "application/json").responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    (c as MusicActivity).notifyFavoriteFinish(false)
                }
                is Result.Success -> {
                    (c as MusicActivity).notifyFavoriteFinish(true)

                }
            }
        }
    }
    fun deleteFavorite(id : String) {
        ("/music/$id/rate/${PreferencesUtils(c).getData("id")}").httpDelete().responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    (c as MusicActivity).notifyFavoriteFinish(false)
                }
                is Result.Success -> {
                    (c as MusicActivity).notifyFavoriteFinish(true)

                }
            }
        }

    }
    fun getMusicData(isFavorite : Boolean) {
        val gson = Gson()
        val resultJson = MusicResponse(true, "error", "", "", JSONArray(), "","","")
        "/music".httpGet().responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    if(isFavorite)
                        (c as FavoriteActivity).notifyFinish(gson.toJson(resultJson))
                    else
                        (c as MusicActivity).notifyFinish(gson.toJson(resultJson))
                }
                is Result.Success -> {
                    if(isFavorite)
                        (c as FavoriteActivity).notifyFinish(result.get().content)
                    else
                        (c as MusicActivity).notifyFinish(result.get().content)
                }
            }
        }
    }
    fun getRankData() {
        val gson = Gson()
        val resultJson = MusicResponse(true, "error", "", "", JSONArray(), "","","")
        "/music".httpGet().responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    (c as MainActivity).notifyFinish(gson.toJson(resultJson))
                }
                is Result.Success -> {
                    (c as MainActivity).notifyFinish(result.get().content)
                }
            }
        }
    }
    fun getSearchData(keyword: String) {
        val gson = Gson()
        val resultJson = MusicResponse(true, "error", "", "", JSONArray(), "","","")
        "/music".httpGet().responseJson { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    (c as SearchActivity).notifyFinish(gson.toJson(resultJson),keyword)
                }
                is Result.Success -> {
                    (c as SearchActivity).notifyFinish(result.get().content, keyword)
                }
            }
        }
    }
}
