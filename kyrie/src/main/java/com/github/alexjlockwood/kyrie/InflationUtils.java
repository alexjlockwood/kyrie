package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.InterpolatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.core.view.animation.PathInterpolatorCompat;

import com.github.alexjlockwood.kyrie.Animation.RepeatMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

final class InflationUtils {

  // <editor-fold desc="Drawable inflation">

  private static final String TAG_ANIMATED_VECTOR = "animated-vector";
  private static final String TAG_TARGET = "target";
  private static final String TAG_VECTOR = "vector";
  private static final String TAG_GROUP = "group";
  private static final String TAG_PATH = "path";
  private static final String TAG_CLIP_PATH = "clip-path";

  public static void inflate(KyrieDrawable.Builder builder, Context context, @DrawableRes int resId)
      throws XmlPullParserException, IOException {
    inflate(builder, context, resId, null);
  }

  private static void inflate(
      KyrieDrawable.Builder builder,
      Context context,
      @DrawableRes int resId,
      @Nullable Map<String, Map<String, Animation[]>> targetMap)
      throws XmlPullParserException, IOException {
    final Resources res = context.getResources();
    @SuppressLint("ResourceType")
    final XmlPullParser parser = res.getXml(resId);
    final AttributeSet attrs = Xml.asAttributeSet(parser);
    int type;
    //noinspection StatementWithEmptyBody
    while ((type = parser.next()) != XmlPullParser.START_TAG
        && type != XmlPullParser.END_DOCUMENT) {
      // Empty loop
    }
    if (type != XmlPullParser.START_TAG) {
      throw new XmlPullParserException("No start tag found");
    }
    final String tagName = parser.getName();
    if (TAG_ANIMATED_VECTOR.equals(tagName)) {
      inflateAnimatedVector(builder, context, parser, attrs);
    } else if (TAG_VECTOR.equals(tagName)) {
      inflateVector(builder, context, parser, attrs, targetMap);
    }
  }

  private static void inflateAnimatedVector(
      KyrieDrawable.Builder builder, Context context, XmlPullParser parser, AttributeSet attrs)
      throws XmlPullParserException, IOException {
    int eventType = parser.getEventType();
    final int innerDepth = parser.getDepth() + 1;

    int drawableResId = 0;
    final Map<String, Map<String, List<Animation<?, ?>>>> targetMap = new ArrayMap<>();

    // Parse everything until the end of the root element.
    while (eventType != XmlPullParser.END_DOCUMENT
        && (innerDepth <= parser.getDepth() || eventType != XmlPullParser.END_TAG)) {
      if (eventType == XmlPullParser.START_TAG) {
        final String tagName = parser.getName();
        if (TAG_ANIMATED_VECTOR.equals(tagName)) {
          final TypedArray a =
              TypedArrayUtils.obtainAttributes(
                  context.getResources(), context.getTheme(), attrs, Styleable.ANIMATED_VECTOR);
          drawableResId = a.getResourceId(Styleable.AnimatedVector.DRAWABLE, 0);
          a.recycle();
        } else if (TAG_TARGET.equals(tagName)) {
          final TypedArray a = context.getResources().obtainAttributes(attrs, Styleable.TARGET);
          final String targetName = a.getString(Styleable.Target.NAME);
          final int animatorResId = a.getResourceId(Styleable.Target.ANIMATION, 0);
          if (animatorResId != 0) {
            final Map<String, List<Animation<?, ?>>> animationMap =
                loadAnimationMap(context, animatorResId);
            if (targetMap.containsKey(targetName)) {
              final Map<String, List<Animation<?, ?>>> existingAnimationMap =
                  targetMap.get(targetName);
              for (Map.Entry<String, List<Animation<?, ?>>> entry : animationMap.entrySet()) {
                final String key = entry.getKey();
                final List<Animation<?, ?>> value = entry.getValue();
                if (existingAnimationMap.containsKey(key)) {
                  existingAnimationMap.get(key).addAll(value);
                } else {
                  existingAnimationMap.put(key, value);
                }
              }
            } else {
              targetMap.put(targetName, animationMap);
            }
          }
          a.recycle();
        }
      }
      eventType = parser.next();
    }

    if (drawableResId != 0) {
      final Map<String, Map<String, Animation[]>> arrayTargetMap = new ArrayMap<>();
      for (Map.Entry<String, Map<String, List<Animation<?, ?>>>> entry : targetMap.entrySet()) {
        final Map<String, List<Animation<?, ?>>> value = entry.getValue();
        final Map<String, Animation[]> arrayValue = new ArrayMap<>();
        for (Map.Entry<String, List<Animation<?, ?>>> e : value.entrySet()) {
          final List<Animation<?, ?>> v = e.getValue();
          final Animation[] arrayV = new Animation[v.size()];
          for (int i = 0, size = arrayV.length; i < size; i++) {
            arrayV[i] = v.get(i);
          }
          arrayValue.put(e.getKey(), arrayV);
        }
        arrayTargetMap.put(entry.getKey(), arrayValue);
      }
      inflate(builder, context, drawableResId, arrayTargetMap);
    }
  }

  private static void inflateVector(
      KyrieDrawable.Builder builder,
      Context context,
      XmlPullParser parser,
      AttributeSet attrs,
      @Nullable Map<String, Map<String, Animation[]>> targetMap)
      throws XmlPullParserException, IOException {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.VECTOR);
    Map<String, Animation[]> animationMap = null;
    if (targetMap != null) {
      final String groupName = a.getString(Styleable.Vector.NAME);
      if (groupName != null) {
        animationMap = targetMap.get(groupName);
      }
    }
    updateVectorFromTypedArray(builder, context, a, parser, animationMap);
    a.recycle();

    // Use a stack to help to build the group tree. The top is always the current group.
    final Stack<GroupNode.Builder> groupBuilderStack = new Stack<>();
    int eventType = parser.getEventType();
    final int innerDepth = parser.getDepth() + 1;

