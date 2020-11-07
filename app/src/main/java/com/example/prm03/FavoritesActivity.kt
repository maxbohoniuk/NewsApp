package com.example.prm03

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {

    lateinit var favAdapter : MyFavAdapter
    lateinit var favFeeds:List<Feed>
    lateinit var email:String

    var dbService = DatabaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        email = intent.getStringExtra("email").toString()

        favFeeds = dbService.readFavFeeds(email)

        favrecyclerView.layoutManager = LinearLayoutManager(this)
        favAdapter = MyFavAdapter(this)
        favrecyclerView.adapter = favAdapter

        refreshFavAdapter(favFeeds)




    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishActivity(1)
    }

    private fun refreshFavAdapter(list: List<Feed>) {
        favAdapter.refresh(list)
    }

    public fun watchCLicked(position: Int){
        var intent = Intent(this,BrowserActivity::class.java)

        intent.putExtra("url",favFeeds.get(position).link)
        intent.putExtra("email",email)

        startActivity(intent)
    }

    public fun favClicked(position: Int){
        var feed = favFeeds.get(position)


        //delete
        dbService.deleteFavorite(email,feed)

        favFeeds = dbService.readFavFeeds(email)

        refreshFavAdapter(favFeeds)
    }
}
