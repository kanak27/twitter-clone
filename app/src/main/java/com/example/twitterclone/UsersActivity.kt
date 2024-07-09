package com.example.twitterclone

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback

class UsersActivity : AppCompatActivity() {

    private var users = ArrayList<String>()

    private lateinit var adapter : ArrayAdapter<*>

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.Tweet){
            val builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Send a Tweet")

            val tweetEditText = EditText(this)

            builder.setView(tweetEditText)
            builder.setPositiveButton("Send"
            ) { _, _ ->
                Log.i("Info", tweetEditText.text.toString())

                val tweet : ParseObject = ParseObject("Tweet")
                tweet.put("tweet", tweetEditText.text.toString())
                tweet.put("username", ParseUser.getCurrentUser().username)

                tweet.saveInBackground { e ->
                    if (e == null) {
                        Toast.makeText(this@UsersActivity, "Tweet sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UsersActivity, "Tweet failed :(", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }

            builder.setNegativeButton("Cancel"
            ) { dialog, _ ->
                Log.i("Info", "I don't wanna tweet")
                dialog?.cancel()
            }

            builder.show()

        } else if (item.itemId == R.id.logout){
            ParseUser.logOut()

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.viewFeed){
            val intent = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        setSupportActionBar(findViewById(R.id.menuBar))
        title = "User List"

        val userList : ListView = findViewById(R.id.userList)
        userList.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_checked, users)
        userList.adapter = adapter

        userList.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, pos, _ ->
                val checkedTextView : CheckedTextView = view as CheckedTextView

                if(checkedTextView.isChecked){
                    Log.i("Info", "Checked")
                    ParseUser.getCurrentUser().add("isfollowing", users[pos])
                }
                else{
                    Log.i("Info", "NOT Checked")
                    ParseUser.getCurrentUser().getList<String>("isfollowing").remove(users[pos])
                    val tempUsers = ParseUser.getCurrentUser().getList<String>("isfollowing")
                    ParseUser.getCurrentUser().remove("isfollowing")
                    ParseUser.getCurrentUser().put("isfollowing", tempUsers)
                }
                ParseUser.getCurrentUser().saveInBackground()
            }
        
        
        val query : ParseQuery<ParseUser> = ParseUser.getQuery()
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().username)
        
        query.findInBackground { objects, e ->
            if (e == null) {
                if (objects != null) {
                    for (user in objects) {
                        users.add(user.username)
                    }

                    adapter.notifyDataSetChanged()

                    for (username in users){
                        if(ParseUser.getCurrentUser().getList<String>("isfollowing")?.contains(username) == true){
                            userList.setItemChecked(users.indexOf(username), true)
                        }
                    }
                }
            }
        }
    }
}