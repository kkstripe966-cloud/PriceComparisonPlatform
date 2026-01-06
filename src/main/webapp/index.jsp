<%--
  Created by IntelliJ IDEA.
  User: stripe
  Date: 2026/1/5
  Time: 21:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>今天吃什么Pro - 外卖比价平台</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
        }
        .container {
            background: rgba(255, 255, 255, 0.1);
            padding: 40px;
            border-radius: 15px;
            backdrop-filter: blur(10px);
        }
        h1 {
            font-size: 3em;
            margin-bottom: 20px;
        }
        p {
            font-size: 1.2em;
            margin-bottom: 30px;
            opacity: 0.9;
        }
        .status {
            background: rgba(255, 255, 255, 0.2);
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
        }
        .api-link {
            display: inline-block;
            margin: 10px;
            padding: 10px 20px;
            background: white;
            color: #667eea;
            text-decoration: none;
            border-radius: 25px;
            font-weight: bold;
            transition: transform 0.3s;
        }
        .api-link:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
<div class="container">
    <h1>🍔 今天吃什么Pro</h1>
    <p>外卖比价平台 - 主页模块API服务已启动</p>

    <div class="status">
        <h3>📊 服务状态：<span style="color: #4CAF50;">正常运行</span></h3>
        <p>当前时间：<span id="currentTime"></span></p>
    </div>

    <div style="margin-top: 30px;">
        <h3>🔧 API接口</h3>
        <a href="/api/home/index" class="api-link" target="_blank">获取首页数据</a>
        <a href="/api/home/categories" class="api-link" target="_blank">获取分类</a>
        <a href="/api/home/dishes/hot" class="api-link" target="_blank">热门菜品</a>
    </div>

    <div style="margin-top: 30px;">
        <p>后端开发完成，前端可以开始对接了！</p>
    </div>
</div>

<script>
    // 显示当前时间
    function updateTime() {
        const now = new Date();
        document.getElementById('currentTime').textContent =
            now.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
    }
    updateTime();
    setInterval(updateTime, 1000);
</script>
</body>
</html>
