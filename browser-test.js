// 在浏览器控制台中运行这些代码来测试API

// 测试用户接口
fetch('http://localhost:8081/api/user/test')
  .then(response => response.json())
  .then(data => console.log('用户测试接口:', data))
  .catch(error => console.error('错误:', error));

// 测试分类接口
fetch('http://localhost:8081/api/home/categories')
  .then(response => response.json())
  .then(data => console.log('分类接口:', data))
  .catch(error => console.error('错误:', error));

// 测试价格健康检查
fetch('http://localhost:8081/api/price/health')
  .then(response => response.json())
  .then(data => console.log('价格健康检查:', data))
  .catch(error => console.error('错误:', error));

// 测试热门菜品
fetch('http://localhost:8081/api/home/dishes/hot?limit=3')
  .then(response => response.json())
  .then(data => console.log('热门菜品:', data))
  .catch(error => console.error('错误:', error));