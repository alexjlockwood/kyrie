package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

final class PropertyTimeline {
  private final List<Property<?>> properties = new ArrayList<>();
  private final Property.Listener listener =
      new Property.Listener() {
        @Override
        public void onCurrentPlayTimeChanged(@NonNull Property<?> property) {
          drawable.invalidateSelf();
        }
      };

  @NonNull private final KyrieDrawable drawable;
  private long totalDuration;

  public PropertyTimeline(KyrieDrawable drawable) {
    this.drawable = drawable;
  }

  @NonNull
  public <V> Property<V> registerAnimatableProperty(List<Animation<?, V>> animations) {
    final Property<V> property = new Property<>(animations);
    properties.add(property);
    property.addListener(listener);
    if (totalDuration != Animation.INFINITE) {
      final long currTotalDuration = property.getTotalDuration();
      if (currTotalDuration == Animation.INFINITE) {
        totalDuration = Animation.INFINITE;
      } else {
        totalDuration = Math.max(currTotalDuration, totalDuration);
      }
    }
    return property;
  }

  public void setCurrentPlayTime(@IntRange(from = 0) long currentPlayTime) {
    for (int i = 0, size = properties.size(); i < size; i++) {
      properties.get(i).setCurrentPlayTime(currentPlayTime);
    }
  }

  public long getTotalDuration() {
    return totalDuration;
  }
}
