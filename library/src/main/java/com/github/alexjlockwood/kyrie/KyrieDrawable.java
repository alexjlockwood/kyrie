package com.github.alexjlockwood.kyrie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.alexjlockwood.kyrie.Node.asAnimation;
import static com.github.alexjlockwood.kyrie.Node.asAnimations;
import static com.github.alexjlockwood.kyrie.Node.replaceAnimations;
import static com.github.alexjlockwood.kyrie.Node.replaceFirstAnimation;

// TODO: support gradients and/or animatable gradients?
// TODO: support text layers?
// TODO: support image layers?
// TODO: avoid using canvas.clipPath (no anti-alias support)?
// TODO: support color state lists for pathData fill/stroke colors
// TODO: don't bother starting the animator if there are no keyframes
// TODO: allow clients to pass in string paths to keyframes (instead of PathData objects)
// TODO: possibly change PathMorphKeyframeAnimation to take strings instead of PathData objects
// TODO: support odd length stroke dash array
// TODO: add convenience methods to builders (i.e. cornerRadius, bounds, viewport etc.)
// TODO: auto-make paths morphable
// TODO: add more path effects (i.e. path dash path effect)?
// TODO: make it possible for PathData to take a standard Path as a constructor argument?
// TODO: set the default pivot x/y values to be the center of the node?
// TODO: add color getInterpolator helpers (similar to d3?)
// TODO: add 'children' methods to the node builders
// TODO: allow null start values for PVH and Keyframe (and then infer their values)
// TODO: rename 'x/y' property to 'left/top' in RectangleNode?
// TODO: double check for copy/paste errors in the builders/nodes/layers
// TODO: reuse paint/other objects more diligently across layers?
// TODO: make it impossible to add 'transform' wrappers to keyframes over and over and over
// TODO: make all strings/pathdata args non null?
// TODO: make it possible to pass Keyframe<PointF> to translate(), scale(), etc.
// TODO: create more examples, add documentation, add README.md (explain minSdkVersion 14)
// TODO: make it possible to specify resource IDs etc. inside the builders?
// TODO: add support for SVG's preserveAspectRatio attribute
// TODO: make API as small as possible
// TODO: create cache for frequently used objs (paths, paints, etc.)
// TODO: support trimming clip paths?
// TODO: support stroked clip paths?
// TODO: think more about how each node builder has two overloaded methods per property
// TODO: allow user to inflate from xml resource as well as drawable resource?
// TODO: support setting playback speed?
// TODO: support playing animation in reverse?
// TODO: avoid using bitmap internally (encourage view software rendering instead)
// TODO: test inflating multi-file AVDs
public final class KyrieDrawable extends Drawable implements Animatable {
  private static final String TAG = "KyrieDrawable";

  private static final Matrix IDENTITY_MATRIX = new Matrix();

  // Cap the bitmap size, such that it won't hurt the performance too much
  // and it won't crash due to a very large scale.
  // The drawable will look blurry above this size.
  private static final int MAX_CACHED_BITMAP_SIZE = 2048;

  @Nullable
  public static KyrieDrawable create(@NonNull Context context, @DrawableRes int resId) {
    try {
      final KyrieDrawable.Builder builder = KyrieDrawable.builder();
      InflationUtils.inflate(builder, context, resId);
      return builder.build();
    } catch (XmlPullParserException | IOException e) {
      e.printStackTrace();
      Log.e(TAG, "Error parsing drawable", e);
      return null;
    }
  }

  @Px private final int width;
  @Px private final int height;

  @FloatRange(from = 0f)
  private final float viewportWidth;

  @FloatRange(from = 0f)
  private final float viewportHeight;

  @NonNull private final Property<Float> alphaProperty;

  @NonNull private final Timeline timeline;
  @NonNull private final KyrieValueAnimator animator;
  private final List<Node.Layer> childrenLayers = new ArrayList<>();

  @IntRange(from = 0, to = 0xff)
  private int alpha = 0xff;

  @Nullable private ColorStateList tintList;
  @NonNull private Mode tintMode;
  @Nullable private PorterDuffColorFilter tintFilter;
  @Nullable private ColorFilter colorFilter;
  private boolean isAutoMirrored;

