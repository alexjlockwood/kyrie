package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

final class PropertyTimeline {

  private final List<AnimatableProperty<?>> properties = new ArrayList<>();
  private final AnimatableProperty.Listener listener =
      new AnimatableProperty.Listener() {
        @Override
        public void onCurrentPlayTimeChanged(@NonNull AnimatableProperty<?> property) {
          drawable.invalidateSelf();
        }
      };

  @NonNull private final KyrieDrawable drawable;
  private long totalDuration;

  public PropertyTimeline(@NonNull KyrieDrawable drawable) {
    this.drawable = drawable;
  }

  @NonNull
  public <V> AnimatableProperty<V> registerAnimatableProperty(
      @NonNull List<PropertyAnimation<?, V>> animations) {
    final AnimatableProperty<V> property = new AnimatableProperty<>(animations);
    properties.add(property);
    property.addListener(listener);
    if (totalDuration != PropertyAnimation.INFINITE) {
      final long currTotalDuration = property.getTotalDuration();
      if (currTotalDuration == PropertyAnimation.INFINITE) {
        totalDuration = PropertyAnimation.INFINITE;
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
