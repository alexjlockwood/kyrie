package com.example.kyrie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.alexjlockwood.kyrie.KyrieDrawable;

public class HeartbreakFragment extends Fragment {

  private ImageView imageView;
  private SeekBar seekBar;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_seekbar, container, false);
    imageView = view.findViewById(R.id.image_view);
    seekBar = view.findViewById(R.id.seekbar);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final KyrieDrawable drawable = KyrieDrawable.create(requireContext(), R.drawable.avd_heartbreak);
    drawable.addListener(new SampleListenerAdapter(seekBar));
    imageView.setImageDrawable(drawable);
    imageView.setOnClickListener(new SampleOnClickListener(drawable));
    seekBar.setOnSeekBarChangeListener(new SampleOnSeekBarChangeListener(drawable));
  }
}
