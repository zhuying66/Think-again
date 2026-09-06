# 安装原生 App 并抓取各屏截图(供与设计图对照)
$adb = 'D:\Android\Sdk\platform-tools\adb.exe'
$apk = 'D:\NabuzhunNative\app\build\outputs\apk\release\app-release.apk'
$out = 'D:\NabuzhunNative\_verify'

New-Item -ItemType Directory -Force -Path $out | Out-Null

# 卸载旧包(清除旧数据,避免与 Expo 版本冲突)
& $adb uninstall com.zhuying.nabuzhun 2>$null | Out-Null
& $adb install -r $apk
& $adb shell am start -n com.zhuying.nabuzhun/.MainActivity
Start-Sleep -Seconds 3
& "$adb exec-out screencap -p" 2>$null | Out-Null

# 抓首页
& $adb exec-out screencap -p > "$out\home.png" 2>$null

Write-Output "installed and captured home.png"
