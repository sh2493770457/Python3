// 全局状态
let currentLanguage = '';
let currentResult = '';
let totalConversions = parseInt(localStorage.getItem('totalConversions') || '0');

// 语言配置
const languages = [
    { id: 'python', name: 'Python', icon: '🐍' },
    { id: 'javascript', name: 'JavaScript', icon: '📜' },
    { id: 'node', name: 'Node.js', icon: '💚' },
    { id: 'java', name: 'Java', icon: '☕' },
    { id: 'go', name: 'Go', icon: '🐹' },
    { id: 'php', name: 'PHP', icon: '🐘' },
    { id: 'ruby', name: 'Ruby', icon: '💎' },
    { id: 'rust', name: 'Rust', icon: '🦀' },
    { id: 'csharp', name: 'C#', icon: '🔷' },
    { id: 'swift', name: 'Swift', icon: '🦉' },
    { id: 'kotlin', name: 'Kotlin', icon: '🅺' },
    { id: 'dart', name: 'Dart', icon: '🎯' },
    { id: 'r', name: 'R', icon: '📊' },
    { id: 'julia', name: 'Julia', icon: '🔮' },
    { id: 'perl', name: 'Perl', icon: '🐪' },
    { id: 'lua', name: 'Lua', icon: '🌙' },
    { id: 'clojure', name: 'Clojure', icon: '🔧' },
    { id: 'elixir', name: 'Elixir', icon: '💧' },
    { id: 'objectivec', name: 'Objective-C', icon: '🍎' },
    { id: 'ocaml', name: 'OCaml', icon: '🐫' },
    { id: 'matlab', name: 'MATLAB', icon: '📐' },
    { id: 'powershell', name: 'PowerShell', icon: '💙' },
    { id: 'ansible', name: 'Ansible', icon: '🔴' },
    { id: 'http', name: 'HTTP', icon: '🌐' },
    { id: 'httpie', name: 'HTTPie', icon: '🔥' },
    { id: 'wget', name: 'Wget', icon: '📥' },
    { id: 'cfml', name: 'CFML', icon: '🔶' },
    { id: 'c', name: 'C', icon: '⚡' }
];

// curl 示例
const examples = {
    get: `curl -X GET https://api.github.com/user \\
  -H "Authorization: Bearer YOUR_TOKEN" \\
  -H "Accept: application/vnd.github.v3+json"`,
    
    post: `curl -X POST https://api.example.com/users \\
  -H "Content-Type: application/json" \\
  -d '{"name":"John Doe","email":"john@example.com","age":30}'`,
    
    auth: `curl -X GET https://api.example.com/protected \\
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \\
  -H "User-Agent: MyApp/1.0"`,
    
    form: `curl -X POST https://httpbin.org/post \\
  -F "name=John Doe" \\
  -F "email=john@example.com" \\
  -F "file=@document.pdf"`,
    
    http: `curl -X POST https://api.example.com/data \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer token123" \\
  -H "User-Agent: MyApp/1.0" \\
  -d '{"message":"Hello World","timestamp":"2024-01-01T00:00:00Z"}'`
};

// Prism 语言映射
const prismLanguageMap = {
    'python': 'python',
    'javascript': 'javascript',
    'node': 'javascript',
    'java': 'java',
    'go': 'go',
    'php': 'php',
    'ruby': 'ruby',
    'rust': 'rust',
    'csharp': 'csharp',
    'swift': 'swift',
    'kotlin': 'kotlin',
    'dart': 'dart',
    'r': 'r',
    'julia': 'julia',
    'perl': 'perl',
    'lua': 'lua',
    'clojure': 'clojure',
    'elixir': 'elixir',
    'objectivec': 'objectivec',
    'ocaml': 'ocaml',
    'matlab': 'matlab',
    'powershell': 'powershell',
    'ansible': 'yaml',
    'http': 'http-message',
    'httpie': 'bash',
    'wget': 'bash',
    'cfml': 'markup',
    'c': 'c'
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// 初始化应用
function initializeApp() {
    setupLanguageSelector();
    setupLanguageShowcase();
    updateStats();
    setupEventListeners();
    
    // 设置默认语言
    selectLanguage('python');
    
    // 检查是否是首次访问
    checkFirstVisit();
}

// 设置语言选择器
function setupLanguageSelector() {
    const select = document.getElementById('languageSelect');
    const grid = document.getElementById('languageGrid');
    
    // 填充下拉选择器
    languages.forEach(lang => {
        const option = document.createElement('option');
        option.value = lang.id;
        option.textContent = lang.name;
        select.appendChild(option);
    });
    
    // 创建语言按钮网格
    languages.forEach(lang => {
        const button = document.createElement('button');
        button.className = 'language-btn';
        button.dataset.language = lang.id;
        button.innerHTML = `${lang.icon} ${lang.name}`;
        button.onclick = () => selectLanguage(lang.id);
        grid.appendChild(button);
    });
    
    // 下拉选择器变化事件
    select.addEventListener('change', function() {
        if (this.value) {
            selectLanguage(this.value);
        }
    });
}

// 设置语言展示区
function setupLanguageShowcase() {
    const showcase = document.getElementById('languagesShowcase');
    
    languages.forEach(lang => {
        const item = document.createElement('div');
        item.className = 'language-showcase-item';
        item.onclick = () => selectLanguage(lang.id);
        item.innerHTML = `
            <div class="icon">${lang.icon}</div>
            <div class="name">${lang.name}</div>
        `;
        showcase.appendChild(item);
    });
}

// 选择语言
function selectLanguage(languageId) {
    currentLanguage = languageId;
    
    // 更新选择器状态
    document.getElementById('languageSelect').value = languageId;
    
    // 更新按钮状态
    document.querySelectorAll('.language-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.language === languageId);
    });
    
    // 启用转换按钮
    updateConvertButton();
}

