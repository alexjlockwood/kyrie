package com.example.kyrie;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements DemoListFragment.Callbacks,
    FragmentManager.OnBackStackChangedListener {
private static final String STATE_TITLE = "state_title";

  private FragmentManager fragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    fragmentManager = getSupportFragmentManager();
    fragmentManager.addOnBackStackChangedListener(this);
    if (savedInstanceState == null) {
      fragmentManager
          .beginTransaction()
          .add(R.id.container, new DemoListFragment())
          .commit();
    } else {
      getSupportActionBar().setTitle(savedInstanceState.getString(STATE_TITLE));
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putCharSequence(STATE_TITLE, getSupportActionBar().getTitle());
  }

  @Override
  public void onListItemClick(@NonNull DemoListFragment.Demo demo) {
    final String fragmentName = demo.fragmentName;
    final Fragment fragment = Fragment.instantiate(this, fragmentName);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.container, fragment)
        .addToBackStack(demo.title)
        .commit();
  }


  @Override
  public void onBackStackChanged() {
    // This is pretty hacky but whatevs...
    final int entryCount = fragmentManager.getBackStackEntryCount();
    final String title =
        entryCount == 0
            ? getString(R.string.app_name)
            : fragmentManager.getBackStackEntryAt(entryCount - 1).getName();
    setTitle(title);
  }
}


