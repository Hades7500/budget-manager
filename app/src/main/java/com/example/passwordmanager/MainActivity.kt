package com.example.passwordmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val categories = Categories()
        val transactions = Transactions()
        val settings = Settings()

        setCurrentFragment(categories)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.categories -> setCurrentFragment(categories)
                R.id.transactions -> setCurrentFragment(transactions)
                R.id.settings -> setCurrentFragment(settings)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.f1Fragment, fragment)
            commit()
        }
    }
}