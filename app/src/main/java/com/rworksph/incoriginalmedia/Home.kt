package com.rworksph.incoriginalmedia

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.media_controller.*

class Home : AppCompatActivity(),MediaOnPlayListener {

    //var task = FetchData("https://api-v2.hearthis.at/mikeds/?type=playlists")
    internal var datalist:MutableList<Sets> = ArrayList()
    var songList = ArrayList<HashMap<String, String>>()
    var data = Data()
    var init = inits()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var boolean : Boolean = false
        bottomSheetLayout.setOnProgressListener { progress -> onprog() }
        var mediaControllerManager = MediaControllerManager()

        init.initListData(this, datalist)
        init.initHomeSongs(this, songList)
        val adapter = ListsAdapter(this, datalist)
        view_pager.adapter =adapter

        rvHome.layoutManager = LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false)
        rvHome.adapter = HomeAdapter(this, songList)


        bottomSheetLayout.visibility = View.GONE

        Toast.makeText(this, mediaControllerManager.Sig.toString(), Toast.LENGTH_SHORT).show()
        ivPlayPauseBurron.setOnClickListener{
            mediaControllerManager.play()
            if (boolean){
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
                boolean = !boolean
            }else{
                ivPlayPauseBurron.setImageResource(R.drawable.ic_baseline_play_arrow_24_d1a538)
                boolean = !boolean
            }


        }
        bHome.setOnClickListener{
            //mediaControllerManager.play("https://api-v2.hearthis.at/mikeds/spotlight-pinugay-daryltanda-ng-pag-ibig-ng-ama/listen/?s=zFr")
        }

       /* bHome.setOnClickListener{
            bottomSheetLayout.visibility = View.VISIBLE
        }*/
        //Log.e("petsdata", FetchData().execute().status.toString())
        /*val sharedPreference =  getSharedPreferences("Data",Context.MODE_PRIVATE)
        val data = sharedPreference.getString("jsonData","")*/



    }

    fun onprog(){
        if (bottomSheetLayout.isExpended()){
           // bottomSheetLayout.visibility = View.GONE
        }
    }
     override fun onMediaPlay(context: Context, data: ArrayList<String>) {

         var mcontext = (context as? Home)
         var fade_in = AnimationUtils.loadAnimation(mcontext, R.anim.fade_in)
         mcontext?.ivPlayPauseBurron?.setImageResource(R.drawable.ic_baseline_pause_24_d1a538)
         val array : ArrayList<String> = data
         mcontext?.tvMediaTitle?.setText(array[0])
         //Toast.makeText(context, array[1], Toast.LENGTH_LONG).show()
         Picasso.get()
             .load(array[1].toString())
             .resize(300, 300)
             .centerCrop()
             .into(mcontext?.ivMediaAlbum)
         mcontext?.bottomSheetLayout?.visibility = View.VISIBLE
         mcontext?.bottomSheetLayout?.startAnimation(fade_in)
    }


}