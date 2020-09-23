package edu.gvsu.cis.traxy

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs

class JournalViewActivity : AppCompatActivity() {

    val args by navArgs<JournalViewActivityArgs>()
    val mediaModel by viewModels<MediaViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_view)
        setSupportActionBar(findViewById(R.id.toolbar))

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    override fun onResume() {
        super.onResume()
        mediaModel.journalKey.value = args.JOURNALKEY
        supportActionBar?.title = args.JOURNALNAME
    }
}