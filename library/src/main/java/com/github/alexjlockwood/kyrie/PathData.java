package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public final class PathData {
  private static final PathDatum[] EMPTY_PATH_DATUMS = {};

  @NonNull
  public static PathData parse(@Nullable String pathData) {
    return PathDataUtils.parse(pathData);
  }

  @NonNull
  public static Path toPath(@Nullable String pathData) {
    return PathDataUtils.toPath(pathData);
  }

  @NonNull
  public static Path toPath(@NonNull PathData pathData) {
    final Path path = new Path();
    PathDataUtils.toPath(pathData, path);
    return path;
  }

  public static void toPath(@NonNull PathData pathData, @NonNull Path outPath) {
    PathDataUtils.toPath(pathData, outPath);
  }

  final PathDatum[] pathDatums;

  public PathData() {
    this(EMPTY_PATH_DATUMS);
  }

  PathData(@NonNull PathDatum[] pathDatums) {
    this.pathDatums = pathDatums;
  }

  public PathData(@NonNull PathData pathData) {
    final PathDatum[] source = pathData.pathDatums;
    pathDatums = new PathDatum[source.length];
    for (int i = 0; i < source.length; i++) {
      pathDatums[i] = new PathData.PathDatum(source[i]);
    }
  }

  public boolean canMorphWith(@NonNull PathData pathData) {
    return PathDataUtils.canMorph(this, pathData);
  }

  public void interpolate(@NonNull PathData from, @NonNull PathData to, float fraction) {
    if (!canMorphWith(from) || !canMorphWith(to)) {
      throw new IllegalArgumentException("Can't interpolate between two incompatible paths");
    }
    for (int i = 0; i < from.pathDatums.length; i++) {
      pathDatums[i].interpolate(from.pathDatums[i], to.pathDatums[i], fraction);
    }
  }

  /** Each PathDatum object represents one command in the "d" attribute of an SVG pathData. */
  static class PathDatum {

    char type;
    @NonNull float[] params;

    PathDatum(char type, @NonNull float[] params) {
      this.type = type;
      this.params = params;
    }

    PathDatum(PathDatum n) {
      type = n.type;
      params = Arrays.copyOfRange(n.params, 0, n.params.length);
    }

    /**
     * The current PathDatum will be interpolated between the from and to values according to the
     * current fraction.
     *
     * @param from The start value as a PathDatum.
     * @param to The end value as a PathDatum
     * @param fraction The fraction to interpolate.
     */
    void interpolate(@NonNull PathDatum from, @NonNull PathDatum to, float fraction) {
      for (int i = 0; i < from.params.length; i++) {
        params[i] = from.params[i] * (1 - fraction) + to.params[i] * fraction;
      }
    }
  }
}
