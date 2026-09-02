# matrix-rain-demo

A tiny sample app showing [matrix-rain-view](https://github.com/boy-offi9-inc/matrix-rain-view)
in the "background effect" use case: a `MatrixRainView` running full-screen
*behind* real UI, rather than as a full-screen effect on its own.

## What it shows

`activity_main.xml` wraps a `FrameLayout` around two children:

1. `MatrixRainView`, bottom-most, filling the screen
2. A centered `LinearLayout` with real content on top

That ordering matters — `LinearLayout` arranges children edge-to-edge and
can't stack them, so the rain view has to be a `FrameLayout` (or
`ConstraintLayout`) child, not a `LinearLayout` sibling. This app is a
working, running proof of that pattern instead of just a doc claim.

## Running it

Open in Android Studio and run the `app` module, or grab a prebuilt debug
APK from the [Actions](../../actions) tab — every push to `main` builds one.

The library itself is pulled from JitPack:

```kotlin
implementation("com.github.Boy-Offi9:matrix-rain-view:1.0.0")
```

## License

MIT — see [LICENSE](./LICENSE).