  private final Matrix tempMatrix = new Matrix();
  private final float[] tempMatrixFloats = new float[9];
  private final Rect tempBounds = new Rect();
  private final PointF viewportScale = new PointF();
  @Nullable private Bitmap offscreenBitmap;
  @Nullable private Paint offscreenPaint;

  private KyrieDrawable(
      @Px int width,
      @Px int height,
      @FloatRange(from = 0f) float viewportWidth,
      @FloatRange(from = 0f) float viewportHeight,
      @NonNull List<Animation<?, Float>> alphaAnimations,
      @NonNull List<Node> childrenNodes,
      @Nullable ColorStateList tintList,
      @NonNull Mode tintMode,
      boolean isAutoMirrored) {
    this.width = width;
    this.height = height;
    this.viewportWidth = viewportWidth;
    this.viewportHeight = viewportHeight;
    this.isAutoMirrored = isAutoMirrored;
    this.tintList = tintList;
    this.tintMode = tintMode;
    this.tintFilter = createTintFilter();
    timeline = new Timeline(this);
    alphaProperty = timeline.registerAnimatableProperty(alphaAnimations);
    for (int i = 0, size = childrenNodes.size(); i < size; i++) {
      childrenLayers.add(childrenNodes.get(i).toLayer(timeline));
    }
    animator = new KyrieValueAnimator(this);
  }

  @Px
  @Override
  public int getIntrinsicWidth() {
    return width;
  }

  @Px
  @Override
  public int getIntrinsicHeight() {
    return height;
  }

  @Override
  public boolean isStateful() {
    return super.isStateful() || tintList != null && tintList.isStateful();
  }

  @Override
  protected boolean onStateChange(int[] stateSet) {
    if (tintList == null) {
      return false;
    }
    tintFilter = createTintFilter();
    invalidateSelf();
    return true;
  }

  @Override
  public void setTint(@ColorInt int tint) {
    setTintList(ColorStateList.valueOf(tint));
  }

  @Override
  public void setTintList(@Nullable ColorStateList tintList) {
    if (this.tintList != tintList) {
      this.tintList = tintList;
      tintFilter = createTintFilter();
      invalidateSelf();
    }
  }

  @Override
  public void setTintMode(@NonNull Mode tintMode) {
    if (this.tintMode != tintMode) {
      this.tintMode = tintMode;
      tintFilter = createTintFilter();
      invalidateSelf();
    }
  }

  @Nullable
  private PorterDuffColorFilter createTintFilter() {
    if (tintList == null) {
      return null;
    }
    final int tintColor = tintList.getColorForState(getState(), Color.TRANSPARENT);
    return new PorterDuffColorFilter(tintColor, tintMode);
  }

  @Override
  public boolean isAutoMirrored() {
    return isAutoMirrored;
  }

  @Override
  public void setAutoMirrored(boolean mirrored) {
    if (isAutoMirrored != mirrored) {
      isAutoMirrored = mirrored;
      invalidateSelf();
    }
  }

  @Override
  public void setAlpha(@IntRange(from = 0, to = 0xff) int alpha) {
    if (this.alpha != alpha) {
      this.alpha = alpha;
      invalidateSelf();
    }
  }