// 设置事件监听器
function setupEventListeners() {
    const curlInput = document.getElementById('curlInput');
    
    // 监听输入变化
    curlInput.addEventListener('input', updateConvertButton);
    
    // 键盘快捷键
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey || e.metaKey) {
            switch (e.key) {
                case 'Enter':
                    e.preventDefault();
                    convertCurl();
                    break;
                case 'k':
                    e.preventDefault();
                    curlInput.focus();
                    break;
            }
        }
    });
}

// 更新转换按钮状态
function updateConvertButton() {
    const curlInput = document.getElementById('curlInput');
    const convertBtn = document.querySelector('.btn-convert');
    
    const hasInput = curlInput.value.trim().length > 0;
    const hasLanguage = currentLanguage.length > 0;
    
    convertBtn.disabled = !(hasInput && hasLanguage);
}

// 加载示例
function loadExample(type) {
    const curlInput = document.getElementById('curlInput');
    curlInput.value = examples[type];
    updateConvertButton();
    
    // 滚动到输入区域
    curlInput.scrollIntoView({ behavior: 'smooth', block: 'center' });
    curlInput.focus();
}

// 清空输入
function clearInput() {
    const curlInput = document.getElementById('curlInput');
    curlInput.value = '';
    updateConvertButton();
    hideOutput();
    curlInput.focus();
}

// 转换 curl 命令
async function convertCurl() {
    const curlInput = document.getElementById('curlInput');
    const convertBtn = document.querySelector('.btn-convert');
    
    const curl = curlInput.value.trim();
    if (!curl || !currentLanguage) {
        showNotification('请输入 curl 命令并选择目标语言', 'warning');
        return;
    }
    
    // 显示加载状态
    convertBtn.classList.add('loading');
    convertBtn.disabled = true;
    
    try {
        const response = await fetch('/convert', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                curl: curl,
                language: currentLanguage
            })
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            currentResult = data.result;
            showResult(data);
            updateConversions();
            showNotification('转换成功！', 'success');
        } else {
            showError(data.error || '转换失败');
            showNotification(data.error || '转换失败', 'error');
        }
    } catch (error) {
        console.error('转换错误:', error);
        showError('网络错误，请检查服务器连接');
        showNotification('网络错误，请检查服务器连接', 'error');
    } finally {
        // 隐藏加载状态
        convertBtn.classList.remove('loading');
        updateConvertButton();
    }
}

