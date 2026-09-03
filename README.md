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

Open in Android Studio and run the `app` module. A prebuilt debug APK is available
from the Actions tab (CI builds run on pushes to the `main` branch).

The library itself is pulled from JitPack (latest release):

```kotlin
implementation("com.github.boy-offi9-inc:matrix-rain-view:1.0.1")
```

## Assets / media files

This demo includes an `assets/` folder (app/src/main/assets) where you can
place media files your demo app needs at runtime. The repository currently
contains a placeholder (`assets/.gitkeep`) so the folder is present in the
repo — add your real media files to the same directory.

Recommended folder layout inside `app/src/main/assets`:

- assets/
  - fonts/           # custom TTF/OTF fonts
  - images/          # PNG/JPG image assets
  - audio/           # MP3/OGG soundtracks or effects
  - data/            # text files, character sets, JSON, etc.

Example files you might add for this demo:

- `assets/data/matrix_chars.txt` — custom character set used by the rain view
- `assets/fonts/retro.ttf` — monospace/retro font for UI
- `assets/images/banner.png` — background or banner image used in the UI
- `assets/audio/ambient_loop.mp3` — optional background audio

How assets are packaged

- Files placed in `app/src/main/assets/` are packaged into the APK as-is and
  can be opened at runtime via `Context.assets` (no resource IDs).
- If you prefer resource IDs (e.g. `R.raw.some_file`), put files under
  `app/src/main/res/raw/` instead.

Loading asset files from Kotlin (examples)

Read a text file (e.g. custom charset):

```kotlin
val chars = assets.open("data/matrix_chars.txt").bufferedReader().use { it.readText() }
// Use `chars` as you need — e.g., parse into a list or feed a view that accepts custom data.
```

Load a font from assets:

```kotlin
val typeface = Typeface.createFromAsset(assets, "fonts/retro.ttf")
textView.typeface = typeface
```

Load a bitmap into an ImageView:

```kotlin
val stream = assets.open("images/banner.png")
val bitmap = BitmapFactory.decodeStream(stream)
imageView.setImageBitmap(bitmap)
stream.close()
```

Notes and best practices

- Keep asset file names lowercase with underscores (no spaces) to avoid issues
  on case-sensitive filesystems and when referencing them from code.
- Avoid committing very large media files to the repository; consider using
  Git LFS for large binaries or hosting big assets externally (CDN, cloud
  storage) and downloading them at runtime.
- If you add files locally with Android Studio, they will be packaged into
  the debug/release APK automatically when you build.

Git housekeeping

- The repo currently includes `assets/.gitkeep` so the directory exists in
  source control. Replace or remove it when you add real files.
- If you add large media, add a `.gitattributes` and use Git LFS, or add
  the files to your `.gitignore` and provide download instructions in the
  README (see below).

Optional: document downloadable assets

If you prefer not to commit large assets, add a small `assets/README.md`
(or a section here) describing how to download them. Example:

1. Run `scripts/fetch-assets.sh` (provided in this repo) to download
   approved assets into `app/src/main/assets/` before building.
2. Or manually copy the following files into `app/src/main/assets/`:
   - `data/matrix_chars.txt`
   - `fonts/retro.ttf`
   - `images/banner.png`

If you'd like, I can update this README in the repository now to add the
section above and (optionally) include a short asset-fetch script. Would
you like me to commit the README change?