    // Parse everything until the end of the root element.
    while (eventType != XmlPullParser.END_DOCUMENT
        && (innerDepth <= parser.getDepth() || eventType != XmlPullParser.END_TAG)) {
      if (eventType == XmlPullParser.START_TAG) {
        final String tagName = parser.getName();
        if (TAG_GROUP.equals(tagName)) {
          final GroupNode.Builder groupBuilder = GroupNode.builder();
          inflateGroup(groupBuilder, context, parser, attrs, targetMap);
          if (groupBuilderStack.isEmpty()) {
            builder.child(groupBuilder);
          } else {
            groupBuilderStack.peek().child(groupBuilder);
          }
          groupBuilderStack.push(groupBuilder);
        } else if (TAG_PATH.equals(tagName)) {
          final PathNode.Builder pathBuilder = PathNode.builder();
          inflatePath(pathBuilder, context, parser, attrs, targetMap);
          if (groupBuilderStack.isEmpty()) {
            builder.child(pathBuilder);
          } else {
            groupBuilderStack.peek().child(pathBuilder);
          }
        } else if (TAG_CLIP_PATH.equals(tagName)) {
          final ClipPathNode.Builder clipPathBuilder = ClipPathNode.builder();
          inflateClipPath(clipPathBuilder, context, parser, attrs, targetMap);
          if (groupBuilderStack.isEmpty()) {
            builder.child(clipPathBuilder);
          } else {
            groupBuilderStack.peek().child(clipPathBuilder);
          }
        }
      } else if (eventType == XmlPullParser.END_TAG) {
        final String tagName = parser.getName();
        if (!groupBuilderStack.isEmpty() && TAG_GROUP.equals(tagName)) {
          groupBuilderStack.pop();
        }
      }
      eventType = parser.next();
    }
  }

  private static void updateVectorFromTypedArray(
      KyrieDrawable.Builder builder,
      Context context,
      TypedArray a,
      XmlPullParser parser,
      @Nullable Map<String, Animation[]> animationMap) {
    builder.tintList(
        TypedArrayUtils.getNamedColorStateList(a, parser, context, "tint", Styleable.Vector.TINT));
    final int tintMode =
        TypedArrayUtils.getNamedInt(a, parser, "tintMode", Styleable.Vector.TINT_MODE, -1);
    builder.tintMode(parseTintMode(tintMode, PorterDuff.Mode.SRC_IN));
    builder.autoMirrored(
        TypedArrayUtils.getNamedBoolean(
            a, parser, "autoMirrored", Styleable.Vector.AUTO_MIRRORED, false));
    builder.viewportWidth(
        TypedArrayUtils.getNamedFloat(
            a, parser, "viewportWidth", Styleable.Vector.VIEWPORT_WIDTH, -1));
    builder.viewportHeight(
        TypedArrayUtils.getNamedFloat(
            a, parser, "viewportHeight", Styleable.Vector.VIEWPORT_HEIGHT, -1));
    builder.width((int) a.getDimension(Styleable.Vector.WIDTH, -1));
    builder.height((int) a.getDimension(Styleable.Vector.HEIGHT, -1));
    builder.alpha(TypedArrayUtils.getNamedFloat(a, parser, "alpha", Styleable.Vector.ALPHA, 1));
    if (animationMap != null && animationMap.containsKey("alpha")) {
      builder.alpha((Animation<?, Float>[]) animationMap.get("alpha"));
    }
  }

  @NonNull
  private static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
    switch (value) {
      case 3:
        return PorterDuff.Mode.SRC_OVER;
      case 5:
        return PorterDuff.Mode.SRC_IN;
      case 9:
        return PorterDuff.Mode.SRC_ATOP;
      case 14:
        return PorterDuff.Mode.MULTIPLY;
      case 15:
        return PorterDuff.Mode.SCREEN;
      case 16:
        return PorterDuff.Mode.ADD;
      default:
        return defaultMode;
    }
  }

  private static void inflateGroup(
      GroupNode.Builder builder,
      Context context,
      XmlPullParser parser,
      AttributeSet attrs,
      @Nullable Map<String, Map<String, Animation[]>> targetMap) {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.GROUP);
    Map<String, Animation[]> animationMap = null;
    if (targetMap != null) {
      final String groupName = a.getString(Styleable.Group.NAME);
      if (groupName != null) {
        animationMap = targetMap.get(groupName);
      }
    }
    updateGroupFromTypedArray(builder, a, parser, animationMap);
    a.recycle();
  }

  private static void updateGroupFromTypedArray(
      GroupNode.Builder builder,
      TypedArray a,
      XmlPullParser parser,
      @Nullable Map<String, Animation[]> animationMap) {
    builder.pivotX(a.getFloat(Styleable.Group.PIVOT_X, 0));
    if (animationMap != null && animationMap.containsKey("pivotX")) {
      builder.pivotX((Animation<?, Float>[]) animationMap.get("pivotX"));
    }
    builder.pivotY(a.getFloat(Styleable.Group.PIVOT_Y, 0));
    if (animationMap != null && animationMap.containsKey("pivotY")) {
      builder.pivotY((Animation<?, Float>[]) animationMap.get("pivotY"));
    }
    builder.rotation(
        TypedArrayUtils.getNamedFloat(a, parser, "rotation", Styleable.Group.ROTATION, 0));
    if (animationMap != null && animationMap.containsKey("rotation")) {
      builder.rotation((Animation<?, Float>[]) animationMap.get("rotation"));
    }
    builder.scaleX(TypedArrayUtils.getNamedFloat(a, parser, "scaleX", Styleable.Group.SCALE_X, 1));
    if (animationMap != null && animationMap.containsKey("scaleX")) {
      builder.scaleX((Animation<?, Float>[]) animationMap.get("scaleX"));
    }
    builder.scaleY(TypedArrayUtils.getNamedFloat(a, parser, "scaleY", Styleable.Group.SCALE_Y, 1));
    if (animationMap != null && animationMap.containsKey("scaleY")) {
      builder.scaleY((Animation<?, Float>[]) animationMap.get("scaleY"));
    }
    builder.translateX(
        TypedArrayUtils.getNamedFloat(a, parser, "translateX", Styleable.Group.TRANSLATE_X, 0));
    if (animationMap != null && animationMap.containsKey("translateX")) {
      builder.translateX((Animation<?, Float>[]) animationMap.get("translateX"));
    }
    builder.translateY(
        TypedArrayUtils.getNamedFloat(a, parser, "translateY", Styleable.Group.TRANSLATE_Y, 0));
    if (animationMap != null && animationMap.containsKey("translateY")) {
      builder.translateY((Animation<?, Float>[]) animationMap.get("translateY"));
    }
  }

  private static void inflatePath(
      PathNode.Builder builder,
      Context context,
      XmlPullParser parser,
      AttributeSet attrs,
      @Nullable Map<String, Map<String, Animation[]>> targetMap) {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.PATH);
    Map<String, Animation[]> animationMap = null;
    if (targetMap != null) {
      final String pathName = a.getString(Styleable.Path.NAME);
      if (pathName != null) {
        animationMap = targetMap.get(pathName);
      }
    }
    updatePathFromTypedArray(builder, a, parser, context, animationMap);
    a.recycle();
  }

  // TODO: support transforms on paths
  private static void updatePathFromTypedArray(
      PathNode.Builder builder,
      TypedArray a,
      XmlPullParser parser,
      Context context,
      @Nullable Map<String, Animation[]> animationMap) {
    final boolean hasPathData = TypedArrayUtils.hasAttribute(parser, "pathData");
    if (!hasPathData) {
      return;
    }
    final String pathData = a.getString(Styleable.Path.PATH_DATA);
    if (pathData != null) {
      builder.pathData(pathData);
      if (animationMap != null && animationMap.containsKey("pathData")) {
        builder.pathData((Animation<?, PathData>[]) animationMap.get("pathData"));
      }
    }

    final ComplexColor fillColorComplex =
        TypedArrayUtils.getNamedComplexColor(
            a, parser, context, "fillColor", Styleable.Path.FILL_COLOR, Color.TRANSPARENT);
    if (fillColorComplex.isGradient()) {
      final Shader shader = fillColorComplex.getShader();
      if (shader instanceof LinearGradient) {
        builder.fillColor((LinearGradient) shader);
      } else if (shader instanceof RadialGradient) {
        builder.fillColor((RadialGradient) shader);
      } else if (shader instanceof SweepGradient) {
        builder.fillColor((SweepGradient) shader);
      } else {
        throw new IllegalStateException("Unsupported shader type");
      }
    } else {
      if (animationMap != null && animationMap.containsKey("fillColor")) {
        builder.fillColor(fillColorComplex.getColor());
        builder.fillColor((Animation<?, Integer>[]) animationMap.get("fillColor"));
      } else {
        if (fillColorComplex.isStateful()) {
          builder.fillColor(fillColorComplex.getColorStateList());
        } else {
          builder.fillColor(fillColorComplex.getColor());
        }
      }
    }

    builder.fillAlpha(
        TypedArrayUtils.getNamedFloat(a, parser, "fillAlpha", Styleable.Path.FILL_ALPHA, 1));
    if (animationMap != null && animationMap.containsKey("fillAlpha")) {
      builder.fillAlpha((Animation<?, Float>[]) animationMap.get("fillAlpha"));
    }

    final ComplexColor strokeColorComplex =
        TypedArrayUtils.getNamedComplexColor(
            a, parser, context, "strokeColor", Styleable.Path.STROKE_COLOR, Color.TRANSPARENT);
    if (strokeColorComplex.isGradient()) {
      final Shader shader = strokeColorComplex.getShader();
      if (shader instanceof LinearGradient) {
        builder.strokeColor((LinearGradient) shader);
      } else if (shader instanceof RadialGradient) {
        builder.strokeColor((RadialGradient) shader);
      } else if (shader instanceof SweepGradient) {
        builder.strokeColor((SweepGradient) shader);
      } else {
        throw new IllegalStateException("Unsupported shader type");
      }
    } else {
      if (animationMap != null && animationMap.containsKey("strokeColor")) {
        builder.strokeColor(strokeColorComplex.getColor());
        builder.strokeColor((Animation<?, Integer>[]) animationMap.get("strokeColor"));
      } else {
        if (strokeColorComplex.isStateful()) {
          builder.strokeColor(strokeColorComplex.getColorStateList());
        } else {
          builder.strokeColor(strokeColorComplex.getColor());
        }
      }
    }

    builder.strokeAlpha(
        TypedArrayUtils.getNamedFloat(a, parser, "strokeAlpha", Styleable.Path.STROKE_ALPHA, 1));
    if (animationMap != null && animationMap.containsKey("strokeAlpha")) {
      builder.strokeAlpha((Animation<?, Float>[]) animationMap.get("strokeAlpha"));
    }
    builder.strokeWidth(
        TypedArrayUtils.getNamedFloat(a, parser, "strokeWidth", Styleable.Path.STROKE_WIDTH, 0));
    if (animationMap != null && animationMap.containsKey("strokeWidth")) {
      builder.strokeWidth((Animation<?, Float>[]) animationMap.get("strokeWidth"));
    }
    builder.trimPathStart(
        TypedArrayUtils.getNamedFloat(
            a, parser, "trimPathStart", Styleable.Path.TRIM_PATH_START, 0));
    if (animationMap != null && animationMap.containsKey("trimPathStart")) {
      builder.trimPathStart((Animation<?, Float>[]) animationMap.get("trimPathStart"));
    }
    builder.trimPathEnd(
        TypedArrayUtils.getNamedFloat(a, parser, "trimPathEnd", Styleable.Path.TRIM_PATH_END, 1));
    if (animationMap != null && animationMap.containsKey("trimPathEnd")) {
      builder.trimPathEnd((Animation<?, Float>[]) animationMap.get("trimPathEnd"));
    }
    builder.trimPathOffset(
        TypedArrayUtils.getNamedFloat(
            a, parser, "trimPathOffset", Styleable.Path.TRIM_PATH_OFFSET, 0));
    if (animationMap != null && animationMap.containsKey("trimPathOffset")) {
      builder.trimPathOffset((Animation<?, Float>[]) animationMap.get("trimPathOffset"));
    }
    final int lineCap =
        TypedArrayUtils.getNamedInt(a, parser, "strokeLineCap", Styleable.Path.STROKE_LINE_CAP, 0);
    builder.strokeLineCap(StrokeLineCap.values()[lineCap]);
    final int lineJoin =
        TypedArrayUtils.getNamedInt(
            a, parser, "strokeLineJoin", Styleable.Path.STROKE_LINE_JOIN, 0);
    builder.strokeLineJoin(StrokeLineJoin.values()[lineJoin]);
    builder.strokeMiterLimit(
        TypedArrayUtils.getNamedFloat(
            a, parser, "strokeMiterLimit", Styleable.Path.STROKE_MITER_LIMIT, 4));
    if (animationMap != null && animationMap.containsKey("strokeMiterLimit")) {
      builder.strokeMiterLimit((Animation<?, Float>[]) animationMap.get("strokeMiterLimit"));
    }
    final int fillType =
        TypedArrayUtils.getNamedInt(a, parser, "fillType", Styleable.Path.FILL_TYPE, 0);
    builder.fillType(FillType.values()[fillType]);
  }

  private static void inflateClipPath(
      ClipPathNode.Builder builder,
      Context context,
      XmlPullParser parser,
      AttributeSet attrs,
      @Nullable Map<String, Map<String, Animation[]>> targetMap) {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.CLIP_PATH);
    Map<String, Animation[]> animationMap = null;
    if (targetMap != null) {
      final String pathName = a.getString(Styleable.ClipPath.NAME);
      if (pathName != null) {
        animationMap = targetMap.get(pathName);
      }
    }
    updateClipPathFromTypedArray(builder, a, parser, animationMap);
    a.recycle();
  }

  // TODO: support transforms on clip paths
  private static void updateClipPathFromTypedArray(
      ClipPathNode.Builder builder,
      TypedArray a,
      XmlPullParser parser,
      @Nullable Map<String, Animation[]> animationMap) {
    final boolean hasPathData = TypedArrayUtils.hasAttribute(parser, "pathData");
    if (!hasPathData) {
      return;
    }
    final String pathData = a.getString(Styleable.ClipPath.PATH_DATA);
    if (pathData != null) {
      builder.pathData(pathData);
      if (animationMap != null && animationMap.containsKey("pathData")) {
        builder.pathData((Animation<?, PathData>[]) animationMap.get("pathData"));
      }
    }

    final int fillType =
        TypedArrayUtils.getNamedInt(a, parser, "fillType", Styleable.ClipPath.FILL_TYPE, 0);
    builder.fillType(FillType.values()[fillType]);
  }

  // </editor-fold>

  // <editor-fold desc="Animator inflation">

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ORDERING_TOGETHER, ORDERING_SEQUENTIALLY})
  @interface Ordering {}

  private static final int ORDERING_TOGETHER = 0;
  private static final int ORDERING_SEQUENTIALLY = 1;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
    VALUE_TYPE_FLOAT,
    VALUE_TYPE_INT,
    VALUE_TYPE_PATH,
    VALUE_TYPE_COLOR,
    VALUE_TYPE_UNDEFINED
  })
  @interface ValueType {}

  private static final int VALUE_TYPE_FLOAT = 0;
  private static final int VALUE_TYPE_INT = 1;
  private static final int VALUE_TYPE_PATH = 2;
  private static final int VALUE_TYPE_COLOR = 3;
  private static final int VALUE_TYPE_UNDEFINED = 4;

  private static final TimeInterpolator DEFAULT_INTERPOLATOR =
      new AccelerateDecelerateInterpolator();

  private static Map<String, List<Animation<?, ?>>> loadAnimationMap(
      Context context, @AnimatorRes @AnimRes int id) throws NotFoundException {
    XmlResourceParser parser = null;
    try {
      parser = context.getResources().getAnimation(id);
      return createAnimatorFromXml(
              context, parser, Xml.asAttributeSet(parser), null, ORDERING_TOGETHER)
          .toMap(0);
    } catch (XmlPullParserException | IOException ex) {
      final NotFoundException rnf =
          new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
      rnf.initCause(ex);
      throw rnf;
    } finally {
      if (parser != null) {
        parser.close();
      }
    }
  }

  private static MyAnimator createAnimatorFromXml(
      Context context,
      XmlPullParser parser,
      AttributeSet attrs,
      @Nullable MyAnimatorSet parent,
      @Ordering int sequenceOrdering)
      throws XmlPullParserException, IOException {
    MyAnimator anim = null;
    ArrayList<MyAnimator> childAnims = null;

    // Make sure we are on a start tag.
    int type;
    int depth = parser.getDepth();

    while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
        && type != XmlPullParser.END_DOCUMENT) {

      if (type != XmlPullParser.START_TAG) {
        continue;
      }

      String name = parser.getName();
      boolean gotValues = false;

      if (name.equals("objectAnimator")) {
        anim = loadObjectAnimator(context, attrs, parser);
      } else if (name.equals("set")) {
        anim = new MyAnimatorSet();
        final TypedArray a =
            TypedArrayUtils.obtainAttributes(
                context.getResources(), context.getTheme(), attrs, Styleable.ANIMATOR_SET);
        final int ordering =
            TypedArrayUtils.getNamedInt(
                a, parser, "ordering", Styleable.AnimatorSet.ORDERING, ORDERING_TOGETHER);
        createAnimatorFromXml(context, parser, attrs, (MyAnimatorSet) anim, ordering);
        a.recycle();
      } else if (name.equals("propertyValuesHolder")) {
        final MyPropertyValuesHolder[] values =
            loadValues(context, parser, Xml.asAttributeSet(parser));
        if (values != null && anim instanceof MyObjectAnimator) {
          ((MyObjectAnimator) anim).setValues(values);
        }
        gotValues = true;
      } else {
        throw new RuntimeException("Unknown animator name: " + parser.getName());
      }

      if (parent != null && !gotValues) {
        if (childAnims == null) {
          childAnims = new ArrayList<>();
        }
        childAnims.add(anim);
      }
    }

    if (parent != null && childAnims != null) {
      final MyAnimator[] animsArray = new MyAnimator[childAnims.size()];
      int index = 0;
      for (MyAnimator a : childAnims) {
        animsArray[index++] = a;
      }
      if (sequenceOrdering == ORDERING_TOGETHER) {
        parent.playTogether(animsArray);
      } else {
        parent.playSequentially(animsArray);
      }
    }

    return anim;
  }

  private static MyObjectAnimator loadObjectAnimator(
      Context context, AttributeSet attrs, XmlPullParser parser) throws NotFoundException {
    final MyObjectAnimator anim = new MyObjectAnimator();
    final TypedArray arrayAnimator =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.ANIMATOR);
    final TypedArray arrayObjectAnimator =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.PROPERTY_ANIMATOR);
    parseAnimatorFromTypeArray(anim, arrayAnimator, arrayObjectAnimator, parser);
    final int resId =
        TypedArrayUtils.getNamedResourceId(
            arrayAnimator, parser, "interpolator", Styleable.Animator.INTERPOLATOR, 0);
    if (resId > 0) {
      anim.setInterpolator(loadInterpolator(context, resId));
    }
    arrayAnimator.recycle();
    arrayObjectAnimator.recycle();
    return anim;
  }

  /**
   * @param anim The animator, must not be null
   * @param arrayAnimator Incoming typed array for Animator's attributes.
   * @param arrayObjectAnimator Incoming typed array for Object Animator's attributes.
   */
  private static void parseAnimatorFromTypeArray(
      MyObjectAnimator anim,
      TypedArray arrayAnimator,
      TypedArray arrayObjectAnimator,
      XmlPullParser parser) {
    final long duration = arrayAnimator.getInt(Styleable.Animator.DURATION, 300);
    final long startDelay = arrayAnimator.getInt(Styleable.Animator.START_OFFSET, 0);
    int valueType =
        TypedArrayUtils.getNamedInt(
            arrayAnimator,
            parser,
            "valueType",
            Styleable.Animator.VALUE_TYPE,
            VALUE_TYPE_UNDEFINED);

    // Change to requiring both value from and to, otherwise, throw exception for now.
    if (TypedArrayUtils.hasAttribute(parser, "valueFrom")
        && TypedArrayUtils.hasAttribute(parser, "valueTo")) {
      if (valueType == VALUE_TYPE_UNDEFINED) {
        valueType =
            inferValueTypeFromValues(
                arrayAnimator, Styleable.Animator.VALUE_FROM, Styleable.Animator.VALUE_TO);
      }
      final String propertyName =
          TypedArrayUtils.getNamedString(
              arrayObjectAnimator,
              parser,
              "propertyName",
              Styleable.PropertyAnimator.PROPERTY_NAME);
      if (propertyName == null) {
        throw new InflateException(
            arrayObjectAnimator.getPositionDescription() + " propertyName must not be null");
      }
      final MyPropertyValuesHolder pvh =
          getPVH(
              arrayAnimator,
              valueType,
              Styleable.Animator.VALUE_FROM,
              Styleable.Animator.VALUE_TO,
              propertyName);
      if (pvh != null) {
        anim.setValues(pvh);
      }
    }

    anim.setDuration(duration);
    anim.setStartDelay(startDelay);
    anim.setRepeatCount(arrayAnimator.getInt(Styleable.Animator.REPEAT_COUNT, 0));
    final int repeatModeInt =
        arrayAnimator.getInt(Styleable.Animator.REPEAT_MODE, ValueAnimator.RESTART);
    final RepeatMode repeatMode;
    if (repeatModeInt == ValueAnimator.RESTART) {
      repeatMode = RepeatMode.RESTART;
    } else if (repeatModeInt == ValueAnimator.REVERSE) {
      repeatMode = RepeatMode.REVERSE;
    } else {
      throw new InflateException("Invalid repeatMode: " + repeatModeInt);
    }
    anim.setRepeatMode(repeatMode);

    // Setup the object animator.
    final String pathData =
        TypedArrayUtils.getNamedString(
            arrayObjectAnimator, parser, "pathData", Styleable.PropertyAnimator.PATH_DATA);

    if (pathData != null) {
      // Path can be involved in an PropertyAnimator in the following 3 ways:
      // 1) Path morphing: the property to be animated is pathData, and valueFrom and valueTo
      //    are both of pathType. valueType = pathType needs to be explicitly defined.
      // 2) A property in X or Y dimension can be animated along a path: the property needs to be
      //    defined in propertyXName or propertyYName attribute, the path will be defined in the
      //    pathData attribute. valueFrom and valueTo will not be necessary for this animation.
      // 3) PathInterpolator can also define a path (in pathData) for its interpolation curve.
      // Here we are dealing with case 2.
      final String propertyXName =
          TypedArrayUtils.getNamedString(
              arrayObjectAnimator,
              parser,
              "propertyXName",
              Styleable.PropertyAnimator.PROPERTY_X_NAME);
      final String propertyYName =
          TypedArrayUtils.getNamedString(
              arrayObjectAnimator,
              parser,
              "propertyYName",
              Styleable.PropertyAnimator.PROPERTY_Y_NAME);
      if (propertyXName == null && propertyYName == null) {
        throw new InflateException(
            arrayObjectAnimator.getPositionDescription()
                + " propertyXName or propertyYName is needed for PathData");
      }
      anim.setValues(
          new MyPathMotionPropertyValuesHolder(
              PathData.toPath(pathData), propertyXName, propertyYName));
    }
  }

  @ValueType
  private static int inferValueTypeFromValues(
      TypedArray styledAttributes, int valueFromId, int valueToId) {
    final TypedValue tvFrom = styledAttributes.peekValue(valueFromId);
    final boolean hasFrom = tvFrom != null;
    final int fromType = hasFrom ? tvFrom.type : 0;
    final TypedValue tvTo = styledAttributes.peekValue(valueToId);
    final boolean hasTo = tvTo != null;
    final int toType = hasTo ? tvTo.type : 0;

    int valueType;
    // Check whether it's a color type. If not, fall back to default type (i.e. float type).
    if ((hasFrom && isColorType(fromType)) || (hasTo && isColorType(toType))) {
      valueType = VALUE_TYPE_COLOR;
    } else {
      valueType = VALUE_TYPE_FLOAT;
    }
    return valueType;
  }

  private static MyPropertyValuesHolder[] loadValues(
      Context context, XmlPullParser parser, AttributeSet attrs)
      throws XmlPullParserException, IOException {
    ArrayList<MyPropertyValuesHolder> values = null;

    int type;
    while ((type = parser.getEventType()) != XmlPullParser.END_TAG
        && type != XmlPullParser.END_DOCUMENT) {
      if (type != XmlPullParser.START_TAG) {
        parser.next();
        continue;
      }

      String name = parser.getName();

      if (name.equals("propertyValuesHolder")) {
        final TypedArray a =
            TypedArrayUtils.obtainAttributes(
                context.getResources(),
                context.getTheme(),
                attrs,
                Styleable.PROPERTY_VALUES_HOLDER);
        final String propertyName =
            TypedArrayUtils.getNamedString(
                a, parser, "propertyName", Styleable.PropertyValuesHolder.PROPERTY_NAME);
        int valueType =
            TypedArrayUtils.getNamedInt(
                a,
                parser,
                "valueType",
                Styleable.PropertyValuesHolder.VALUE_TYPE,
                VALUE_TYPE_UNDEFINED);

        MyPropertyValuesHolder pvh = loadPvh(context, parser, propertyName, valueType);
        if (pvh == null) {
          pvh =
              getPVH(
                  a,
                  valueType,
                  Styleable.PropertyValuesHolder.VALUE_FROM,
                  Styleable.PropertyValuesHolder.VALUE_TO,
                  propertyName);
        }
        if (values == null) {
          values = new ArrayList<>();
        }
        values.add(pvh);
        a.recycle();
      }

      parser.next();
    }

    MyPropertyValuesHolder[] valuesArray = null;
    if (values != null) {
      int count = values.size();
      valuesArray = new MyPropertyValuesHolder[count];
      for (int i = 0; i < count; ++i) {
        valuesArray[i] = values.get(i);
      }
    }
    return valuesArray;
  }

  /** Load property values holder if there are keyframes defined in it. Otherwise return null. */
  @Nullable
  private static MyPropertyValuesHolder loadPvh(
      Context context, XmlPullParser parser, String propertyName, @ValueType int valueType)
      throws XmlPullParserException, IOException {
    ArrayList<Keyframe> keyframes = null;

    int type;
    while ((type = parser.next()) != XmlPullParser.END_TAG && type != XmlPullParser.END_DOCUMENT) {
      final String name = parser.getName();
      if (name.equals("keyframe")) {
        if (valueType == VALUE_TYPE_UNDEFINED) {
          valueType = inferValueTypeOfKeyframe(context, Xml.asAttributeSet(parser), parser);
        }
        final Keyframe keyframe =
            loadKeyframe(context, Xml.asAttributeSet(parser), valueType, parser);
        if (keyframe != null) {
          if (keyframes == null) {
            keyframes = new ArrayList<>();
          }
          keyframes.add(keyframe);
        }
        parser.next();
      }
    }

    int count;
    if (keyframes != null && (count = keyframes.size()) > 0) {
      // make sure we have keyframes at 0 and 1
      // If we have keyframes with set fractions, add keyframes at start/end
      // appropriately. If start/end have no set fractions:
      // if there's only one keyframe, set its fraction to 1 and add one at 0
      // if >1 keyframe, set the last fraction to 1, the first fraction to 0
      Keyframe firstKeyframe = keyframes.get(0);
      Keyframe lastKeyframe = keyframes.get(count - 1);
      float endFraction = lastKeyframe.getFraction();
      if (endFraction < 1) {
        if (endFraction < 0) {
          lastKeyframe.fraction(1);
        } else {
          keyframes.add(keyframes.size(), Keyframe.of(1));
          count++;
        }
      }
      float startFraction = firstKeyframe.getFraction();
      if (startFraction != 0) {
        if (startFraction < 0) {
          firstKeyframe.fraction(0);
        } else {
          keyframes.add(0, Keyframe.of(0));
          count++;
        }
      }
      Keyframe[] keyframeArray = new Keyframe[count];
      keyframes.toArray(keyframeArray);
      for (int i = 0; i < count; i++) {
        Keyframe keyframe = keyframeArray[i];
        if (keyframe.getFraction() < 0) {
          if (i == 0) {
            keyframe.fraction(0);
          } else if (i == count - 1) {
            keyframe.fraction(1);
          } else {
            // figure out the start/end parameters of the current gap
            // in fractions and distribute the gap among those keyframes
            int startIndex = i;
            int endIndex = i;
            for (int j = startIndex + 1; j < count - 1; ++j) {
              if (keyframeArray[j].getFraction() >= 0) {
                break;
              }
              endIndex = j;
            }
            float gap =
                keyframeArray[endIndex + 1].getFraction()
                    - keyframeArray[startIndex - 1].getFraction();
            distributeKeyframes(keyframeArray, gap, startIndex, endIndex);
          }
        }
      }
      return new MyKeyframePropertyValuesHolder(propertyName, keyframeArray, valueType);
    }

    return null;
  }

  /**
   * When no value type is provided in keyframe, we need to infer the type from the value. i.e. if
   * value is defined in the style of a color value, then the color type is returned. Otherwise,
   * default float type is returned.
   */
  @ValueType
  private static int inferValueTypeOfKeyframe(
      Context context, AttributeSet attrs, XmlPullParser parser) {
    @ValueType int valueType;
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.KEYFRAME);
    final TypedValue keyframeValue =
        TypedArrayUtils.peekNamedValue(a, parser, "value", Styleable.Keyframe.VALUE);
    final boolean hasValue = keyframeValue != null;
    // When no value type is provided, check whether it's a color type first.
    // If not, fall back to default float value type.
    if (hasValue && isColorType(keyframeValue.type)) {
      valueType = VALUE_TYPE_COLOR;
    } else {
      valueType = VALUE_TYPE_FLOAT;
    }
    a.recycle();
    return valueType;
  }

  @Nullable
  private static MyPropertyValuesHolder getPVH(
      TypedArray styledAttributes,
      @ValueType int valueType,
      int valueFromId,
      int valueToId,
      String propertyName) {
    final TypedValue tvFrom = styledAttributes.peekValue(valueFromId);
    final boolean hasFrom = tvFrom != null;
    final int fromType = hasFrom ? tvFrom.type : 0;
    final TypedValue tvTo = styledAttributes.peekValue(valueToId);
    final boolean hasTo = tvTo != null;
    final int toType = hasTo ? tvTo.type : 0;

    if (valueType == VALUE_TYPE_UNDEFINED) {
      // Check whether it's color type. If not, fall back to default type (i.e. float type)
      if ((hasFrom && isColorType(fromType)) || (hasTo && isColorType(toType))) {
        valueType = VALUE_TYPE_COLOR;
      } else {
        valueType = VALUE_TYPE_FLOAT;
      }
    }

    if (valueType == VALUE_TYPE_PATH) {
      final String fromString = styledAttributes.getString(valueFromId);
      final String toString = styledAttributes.getString(valueToId);
      final PathData nodesFrom = PathData.parse(fromString == null ? "" : fromString);
      final PathData nodesTo = PathData.parse(toString == null ? "" : toString);
      if (!nodesFrom.canMorphWith(nodesTo)) {
        throw new InflateException("Can't morph from " + fromString + " to " + toString);
      }
      return new MySimplePropertyValuesHolder(propertyName, nodesFrom, nodesTo, valueType);
    }

    if (valueType == VALUE_TYPE_FLOAT) {
      float valueFrom;
      float valueTo;
      if (hasFrom) {
        if (fromType == TypedValue.TYPE_DIMENSION) {
          valueFrom = styledAttributes.getDimension(valueFromId, 0f);
        } else {
          valueFrom = styledAttributes.getFloat(valueFromId, 0f);
        }
        if (hasTo) {
          if (toType == TypedValue.TYPE_DIMENSION) {
            valueTo = styledAttributes.getDimension(valueToId, 0f);
          } else {
            valueTo = styledAttributes.getFloat(valueToId, 0f);
          }
          return new MySimplePropertyValuesHolder(propertyName, valueFrom, valueTo, valueType);
        } else {
          return new MySimplePropertyValuesHolder(propertyName, valueFrom, valueFrom, valueType);
        }
      } else {
        if (toType == TypedValue.TYPE_DIMENSION) {
          valueTo = styledAttributes.getDimension(valueToId, 0f);
        } else {
          valueTo = styledAttributes.getFloat(valueToId, 0f);
        }
        return new MySimplePropertyValuesHolder(propertyName, null, valueTo, valueType);
      }
    }

    int valueFrom;
    int valueTo;
    if (hasFrom) {
      if (fromType == TypedValue.TYPE_DIMENSION) {
        valueFrom = (int) styledAttributes.getDimension(valueFromId, 0f);
      } else if (isColorType(fromType)) {
        valueFrom = styledAttributes.getColor(valueFromId, 0);
      } else {
        valueFrom = styledAttributes.getInt(valueFromId, 0);
      }
      if (hasTo) {
        if (toType == TypedValue.TYPE_DIMENSION) {
          valueTo = (int) styledAttributes.getDimension(valueToId, 0f);
        } else if (isColorType(toType)) {
          valueTo = styledAttributes.getColor(valueToId, 0);
        } else {
          valueTo = styledAttributes.getInt(valueToId, 0);
        }
        return new MySimplePropertyValuesHolder(propertyName, valueFrom, valueTo, valueType);
      } else {
        return new MySimplePropertyValuesHolder(propertyName, valueFrom, valueFrom, valueType);
      }
    } else {
      if (hasTo) {
        if (toType == TypedValue.TYPE_DIMENSION) {
          valueTo = (int) styledAttributes.getDimension(valueToId, 0f);
        } else if (isColorType(toType)) {
          valueTo = styledAttributes.getColor(valueToId, 0);
        } else {
          valueTo = styledAttributes.getInt(valueToId, 0);
        }
        return new MySimplePropertyValuesHolder(propertyName, null, valueTo, valueType);
      }
    }

    return null;
  }

  /**
   * Utility function to set fractions on keyframes to cover a gap in which the fractions are not
   * currently set. Keyframe fractions will be distributed evenly in this gap. For example, a gap of
   * 1 keyframe in the range 0-1 will be at .5, a gap of .6 spread between two keyframes will be at
   * .2 and .4 beyond the fraction at the keyframe before startIndex. Assumptions: - First and last
   * keyframe fractions (bounding this spread) are already set. So, for example, if no fractions are
   * set, we will already set first and last keyframe fraction values to 0 and 1. - startIndex must
   * be >0 (which follows from first assumption). - endIndex must be >= startIndex.
   *
   * @param keyframes the array of keyframes
   * @param gap The total gap we need to distribute
   * @param startIndex The index of the first keyframe whose fraction must be set
   * @param endIndex The index of the last keyframe whose fraction must be set
   */
  private static void distributeKeyframes(
      Keyframe[] keyframes, float gap, int startIndex, int endIndex) {
    int count = endIndex - startIndex + 2;
    float increment = gap / count;
    for (int i = startIndex; i <= endIndex; ++i) {
      keyframes[i].fraction(keyframes[i - 1].getFraction() + increment);
    }
  }

  private static Keyframe loadKeyframe(
      Context context, AttributeSet attrs, @ValueType int valueType, XmlPullParser parser) {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.KEYFRAME);
    float fraction =
        TypedArrayUtils.getNamedFloat(a, parser, "fraction", Styleable.Keyframe.FRACTION, -1);

    final TypedValue keyframeValue =
        TypedArrayUtils.peekNamedValue(a, parser, "value", Styleable.Keyframe.VALUE);
    final boolean hasValue = (keyframeValue != null);
    if (valueType == VALUE_TYPE_UNDEFINED) {
      // When no value type is provided, check whether it's a color type first.
      // If not, fall back to default value type (i.e. float type).
      if (hasValue && isColorType(keyframeValue.type)) {
        valueType = VALUE_TYPE_COLOR;
      } else {
        valueType = VALUE_TYPE_FLOAT;
      }
    }

    // TODO: support keyframes for path morphing animations?
    Keyframe keyframe = null;
    if (hasValue) {
      switch (valueType) {
        case VALUE_TYPE_FLOAT:
          float value =
              TypedArrayUtils.getNamedFloat(a, parser, "value", Styleable.Keyframe.VALUE, 0);
          keyframe = Keyframe.<Float>of(fraction, value);
          break;
        case VALUE_TYPE_COLOR:
        case VALUE_TYPE_INT:
          int intValue =
              TypedArrayUtils.getNamedInt(a, parser, "value", Styleable.Keyframe.VALUE, 0);
          keyframe = Keyframe.<Integer>of(fraction, intValue);
          break;
      }
    } else {
      keyframe = (valueType == VALUE_TYPE_FLOAT) ? Keyframe.of(fraction) : Keyframe.of(fraction);
    }

    final int resId =
        TypedArrayUtils.getNamedResourceId(
            a, parser, "interpolator", Styleable.Keyframe.INTERPOLATOR, 0);
    if (resId > 0) {
      keyframe.interpolator(loadInterpolator(context, resId));
    }
    a.recycle();

    return keyframe;
  }

  private static boolean isColorType(int type) {
    return TypedValue.TYPE_FIRST_COLOR_INT <= type && type <= TypedValue.TYPE_LAST_COLOR_INT;
  }

  private abstract static class MyAnimator {
    public abstract long getTotalDuration();

    public abstract Map<String, List<Animation<?, ?>>> toMap(long extraStartDelay);
  }

  private static class MyAnimatorSet extends MyAnimator {
    private MyAnimator[] animators = {};
    private boolean isOrderingSequential = true;

    void playTogether(MyAnimator[] animators) {
      this.animators = animators;
      isOrderingSequential = false;
    }

    void playSequentially(MyAnimator[] animators) {
      this.animators = animators;
      isOrderingSequential = true;
    }

    @Override
    public long getTotalDuration() {
      long maxDuration = 0;
      long totalDuration = 0;
      for (MyAnimator animator : animators) {
        final long duration = animator.getTotalDuration();
        if (duration > maxDuration) {
          maxDuration = duration;
        }
        totalDuration += duration;
      }
      return isOrderingSequential ? totalDuration : maxDuration;
    }

    @NonNull
    @Override
    public Map<String, List<Animation<?, ?>>> toMap(long extraStartDelay) {
      final List<Map<String, List<Animation<?, ?>>>> maps = new ArrayList<>(animators.length);
      for (MyAnimator animator : animators) {
        maps.add(animator.toMap(extraStartDelay));
        if (isOrderingSequential) {
          extraStartDelay += animator.getTotalDuration();
        }
      }
      return mergeMaps(maps);
    }
  }

  private static class MyObjectAnimator extends MyAnimator {
    private long startDelay;
    private long duration;
    private RepeatMode repeatMode;
    private long repeatCount;
    @Nullable private TimeInterpolator interpolator;
    private MyPropertyValuesHolder[] values = {};

    public void setStartDelay(long startDelay) {
      this.startDelay = startDelay;
    }

    public long getStartDelay() {
      return startDelay;
    }

    public void setDuration(long duration) {
      this.duration = duration;
    }

    public long getDuration() {
      return duration;
    }

    public void setRepeatCount(long repeatCount) {
      this.repeatCount = repeatCount;
    }

    public long getRepeatCount() {
      return repeatCount;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
      this.repeatMode = repeatMode;
    }

    public RepeatMode getRepeatMode() {
      return repeatMode;
    }

    public void setInterpolator(@Nullable TimeInterpolator interpolator) {
      this.interpolator = interpolator;
    }

    @Nullable
    public TimeInterpolator getInterpolator() {
      return interpolator;
    }

    public void setValues(MyPropertyValuesHolder... values) {
      this.values = values;
    }

    @Override
    public long getTotalDuration() {
      return startDelay + duration;
    }

    @NonNull
    @Override
    public Map<String, List<Animation<?, ?>>> toMap(long extraStartDelay) {
      final long startTime = extraStartDelay + startDelay;
      final long endTime = startTime + duration;
      final List<Map<String, List<Animation<?, ?>>>> maps = new ArrayList<>();
      for (MyPropertyValuesHolder value : values) {
        maps.add(value.toMap(startTime, endTime, interpolator, repeatCount, repeatMode));
      }
      return mergeMaps(maps);
    }
  }

  private abstract static class MyPropertyValuesHolder {
    @NonNull
    public abstract Map<String, List<Animation<?, ?>>> toMap(
        long startTime,
        long endTime,
        TimeInterpolator interpolator,
        long repeatCount,
        RepeatMode repeatMode);
  }

  private static class MySimplePropertyValuesHolder extends MyPropertyValuesHolder {
    @NonNull private final String propertyName;
    @Nullable private final Object fromValue;
    @NonNull private final Object toValue;
    @ValueType private final int valueType;

    MySimplePropertyValuesHolder(
        String propertyName, @Nullable Object fromValue, Object toValue, @ValueType int valueType) {
      this.propertyName = propertyName;
      this.fromValue = fromValue;
      this.toValue = toValue;
      this.valueType = valueType;
    }

    @NonNull
    @Override
    public Map<String, List<Animation<?, ?>>> toMap(
        long startTime,
        long endTime,
        @Nullable TimeInterpolator interpolator,
        long repeatCount,
        RepeatMode repeatMode) {
      Animation<?, ?> anim;
      switch (valueType) {
        case VALUE_TYPE_FLOAT:
          if (fromValue == null) {
            anim = Animation.ofFloat((Float) toValue);
          } else {
            anim = Animation.ofFloat((Float) fromValue, (Float) toValue);
          }
          break;
        case VALUE_TYPE_COLOR:
          if (fromValue == null) {
            anim = Animation.ofArgb((Integer) toValue);
          } else {
            anim = Animation.ofArgb((Integer) fromValue, (Integer) toValue);
          }
          break;
        case VALUE_TYPE_PATH:
          if (fromValue == null) {
            anim =
                Animation.ofPathMorph((PathData) toValue)
                    .startDelay(startTime)
                    .duration(endTime - startTime);
          } else {
            anim = Animation.ofPathMorph((PathData) fromValue, (PathData) toValue);
          }
          break;
        default:
          throw new IllegalStateException("Invalid value type: " + valueType);
      }
      final List<Animation<?, ?>> anims = new ArrayList<>(1);
      if (interpolator == null) {
        interpolator = new AccelerateDecelerateInterpolator();
      }
      anims.add(
          anim.startDelay(startTime)
              .duration(endTime - startTime)
              .interpolator(interpolator == null ? DEFAULT_INTERPOLATOR : interpolator)
              .repeatCount(repeatCount)
              .repeatMode(repeatMode));
      final Map<String, List<Animation<?, ?>>> map = new ArrayMap<>(1);
      map.put(propertyName, anims);
      return map;
    }
  }

  private static class MyPathMotionPropertyValuesHolder extends MyPropertyValuesHolder {
    @NonNull private final Path path;
    @Nullable private final String propertyNameX;
    @Nullable private final String propertyNameY;

    MyPathMotionPropertyValuesHolder(
        Path path, @Nullable String propertyNameX, @Nullable String propertyNameY) {
      this.path = path;
      this.propertyNameX = propertyNameX;
      this.propertyNameY = propertyNameY;
    }

    @NonNull
    @Override
    public Map<String, List<Animation<?, ?>>> toMap(
        long startTime,
        long endTime,
        @Nullable TimeInterpolator interpolator,
        long repeatCount,
        RepeatMode repeatMode) {
      final Map<String, List<Animation<?, ?>>> map = new ArrayMap<>();
      if (interpolator == null) {
        interpolator = new AccelerateDecelerateInterpolator();
      }
      final Animation<PointF, PointF> anim =
          Animation.ofPathMotion(path)
              .startDelay(startTime)
              .duration(endTime - startTime)
              .interpolator(interpolator == null ? DEFAULT_INTERPOLATOR : interpolator)
              .repeatCount(repeatCount)
              .repeatMode(repeatMode);
      if (propertyNameX != null) {
        final List<Animation<?, ?>> list = new ArrayList<>(1);
        list.add(
            anim.transform(
                new Animation.ValueTransformer<PointF, Float>() {
                  @NonNull
                  @Override
                  public Float transform(PointF value) {
                    return value.x;
                  }
                }));
        map.put(propertyNameX, list);
      }
      if (propertyNameY != null) {
        final List<Animation<?, ?>> list = new ArrayList<>(1);
        list.add(
            anim.transform(
                new Animation.ValueTransformer<PointF, Float>() {
                  @NonNull
                  @Override
                  public Float transform(PointF value) {
                    return value.y;
                  }
                }));
        map.put(propertyNameY, list);
      }
      return map;
    }
  }

  private static class MyKeyframePropertyValuesHolder extends MyPropertyValuesHolder {
    @NonNull private final String propertyName;
    @NonNull private final Keyframe[] keyframes;
    @ValueType private final int valueType;

    public MyKeyframePropertyValuesHolder(
        String propertyName, Keyframe[] keyframes, @ValueType int valueType) {
      this.propertyName = propertyName;
      this.keyframes = keyframes;
      this.valueType = valueType;
    }

    @NonNull
    @Override
    public Map<String, List<Animation<?, ?>>> toMap(
        long startTime,
        long endTime,
        @Nullable TimeInterpolator interpolator,
        long repeatCount,
        RepeatMode repeatMode) {
      final Map<String, List<Animation<?, ?>>> map = new ArrayMap<>();
      Animation<?, ?> anim;
      switch (valueType) {
        case VALUE_TYPE_FLOAT:
          anim = Animation.ofFloat(keyframes);
          break;
        case VALUE_TYPE_COLOR:
          anim = Animation.ofArgb(keyframes);
          break;
        case VALUE_TYPE_PATH:
          anim = Animation.ofPathMorph(keyframes);
          break;
        default:
          throw new IllegalStateException("Invalid value type: " + valueType);
      }
      final List<Animation<?, ?>> anims = new ArrayList<>(1);
      anims.add(
          anim.startDelay(startTime)
              .duration(endTime - startTime)
              .interpolator(interpolator == null ? DEFAULT_INTERPOLATOR : interpolator)
              .repeatCount(repeatCount)
              .repeatMode(repeatMode));
      map.put(propertyName, anims);
      return map;
    }
  }

  @NonNull
  private static Map<String, List<Animation<?, ?>>> mergeMaps(
      List<Map<String, List<Animation<?, ?>>>> maps) {
    final Map<String, List<Animation<?, ?>>> outMap = new ArrayMap<>();
    for (Map<String, List<Animation<?, ?>>> map : maps) {
      for (Map.Entry<String, List<Animation<?, ?>>> entry : map.entrySet()) {
        if (outMap.containsKey(entry.getKey())) {
          outMap.get(entry.getKey()).addAll(entry.getValue());
        } else {
          outMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
      }
    }
    return outMap;
  }

  // </editor-fold>

  // <editor-fold desc="Interpolator inflation">

  private static final TimeInterpolator FAST_OUT_SLOW_IN =
      PathInterpolatorCompat.create(0.4f, 0, 0.2f, 1);
  private static final TimeInterpolator FAST_OUT_LINEAR_IN =
      PathInterpolatorCompat.create(0.4f, 0, 1, 1);
  private static final TimeInterpolator LINEAR_OUT_SLOW_IN =
      PathInterpolatorCompat.create(0, 0, 0.2f, 1);

  /**
   * Loads an {@link TimeInterpolator} object from a resource.
   *
   * @param context Application context used to access resources
   * @param id The resource id of the animation to load
   * @return The animation object reference by the specified id
   */
  private static TimeInterpolator loadInterpolator(
      Context context, @AnimRes @InterpolatorRes int id) throws NotFoundException {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return AnimationUtils.loadInterpolator(context, id);
    }

    XmlResourceParser parser = null;
    try {
      // Special treatment for the interpolators introduced in API 21.
      if (id == android.R.interpolator.fast_out_slow_in) {
        return FAST_OUT_SLOW_IN;
      } else if (id == android.R.interpolator.fast_out_linear_in) {
        return FAST_OUT_LINEAR_IN;
      } else if (id == android.R.interpolator.linear_out_slow_in) {
        return LINEAR_OUT_SLOW_IN;
      }
      parser = context.getResources().getAnimation(id);
      return createInterpolatorFromXml(context, parser);
    } catch (XmlPullParserException | IOException e) {
      final NotFoundException rnf =
          new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
      rnf.initCause(e);
      throw rnf;
    } finally {
      if (parser != null) {
        parser.close();
      }
    }
  }

  @Nullable
  private static TimeInterpolator createInterpolatorFromXml(Context context, XmlPullParser parser)
      throws XmlPullParserException, IOException {
    TimeInterpolator interpolator = null;

    // Make sure we are on a start tag.
    int type;
    int depth = parser.getDepth();

    while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
        && type != XmlPullParser.END_DOCUMENT) {

      if (type != XmlPullParser.START_TAG) {
        continue;
      }

      final AttributeSet attrs = Xml.asAttributeSet(parser);
      final String name = parser.getName();

      //noinspection IfCanBeSwitch
      if (name.equals("linearInterpolator")) {
        interpolator = new LinearInterpolator();
      } else if (name.equals("accelerateInterpolator")) {
        interpolator = new AccelerateInterpolator(context, attrs);
      } else if (name.equals("decelerateInterpolator")) {
        interpolator = new DecelerateInterpolator(context, attrs);
      } else if (name.equals("accelerateDecelerateInterpolator")) {
        interpolator = new AccelerateDecelerateInterpolator();
      } else if (name.equals("cycleInterpolator")) {
        interpolator = new CycleInterpolator(context, attrs);
      } else if (name.equals("anticipateInterpolator")) {
        interpolator = new AnticipateInterpolator(context, attrs);
      } else if (name.equals("overshootInterpolator")) {
        interpolator = new OvershootInterpolator(context, attrs);
      } else if (name.equals("anticipateOvershootInterpolator")) {
        interpolator = new AnticipateOvershootInterpolator(context, attrs);
      } else if (name.equals("bounceInterpolator")) {
        interpolator = new BounceInterpolator();
      } else if (name.equals("pathInterpolator")) {
        interpolator = inflatePathInterpolator(context, attrs, parser);
      } else {
        throw new RuntimeException("Unknown interpolator name: " + parser.getName());
      }
    }

    return interpolator;
  }

  @NonNull
  private static TimeInterpolator inflatePathInterpolator(
      Context context, AttributeSet attrs, XmlPullParser parser) {
    final TypedArray a =
        TypedArrayUtils.obtainAttributes(
            context.getResources(), context.getTheme(), attrs, Styleable.PATH_INTERPOLATOR);
    final TimeInterpolator interpolator = parseInterpolatorFromTypeArray(a, parser);
    a.recycle();
    return interpolator;
  }

  @NonNull
  private static TimeInterpolator parseInterpolatorFromTypeArray(
      TypedArray a, XmlPullParser parser) {
    if (TypedArrayUtils.hasAttribute(parser, "pathData")) {
      final String pathData =
          TypedArrayUtils.getNamedString(
              a, parser, "pathData", Styleable.PathInterpolator.PATH_DATA);
      final Path path = PathData.toPath(pathData == null ? "" : pathData);
      if (path.isEmpty()) {
        throw new InflateException("The path cannot be empty");
      }
      return PathInterpolatorCompat.create(path);
    }

    if (!TypedArrayUtils.hasAttribute(parser, "controlX1")) {
      throw new InflateException("pathInterpolator requires the controlX1 attribute");
    } else if (!TypedArrayUtils.hasAttribute(parser, "controlY1")) {
      throw new InflateException("pathInterpolator requires the controlY1 attribute");
    }

    final float x1 =
        TypedArrayUtils.getNamedFloat(
            a, parser, "controlX1", Styleable.PathInterpolator.CONTROL_X1, 0);
    final float y1 =
        TypedArrayUtils.getNamedFloat(
            a, parser, "controlY1", Styleable.PathInterpolator.CONTROL_Y1, 0);

    final boolean hasX2 = TypedArrayUtils.hasAttribute(parser, "controlX2");
    final boolean hasY2 = TypedArrayUtils.hasAttribute(parser, "controlY2");

    if (hasX2 != hasY2) {
      throw new InflateException(
          "pathInterpolator requires both controlX2 and controlY2 for cubic bezier curves");
    }

    if (hasX2) {
      float x2 =
          TypedArrayUtils.getNamedFloat(
              a, parser, "controlX2", Styleable.PathInterpolator.CONTROL_X2, 0);
      float y2 =
          TypedArrayUtils.getNamedFloat(
              a, parser, "controlY2", Styleable.PathInterpolator.CONTROL_Y2, 0);
      return PathInterpolatorCompat.create(x1, y1, x2, y2);
    } else {
      return PathInterpolatorCompat.create(x1, y1);
    }
  }

  // </editor-fold>

  private InflationUtils() {}
}