// 显示结果
function showResult(data) {
    const placeholder = document.getElementById('outputPlaceholder');
    const result = document.getElementById('outputResult');
    const error = document.getElementById('outputError');
    
    // 隐藏其他状态
    placeholder.style.display = 'none';
    error.style.display = 'none';
    
    // 显示结果
    result.style.display = 'block';
    
    // 更新结果内容
    const languageInfo = languages.find(lang => lang.id === currentLanguage);
    document.getElementById('resultLanguage').textContent = languageInfo ? languageInfo.name : currentLanguage;
    
    const codeElement = document.getElementById('resultCode');
    
    // 特殊处理HTTP格式
    if (currentLanguage === 'http') {
        formatHttpRequest(codeElement, data.result);
    } else {
        codeElement.textContent = data.result;
        codeElement.className = `language-${prismLanguageMap[currentLanguage] || 'text'}`;
        
        // 应用语法高亮
        if (window.Prism) {
            Prism.highlightElement(codeElement);
        }
    }
    
    // 显示警告（如果有）
    const warnings = document.getElementById('warnings');
    const warningsList = document.getElementById('warningsList');
    
    if (data.warnings && data.warnings.length > 0) {
        warningsList.innerHTML = '';
        data.warnings.forEach(warning => {
            const li = document.createElement('li');
            li.textContent = warning;
            warningsList.appendChild(li);
        });
        warnings.style.display = 'block';
    } else {
        warnings.style.display = 'none';
    }
    
    // 启用操作按钮
    document.querySelector('.btn-copy').disabled = false;
    document.querySelector('.output-actions .btn:last-child').disabled = false;
    
    // 滚动到结果区域
    result.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// 格式化HTTP请求显示
function formatHttpRequest(element, httpText) {
    // 移除原有类名
    element.className = 'http-formatted';
    
    // 清理文本，移除多余的空白
    const cleanText = httpText.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
    const lines = cleanText.split('\n').filter(line => line !== '');
    
    let formattedHtml = '';
    let inBody = false;
    let bodyStarted = false;
    
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmedLine = line.trim();
        
        // 检测请求行（第一行且匹配HTTP方法）
        if (i === 0 && trimmedLine.match(/^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT)\s+/i)) {
            const parts = trimmedLine.split(/\s+/);
            if (parts.length >= 2) {
                formattedHtml += `<span class="method">${escapeHtml(parts[0])}</span> `;
                formattedHtml += `<span class="url">${escapeHtml(parts[1])}</span>`;
                if (parts.length >= 3) {
                    formattedHtml += ` <span class="version">${escapeHtml(parts[2])}</span>`;
                }
                formattedHtml += '\n';
            } else {
                formattedHtml += escapeHtml(line) + '\n';
            }
        }
        // 检测空行（开始请求体）
        else if (trimmedLine === '' && !inBody) {
            formattedHtml += '\n';
            inBody = true;
        }
        // 检测请求头（包含冒号且不在请求体中）
        else if (trimmedLine.includes(':') && !inBody && !trimmedLine.startsWith('{') && !trimmedLine.startsWith('[')) {
            const colonIndex = trimmedLine.indexOf(':');
            const headerName = trimmedLine.substring(0, colonIndex).trim();
            const headerValue = trimmedLine.substring(colonIndex + 1).trim();
            formattedHtml += `<span class="header-name">${escapeHtml(headerName)}</span>: `;
            formattedHtml += `<span class="header-value">${escapeHtml(headerValue)}</span>\n`;
        }
        // 请求体内容
        else {
            if (!bodyStarted) {
                formattedHtml += '<span class="body">';
                bodyStarted = true;
                inBody = true;
            }
            
            // 尝试格式化JSON
            if (trimmedLine.startsWith('{') || trimmedLine.startsWith('[')) {
                try {
                    const jsonObj = JSON.parse(trimmedLine);
                    const prettyJson = JSON.stringify(jsonObj, null, 2);
                    formattedHtml += escapeHtml(prettyJson) + '\n';
                } catch (e) {
                    formattedHtml += escapeHtml(line) + '\n';
                }
            } else {
                formattedHtml += escapeHtml(line) + '\n';
            }
        }
    }
    
    // 关闭body标签
    if (bodyStarted) {
        formattedHtml += '</span>';
    }
    
    // 如果没有任何格式化内容，回退到原始文本
    if (!formattedHtml.trim()) {
        element.textContent = httpText;
        element.className = 'language-text';
        return;
    }
    
    element.innerHTML = formattedHtml;
}

// HTML转义函数
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// 显示错误
function showError(message) {
    const placeholder = document.getElementById('outputPlaceholder');
    const result = document.getElementById('outputResult');
    const error = document.getElementById('outputError');
    
    // 隐藏其他状态
    placeholder.style.display = 'none';
    result.style.display = 'none';
    
    // 显示错误
    error.style.display = 'flex';
    document.getElementById('errorMessage').textContent = message;
    
    // 禁用操作按钮
    document.querySelector('.btn-copy').disabled = true;
    document.querySelector('.output-actions .btn:last-child').disabled = true;
}

