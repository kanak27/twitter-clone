package com.example.twitterclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.HashMap

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val listView : ListView = findViewById(R.id.listView)

        val tweetData : MutableList<Map<String, String>> = ArrayList()
        
        val query : ParseQuery<ParseObject> = ParseQuery.getQuery("Tweet")
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isfollowing"))
        query.orderByDescending("createdAt")
        query.limit = 20
        
        query.findInBackground { objects, e ->
            if (e == null) {
                if (objects != null) {
                    for (tweet: ParseObject in objects) {
                        val tweetInfo: MutableMap<String, String> = HashMap<String, String>()
                        tweetInfo["content"] = tweet.getString("tweet")
                        tweetInfo["username"] = tweet.getString("username")
                        tweetData.add(tweetInfo)
                    }
                    val simpleAdapter = SimpleAdapter(this@FeedActivity, tweetData, android.R.layout.simple_list_item_2, arrayOf("content", "username"), intArrayOf( android.R.id.text1, android.R.id.text2))
                    listView.adapter = simpleAdapter
                }
            }
        }
    }
}