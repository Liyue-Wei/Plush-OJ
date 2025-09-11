<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Plush::OJ</title>
    <link rel="stylesheet" href="style_Judge.css">
</head>

<?php
// 1. 檢查 'problem' 參數是否存在，並進行安全處理
$problem_id_display = '未指定題目'; // 預設顯示文字

// 檢查 URL 中是否有 'problem' 參數
if (isset($_GET['problem']))
{
    // 使用 htmlspecialchars 避免 XSS 攻擊
    $prob = htmlspecialchars($_GET['problem']);
    $problem_id_display = $prob; // 將處理過的值用於顯示
}
// 如果需要原始值進行後續處理（例如資料庫查詢），可以另外儲存
// $raw_prob = isset($_GET['problem']) ? $_GET['problem'] : null;
?>

<body>
<div class="header-mask"></div>
<div class="header">
    <div class="logo">Plush::OJ</div>
    <nav class="nav">
        <a href="Home.html">首頁</a>
        <!-- <a href="#rank">排名</a> -->
        <a href="#question">題庫</a>
        <a href="#contest">比賽</a>
        <a href="#event">動態</a>
        <a href="Login.html">登入</a>
    </nav>
</div>

<div style="padding-left: 20px; padding-right: 20px;">
    <div class="question-number">
        <!-- 2. 使用 echo 輸出 PHP 變數 -->
        <h2>題目：<?php echo $problem_id_display; ?></h2>
        <!-- 或者使用短語法： <h2>題目：<?= $problem_id_display ?></h2> -->

        <!-- 根據題目 ID 載入的內容可以放在這裡 -->
        <?php
        // 範例：如果成功獲取到題號，可以顯示不同的內容
        // 檢查 $prob 變數是否已經被設定 (這表示 URL 中有 problem 參數)
        if (isset($prob))
        {
            echo "<p>正在顯示題目 " . $prob . " 的詳細資訊...</p>";
            // 在這裡可以加入更多根據 $prob 載入題目內容的 PHP 程式碼
        }
        else // 如果 $prob 未設定 (URL 中沒有 problem 參數)
        {
            echo "<p>請從題庫選擇一個題目。</p>";
        }
        ?>

        <!-- 移除或保留靜態測試內容 -->
        <!--
        <p>Test 1</p>
        <p>Test 2</p>
        <p>Test 3</p>
        -->
    </div>
</div>
</body>
</html>