// 隐藏输出
function hideOutput() {
    const placeholder = document.getElementById('outputPlaceholder');
    const result = document.getElementById('outputResult');
    const error = document.getElementById('outputError');
    
    placeholder.style.display = 'flex';
    result.style.display = 'none';
    error.style.display = 'none';
    
    // 禁用操作按钮
    document.querySelector('.btn-copy').disabled = true;
    document.querySelector('.output-actions .btn:last-child').disabled = true;
    
    currentResult = '';
}

// 复制结果
async function copyResult() {
    if (!currentResult) {
        showNotification('没有可复制的内容', 'warning');
        return;
    }
    
    try {
        await navigator.clipboard.writeText(currentResult);
        showNotification('代码已复制到剪贴板！', 'success');
        
        // 添加复制反馈动画
        const copyBtn = document.querySelector('.btn-copy');
        copyBtn.classList.add('copy-success');
        setTimeout(() => copyBtn.classList.remove('copy-success'), 2000);
    } catch (error) {
        console.error('复制失败:', error);
        
        // 备用复制方法
        const textArea = document.createElement('textarea');
        textArea.value = currentResult;
        document.body.appendChild(textArea);
        textArea.select();
        
        try {
            document.execCommand('copy');
            showNotification('代码已复制到剪贴板！', 'success');
        } catch (err) {
            showNotification('复制失败，请手动复制', 'error');
        }
        
        document.body.removeChild(textArea);
    }
}

