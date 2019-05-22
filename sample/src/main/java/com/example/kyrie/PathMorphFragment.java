package com.example.kyrie;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.Keyframe;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;

public class PathMorphFragment extends Fragment {
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
    final Context ctx = requireContext();
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
                    .strokeColor(Color.BLACK)
                    .strokeWidth(1f)
                    .fillColor(
                        Animation.ofArgb(hippoFillColor, elephantFillColor).duration(300),
                        Animation.ofArgb(buffaloFillColor).startDelay(600).duration(300),
                        Animation.ofArgb(hippoFillColor).startDelay(1200).duration(300))
                    .pathData(
                        Animation.ofPathMorph(
                                Keyframe.of(0, hippoPathData),
                                Keyframe.of(0.2f, elephantPathData),
                                Keyframe.of(0.4f, elephantPathData),
                                Keyframe.of(0.6f, buffaloPathData),
                                Keyframe.of(0.8f, buffaloPathData),
                                Keyframe.of(1, hippoPathData))
                            .duration(1500)))
            .build();
    kyrieDrawable.addListener(new SampleListenerAdapter(seekBar));
    return kyrieDrawable;
  }
}
