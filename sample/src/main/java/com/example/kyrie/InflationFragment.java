package com.example.kyrie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.alexjlockwood.kyrie.KyrieDrawable;

public class InflationFragment extends Fragment {
  private ImageView imageView;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_default, container, false);
    imageView = view.findViewById(R.id.image_view);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final KyrieDrawable drawable = KyrieDrawable.create(getContext(), R.drawable.avd);
    //    final AnimatedVectorDrawableCompat drawable =
    //        AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd);
    imageView.setImageDrawable(drawable);
    imageView.setOnClickListener(v -> drawable.start());
  }
}
