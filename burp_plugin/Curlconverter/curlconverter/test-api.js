#!/usr/bin/env node

/**
 * CurlConverter API 测试脚本
 * 
 * 使用方法:
 * node test-api.js
 */

const API_BASE_URL = 'http://localhost:3000';

// 测试用例
const testCases = [
  {
    name: '简单GET请求',
    curl: 'curl https://httpbin.org/get',
    language: 'python'
  },
  {
    name: 'POST请求带JSON数据',
    curl: 'curl -X POST https://httpbin.org/post -H "Content-Type: application/json" -d \'{"name":"John","age":30}\'',
    language: 'javascript'
  },
  {
    name: '带认证的请求',
    curl: 'curl -X GET https://api.github.com/user -H "Authorization: Bearer YOUR_TOKEN"',
    language: 'go'
  },
  {
    name: '表单数据提交',
    curl: 'curl -X POST https://httpbin.org/post -d "name=John&email=john@example.com"',
    language: 'java'
  },
  {
    name: '文件上传',
    curl: 'curl -X POST https://httpbin.org/post -F "file=@example.txt" -F "name=test"',
    language: 'php'
  }
];

async function makeRequest(url, options = {}) {
  try {
    const response = await fetch(url, options);
    const data = await response.json();
    return { success: response.ok, data, status: response.status };
  } catch (error) {
    return { success: false, error: error.message };
  }
}

async function testHealthCheck() {
  console.log('🔍 测试健康检查...');
  const result = await makeRequest(`${API_BASE_URL}/health`);
  
  if (result.success) {
    console.log('✅ 健康检查通过');
    console.log(`   状态: ${result.data.status}`);
    console.log(`   时间: ${result.data.timestamp}`);
  } else {
    console.log('❌ 健康检查失败');
    console.log(`   错误: ${result.error || result.data?.error}`);
  }
  console.log();
}

async function testLanguagesList() {
  console.log('🔍 测试支持的语言列表...');
  const result = await makeRequest(`${API_BASE_URL}/languages`);
  
  if (result.success) {
    console.log('✅ 获取语言列表成功');
    console.log(`   支持的语言数量: ${result.data.count}`);
    console.log(`   前5种语言: ${result.data.supported_languages.slice(0, 5).join(', ')}`);
  } else {
    console.log('❌ 获取语言列表失败');
    console.log(`   错误: ${result.error || result.data?.error}`);
  }
  console.log();
}

async function testConversion(testCase) {
  console.log(`🔍 测试转换: ${testCase.name}`);
  console.log(`   curl: ${testCase.curl}`);
  console.log(`   目标语言: ${testCase.language}`);
  
  const result = await makeRequest(`${API_BASE_URL}/convert`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      curl: testCase.curl,
      language: testCase.language
    })
  });
  
  if (result.success) {
    console.log('✅ 转换成功');
    console.log(`   语言: ${result.data.language}`);
    console.log(`   警告数量: ${result.data.warnings?.length || 0}`);
    console.log('   生成的代码:');
    console.log('   ─────────────────────────────────────');
    console.log(result.data.result.split('\n').map(line => `   ${line}`).join('\n'));
    console.log('   ─────────────────────────────────────');
    
    if (result.data.warnings && result.data.warnings.length > 0) {
      console.log('   警告信息:');
      result.data.warnings.forEach((warning, index) => {
        console.log(`   ${index + 1}. ${warning}`);
      });
    }
  } else {
    console.log('❌ 转换失败');
    console.log(`   状态码: ${result.status}`);
    console.log(`   错误: ${result.error || result.data?.error}`);
  }
  console.log();
}

async function testErrorHandling() {
  console.log('🔍 测试错误处理...');
  
  // 测试缺少必需字段
  console.log('   测试缺少curl字段...');
  const result1 = await makeRequest(`${API_BASE_URL}/convert`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      language: 'python'
    })
  });
  
  if (!result1.success && result1.status === 400) {
    console.log('   ✅ 正确处理缺少curl字段');
  } else {
    console.log('   ❌ 未正确处理缺少curl字段');
  }
  
  // 测试不支持的语言
  console.log('   测试不支持的语言...');
  const result2 = await makeRequest(`${API_BASE_URL}/convert`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      curl: 'curl https://example.com',
      language: 'unsupported-language'
    })
  });
  
  if (!result2.success && result2.status === 400) {
    console.log('   ✅ 正确处理不支持的语言');
  } else {
    console.log('   ❌ 未正确处理不支持的语言');
  }
  
  // 测试无效的curl命令
  console.log('   测试无效的curl命令...');
  const result3 = await makeRequest(`${API_BASE_URL}/convert`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      curl: 'invalid command',
      language: 'python'
    })
  });
  
  if (!result3.success && result3.status === 400) {
    console.log('   ✅ 正确处理无效的curl命令');
  } else {
    console.log('   ❌ 未正确处理无效的curl命令');
  }
  
  console.log();
}

async function runTests() {
  console.log('🚀 开始测试 CurlConverter API');
  console.log(`📡 API地址: ${API_BASE_URL}`);
  console.log();
  
  // 基础功能测试
  await testHealthCheck();
  await testLanguagesList();
  
  // 转换功能测试
  console.log('🔄 开始转换功能测试...');
  for (const testCase of testCases) {
    await testConversion(testCase);
  }
  
  // 错误处理测试
  await testErrorHandling();
  
  console.log('🏁 测试完成！');
}

// 检查fetch是否可用（Node.js 18+）
if (typeof fetch === 'undefined') {
  console.error('❌ 此脚本需要Node.js 18+版本（支持原生fetch）');
  console.error('或者请安装node-fetch: npm install node-fetch');
  process.exit(1);
}

// 运行测试
runTests().catch(error => {
  console.error('❌ 测试运行失败:', error.message);
  console.error('请确保API服务器正在运行: npm run api');
  process.exit(1);
}); 