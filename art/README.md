# App Icon Assets

`icon.svg` is the source of truth for JTetris app icons. It intentionally avoids text so the same source works across macOS, Windows, and Linux.

Generated assets:
- `icon.icns`: macOS app packaging.
- `icon.ico`: Windows packaging.
- `icon-1024.png`: large preview/source raster.
- `icons/icon-*.png`: reusable Linux and desktop packaging sizes.

When regenerating assets, render PNG sizes from the SVG with `rsvg-convert`, build `icon.ico` with ImageMagick, and verify `icon.icns` with both `file art/icon.icns` and `mvn -Pmac clean package`.