  @IntRange(from = 0, to = 0xff)
  @Override
  public int getAlpha() {
    return alpha;
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    if (this.colorFilter != colorFilter) {
      this.colorFilter = colorFilter;
      invalidateSelf();
    }
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    if (!isVisible()) {
      return;
    }
    final Rect bounds = tempBounds;
    copyBounds(bounds);
    if (bounds.width() <= 0 || bounds.height() <= 0) {
      return;
    }

    // Color filters always override tint filters.
    final ColorFilter cf = colorFilter == null ? tintFilter : colorFilter;

    // The ImageView can scale the canvas in different ways, so in order to
    // avoid blurry scaling we have to draw into a bitmap with exact pixel
    // size first. This bitmap size is determined by the bounds and the
    // canvas scale.
    canvas.getMatrix(tempMatrix);
    tempMatrix.getValues(tempMatrixFloats);
    float canvasScaleX = Math.abs(tempMatrixFloats[Matrix.MSCALE_X]);
    float canvasScaleY = Math.abs(tempMatrixFloats[Matrix.MSCALE_Y]);
    final float canvasSkewX = Math.abs(tempMatrixFloats[Matrix.MSKEW_X]);
    final float canvasSkewY = Math.abs(tempMatrixFloats[Matrix.MSKEW_Y]);

    // The scale is invalid if there is any rotation or skew.
    if (canvasSkewX != 0 || canvasSkewY != 0) {
      canvasScaleX = 1f;
      canvasScaleY = 1f;
    }

    final int scaledWidth = Math.min((int) (bounds.width() * canvasScaleX), MAX_CACHED_BITMAP_SIZE);
    final int scaledHeight =
        Math.min((int) (bounds.height() * canvasScaleY), MAX_CACHED_BITMAP_SIZE);
    if (scaledWidth <= 0 || scaledHeight <= 0) {
      return;
    }

    final int saveCount = canvas.save();
    canvas.translate(bounds.left, bounds.top);

    // Handle RTL mirroring.
    final boolean shouldAutoMirror =
        isAutoMirrored
            && DrawableCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    if (shouldAutoMirror) {
      canvas.translate(bounds.width(), 0);
      canvas.scale(-1f, 1f);
    }

    // At this point, the canvas has been translated to the right position.
    // We use these bounds as the destination rect when drawing the bitmap, so
    // offset to (0, 0);
    bounds.offsetTo(0, 0);

    // Recreate the offscreen bitmap if the dimensions have changed.
    if (offscreenBitmap == null
        || scaledWidth != offscreenBitmap.getWidth()
        || scaledHeight != offscreenBitmap.getHeight()) {
      offscreenBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
    }

    // Clear the offscreen bitmap.
    offscreenBitmap.eraseColor(Color.TRANSPARENT);
    viewportScale.set(scaledWidth / viewportWidth, scaledHeight / viewportHeight);
    final Canvas offscreenCanvas = new Canvas(offscreenBitmap);
    for (int i = 0, size = childrenLayers.size(); i < size; i++) {
      childrenLayers.get(i).draw(offscreenCanvas, IDENTITY_MATRIX, viewportScale);
    }

    // Draw the offscreen bitmap.
    Paint paint = null;
    final float alphaFloat = (this.alpha / 255f) * alphaProperty.getAnimatedValue();
    final int alphaInt = Math.round(alphaFloat * 255f);
    if (alphaInt < 0xff || cf != null) {
      if (offscreenPaint == null) {
        offscreenPaint = new Paint();
        offscreenPaint.setFilterBitmap(true);
      }
      offscreenPaint.setAlpha(alphaInt);
      offscreenPaint.setColorFilter(cf);
      paint = offscreenPaint;
    }
    canvas.drawBitmap(offscreenBitmap, null, bounds, paint);
    canvas.restoreToCount(saveCount);
  }

  // <editor-fold desc="Animation">

  @Override
  public void start() {
    animator.start();
  }

  @Override
  public void stop() {
    animator.cancel();
  }

  public void pause() {
    animator.pause();
  }

  public void resume() {
    animator.resume();
  }

  public boolean isStarted() {
    return animator.isStarted();
  }

  public boolean isPaused() {
    return animator.isPaused();
  }

  @Override
  public boolean isRunning() {
    return animator.isRunning();
  }

  public void addListener(@NonNull Listener listener) {
    animator.addListener(listener);
  }

  public void removeListener(@NonNull Listener listener) {
    animator.removeListener(listener);
  }

  public void clearListeners() {
    animator.clearListeners();
  }

  public long getTotalDuration() {
    return timeline.getTotalDuration();
  }

  @IntRange(from = 0L)
  public long getCurrentPlayTime() {
    return animator.getCurrentPlayTime();
  }

