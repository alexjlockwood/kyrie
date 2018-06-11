@javax.annotation.ParametersAreNonnullByDefault
package com.github.alexjlockwood.kyrie;

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
// TODO: support setting playback speed?
// TODO: allow user to inflate from xml resource as well as drawable resource?
// TODO: support playing animation in reverse?
// TODO: avoid using bitmap internally (encourage view software rendering instead)
// TODO: test inflating multi-file AVDs
// TODO: create kyrie view?
// TODO: make it clear what stuff shouldn't change after the kyrie drawable has been created!!!!!!!!
// TODO: customize behavior when ValueAnimator#areAnimatorsEnabled returns true
// TODO: make sure it works with AnimatedStateListDrawable?
// TODO: avoid setting initial state and animations on nodes separately? combine them somehow?
// TODO: should we use "startOffset" or "startDelay" as terminology? AVD object animators use startOffset
// TODO: should we use linear or accelerate/decelerate as the default interpolator
// TODO: publish sample app on play store?
