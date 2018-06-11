package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;

import java.util.Arrays;

/** A simple container class that represents an SVG path string. */
public final class PathData {

  /**
   * Constructs a {@link PathData} object from the provided SVG path data string.
   *
   * @param pathData The SVG path data string to convert.
   * @return A {@link PathData} object represented by the provided SVG path data string.
   */
  @NonNull
  public static PathData parse(String pathData) {
    return PathDataUtils.parse(pathData);
  }

  /**
   * Constructs a {@link Path} from the provided {@link PathData} object.
   *
   * @param pathData The SVG path data string to convert.
   * @return A {@link Path} represented by the provided SVG path data string.
   */
  @NonNull
  public static Path toPath(String pathData) {
    return PathDataUtils.toPath(pathData);
  }

  /**
   * Constructs a {@link Path} from the provided {@link PathData} object.
   *
   * @param pathData The {@link PathData} object to convert.
   * @return A {@link Path} represented by the provided {@link PathData} object.
   */
  @NonNull
  public static Path toPath(PathData pathData) {
    final Path path = new Path();
    PathDataUtils.toPath(pathData, path);
    return path;
  }

  /**
   * Initializes a {@link Path} from the provided {@link PathData} object.
   *
   * @param pathData The {@link PathData} object to convert.
   * @param outPath The {@link Path} to write to.
   */
  public static void toPath(PathData pathData, Path outPath) {
    PathDataUtils.toPath(pathData, outPath);
  }

  private static final PathDatum[] EMPTY_PATH_DATUMS = {};

  final PathDatum[] pathDatums;

  PathData() {
    this(EMPTY_PATH_DATUMS);
  }

  PathData(PathDatum[] pathDatums) {
    this.pathDatums = pathDatums;
  }

  PathData(PathData pathData) {
    final PathDatum[] source = pathData.pathDatums;
    pathDatums = new PathDatum[source.length];
    for (int i = 0; i < source.length; i++) {
      pathDatums[i] = new PathData.PathDatum(source[i]);
    }
  }

  /**
   * Checks if this {@link PathData} object is morphable with another {@link PathData} object.
   *
   * @param pathData The {@link PathData} object to compare against.
   * @return true iff this {@link PathData} object is morphable with the provided {@link PathData}
   *     object.
   */
  public boolean canMorphWith(PathData pathData) {
    return PathDataUtils.canMorph(this, pathData);
  }

  /**
   * Interpolates this {@link PathData} object between two {@link PathData} objects by the given
   * fraction.
   *
   * @param from The starting {@link PathData} object.
   * @param to The ending {@link PathData} object.
   * @param fraction The interpolation fraction.
   * @throws IllegalArgumentException If the from or to {@link PathData} arguments aren't morphable
   *     with this {@link PathData} object.
   */
  void interpolate(PathData from, PathData to, float fraction) {
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

    PathDatum(char type, float[] params) {
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
    void interpolate(PathDatum from, PathDatum to, float fraction) {
      for (int i = 0; i < from.params.length; i++) {
        params[i] = from.params[i] * (1 - fraction) + to.params[i] * fraction;
      }
    }
  }
}