// 下载结果
function downloadResult() {
    if (!currentResult) {
        showNotification('没有可下载的内容', 'warning');
        return;
    }
    
    const languageInfo = languages.find(lang => lang.id === currentLanguage);
    const fileExtensions = {
        'python': 'py',
        'javascript': 'js',
        'node': 'js',
        'java': 'java',
        'go': 'go',
        'php': 'php',
        'ruby': 'rb',
        'rust': 'rs',
        'csharp': 'cs',
        'swift': 'swift',
        'kotlin': 'kt',
        'dart': 'dart',
        'r': 'r',
        'julia': 'jl',
        'perl': 'pl',
        'lua': 'lua',
        'clojure': 'clj',
        'elixir': 'ex',
        'objectivec': 'm',
        'ocaml': 'ml',
        'matlab': 'm',
        'powershell': 'ps1',
        'ansible': 'yml',
        'http': 'http',
        'httpie': 'sh',
        'wget': 'sh',
        'cfml': 'cfm',
        'c': 'c'
    };
    
    const extension = fileExtensions[currentLanguage] || 'txt';
    const filename = `converted_request.${extension}`;
    
    const blob = new Blob([currentResult], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
    
    showNotification(`文件已下载: ${filename}`, 'success');
}

// 更新转换统计
function updateConversions() {
    totalConversions++;
    localStorage.setItem('totalConversions', totalConversions.toString());
    updateStats();
}

// 更新统计数字
function updateStats() {
    const element = document.getElementById('totalConversions');
    if (element) {
        element.textContent = totalConversions.toLocaleString();
    }
}

// 显示通知
function showNotification(message, type = 'info') {
    const notifications = document.getElementById('notifications');
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    notifications.appendChild(notification);
    
    // 触发显示动画
    setTimeout(() => notification.classList.add('show'), 100);
    
    // 自动隐藏
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notifications.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// 显示关于对话框
function showAbout() {
    const content = `
        <h4>关于 CurlConverter</h4>
        <p>CurlConverter 是一个强大的在线工具，可以将 curl 命令快速转换为各种编程语言的代码。</p>
        
        <h5>主要特性：</h5>
        <ul>
            <li>🚀 支持 25+ 种编程语言</li>
            <li>🛡️ 本地处理，保护隐私</li>
            <li>💻 开源免费</li>
            <li>📱 响应式设计，支持移动设备</li>
            <li>🎯 简单易用的界面</li>
        </ul>
        
        <h5>使用方法：</h5>
        <ol>
            <li>粘贴你的 curl 命令</li>
            <li>选择目标编程语言</li>
            <li>点击"转换代码"按钮</li>
            <li>复制或下载生成的代码</li>
        </ol>
        
        <p>本项目基于开源的 <a href="https://github.com/curlconverter/curlconverter" target="_blank">curlconverter</a> 库构建。</p>
    `;
    
    showModal('关于 CurlConverter', content);
}

// 显示 API 文档
function showApiDocs() {
    const content = `
        <h4>API 文档</h4>
        <p>CurlConverter 提供了 REST API，你可以在自己的应用中集成转换功能。</p>
        
        <h5>转换端点：</h5>
        <pre><code>POST /convert
Content-Type: application/json

{
  "curl": "curl -X GET https://api.example.com",
  "language": "python"
}</code></pre>
        
        <h5>响应示例：</h5>
        <pre><code>{
  "success": true,
  "result": "import requests\\n\\nresponse = requests.get('https://api.example.com')",
  "language": "python",
  "warnings": []
}</code></pre>
        
        <h5>支持的语言：</h5>
        <p>${languages.map(lang => lang.name).join(', ')}</p>
        
        <h5>其他端点：</h5>
        <ul>
            <li><code>GET /health</code> - 健康检查</li>
            <li><code>GET /languages</code> - 获取支持的语言列表</li>
        </ul>
        
        <p>完整的 API 文档请访问：<a href="/api-docs" target="_blank">API 文档</a></p>
    `;
    
    showModal('API 文档', content);
}

// 显示模态框
function showModal(title, content) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');
    
    modalTitle.textContent = title;
    modalBody.innerHTML = content;
    
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

// 关闭模态框
function closeModal() {
    const modal = document.getElementById('modal');
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

// 添加全局错误处理
window.addEventListener('error', function(e) {
    console.error('全局错误:', e);
    showNotification('发生了未知错误，请刷新页面重试', 'error');
});

// 添加未处理的 Promise 拒绝处理
window.addEventListener('unhandledrejection', function(e) {
    console.error('未处理的 Promise 拒绝:', e);
    showNotification('网络请求失败，请检查网络连接', 'error');
});

// 检查首次访问
function checkFirstVisit() {
    const hasVisited = localStorage.getItem('hasVisited');
    if (!hasVisited) {
        localStorage.setItem('hasVisited', 'true');
        // 延迟显示欢迎提示
        setTimeout(() => {
            showWelcomeGuide();
        }, 1000);
    }
}

// 显示欢迎指南
function showWelcomeGuide() {
    const content = `
        <h4>🎉 欢迎使用 CurlConverter！</h4>
        <p>这是一个强大的 curl 命令转换工具，让你轻松将 curl 命令转换为各种编程语言代码。</p>
        
        <h5>🚀 快速上手：</h5>
        <ol>
            <li><strong>粘贴 curl 命令</strong> - 将你的 curl 命令粘贴到输入框中</li>
            <li><strong>选择目标语言</strong> - 从 25+ 种编程语言中选择一个</li>
            <li><strong>点击转换</strong> - 点击"转换代码"按钮获得结果</li>
            <li><strong>复制或下载</strong> - 将生成的代码复制或下载使用</li>
        </ol>
        
        <h5>💡 实用提示：</h5>
        <ul>
            <li>使用示例按钮快速尝试不同类型的请求</li>
            <li>支持键盘快捷键：Ctrl+Enter 转换，Ctrl+K 聚焦输入框</li>
            <li>转换结果支持语法高亮，便于阅读</li>
            <li>所有操作都在本地进行，保护你的数据安全</li>
        </ul>
        
        <div style="text-align: center; margin-top: 20px;">
            <button class="btn btn-primary" onclick="closeModal(); loadExample('get')">
                🎯 试试 GET 请求示例
            </button>
        </div>
    `;
    
    showModal('欢迎使用 CurlConverter', content);
}

// 添加键盘快捷键提示
function showShortcutsHelp() {
    const content = `
        <h4>⌨️ 键盘快捷键</h4>
        <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 10px; font-family: monospace;">
            <strong>Ctrl + Enter</strong><span>转换 curl 命令</span>
            <strong>Ctrl + K</strong><span>聚焦输入框</span>
            <strong>Ctrl + C</strong><span>复制转换结果（在结果区域时）</span>
            <strong>Esc</strong><span>关闭模态框</span>
        </div>
        
        <h5 style="margin-top: 20px;">🔧 实用功能：</h5>
        <ul>
            <li>点击语言卡片快速选择语言</li>
            <li>使用示例按钮快速加载常用请求</li>
            <li>双击代码区域全选结果</li>
            <li>长按复制按钮显示复制确认</li>
        </ul>
    `;
    
    showModal('快捷键和技巧', content);
}

// 添加 ESC 键关闭模态框
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// 添加代码区域双击全选功能
document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('dblclick', function(e) {
        if (e.target.tagName === 'CODE') {
            const selection = window.getSelection();
            const range = document.createRange();
            range.selectNodeContents(e.target);
            selection.removeAllRanges();
            selection.addRange(range);
        }
    });
});

// 导出供测试使用的函数
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        selectLanguage,
        convertCurl,
        showNotification,
        loadExample,
        showWelcomeGuide,
        showShortcutsHelp
    };
} 