  public void setCurrentPlayTime(@IntRange(from = 0L) long currentPlayTime) {
    currentPlayTime = Math.max(0, currentPlayTime);
    final long totalDuration = getTotalDuration();
    if (totalDuration != Animation.INFINITE) {
      currentPlayTime = Math.min(totalDuration, currentPlayTime);
    }
    animator.setCurrentPlayTime(currentPlayTime);
  }

  /**
   * A listener that receives notifications from an animation. Notifications indicate animation
   * related events, such as the start or end of the animation.
   */
  public abstract static class Listener {
    /**
     * Notifies the start of the animation.
     *
     * @param drawable The KyrieDrawable instance being started.
     */
    public void onAnimationStart(@NonNull KyrieDrawable drawable) {}

    /**
     * Notifies the occurrence of another frame of the animation.
     *
     * @param drawable The KyrieDrawable instance being updated.
     */
    public void onAnimationUpdate(@NonNull KyrieDrawable drawable) {}

    /**
     * Notifies that the animation was paused.
     *
     * @param drawable The KyrieDrawable instance being paused.
     * @see #pause()
     */
    public void onAnimationPause(@NonNull KyrieDrawable drawable) {}

    /**
     * Notifies that the animation was resumed, after being previously paused.
     *
     * @param drawable The KyrieDrawable instance being resumed.
     * @see #resume()
     */
    public void onAnimationResume(@NonNull KyrieDrawable drawable) {}

    /**
     * Notifies the cancellation of the animation.
     *
     * @param drawable The KyrieDrawable instance being canceled.
     */
    public void onAnimationCancel(@NonNull KyrieDrawable drawable) {}

    /**
     * Notifies the end of the animation. This callback is not invoked for animations with repeat
     * count set to INFINITE.
     *
     * @param drawable The KyrieDrawable instance being ended.
     */
    public void onAnimationEnd(@NonNull KyrieDrawable drawable) {}
  }

  private static class KyrieValueAnimator extends ValueAnimator {
    private final KyrieDrawable drawable;
    private final List<Listener> listeners = new ArrayList<>();
    private boolean isPaused;

    @IntRange(from = 0L)
    private long currentPlayTime;

    private final AnimatorListenerAdapter listenerAdapter =
        new AnimatorListenerAdapter() {
          @Override
          public void onAnimationStart(Animator animation) {
            for (int i = 0, size = listeners.size(); i < size; i++) {
              listeners.get(i).onAnimationStart(drawable);
            }
          }

          @Override
          public void onAnimationCancel(Animator animation) {
            for (int i = 0, size = listeners.size(); i < size; i++) {
              listeners.get(i).onAnimationCancel(drawable);
            }
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            for (int i = 0, size = listeners.size(); i < size; i++) {
              listeners.get(i).onAnimationEnd(drawable);
            }
          }
        };

