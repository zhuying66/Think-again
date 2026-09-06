# 驱动原生 App:进小问题答题、截图、选答案、进结果页截图
param(
    [string]$outDir = "D:\NabuzhunNative\_verify"
)
$adb = 'D:\Android\Sdk\platform-tools\adb.exe'
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

function Shot($name) {
    & $adb exec-out screencap -p > "$outDir\$name.png" 2>$null
    Write-Output "shot: $name"
}
function Screen() {
    $xml = & $adb shell uiautomator dump /sdcard/ui.xml 2>$null
    & $adb pull /sdcard/ui.xml "$outDir\ui.xml" 2>$null | Out-Null
    return Get-Content "$outDir\ui.xml" -Raw
}
function TapText($text) {
    $xml = Screen()
    $m = [regex]::Match($xml, "text=\"$text\"[^>]*bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"")
    if ($m.Success) {
        $cx = [int]( ([int]$m.Groups[1].Value + [int]$m.Groups[3].Value) / 2 )
        $cy = [int]( ([int]$m.Groups[2].Value + [int]$m.Groups[4].Value) / 2 )
        & $adb shell input tap $cx $cy 2>$null
        Write-Output "tapped: $text ($cx,$cy)"
        Start-Sleep -Milliseconds 800
    } else {
        Write-Output "NOT FOUND: $text"
    }
}
function TapContentDesc($desc) {
    $xml = Screen()
    $m = [regex]::Match($xml, "content-desc=\"$desc\"[^>]*")
    Write-Output "searching desc: $desc"
    if ($m.Success) {
        $b = [regex]::Match($m.Value, "bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"")
        if ($b.Success) {
            $cx = [int]( ([int]$b.Groups[1].Value + [int]$b.Groups[3].Value) / 2 )
            $cy = [int]( ([int]$b.Groups[2].Value + [int]$b.Groups[4].Value) / 2 )
            & $adb shell input tap $cx $cy 2>$null
            Write-Output "tapped desc: $desc ($cx,$cy)"
            Start-Sleep -Milliseconds 800
        }
    } else {
        Write-Output "desc NOT FOUND: $desc"
    }
}

# 首页
Shot "home"
# 点小问题卡片(de。 内容有'小问题'文本)
TapText "小问题"
# 答题页首页
Shot "quiz_small"
# 点击第一个答案(会选中)
TapText "会有积极影响"
Start-Sleep -Milliseconds 500
Shot "quiz_answer_selected"
# 点下一题
TapText "下一题"
# 连点几次下一题(不选答案则disabled,先选答案)
Write-Output "done basic drive"
