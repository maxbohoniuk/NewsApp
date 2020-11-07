package com.example.prm03

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_feed.view.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var adapter : MyAdapter

    lateinit var feeds:List<Feed>
    lateinit var favFeeds:List<Feed>
    var dbService = DatabaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)




        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this)





        loadFeed()


        recyclerView.adapter = adapter


        auth = Firebase.auth

        emailText.text = auth.currentUser?.email.toString()
        
        favFeeds = dbService.readFavFeeds(auth.currentUser?.email.toString())





    }

    private fun refreshAdapter(list: List<Feed>) {
        adapter.refresh(list)
    }



    public fun logoutClicked(view: View){
        signOut()

        finish()

        val intent = Intent(this,LoginActivity::class.java)

        startActivity(intent)
    }

    public fun signOut() {
        auth.signOut()

    }

    public fun emailClicked(view: View){
        var alert = AlertDialog.Builder(this)
        alert.setMessage("Do you want to log out?")
        alert.setCancelable(false)
        alert.setPositiveButton("Log out", DialogInterface.OnClickListener{ dialog, which -> logoutClicked(view) })
        alert.setNegativeButton("Cancel",DialogInterface.OnClickListener{ dialog, which -> dialog.cancel() })

        var a = alert.create()
        a.setTitle("Confirm your action")
        a.show()
    }

    public fun loadFeed(){
        var list = MyXMLParse().execute().get()

        refreshAdapter(list)

        feeds = list
    }

    public fun watchCLicked(position: Int){
        var intent = Intent(this,BrowserActivity::class.java)

        intent.putExtra("url",feeds.get(position).link)
        intent.putExtra("email",auth.currentUser?.email)

        startActivity(intent)
    }

    public fun favClicked(position: Int){
        var feed = feeds.get(position)
        
        if(isInFav(feed)){
            //delete
            dbService.deleteFavorite(auth.currentUser?.email!!,feed)
        }
        else{
            //add
            dbService.addFavorite(auth.currentUser?.email!!,feed)
        }
        favFeeds = dbService.readFavFeeds(auth.currentUser?.email!!)
        refreshAdapter(feeds)
    }

    public fun isInFav(feed:Feed):Boolean{
        for(f in favFeeds){
            if(f.link.equals(feed.link))
                return true
        }
        return false
    }

    fun myFavClicked(view: View){
        var intent = Intent(this,FavoritesActivity::class.java)
        intent.putExtra("email",auth.currentUser?.email.toString())

        startActivityForResult(intent,1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        favFeeds = dbService.readFavFeeds(auth.currentUser?.email!!)
        refreshAdapter(feeds)
    }
    
    


}
