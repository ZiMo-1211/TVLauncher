# TV Launcher

A custom Android TV launcher with app grid, reflection icons, and bottom navigation buttons.

## Features

- 4-column app grid with **icon reflection effects**
- 5 bottom navigation buttons: Keystone, Miracast, Signal Source, My Apps, Settings
- Remote control focus highlight with **scale animation** (1.08x-1.1x)
- Real-time clock display
- All apps list page with alphabetical sorting
- Placeholder icon fallback for uninstalled apps
- Deep dark theme optimized for TV viewing

## Screenshots

*(Add screenshots here)*

## Tech Stack

- **Language**: Java
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: Single Activity + RecyclerView
- **UI Components**: `RelativeLayout`, `RecyclerView` (GridLayoutManager / LinearLayoutManager), custom drawable shapes

## Project Structure

```
app/src/main/java/com/example/tvlauncher/
├── MainActivity.java          # Home screen: clock, app grid, bottom buttons
├── AllAppsActivity.java       # All installed apps list page
├── AppGridAdapter.java        # Grid adapter with reflection support
├── AllAppsAdapter.java        # All apps list adapter
├── AppInfo.java               # App data model
└── ReflectionHelper.java      # Reflection bitmap generation utility

app/src/main/res/
├── layout/
│   ├── activity_main.xml      # Home screen layout
│   ├── activity_all_apps.xml  # All apps page layout
│   ├── item_app_grid.xml      # Grid item (icon + reflection + name)
│   └── item_all_app.xml       # All apps list item
├── drawable/
│   ├── bg_button.xml          # Bottom button background
│   ├── bg_focus.xml           # Focus highlight overlay
│   ├── bg_item.xml            # Grid/list item background
│   ├── gradient_reflection.xml
│   ├── ic_keystone.xml
│   ├── ic_miracast.xml
│   ├── ic_signal_source.xml
│   ├── ic_my_apps.xml
│   └── ic_settings.xml
└── values/
    ├── colors.xml
    ├── dimens.xml
    ├── strings.xml
    └── themes.xml
```

## Build & Run

1. Open project in **Android Studio**
2. Sync Gradle
3. Select a TV device/emulator (landscape recommended)
4. Click **Run** or execute:
   ```bash
   ./gradlew assembleDebug
   ```

## Customization

- **Grid columns**: Change `spanCount` in `MainActivity.java:setupRecyclerView()`
- **Default apps**: Edit the `defaultApps` array in `MainActivity.java:loadDefaultApps()`
- **Icon size**: Modify `app_icon_size` in `res/values/dimens.xml`
- **Button size**: Adjust `layout_width`/`layout_height` in `activity_main.xml`
- **Colors**: Update `res/values/colors.xml`

## License

MIT
