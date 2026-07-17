# TV Launcher

一个自定义 Android TV 桌面启动器，支持应用网格倒影图标、底部导航按钮和全部应用列表。

## 功能特性

- 4 列应用网格，支持 **图标倒影效果**
- 5 个底部导航按钮：Keystone、Miracast、Signal Source、My Apps、Settings
- 遥控器焦点高亮 + **缩放动画** (1.08x-1.1x)
- 实时时钟显示
- 全部应用列表页，按字母排序
- 未安装应用自动生成占位图标
- 深色主题，适配电视观看场景

## 截图

*(待补充)*

## 技术栈

- **语言**: Java
- **最低 SDK**: 21 (Android 5.0)
- **目标 SDK**: 34 (Android 14)
- **架构**: 单 Activity + RecyclerView
- **UI 组件**: `RelativeLayout`、`RecyclerView` (GridLayoutManager / LinearLayoutManager)、自定义 shape drawable

## 项目结构

```
app/src/main/java/com/example/tvlauncher/
├── MainActivity.java          # 主界面：时钟、应用网格、底部按钮
├── AllAppsActivity.java       # 全部应用列表页
├── AppGridAdapter.java        # 网格适配器（倒影图标）
├── AllAppsAdapter.java        # 全部应用列表适配器
├── AppInfo.java               # 应用数据模型
└── ReflectionHelper.java      # 倒影位图生成工具

app/src/main/res/
├── layout/
│   ├── activity_main.xml      # 主界面布局
│   ├── activity_all_apps.xml  # 全部应用页布局
│   ├── item_app_grid.xml      # 网格条目（图标 + 倒影 + 名称）
│   └── item_all_app.xml       # 列表条目
├── drawable/
│   ├── bg_button.xml          # 底部按钮背景
│   ├── bg_focus.xml           # 焦点高亮遮罩
│   ├── bg_item.xml            # 网格/列表条目背景
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

## 构建运行

1. 用 **Android Studio** 打开项目
2. 同步 Gradle
3. 选择 TV 设备或模拟器（推荐横屏）
4. 点击 **Run** 或执行：
   ```bash
   ./gradlew assembleDebug
   ```

## 自定义配置

- **网格列数**: 修改 `MainActivity.java` 中 `setupRecyclerView()` 的 `spanCount`
- **默认应用**: 编辑 `MainActivity.java` 中 `loadDefaultApps()` 的 `defaultApps` 数组
- **图标尺寸**: 修改 `res/values/dimens.xml` 中的 `app_icon_size`
- **按钮尺寸**: 调整 `activity_main.xml` 中的 `layout_width`/`layout_height`
- **颜色主题**: 修改 `res/values/colors.xml`

## 许可证

MIT
