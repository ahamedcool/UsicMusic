@file:Suppress("DEPRECATION")

package com.narsha2018.usicmusic.activity

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.google.gson.Gson
import com.narsha2018.usicmusic.R
import com.narsha2018.usicmusic.model.LoginRequest
import com.narsha2018.usicmusic.model.LoginResponse
import com.narsha2018.usicmusic.util.BitmapUtils
import com.narsha2018.usicmusic.util.FuelUtils
import com.narsha2018.usicmusic.util.PreferencesUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread


class LoginActivity : AppCompatActivity() {

    private val gson = Gson()
    private var progressDialog : ProgressDialog? = null
    private val fuelUtil = FuelUtils(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btmDrawable:BitmapDrawable = ContextCompat.getDrawable(this, R.drawable.bg_img_login) as BitmapDrawable
        val btmBitmap: Bitmap = BitmapUtils.blurBitmap(this, btmDrawable.bitmap, 25)
        val resultDrawable = BitmapDrawable(resources, btmBitmap)
        logo_login.background = resultDrawable
        progressDialog = ProgressDialog(this)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setMessage("Loading...")
        Toasty.Config.reset()
        go.setOnClickListener{ doLogin() }
        start.setOnClickListener { startActivity<RegisterActivity>() }
        back.setOnClickListener{ onBackPressed() }
        pw.setOnKeyListener(View.OnKeyListener { _, p1, p2 ->
            if ((p2!!.action == KeyEvent.ACTION_DOWN) && (p1 == KeyEvent.KEYCODE_ENTER)) {
                doLogin()
                return@OnKeyListener true
            }
            false
        })
    }
    private fun doLogin() {
        val id: String = id.text.toString()
        val pw: String = pw.text.toString()
        if (id == "" || pw == "") {
            Toasty.warning(this, "Please type id and password.").show()
            return
        }
        progressDialog?.show()
        doAsync {
            fuelUtil.postData("/auth/login", LoginRequest(id, pw), FuelUtils.PostEnum.Login)
        }
    }

    fun notifyFinish(accountResponse : String){
        doAsync {
            val resultJson: LoginResponse = gson.fromJson(accountResponse, LoginResponse::class.java)
            if (resultJson.status == 200 && resultJson.message.trim() != "") { // success
                PreferencesUtils(this@LoginActivity).apply {
                    saveData("token", resultJson.token)
                    saveData("id", id.text.toString())
                    saveData("pw", pw.text.toString())
                    saveData("nick", resultJson.nickname)
                }
                uiThread {
                    Toasty.success(it, resultJson.message).show()
                    it.startActivity<MainActivity>()
                    it.finish()
                }
            } else {
                uiThread {
                    Toasty.error(it, "비밀번호를 확인해주세요").show()
                    pw.setText("")
                }
            }
            uiThread {
                progressDialog?.dismiss()
            }
        }
    }
}
