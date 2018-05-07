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
import android.util.Log;

import java.util.Arrays;
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
  private final boolean isScalingStroke;

  RenderNode(
      List<Animation<?, Float>> rotation,
      List<Animation<?, Float>> pivotX,
      List<Animation<?, Float>> pivotY,
      List<Animation<?, Float>> scaleX,
      List<Animation<?, Float>> scaleY,
      List<Animation<?, Float>> translateX,
      List<Animation<?, Float>> translateY,
      List<Animation<?, Integer>> fillColor,
      List<Animation<?, Float>> fillAlpha,
      List<Animation<?, Integer>> strokeColor,
      List<Animation<?, Float>> strokeAlpha,
      List<Animation<?, Float>> strokeWidth,
      List<Animation<?, Float>> trimPathStart,
      List<Animation<?, Float>> trimPathEnd,
      List<Animation<?, Float>> trimPathOffset,
      @StrokeLineCap int strokeLineCap,
      @StrokeLineJoin int strokeLineJoin,
      List<Animation<?, Float>> strokeMiterLimit,
      List<Animation<?, float[]>> strokeDashArray,
      List<Animation<?, Float>> strokeDashOffset,
      @FillType int fillType,
      boolean isScalingStroke) {
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
    this.isScalingStroke = isScalingStroke;
  }

  @NonNull
  final List<Animation<?, Integer>> getFillColor() {
    return fillColor;
  }

  @NonNull
  final List<Animation<?, Float>> getFillAlpha() {
    return fillAlpha;
  }

  @NonNull
  final List<Animation<?, Integer>> getStrokeColor() {
    return strokeColor;
  }

  @NonNull
  final List<Animation<?, Float>> getStrokeAlpha() {
    return strokeAlpha;
  }

  @NonNull
  final List<Animation<?, Float>> getStrokeWidth() {
    return strokeWidth;
  }

  @NonNull
  final List<Animation<?, Float>> getTrimPathStart() {
    return trimPathStart;
  }

  @NonNull
  final List<Animation<?, Float>> getTrimPathEnd() {
    return trimPathEnd;
  }

  @NonNull
  final List<Animation<?, Float>> getTrimPathOffset() {
    return trimPathOffset;
  }

  @StrokeLineCap
  final int getStrokeLineCap() {
    return strokeLineCap;
  }

  @StrokeLineJoin
  final int getStrokeLineJoin() {
    return strokeLineJoin;
  }

  @NonNull
  final List<Animation<?, Float>> getStrokeMiterLimit() {
    return strokeMiterLimit;
  }

  @NonNull
  final List<Animation<?, float[]>> getStrokeDashArray() {
    return strokeDashArray;
  }

  @NonNull
  final List<Animation<?, Float>> getStrokeDashOffset() {
    return strokeDashOffset;
  }

  @FillType
  final int getFillType() {
    return fillType;
  }

  final boolean isScalingStroke() {
    return isScalingStroke;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  abstract RenderLayer toLayer(PropertyTimeline timeline);

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

    public RenderLayer(PropertyTimeline timeline, RenderNode node) {
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
      isStrokeScaling = node.isScalingStroke();
    }

    public abstract void onInitPath(Path outPath);

    @Override
    public final void onDraw(Canvas canvas, Matrix parentMatrix, PointF viewportScale) {
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

    private void applyTrimPathIfNeeded(Path outPath) {
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

    private void drawFillIfNeeded(Canvas canvas, Path path) {
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

    private void drawStrokeIfNeeded(Canvas canvas, Path path, float strokeScaleFactor) {
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
      // DashPathEffect throws an exception if the dash array is odd in length,
      // so double the size of the array if this is the case.
      final int initialSize = strokeDashArray.length;
      final int expansionFactor = initialSize % 2 == 0 ? 1 : 2;
      final int requiredSize = initialSize * expansionFactor;
      if (tempStrokeDashArray == null || tempStrokeDashArray.length != requiredSize) {
        tempStrokeDashArray = new float[requiredSize];
      }
      for (int i = 0; i < initialSize; i++) {
        tempStrokeDashArray[i] = strokeDashArray[i] * strokeScaleFactor;
      }
      System.arraycopy(
          tempStrokeDashArray, 0, tempStrokeDashArray, initialSize, requiredSize - initialSize);
      final float strokeDashOffset = this.strokeDashOffset.getAnimatedValue();
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

  abstract static class Builder<B extends Builder<B>> extends BaseNode.Builder<B> {
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
    boolean isScalingStroke = true;

    Builder() {}

    // Fill color.

    public final B fillColor(@ColorInt int initialFillColor) {
      return replaceFirstAnimation(fillColor, asAnimation(initialFillColor));
    }

    @SafeVarargs
    public final B fillColor(Animation<?, Integer>... animations) {
      return replaceAnimations(fillColor, animations);
    }

    public final B fillColor(List<Animation<?, Integer>> animations) {
      return replaceAnimations(fillColor, animations);
    }

    // Fill alpha.

    public final B fillAlpha(@FloatRange(from = 0f, to = 1f) float initialFillAlpha) {
      return replaceFirstAnimation(fillAlpha, asAnimation(initialFillAlpha));
    }

    @SafeVarargs
    public final B fillAlpha(Animation<?, Float>... animations) {
      return replaceAnimations(fillAlpha, animations);
    }

    public final B fillAlpha(List<Animation<?, Float>> animations) {
      return replaceAnimations(fillAlpha, animations);
    }

    // Stroke color.

    public final B strokeColor(@ColorInt int initialStrokeColor) {
      return replaceFirstAnimation(strokeColor, asAnimation(initialStrokeColor));
    }

    @SafeVarargs
    public final B strokeColor(Animation<?, Integer>... animations) {
      return replaceAnimations(strokeColor, animations);
    }

    public final B strokeColor(List<Animation<?, Integer>> animations) {
      return replaceAnimations(strokeColor, animations);
    }

    // Stroke alpha.

    public final B strokeAlpha(@FloatRange(from = 0f, to = 1f) float initialStrokeAlpha) {
      return replaceFirstAnimation(strokeAlpha, asAnimation(initialStrokeAlpha));
    }

    @SafeVarargs
    public final B strokeAlpha(Animation<?, Float>... animations) {
      return replaceAnimations(strokeAlpha, animations);
    }

    public final B strokeAlpha(List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeAlpha, animations);
    }

    // Stroke width.

    public final B strokeWidth(@FloatRange(from = 0f) float initialStrokeWidth) {
      return replaceFirstAnimation(strokeWidth, asAnimation(initialStrokeWidth));
    }

    @SafeVarargs
    public final B strokeWidth(Animation<?, Float>... animations) {
      return replaceAnimations(strokeWidth, animations);
    }

    public final B strokeWidth(List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeWidth, animations);
    }

    // Trim path start.

    public final B trimPathStart(@FloatRange(from = 0f, to = 1f) float initialTrimPathStart) {
      return replaceFirstAnimation(trimPathStart, asAnimation(initialTrimPathStart));
    }

    @SafeVarargs
    public final B trimPathStart(Animation<?, Float>... animations) {
      return replaceAnimations(trimPathStart, animations);
    }

    public final B trimPathStart(List<Animation<?, Float>> animations) {
      return replaceAnimations(trimPathStart, animations);
    }

    // Trim path end.

    public final B trimPathEnd(@FloatRange(from = 0f, to = 1f) float initialTrimPathEnd) {
      return replaceFirstAnimation(trimPathEnd, asAnimation(initialTrimPathEnd));
    }

    @SafeVarargs
    public final B trimPathEnd(Animation<?, Float>... animations) {
      return replaceAnimations(trimPathEnd, animations);
    }

    public final B trimPathEnd(List<Animation<?, Float>> animations) {
      return replaceAnimations(trimPathEnd, animations);
    }

    // Trim path offset.

    public final B trimPathOffset(@FloatRange(from = 0f, to = 1f) float initialTrimPathOffset) {
      return replaceFirstAnimation(trimPathOffset, asAnimation(initialTrimPathOffset));
    }

    @SafeVarargs
    public final B trimPathOffset(Animation<?, Float>... animations) {
      return replaceAnimations(trimPathOffset, animations);
    }

    public final B trimPathOffset(List<Animation<?, Float>> animations) {
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

    public final B strokeMiterLimit(@FloatRange(from = 0f, to = 1f) float initialStrokeMiterLimit) {
      return replaceFirstAnimation(strokeMiterLimit, asAnimation(initialStrokeMiterLimit));
    }

    @SafeVarargs
    public final B strokeMiterLimit(Animation<?, Float>... animations) {
      return replaceAnimations(strokeMiterLimit, animations);
    }

    public final B strokeMiterLimit(List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeMiterLimit, animations);
    }

    // Stroke dash array.

    public final B strokeDashArray(@Nullable float[] initialStrokeDashArray) {
      if (initialStrokeDashArray == null) {
        initialStrokeDashArray = new float[0];
      }
      return replaceFirstAnimation(strokeDashArray, asAnimation(initialStrokeDashArray));
    }

    @SafeVarargs
    public final B strokeDashArray(@NonNull Animation<?, float[]>... animations) {
      return replaceAnimations(strokeDashArray, animations);
    }

    public final B strokeDashArray(@NonNull List<Animation<?, float[]>> animations) {
      return replaceAnimations(strokeDashArray, animations);
    }

    // Stroke dash offset.

    public final B strokeDashOffset(@FloatRange(from = 0f, to = 1f) float initialStrokeDashOffset) {
      return replaceFirstAnimation(strokeDashOffset, asAnimation(initialStrokeDashOffset));
    }

    @SafeVarargs
    public final B strokeDashOffset(Animation<?, Float>... animations) {
      return replaceAnimations(strokeDashOffset, animations);
    }

    public final B strokeDashOffset(List<Animation<?, Float>> animations) {
      return replaceAnimations(strokeDashOffset, animations);
    }

    // Fill type.

    public final B fillType(@FillType int fillType) {
      this.fillType = fillType;
      return self;
    }

    // Scaling stroke.

    public final B scalingStroke(boolean isScalingStroke) {
      this.isScalingStroke = isScalingStroke;
      return self;
    }

    @NonNull
    @Override
    abstract RenderNode build();
  }

  // </editor-fold>
}
