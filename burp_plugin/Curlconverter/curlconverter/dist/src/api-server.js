import express from 'express';
import cors from 'cors';
import * as curlConverters from './index.js';
const app = express();
const PORT = process.env.PORT || 3000;
// 中间件
app.use(cors());
app.use(express.json({ limit: '1mb' }));
app.use(express.text({ limit: '1mb' }));
// 静态文件服务
app.use(express.static('public'));
// 错误处理中间件
const asyncHandler = (fn) => (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next);
};
// 健康检查
app.get('/health', (req, res) => {
    res.json({ status: 'ok', timestamp: new Date().toISOString() });
});
// 获取支持的语言列表
app.get('/languages', (req, res) => {
    const languages = [
        'python', 'javascript', 'node', 'java', 'go', 'php', 'ruby', 'rust',
        'csharp', 'swift', 'kotlin', 'dart', 'r', 'julia', 'perl', 'lua',
        'clojure', 'elixir', 'objectivec', 'ocaml', 'matlab', 'powershell',
        'ansible', 'http', 'httpie', 'wget', 'cfml', 'c'
    ];
    res.json({
        supported_languages: languages,
        count: languages.length
    });
});
// API 文档路由
app.get('/api-docs', (req, res) => {
    res.redirect('/API-README.md');
});
// 主要的转换接口
app.post('/convert', asyncHandler(async (req, res) => {
    const { curl, language } = req.body;
    if (!curl) {
        return res.status(400).json({
            error: 'Missing required field: curl'
        });
    }
    if (!language) {
        return res.status(400).json({
            error: 'Missing required field: language'
        });
    }
    try {
        let result;
        let resultWarnings = [];
        // 根据语言选择相应的转换器
        switch (language.toLowerCase()) {
            case 'python':
                [result, resultWarnings] = curlConverters.toPythonWarn(curl);
                break;
            case 'javascript':
            case 'js':
                [result, resultWarnings] = curlConverters.toJavaScriptWarn(curl);
                break;
            case 'node':
            case 'nodejs':
                [result, resultWarnings] = curlConverters.toNodeWarn(curl);
                break;
            case 'java':
                [result, resultWarnings] = curlConverters.toJavaWarn(curl);
                break;
            case 'go':
                [result, resultWarnings] = curlConverters.toGoWarn(curl);
                break;
            case 'php':
                [result, resultWarnings] = curlConverters.toPhpWarn(curl);
                break;
            case 'ruby':
                [result, resultWarnings] = curlConverters.toRubyWarn(curl);
                break;
            case 'rust':
                [result, resultWarnings] = curlConverters.toRustWarn(curl);
                break;
            case 'csharp':
            case 'c#':
                [result, resultWarnings] = curlConverters.toCSharpWarn(curl);
                break;
            case 'swift':
                [result, resultWarnings] = curlConverters.toSwiftWarn(curl);
                break;
            case 'kotlin':
                [result, resultWarnings] = curlConverters.toKotlinWarn(curl);
                break;
            case 'dart':
                [result, resultWarnings] = curlConverters.toDartWarn(curl);
                break;
            case 'r':
                [result, resultWarnings] = curlConverters.toRWarn(curl);
                break;
            case 'julia':
                [result, resultWarnings] = curlConverters.toJuliaWarn(curl);
                break;
            case 'perl':
                [result, resultWarnings] = curlConverters.toPerlWarn(curl);
                break;
            case 'lua':
                [result, resultWarnings] = curlConverters.toLuaWarn(curl);
                break;
            case 'clojure':
                [result, resultWarnings] = curlConverters.toClojureWarn(curl);
                break;
            case 'elixir':
                [result, resultWarnings] = curlConverters.toElixirWarn(curl);
                break;
            case 'objectivec':
            case 'objc':
                [result, resultWarnings] = curlConverters.toObjectiveCWarn(curl);
                break;
            case 'ocaml':
                [result, resultWarnings] = curlConverters.toOCamlWarn(curl);
                break;
            case 'matlab':
                [result, resultWarnings] = curlConverters.toMATLABWarn(curl);
                break;
            case 'powershell':
            case 'ps1':
                [result, resultWarnings] = curlConverters.toPowershellRestMethodWarn(curl);
                break;
            case 'ansible':
                [result, resultWarnings] = curlConverters.toAnsibleWarn(curl);
                break;
            case 'http':
                [result, resultWarnings] = curlConverters.toHTTPWarn(curl);
                break;
            case 'httpie':
                [result, resultWarnings] = curlConverters.toHttpieWarn(curl);
                break;
            case 'wget':
                [result, resultWarnings] = curlConverters.toWgetWarn(curl);
                break;
            case 'cfml':
                [result, resultWarnings] = curlConverters.toCFMLWarn(curl);
                break;
            case 'c':
                [result, resultWarnings] = curlConverters.toCWarn(curl);
                break;
            default:
                return res.status(400).json({
                    error: `Unsupported language: ${language}. Use /languages endpoint to see supported languages.`
                });
        }
        return res.json({
            success: true,
            result: result,
            language: language,
            warnings: resultWarnings.length > 0 ? resultWarnings : undefined
        });
    }
    catch (error) {
        console.error('Conversion error:', error);
        return res.status(400).json({
            error: error.message || 'Failed to convert curl command'
        });
    }
}));
// 错误处理中间件
app.use((error, req, res, next) => {
    console.error('Unhandled error:', error);
    res.status(500).json({
        error: 'Internal server error'
    });
});
// 404处理
app.use((req, res) => {
    res.status(404).json({
        error: 'Endpoint not found'
    });
});
// 启动服务器
app.listen(PORT, () => {
    console.log(`🚀 CurlConverter Server running on port ${PORT}`);
    console.log(`🌐 Web界面: http://localhost:${PORT}`);
    console.log(`📋 Health check: http://localhost:${PORT}/health`);
    console.log(`📚 Languages: http://localhost:${PORT}/languages`);
    console.log(`🔄 Convert API: POST http://localhost:${PORT}/convert`);
    console.log(`📖 API文档: http://localhost:${PORT}/api-docs`);
});
export default app;
//# sourceMappingURL=api-server.js.map