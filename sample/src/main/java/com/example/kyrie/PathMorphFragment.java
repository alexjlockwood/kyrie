package com.example.kyrie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.Keyframe;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;

public class PathMorphFragment extends Fragment {
  private static final int DURATION = 1500;

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

    final KyrieDrawable drawable = createDrawable();
    imageView.setImageDrawable(drawable);
    imageView.setOnClickListener(new SampleOnClickListener(drawable));
    seekBar.setOnSeekBarChangeListener(new SampleOnSeekBarChangeListener(drawable));
  }

  private KyrieDrawable createDrawable() {
    final Context ctx = getContext();
    final PathData hippoPathData = PathData.parse(getString(R.string.hippo));
    final PathData elephantPathData = PathData.parse(getString(R.string.elephant));
    final PathData buffaloPathData = PathData.parse(getString(R.string.buffalo));
    final int hippoFillColor = ContextCompat.getColor(ctx, R.color.hippo);
    final int elephantFillColor = ContextCompat.getColor(ctx, R.color.elephant);
    final int buffaloFillColor = ContextCompat.getColor(ctx, R.color.buffalo);
    final KyrieDrawable kyrieDrawable =
        KyrieDrawable.builder()
            .viewport(409, 280)
            .child(
                PathNode.builder()
                    .pathData(
                        Animation.ofPathMorph(
                                Keyframe.of(0, hippoPathData),
                                Keyframe.of(0.2f, elephantPathData),
                                Keyframe.of(0.4f, elephantPathData),
                                Keyframe.of(0.6f, buffaloPathData),
                                Keyframe.of(0.8f, buffaloPathData),
                                Keyframe.of(1, hippoPathData))
                            .duration(DURATION))
                    .fillColor(
                        Animation.ofArgb(
                                Keyframe.of(0, hippoFillColor),
                                Keyframe.of(0.2f, elephantFillColor),
                                Keyframe.of(0.4f, elephantFillColor),
                                Keyframe.of(0.6f, buffaloFillColor),
                                Keyframe.of(0.8f, buffaloFillColor),
                                Keyframe.of(1, hippoFillColor))
                            .duration(DURATION)))
            .build();
    kyrieDrawable.addListener(new SampleListenerAdapter(seekBar));
    return kyrieDrawable;
  }
}