    public KyrieValueAnimator(@NonNull KyrieDrawable d) {
      drawable = d;
      setFloatValues(0f, 1f);
      setInterpolator(new LinearInterpolator());
      final Timeline timeline = drawable.timeline;
      addUpdateListener(
          new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              currentPlayTime = animation.getCurrentPlayTime();
              timeline.setCurrentPlayTime(currentPlayTime);
              for (int i = 0, size = listeners.size(); i < size; i++) {
                listeners.get(i).onAnimationUpdate(drawable);
              }
            }
          });
      final long totalDuration = timeline.getTotalDuration();
      setDuration(totalDuration == Animation.INFINITE ? Long.MAX_VALUE : totalDuration);
    }

    @Override
    public void pause() {
      if (isStarted() && !isPaused) {
        isPaused = true;
        final long currentPlayTime = this.currentPlayTime;
        cancelWithoutNotify();
        setCurrentPlayTime(currentPlayTime);
        for (int i = 0, size = listeners.size(); i < size; i++) {
          listeners.get(i).onAnimationPause(drawable);
        }
      }
    }

    private void cancelWithoutNotify() {
      removeListener(listenerAdapter);
      cancel();
      addListener(listenerAdapter);
    }

    @Override
    public void resume() {
      if (isPaused) {
        isPaused = false;
        final long currentPlayTime = this.currentPlayTime;
        startWithoutNotify();
        setCurrentPlayTime(currentPlayTime);
        for (int i = 0, size = listeners.size(); i < size; i++) {
          listeners.get(i).onAnimationResume(drawable);
        }
      }
    }

    private void startWithoutNotify() {
      removeListener(listenerAdapter);
      start();
      addListener(listenerAdapter);
    }

    public boolean isPaused() {
      return isPaused;
    }

    public void addListener(@NonNull Listener listener) {
      listeners.add(listener);
    }

    public void removeListener(@NonNull Listener listener) {
      listeners.remove(listener);
    }

    public void clearListeners() {
      listeners.clear();
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private static final Mode DEFAULT_TINT_MODE = Mode.SRC_IN;

    private int width = -1;
    private int height = -1;
    private float viewportWidth = -1;
    private float viewportHeight = -1;
    @NonNull private final List<Animation<?, Float>> alpha = asAnimations(1f);
    private final List<Node> children = new ArrayList<>();
    private boolean isAutoMirrored;
    @Nullable private ColorStateList tintList;
    @NonNull private Mode tintMode = DEFAULT_TINT_MODE;

    private Builder() {}

    // Dimensions.

    public final Builder width(@Px int width) {
      this.width = width;
      return this;
    }

    public final Builder height(@Px int height) {
      this.height = height;
      return this;
    }

    public final Builder dimensions(@Px int width, @Px int height) {
      width(width);
      height(height);
      return this;
    }

    // Viewport

    public final Builder viewportWidth(@FloatRange(from = 0f) float viewportWidth) {
      this.viewportWidth = viewportWidth;
      return this;
    }

    public final Builder viewportHeight(@FloatRange(from = 0f) float viewportHeight) {
      this.viewportHeight = viewportHeight;
      return this;
    }

    public final Builder viewport(
        @FloatRange(from = 0f) float viewportWidth, @FloatRange(from = 0f) float viewportHeight) {
      viewportWidth(viewportWidth);
      viewportHeight(viewportHeight);
      return this;
    }

    // Alpha.

    public final Builder alpha(@FloatRange(from = 0f, to = 1f) float alpha) {
      replaceFirstAnimation(this.alpha, asAnimation(alpha));
      return this;
    }

    @SafeVarargs
    public final Builder alpha(@NonNull Animation<?, Float>... animations) {
      replaceAnimations(alpha, animations);
      return this;
    }

    public final Builder alpha(@NonNull List<Animation<?, Float>> animations) {
      replaceAnimations(alpha, animations);
      return this;
    }

    // Auto-mirror.

    public final Builder autoMirrored(boolean isAutoMirrored) {
      this.isAutoMirrored = isAutoMirrored;
      return this;
    }

    // Tint.

    public final Builder tint(@ColorInt int tint) {
      return tintList(ColorStateList.valueOf(tint));
    }

    public final Builder tintList(@Nullable ColorStateList tintList) {
      this.tintList = tintList;
      return this;
    }

    public final Builder tintMode(@NonNull Mode tintMode) {
      this.tintMode = tintMode;
      return this;
    }

    // Children.

    public final Builder child(@NonNull Node node) {
      children.add(node);
      return this;
    }

    public final Builder child(@NonNull Node.Builder builder) {
      return child(builder.build());
    }

    public final KyrieDrawable build() {
      if (viewportWidth <= 0 || viewportHeight <= 0) {
        throw new IllegalStateException(
            "Viewport width/height must be greater than 0: "
                + "viewportWidth="
                + viewportWidth
                + ", viewportHeight="
                + viewportHeight);
      }
      if (width < 0 && height < 0) {
        width = Math.round(viewportWidth);
        height = Math.round(viewportHeight);
      } else if (width < 0) {
        width = Math.round(height * viewportWidth / viewportHeight);
      } else if (height < 0) {
        height = Math.round(width * viewportHeight / viewportWidth);
      }
      // TODO: handle viewport/dimensions aspect ratio mismatch using preserveAspectRatio
      return new KyrieDrawable(
          width,
          height,
          viewportWidth,
          viewportHeight,
          alpha,
          children,
          tintList,
          tintMode,
          isAutoMirrored);
    }
  }

  // </editor-fold>
}
