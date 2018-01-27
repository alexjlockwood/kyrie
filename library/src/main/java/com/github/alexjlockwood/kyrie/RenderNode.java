package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

abstract class RenderNode extends BaseNode {
  @NonNull private final List<Animation<?, Integer>> fillColor;
  @NonNull private final List<Animation<?, Float>> fillAlpha;
  @NonNull private final List<Animation<?, Integer>> strokeColor;
  @NonNull private final List<Animation<?, Float>> strokeAlpha;
  @NonNull private final List<Animation<?, Float>> strokeWidth;
  @NonNull private final List<Animation<?, Float>> trimPathStart;
  @NonNull private final List<Animation<?, Float>> trimPathEnd;
  @NonNull private final List<Animation<?, Float>> trimPathOffset;
  @StrokeLineCap private final int strokeLineCap;
  @StrokeLineJoin private final int strokeLineJoin;
  @NonNull private final List<Animation<?, Float>> strokeMiterLimit;
  @NonNull private final List<Animation<?, float[]>> strokeDashArray;
  @NonNull private final List<Animation<?, Float>> strokeDashOffset;
  @FillType private final int fillType;
  private final boolean isStrokeScaling;

  RenderNode(
      @NonNull List<Animation<?, Float>> rotation,
      @NonNull List<Animation<?, Float>> pivotX,
      @NonNull List<Animation<?, Float>> pivotY,
      @NonNull List<Animation<?, Float>> scaleX,
      @NonNull List<Animation<?, Float>> scaleY,
      @NonNull List<Animation<?, Float>> translateX,
      @NonNull List<Animation<?, Float>> translateY,
      @NonNull List<Animation<?, Integer>> fillColor,
      @NonNull List<Animation<?, Float>> fillAlpha,
      @NonNull List<Animation<?, Integer>> strokeColor,
      @NonNull List<Animation<?, Float>> strokeAlpha,
      @NonNull List<Animation<?, Float>> strokeWidth,
      @NonNull List<Animation<?, Float>> trimPathStart,
      @NonNull List<Animation<?, Float>> trimPathEnd,
      @NonNull List<Animation<?, Float>> trimPathOffset,
      @StrokeLineCap int strokeLineCap,
      @StrokeLineJoin int strokeLineJoin,
      @NonNull List<Animation<?, Float>> strokeMiterLimit,
      @NonNull List<Animation<?, float[]>> strokeDashArray,
      @NonNull List<Animation<?, Float>> strokeDashOffset,
      @FillType int fillType,
      boolean isStrokeScaling) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.fillColor = fillColor;
    this.fillAlpha = fillAlpha;
    this.strokeColor = strokeColor;
    this.strokeAlpha = strokeAlpha;
    this.strokeWidth = strokeWidth;
    this.trimPathStart = trimPathStart;
    this.trimPathEnd = trimPathEnd;
    this.trimPathOffset = trimPathOffset;
    this.strokeLineCap = strokeLineCap;
    this.strokeLineJoin = strokeLineJoin;
    this.strokeMiterLimit = strokeMiterLimit;
    this.strokeDashArray = strokeDashArray;
    this.strokeDashOffset = strokeDashOffset;
    this.fillType = fillType;
    this.isStrokeScaling = isStrokeScaling;
  }

  @NonNull
  public final List<Animation<?, Integer>> getFillColor() {
    return fillColor;
  }

  @NonNull
  public final List<Animation<?, Float>> getFillAlpha() {
    return fillAlpha;
  }

  @NonNull
  public final List<Animation<?, Integer>> getStrokeColor() {
    return strokeColor;
  }

  @NonNull
  public final List<Animation<?, Float>> getStrokeAlpha() {
    return strokeAlpha;
  }

  @NonNull
  public final List<Animation<?, Float>> getStrokeWidth() {
    return strokeWidth;
  }

  @NonNull
  public final List<Animation<?, Float>> getTrimPathStart() {
    return trimPathStart;
  }

  @NonNull
  public final List<Animation<?, Float>> getTrimPathEnd() {
    return trimPathEnd;
  }

  @NonNull
  public final List<Animation<?, Float>> getTrimPathOffset() {
    return trimPathOffset;
  }

  @StrokeLineCap
  public final int getStrokeLineCap() {
    return strokeLineCap;
  }

  @StrokeLineJoin
  public final int getStrokeLineJoin() {
    return strokeLineJoin;
  }

  @NonNull
  public final List<Animation<?, Float>> getStrokeMiterLimit() {
    return strokeMiterLimit;
  }

  @NonNull
  public final List<Animation<?, float[]>> getStrokeDashArray() {
    return strokeDashArray;
  }

  @NonNull
  public final List<Animation<?, Float>> getStrokeDashOffset() {
    return strokeDashOffset;
  }

  @FillType
  public final int getFillType() {
    return fillType;
  }

  public final boolean isStrokeScaling() {
    return isStrokeScaling;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  abstract RenderLayer toLayer(@NonNull Timeline timeline);

  abstract static class RenderLayer extends BaseLayer {
    @NonNull private final Property<Integer> fillColor;
    @NonNull private final Property<Float> fillAlpha;
    @NonNull private final Property<Integer> strokeColor;
    @NonNull private final Property<Float> strokeAlpha;
    @NonNull private final Property<Float> strokeWidth;
    @NonNull private final Property<Float> trimPathStart;
    @NonNull private final Property<Float> trimPathEnd;
    @NonNull private final Property<Float> trimPathOffset;
    @StrokeLineCap private final int strokeLineCap;
    @StrokeLineJoin private final int strokeLineJoin;
    @NonNull private final Property<Float> strokeMiterLimit;
    @NonNull private final Property<float[]> strokeDashArray;
    @NonNull private final Property<Float> strokeDashOffset;
    @FillType private final int fillType;
    private boolean isStrokeScaling;

    private final Matrix tempMatrix = new Matrix();
    private final Path tempPath = new Path();
    private final Path tempRenderPath = new Path();
    @Nullable private Paint tempStrokePaint;
    @Nullable private Paint tempFillPaint;
    @Nullable private PathMeasure tempPathMeasure;
    @Nullable private float[] tempStrokeDashArray;

    public RenderLayer(@NonNull Timeline timeline, @NonNull RenderNode node) {
      super(timeline, node);
      fillColor = registerAnimatableProperty(node.getFillColor());
      fillAlpha = registerAnimatableProperty(node.getFillAlpha());
      strokeColor = registerAnimatableProperty(node.getStrokeColor());
      strokeAlpha = registerAnimatableProperty(node.getStrokeAlpha());
      strokeWidth = registerAnimatableProperty(node.getStrokeWidth());
      trimPathStart = registerAnimatableProperty(node.getTrimPathStart());
      trimPathEnd = registerAnimatableProperty(node.getTrimPathEnd());
      trimPathOffset = registerAnimatableProperty(node.getTrimPathOffset());
      strokeLineCap = node.getStrokeLineCap();
      strokeLineJoin = node.getStrokeLineJoin();
      strokeMiterLimit = registerAnimatableProperty(node.getStrokeMiterLimit());
      strokeDashArray = registerAnimatableProperty(node.getStrokeDashArray());
      strokeDashOffset = registerAnimatableProperty(node.getStrokeDashOffset());
      fillType = node.getFillType();
      isStrokeScaling = node.isStrokeScaling();
    }

    public abstract void onInitPath(@NonNull Path outPath);

    @Override
    public final void onDraw(
        @NonNull Canvas canvas, @NonNull Matrix parentMatrix, @NonNull PointF viewportScale) {
      final float matrixScale = getMatrixScale(parentMatrix);
      if (matrixScale == 0) {
        return;
      }

      final float scaleX = viewportScale.x;
      final float scaleY = viewportScale.y;
      tempMatrix.set(parentMatrix);
      if (scaleX != 1f || scaleY != 1f) {
        tempMatrix.postScale(scaleX, scaleY);
      }

      tempPath.reset();
      onInitPath(tempPath);
      applyTrimPathIfNeeded(tempPath);
      tempRenderPath.reset();
      tempRenderPath.addPath(tempPath, tempMatrix);
      drawFillIfNeeded(canvas, tempRenderPath);
      final float strokeScaleFactor =
          Math.min(scaleX, scaleY) * (isStrokeScaling ? matrixScale : 1);
      drawStrokeIfNeeded(canvas, tempRenderPath, strokeScaleFactor);
    }

    private void applyTrimPathIfNeeded(@NonNull Path outPath) {
      final float trimPathStart = this.trimPathStart.getAnimatedValue();
      final float trimPathEnd = this.trimPathEnd.getAnimatedValue();
      final float trimPathOffset = this.trimPathOffset.getAnimatedValue();
      if (trimPathStart == 0f && trimPathEnd == 1f) {
        return;
      }
      float start = (trimPathStart + trimPathOffset) % 1f;
      float end = (trimPathEnd + trimPathOffset) % 1f;
      if (tempPathMeasure == null) {
        tempPathMeasure = new PathMeasure();
      }
      tempPathMeasure.setPath(outPath, false);
      final float len = tempPathMeasure.getLength();
      start = start * len;
      end = end * len;
      outPath.reset();
      if (start > end) {
        tempPathMeasure.getSegment(start, len, outPath, true);
        tempPathMeasure.getSegment(0f, end, outPath, true);
      } else {
        tempPathMeasure.getSegment(start, end, outPath, true);
      }
      // Required for Android 4.4 and earlier.
      outPath.rLineTo(0f, 0f);
    }

    private void drawFillIfNeeded(@NonNull Canvas canvas, @NonNull Path path) {
      final int fillColor = this.fillColor.getAnimatedValue();
      final float fillAlpha = this.fillAlpha.getAnimatedValue();
      if (fillColor == Color.TRANSPARENT) {
        return;
      }
      if (tempFillPaint == null) {
        tempFillPaint = new Paint();
        tempFillPaint.setStyle(Paint.Style.FILL);
        tempFillPaint.setAntiAlias(true);
      }
      final Paint paint = tempFillPaint;
      paint.setColor(applyAlpha(fillColor, fillAlpha));
      path.setFillType(getPaintFillType(fillType));
      canvas.drawPath(path, paint);
    }

    private void drawStrokeIfNeeded(
        @NonNull Canvas canvas, @NonNull Path path, float strokeScaleFactor) {
      final int strokeColor = this.strokeColor.getAnimatedValue();
      final float strokeAlpha = this.strokeAlpha.getAnimatedValue();
      final float strokeWidth = this.strokeWidth.getAnimatedValue();
      if (strokeColor == Color.TRANSPARENT || strokeWidth == 0) {
        return;
      }
      if (tempStrokePaint == null) {
        tempStrokePaint = new Paint();
        tempStrokePaint.setStyle(Paint.Style.STROKE);
        tempStrokePaint.setAntiAlias(true);
      }
      final Paint paint = tempStrokePaint;
      paint.setStrokeCap(getPaintStrokeLineCap(strokeLineCap));
      paint.setStrokeJoin(getPaintStrokeLineJoin(strokeLineJoin));
      paint.setStrokeMiter(strokeMiterLimit.getAnimatedValue());
      paint.setColor(applyAlpha(strokeColor, strokeAlpha));
      paint.setStrokeWidth(strokeWidth * strokeScaleFactor);
      // TODO: can/should we cache path effects?
      paint.setPathEffect(getDashPathEffect(strokeScaleFactor));
      canvas.drawPath(path, paint);
    }

    @Nullable
    private DashPathEffect getDashPathEffect(float strokeScaleFactor) {
      final float[] strokeDashArray = this.strokeDashArray.getAnimatedValue();
      if (strokeDashArray.length == 0) {
        return null;
      }
      final float strokeDashOffset = this.strokeDashOffset.getAnimatedValue();
      if (tempStrokeDashArray == null || tempStrokeDashArray.length != strokeDashArray.length) {
        tempStrokeDashArray = new float[strokeDashArray.length];
      }
      for (int i = 0; i < tempStrokeDashArray.length; i++) {
        tempStrokeDashArray[i] = strokeDashArray[i] * strokeScaleFactor;
      }
      return new DashPathEffect(tempStrokeDashArray, strokeDashOffset);
    }

    @ColorInt
    private static int applyAlpha(@ColorInt int color, float alpha) {
      final int alphaBytes = Color.alpha(color);
      color &= 0x00FFFFFF;
      color |= ((int) (alphaBytes * alpha)) << 24;
      return color;
    }

    private static Paint.Cap getPaintStrokeLineCap(@StrokeLineCap int strokeLineCap) {
      switch (strokeLineCap) {
        case StrokeLineCap.BUTT:
          return Paint.Cap.BUTT;
        case StrokeLineCap.ROUND:
          return Paint.Cap.ROUND;
        case StrokeLineCap.SQUARE:
          return Paint.Cap.SQUARE;
        default:
          throw new IllegalArgumentException("Invalid stroke line cap: " + strokeLineCap);
      }
    }

    private static Paint.Join getPaintStrokeLineJoin(@StrokeLineJoin int strokeLineJoin) {
      switch (strokeLineJoin) {
        case StrokeLineJoin.MITER:
          return Paint.Join.MITER;
        case StrokeLineJoin.ROUND:
          return Paint.Join.ROUND;
        case StrokeLineJoin.BEVEL:
          return Paint.Join.BEVEL;
        default:
          throw new IllegalArgumentException("Invalid stroke line join: " + strokeLineJoin);
      }
    }

    private static Path.FillType getPaintFillType(@FillType int fillType) {
      switch (fillType) {
        case FillType.NON_ZERO:
          return Path.FillType.WINDING;
        case FillType.EVEN_ODD:
          return Path.FillType.EVEN_ODD;
        default:
          throw new IllegalArgumentException("Invalid fill type: " + fillType);
      }
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  abstract static class Builder<N extends RenderNode, B extends Builder<N, B>>
      extends BaseNode.Builder<N, B> {
    @NonNull final List<Animation<?, Integer>> fillColor = asAnimations(Color.TRANSPARENT);
    @NonNull final List<Animation<?, Float>> fillAlpha = asAnimations(1f);

    @NonNull final List<Animation<?, Integer>> strokeColor = asAnimations(Color.TRANSPARENT);

    @NonNull final List<Animation<?, Float>> strokeAlpha = asAnimations(1f);
    @NonNull final List<Animation<?, Float>> strokeWidth = asAnimations(0f);
    @NonNull final List<Animation<?, Float>> trimPathStart = asAnimations(0f);
    @NonNull final List<Animation<?, Float>> trimPathEnd = asAnimations(1f);
    @NonNull final List<Animation<?, Float>> trimPathOffset = asAnimations(0f);
    @StrokeLineCap int strokeLineCap = StrokeLineCap.BUTT;
    @StrokeLineJoin int strokeLineJoin = StrokeLineJoin.MITER;
    @NonNull final List<Animation<?, Float>> strokeMiterLimit = asAnimations(4f);
    @NonNull final List<Animation<?, float[]>> strokeDashArray = asAnimations(new float[0]);
    @NonNull final List<Animation<?, Float>> strokeDashOffset = asAnimations(0f);
    @FillType int fillType = FillType.NON_ZERO;
    boolean isStrokeScaling = true;

    Builder() {}

    // Fill color.

    public final B fillColor(@ColorInt int fillColor) {
      return replaceFirstAnimation(this.fillColor, asAnimation(fillColor));
    }

    @SafeVarargs
    public final B fillColor(@NonNull Animation<?, Integer>... animations) {
      return replaceAnimations(fillColor, animations);
    }

    public final B fillColor(@NonNull List<Animation<?, Integer>> animations) {
      return replaceAnimations(fillColor, animations);
    }

    // Fill alpha.

    public final B fillAlpha(@FloatRange(from = 0f, to = 1f) float fillAlpha) {
      return replaceFirstAnimation(this.fillAlpha, asAnimation(fillAlpha));
    }

    @SafeVarargs
    public final B fillAlpha(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(fillAlpha, animations);
    }

    public final B fillAlpha(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(fillAlpha, animations);
    }

    // Stroke color.

    public final B strokeColor(@ColorInt int strokeColor) {
      return replaceFirstAnimation(this.strokeColor, asAnimation(strokeColor));
    }

    @SafeVarargs
    public final B strokeColor(@NonNull Animation<?, Integer>... animations) {
      return replaceAnimations(strokeColor, animations);
    }

    public final B strokeColor(@NonNull List<Animation<?, Integer>> animations) {
      return replaceAnimations(strokeColor, animations);
    }

    // Stroke alpha.

    public final B strokeAlpha(@FloatRange(from = 0f, to = 1f) float strokeAlpha) {
      return replaceFirstAnimation(this.strokeAlpha, asAnimation(strokeAlpha));
    }

    @SafeVarargs
    public final B strokeAlpha(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(strokeAlpha, animations);
    }

    public final B strokeAlpha(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeAlpha, animations);
    }

    // Stroke width.

    public final B strokeWidth(@FloatRange(from = 0f) float strokeWidth) {
      return replaceFirstAnimation(this.strokeWidth, asAnimation(strokeWidth));
    }

    @SafeVarargs
    public final B strokeWidth(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(strokeWidth, animations);
    }

    public final B strokeWidth(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeWidth, animations);
    }

    // Trim path start.

    public final B trimPathStart(@FloatRange(from = 0f, to = 1f) float trimPathStart) {
      return replaceFirstAnimation(this.trimPathStart, asAnimation(trimPathStart));
    }

    @SafeVarargs
    public final B trimPathStart(@NonNull Animation<?, Float>... keyframes) {
      return replaceAnimations(trimPathStart, keyframes);
    }

    public final B trimPathStart(@NonNull List<Animation<?, Float>> keyframes) {
      return replaceAnimations(trimPathStart, keyframes);
    }

    // Trim path end.

    public final B trimPathEnd(@FloatRange(from = 0f, to = 1f) float trimPathEnd) {
      return replaceFirstAnimation(this.trimPathEnd, asAnimation(trimPathEnd));
    }

    @SafeVarargs
    public final B trimPathEnd(@NonNull Animation<?, Float>... keyframes) {
      return replaceAnimations(trimPathEnd, keyframes);
    }

    public final B trimPathEnd(@NonNull List<Animation<?, Float>> keyframes) {
      return replaceAnimations(trimPathEnd, keyframes);
    }

    // Trim path offset.

    public final B trimPathOffset(@FloatRange(from = 0f, to = 1f) float trimPathOffset) {
      return replaceFirstAnimation(this.trimPathOffset, asAnimation(trimPathOffset));
    }

    @SafeVarargs
    public final B trimPathOffset(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(trimPathOffset, animations);
    }

    public final B trimPathOffset(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(trimPathOffset, animations);
    }

    // Stroke line cap.

    public final B strokeLineCap(@StrokeLineCap int strokeLineCap) {
      this.strokeLineCap = strokeLineCap;
      return self;
    }

    // Stroke line join.

    public final B strokeLineJoin(@StrokeLineJoin int strokeLineJoin) {
      this.strokeLineJoin = strokeLineJoin;
      return self;
    }

    // Stroke miter limit.

    public final B strokeMiterLimit(@FloatRange(from = 0f, to = 1f) float strokeMiterLimit) {
      return replaceFirstAnimation(this.strokeMiterLimit, asAnimation(strokeMiterLimit));
    }

    @SafeVarargs
    public final B strokeMiterLimit(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(strokeMiterLimit, animations);
    }

    public final B strokeMiterLimit(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeMiterLimit, animations);
    }

    // Stroke dash array.

    public final B strokeDashArray(@Nullable float[] strokeDashArray) {
      strokeDashArray = strokeDashArray == null ? new float[0] : strokeDashArray;
      return replaceFirstAnimation(this.strokeDashArray, asAnimation(strokeDashArray));
    }

    @SafeVarargs
    public final B strokeDashArray(@NonNull Animation<?, float[]>... animations) {
      return replaceAnimations(strokeDashArray, animations);
    }

    public final B strokeDashArray(@NonNull List<Animation<?, float[]>> animations) {
      return replaceAnimations(strokeDashArray, animations);
    }

    // Stroke dash offset.

    public final B strokeDashOffset(@FloatRange(from = 0f, to = 1f) float strokeDashOffset) {
      return replaceFirstAnimation(this.strokeDashOffset, asAnimation(strokeDashOffset));
    }

    @SafeVarargs
    public final B strokeDashOffset(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(strokeDashOffset, animations);
    }

    public final B strokeDashOffset(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeDashOffset, animations);
    }

    // Fill type.

    public final B fillType(@FillType int fillType) {
      this.fillType = fillType;
      return self;
    }

    // Stroke scaling.

    public final B strokeScaling(boolean isStrokeScaling) {
      this.isStrokeScaling = isStrokeScaling;
      return self;
    }
  }

  // </editor-fold>
}
