import * as curlconverter from "../src/index.js";
export declare const fixturesDir: string;
declare function toParser(curl: string | string[]): string;
declare const converters: {
    readonly ansible: {
        readonly name: "Ansible";
        readonly extension: ".yml";
        readonly converter: typeof curlconverter.toAnsible;
    };
    readonly c: {
        readonly name: "C";
        readonly extension: ".c";
        readonly converter: typeof curlconverter.toC;
    };
    readonly cfml: {
        readonly name: "ColdFusion";
        readonly extension: ".cfm";
        readonly converter: typeof curlconverter.toCFML;
    };
    readonly clojure: {
        readonly name: "Clojure";
        readonly extension: ".clj";
        readonly converter: typeof curlconverter.toClojure;
    };
    readonly csharp: {
        readonly name: "C#";
        readonly extension: ".cs";
        readonly converter: typeof curlconverter.toCSharp;
    };
    readonly dart: {
        readonly name: "Dart";
        readonly extension: ".dart";
        readonly converter: typeof curlconverter.toDart;
    };
    readonly elixir: {
        readonly name: "Elixir";
        readonly extension: ".ex";
        readonly converter: typeof curlconverter.toElixir;
    };
    readonly go: {
        readonly name: "Go";
        readonly extension: ".go";
        readonly converter: typeof curlconverter.toGo;
    };
    readonly har: {
        readonly name: "HAR";
        readonly extension: ".json";
        readonly converter: typeof curlconverter.toHarString;
    };
    readonly http: {
        readonly name: "HTTP";
        readonly extension: ".txt";
        readonly converter: typeof curlconverter.toHTTP;
    };
    readonly httpie: {
        readonly name: "HTTPie";
        readonly extension: ".sh";
        readonly converter: typeof curlconverter.toHttpie;
    };
    readonly java: {
        readonly name: "Java + HttpClient";
        readonly extension: ".java";
        readonly converter: typeof curlconverter.toJava;
    };
    readonly "java-httpurlconnection": {
        readonly name: "Java + HttpUrlConnection";
        readonly extension: ".java";
        readonly converter: typeof curlconverter.toJavaHttpUrlConnection;
    };
    readonly "java-jsoup": {
        readonly name: "Java + jsoup";
        readonly extension: ".java";
        readonly converter: (curlCommand: string | string[]) => string;
    };
    readonly "java-okhttp": {
        readonly name: "Java + OkHttp";
        readonly extension: ".java";
        readonly converter: typeof curlconverter.toJavaOkHttp;
    };
    readonly javascript: {
        readonly name: "JavaScript";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toJavaScript;
    };
    readonly "javascript-jquery": {
        readonly name: "JavaScript + jQuery";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toJavaScriptJquery;
    };
    readonly "javascript-xhr": {
        readonly name: "JavaScript + XHR";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toJavaScriptXHR;
    };
    readonly json: {
        readonly name: "Json";
        readonly extension: ".json";
        readonly converter: typeof curlconverter.toJsonString;
    };
    readonly julia: {
        readonly name: "Julia";
        readonly extension: ".jl";
        readonly converter: typeof curlconverter.toJulia;
    };
    readonly kotlin: {
        readonly name: "Kotlin";
        readonly extension: ".kt";
        readonly converter: typeof curlconverter.toKotlin;
    };
    readonly lua: {
        readonly name: "Lua";
        readonly extension: ".lua";
        readonly converter: typeof curlconverter.toLua;
    };
    readonly matlab: {
        readonly name: "MATLAB";
        readonly extension: ".m";
        readonly converter: typeof curlconverter.toMATLAB;
    };
    readonly node: {
        readonly name: "Node";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNode;
    };
    readonly "node-axios": {
        readonly name: "Node + Axios";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeAxios;
    };
    readonly "node-got": {
        readonly name: "Node + Got";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeGot;
    };
    readonly "node-http": {
        readonly name: "Node + http";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeHttp;
    };
    readonly "node-ky": {
        readonly name: "Node + Ky";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeKy;
    };
    readonly "node-request": {
        readonly name: "Node + request";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeRequest;
    };
    readonly "node-superagent": {
        readonly name: "Node + SuperAgent";
        readonly extension: ".js";
        readonly converter: typeof curlconverter.toNodeSuperAgent;
    };
    readonly objectivec: {
        readonly name: "Objective-C";
        readonly extension: ".m";
        readonly converter: typeof curlconverter.toObjectiveC;
    };
    readonly ocaml: {
        readonly name: "OCaml";
        readonly extension: ".ml";
        readonly converter: typeof curlconverter.toOCaml;
    };
    readonly perl: {
        readonly name: "Perl";
        readonly extension: ".pl";
        readonly converter: typeof curlconverter.toPerl;
    };
    readonly php: {
        readonly name: "PHP";
        readonly extension: ".php";
        readonly converter: typeof curlconverter.toPhp;
    };
    readonly "php-guzzle": {
        readonly name: "PHP + Guzzle";
        readonly extension: ".php";
        readonly converter: typeof curlconverter.toPhpGuzzle;
    };
    readonly powershell: {
        readonly name: "PowerShell";
        readonly extension: ".ps1";
        readonly converter: typeof curlconverter.toPowershellRestMethod;
    };
    readonly python: {
        readonly name: "Python";
        readonly extension: ".py";
        readonly converter: typeof curlconverter.toPython;
    };
    readonly r: {
        readonly name: "R";
        readonly extension: ".r";
        readonly converter: typeof curlconverter.toR;
    };
    readonly "r-httr2": {
        readonly name: "R + httr2";
        readonly extension: ".r";
        readonly converter: typeof curlconverter.toRHttr2;
    };
    readonly ruby: {
        readonly name: "Ruby";
        readonly extension: ".rb";
        readonly converter: typeof curlconverter.toRuby;
    };
    readonly "ruby-httparty": {
        readonly name: "Ruby HTTParty";
        readonly extension: ".rb";
        readonly converter: typeof curlconverter.toRubyHttparty;
    };
    readonly rust: {
        readonly name: "Rust";
        readonly extension: ".rs";
        readonly converter: typeof curlconverter.toRust;
    };
    readonly swift: {
        readonly name: "Swift";
        readonly extension: ".swift";
        readonly converter: typeof curlconverter.toSwift;
    };
    readonly wget: {
        readonly name: "Wget";
        readonly extension: ".sh";
        readonly converter: typeof curlconverter.toWget;
    };
    readonly parser: {
        readonly name: "Parser";
        readonly extension: ".json";
        readonly converter: typeof toParser;
    };
};
type Converter = keyof typeof converters;
export { converters };
export type { Converter };
