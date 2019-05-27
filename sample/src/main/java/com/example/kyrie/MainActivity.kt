package com.example.kyrie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

private const val STATE_TITLE = "state_title"

class MainActivity : AppCompatActivity(), DemoListFragment.Callbacks, FragmentManager.OnBackStackChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, DemoListFragment())
                    .commit()
        } else {
            supportActionBar!!.setTitle(savedInstanceState.getString(STATE_TITLE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(STATE_TITLE, supportActionBar!!.title)
    }

    override fun onListItemClick(demo: DemoListFragment.Demo) {
        val fragmentName = demo.fragmentClassName
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, fragmentName)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(demo.title)
                .commit()
    }


    override fun onBackStackChanged() {
        // This is pretty hacky but whatevs...
        val entryCount = supportFragmentManager.backStackEntryCount
        val title = if (entryCount == 0) {
            getString(R.string.app_name)
        } else {
            supportFragmentManager.getBackStackEntryAt(entryCount - 1).name
        }
        setTitle(title)
    }
}


