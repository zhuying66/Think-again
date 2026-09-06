# 跑通「小问题」完整链路:进答题 → 逐题选「会有积极影响」→ 下一题 → 结果页
$adb = 'D:\Android\Sdk\platform-tools\adb.exe'
$out = 'D:\NabuzhunNative\_verify'

function Shot($name) { & $adb exec-out screencap -p > "$out\$name.png" 2>$null; Write-Output "shot $name" }
function Tap($x,$y) { & $adb shell input tap $x $y 2>$null; Write-Output "tap $x,$y"; Start-Sleep -Milliseconds 900 }
function Alive { (& $adb shell pidof com.zhuying.nabuzhun 2>$null).Trim() }

# 确保在首页
& $adb shell am start -n com.zhuying.nabuzhun/.MainActivity 2>$null | Out-Null
Start-Sleep -Seconds 2
Shot "f0_home"

# 点小问题卡片 (x540, y1310)
Tap 540 1310
Shot "f1_quiz_q1"

# 逐题:选「会有积极影响」再点下一题。答案卡第一张 y~475,下一题按钮在底部 y~2180
$questions = 5
for ($i=1; $i -le $questions; $i++) {
    Tap 540 475          # 选「会有积极影响」
    Shot "f2_sel_q$i"
    Tap 540 2180         # 下一题
    Start-Sleep -Milliseconds 600
}
# 最后一题选完后应进结果页
Shot "f3_result"

Write-Output ("alive: " + (Alive))
