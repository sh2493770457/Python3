window = (function(){
  var v_saf;!function(){var n=Function.toString,t=[],i=[],o=[].indexOf.bind(t),e=[].push.bind(t),r=[].push.bind(i);function u(n,t){return-1==o(n)&&(e(n),r(`function ${t||n.name||""}() { [native code] }`)),n}Object.defineProperty(Function.prototype,"toString",{enumerable:!1,configurable:!0,writable:!0,value:function(){return"function"==typeof this&&i[o(this)]||n.call(this)}}),u(Function.prototype.toString,"toString"),v_saf=u}();


  function _inherits(t, e) {
    t.prototype = Object.create(e.prototype, {
      constructor: { value: t, writable: !0, configurable: !0 }
    }), e && Object.setPrototypeOf(t, e) }
  Object.defineProperty(Object.prototype, Symbol.toStringTag, {
  get() {
    const proto = Object.getPrototypeOf(this);
    return proto && proto.constructor ? proto.constructor.name : "Object";
  },
  configurable: true,
});
  var v_new_toggle = true
  var v_console_logger = console.log
  var v_allow_types = ["string", "number", "boolean"]
  console.log=v_saf(function(a){if (v_allow_types.indexOf(typeof a)!=-1){v_console_logger.apply(this, arguments)}}, "log")
  console.debug=v_saf(function(a){if (v_allow_types.indexOf(typeof a)!=-1){v_console_logger.apply(this, arguments)}}, "debug")
  console.warn=v_saf(function(a){if (v_allow_types.indexOf(typeof a)!=-1){v_console_logger.apply(this, arguments)}}, "warn")
  console.info=v_saf(function(a){if (v_allow_types.indexOf(typeof a)!=-1){v_console_logger.apply(this, arguments)}}, "info")
  var v_console_log = function(){if (!v_new_toggle){ v_console_logger.apply(this, arguments) }}
  var v_random = (function() { var seed = 276951438; return function random() { return seed = (seed * 9301 + 49297) % 233280, (seed / 233280)} })()
  var v_new = function(v){var temp=v_new_toggle; v_new_toggle = true; var r = new v; v_new_toggle = temp; return r}


  Window = v_saf(function Window(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(Window, EventTarget)
  Screen = v_saf(function Screen(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(Screen, EventTarget)
  Node = v_saf(function Node(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(Node, EventTarget)
  Document = v_saf(function Document(){;}); _inherits(Document, Node)
  HTMLDocument = v_saf(function HTMLDocument(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };Object.defineProperty(this, 'location', {get(){return location}})}); _inherits(HTMLDocument, Document)
  Element = v_saf(function Element(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(Element, Node)
  HTMLElement = v_saf(function HTMLElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLElement, Element)
  HTMLHtmlElement = v_saf(function HTMLHtmlElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLHtmlElement, HTMLElement)
  HTMLHeadElement = v_saf(function HTMLHeadElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLHeadElement, HTMLElement)
  HTMLBodyElement = v_saf(function HTMLBodyElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLBodyElement, HTMLElement)
  Navigator = v_saf(function Navigator(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };this._plugins = typeof PluginArray=='undefined'?[]:v_new(PluginArray); this._mimeTypes = typeof MimeTypeArray=='undefined'?[]:v_new(MimeTypeArray)})
  PluginArray = v_saf(function PluginArray(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };
    this[0]=v_new(Plugin);this[0].description="Portable Document Format";this[0].filename="internal-pdf-viewer";this[0].length=2;this[0].name="PDF Viewer";
    this[1]=v_new(Plugin);this[1].description="Portable Document Format";this[1].filename="internal-pdf-viewer";this[1].length=2;this[1].name="Chrome PDF Viewer";
    this[2]=v_new(Plugin);this[2].description="Portable Document Format";this[2].filename="internal-pdf-viewer";this[2].length=2;this[2].name="Chromium PDF Viewer";
    this[3]=v_new(Plugin);this[3].description="Portable Document Format";this[3].filename="internal-pdf-viewer";this[3].length=2;this[3].name="Microsoft Edge PDF Viewer";
    this[4]=v_new(Plugin);this[4].description="Portable Document Format";this[4].filename="internal-pdf-viewer";this[4].length=2;this[4].name="WebKit built-in PDF";})
  Plugin = v_saf(function Plugin(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  MimeTypeArray = v_saf(function MimeTypeArray(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };
    this[0]=v_new(Plugin);this[0].description="Portable Document Format";this[0].enabledPlugin={"0":{},"1":{}};this[0].suffixes="pdf";this[0].type="application/pdf";
    this[1]=v_new(Plugin);this[1].description="Portable Document Format";this[1].enabledPlugin={"0":{},"1":{}};this[1].suffixes="pdf";this[1].type="text/pdf";})
  MimeType = v_saf(function MimeType(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  CSSStyleDeclaration = v_saf(function CSSStyleDeclaration(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  Location = v_saf(function Location(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  HTMLCanvasElement = v_saf(function HTMLCanvasElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLCanvasElement, HTMLElement)
  WebGLRenderingContext = v_saf(function WebGLRenderingContext(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };
    function WebGLBuffer(){}
    function WebGLProgram(){}
    function WebGLShader(){}
    this._toggle = {}
    this.createBuffer = function(){ v_console_log('  [*] WebGLRenderingContext -> createBuffer[func]'); return v_new(WebGLBuffer) }
    this.createProgram = function(){ v_console_log('  [*] WebGLRenderingContext -> createProgram[func]'); return v_new(WebGLProgram) }
    this.createShader = function(){ v_console_log('  [*] WebGLRenderingContext -> createShader[func]'); return v_new(WebGLShader) }
    this.getSupportedExtensions = function(){
      v_console_log('  [*] WebGLRenderingContext -> getSupportedExtensions[func]')
      return [
        "ANGLE_instanced_arrays", "EXT_blend_minmax", "EXT_color_buffer_half_float", "EXT_disjoint_timer_query", "EXT_float_blend", "EXT_frag_depth",
        "EXT_shader_texture_lod", "EXT_texture_compression_bptc", "EXT_texture_compression_rgtc", "EXT_texture_filter_anisotropic", "WEBKIT_EXT_texture_filter_anisotropic", "EXT_sRGB",
        "KHR_parallel_shader_compile", "OES_element_index_uint", "OES_fbo_render_mipmap", "OES_standard_derivatives", "OES_texture_float", "OES_texture_float_linear",
        "OES_texture_half_float", "OES_texture_half_float_linear", "OES_vertex_array_object", "WEBGL_color_buffer_float", "WEBGL_compressed_texture_s3tc",
        "WEBKIT_WEBGL_compressed_texture_s3tc", "WEBGL_compressed_texture_s3tc_srgb", "WEBGL_debug_renderer_info", "WEBGL_debug_shaders",
        "WEBGL_depth_texture","WEBKIT_WEBGL_depth_texture","WEBGL_draw_buffers","WEBGL_lose_context","WEBKIT_WEBGL_lose_context","WEBGL_multi_draw",
      ]
    }
    var self = this
    this.getExtension = function(key){
      v_console_log('  [*] WebGLRenderingContext -> getExtension[func]:', key)
      class WebGLDebugRendererInfo{
        get UNMASKED_VENDOR_WEBGL(){self._toggle[37445]=1;return 37445}
        get UNMASKED_RENDERER_WEBGL(){self._toggle[37446]=1;return 37446}
      }
      class EXTTextureFilterAnisotropic{}
      class WebGLLoseContext{
        loseContext(){}
        restoreContext(){}
      }
      if (key == 'WEBGL_debug_renderer_info'){ var r = new WebGLDebugRendererInfo }
      if (key == 'EXT_texture_filter_anisotropic'){ var r = new EXTTextureFilterAnisotropic }
      if (key == 'WEBGL_lose_context'){ var r = new WebGLLoseContext }
      else{ var r = new WebGLDebugRendererInfo }
      return r
    }
    this.getParameter = function(key){
      v_console_log('  [*] WebGLRenderingContext -> getParameter[func]:', key)
      if (this._toggle[key]){
        if (key == 37445){ return "Google Inc. (NVIDIA)" }
        if (key == 37446){ return "ANGLE (NVIDIA, NVIDIA GeForce GTX 1050 Ti Direct3D11 vs_5_0 ps_5_0, D3D11-27.21.14.5671)" }
      }else{
        if (key == 33902){ return new Float32Array([1,1]) }
        if (key == 33901){ return new Float32Array([1,1024]) }
        if (key == 35661){ return 32 }
        if (key == 34047){ return 16 }
        if (key == 34076){ return 16384 }
        if (key == 36349){ return 1024 }
        if (key == 34024){ return 16384 }
        if (key == 34930){ return 16 }
        if (key == 3379){ return 16384 }
        if (key == 36348){ return 30 }
        if (key == 34921){ return 16 }
        if (key == 35660){ return 16 }
        if (key == 36347){ return 4095 }
        if (key == 3386){ return new Int32Array([32767, 32767]) }
        if (key == 3410){ return 8 }
        if (key == 7937){ return "WebKit WebGL" }
        if (key == 35724){ return "WebGL GLSL ES 1.0 (OpenGL ES GLSL ES 1.0 Chromium)" }
        if (key == 3415){ return 0 }
        if (key == 7936){ return "WebKit" }
        if (key == 7938){ return "WebGL 1.0 (OpenGL ES 2.0 Chromium)" }
        if (key == 3411){ return 8 }
        if (key == 3412){ return 8 }
        if (key == 3413){ return 8 }
        if (key == 3414){ return 24 }
        return null
      }
    }
    this.getContextAttributes = function(){
      v_console_log('  [*] WebGLRenderingContext -> getContextAttributes[func]')
      return {
        alpha: true,
        antialias: true,
        depth: true,
        desynchronized: false,
        failIfMajorPerformanceCaveat: false,
        powerPreference: "default",
        premultipliedAlpha: true,
        preserveDrawingBuffer: false,
        stencil: false,
        xrCompatible: false,
      }
    }
    this.getShaderPrecisionFormat = function(a,b){
      v_console_log('  [*] WebGLRenderingContext -> getShaderPrecisionFormat[func]')
      function WebGLShaderPrecisionFormat(){}
      var r1 = v_new(WebGLShaderPrecisionFormat)
      r1.rangeMin = 127
      r1.rangeMax = 127
      r1.precision = 23
      var r2 = v_new(WebGLShaderPrecisionFormat)
      r2.rangeMin = 31
      r2.rangeMax = 30
      r2.precision = 0
      if (a == 35633 && b == 36338){ return r1 } if (a == 35633 && b == 36337){ return r1 } if (a == 35633 && b == 36336){ return r1 }
      if (a == 35633 && b == 36341){ return r2 } if (a == 35633 && b == 36340){ return r2 } if (a == 35633 && b == 36339){ return r2 }
      if (a == 35632 && b == 36338){ return r1 } if (a == 35632 && b == 36337){ return r1 } if (a == 35632 && b == 36336){ return r1 }
      if (a == 35632 && b == 36341){ return r2 } if (a == 35632 && b == 36340){ return r2 } if (a == 35632 && b == 36339){ return r2 }
      throw Error('getShaderPrecisionFormat')
    }
    v_saf(this.createBuffer, 'createBuffer')
    v_saf(this.createProgram, 'createProgram')
    v_saf(this.createShader, 'createShader')
    v_saf(this.getSupportedExtensions, 'getSupportedExtensions')
    v_saf(this.getExtension, 'getExtension')
    v_saf(this.getParameter, 'getParameter')
    v_saf(this.getContextAttributes, 'getContextAttributes')
    v_saf(this.getShaderPrecisionFormat, 'getShaderPrecisionFormat')})
  CanvasRenderingContext2D = v_saf(function CanvasRenderingContext2D(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  Performance = v_saf(function Performance(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(Performance, EventTarget)
  PerformanceEntry = v_saf(function PerformanceEntry(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  PerformanceElementTiming = v_saf(function PerformanceElementTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceElementTiming, PerformanceEntry)
  PerformanceEventTiming = v_saf(function PerformanceEventTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceEventTiming, PerformanceEntry)
  PerformanceLongTaskTiming = v_saf(function PerformanceLongTaskTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceLongTaskTiming, PerformanceEntry)
  PerformanceMark = v_saf(function PerformanceMark(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceMark, PerformanceEntry)
  PerformanceMeasure = v_saf(function PerformanceMeasure(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceMeasure, PerformanceEntry)
  PerformanceNavigation = v_saf(function PerformanceNavigation(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  PerformanceResourceTiming = v_saf(function PerformanceResourceTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceResourceTiming, PerformanceEntry)
  PerformanceNavigationTiming = v_saf(function PerformanceNavigationTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformanceNavigationTiming, PerformanceResourceTiming)
  PerformanceObserver = v_saf(function PerformanceObserver(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  PerformanceObserverEntryList = v_saf(function PerformanceObserverEntryList(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  PerformancePaintTiming = v_saf(function PerformancePaintTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PerformancePaintTiming, PerformanceEntry)
  PerformanceServerTiming = v_saf(function PerformanceServerTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  PerformanceTiming = v_saf(function PerformanceTiming(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  Image = v_saf(function Image(){;return v_new(HTMLImageElement)})
  HTMLImageElement = v_saf(function HTMLImageElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLImageElement, HTMLElement)
  HTMLMediaElement = v_saf(function HTMLMediaElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLMediaElement, HTMLElement)
  HTMLUnknownElement = v_saf(function HTMLUnknownElement(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(HTMLUnknownElement, HTMLElement)
  XMLHttpRequestEventTarget = v_saf(function XMLHttpRequestEventTarget(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(XMLHttpRequestEventTarget, EventTarget)
  XMLHttpRequest = v_saf(function XMLHttpRequest(){;}); _inherits(XMLHttpRequest, XMLHttpRequestEventTarget)
  Storage = v_saf(function Storage(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  DOMTokenList = v_saf(function DOMTokenList(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  Touch = v_saf(function Touch(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  UIEvent = v_saf(function UIEvent(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(UIEvent, Event)
  TouchEvent = v_saf(function TouchEvent(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(TouchEvent, UIEvent)
  Event = v_saf(function Event(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };})
  MouseEvent = v_saf(function MouseEvent(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(MouseEvent, UIEvent)
  PointerEvent = v_saf(function PointerEvent(){if (!v_new_toggle){ throw TypeError("Illegal constructor") };}); _inherits(PointerEvent, MouseEvent)
  Object.defineProperties(Window.prototype, {
    PERSISTENT: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"Window",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Screen.prototype, {
    availWidth: {"enumerable":true,"configurable":true,"get":function(){return 1707},"set":function(){return true}},
    availHeight: {"enumerable":true,"configurable":true,"get":function(){return 1019},"set":function(){return true}},
    width: {"enumerable":true,"configurable":true,"get":function(){return 1707},"set":function(){return true}},
    height: {"enumerable":true,"configurable":true,"get":function(){return 1067},"set":function(){return true}},
    colorDepth: {"enumerable":true,"configurable":true,"get":function(){return 24},"set":function(){return true}},
    pixelDepth: {"enumerable":true,"configurable":true,"get":function(){return 24},"set":function(){return true}},
    availLeft: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    availTop: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    isExtended: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"Screen",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Node.prototype, {
    nodeType: {"enumerable":true,"configurable":true,"get":function(){return 9},"set":function(){return true}},
    nodeName: {"enumerable":true,"configurable":true,"get":function(){return "#document"},"set":function(){return true}},
    baseURI: {"enumerable":true,"configurable":true,"get":function(){return "https://www.mashangpa.com/problem-detail/12/"},"set":function(){return true}},
    isConnected: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    ELEMENT_NODE: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    ATTRIBUTE_NODE: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    TEXT_NODE: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    CDATA_SECTION_NODE: {"value":4,"writable":false,"enumerable":true,"configurable":false},
    ENTITY_REFERENCE_NODE: {"value":5,"writable":false,"enumerable":true,"configurable":false},
    ENTITY_NODE: {"value":6,"writable":false,"enumerable":true,"configurable":false},
    PROCESSING_INSTRUCTION_NODE: {"value":7,"writable":false,"enumerable":true,"configurable":false},
    COMMENT_NODE: {"value":8,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_NODE: {"value":9,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_TYPE_NODE: {"value":10,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_FRAGMENT_NODE: {"value":11,"writable":false,"enumerable":true,"configurable":false},
    NOTATION_NODE: {"value":12,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_DISCONNECTED: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_PRECEDING: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_FOLLOWING: {"value":4,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_CONTAINS: {"value":8,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_CONTAINED_BY: {"value":16,"writable":false,"enumerable":true,"configurable":false},
    DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC: {"value":32,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"Node",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Document.prototype, {
    URL: {"enumerable":true,"configurable":true,"get":function(){return "https://www.mashangpa.com/problem-detail/12/"},"set":function(){return true}},
    documentURI: {"enumerable":true,"configurable":true,"get":function(){return "https://www.mashangpa.com/problem-detail/12/"},"set":function(){return true}},
    compatMode: {"enumerable":true,"configurable":true,"get":function(){return "CSS1Compat"},"set":function(){return true}},
    characterSet: {"enumerable":true,"configurable":true,"get":function(){return "UTF-8"},"set":function(){return true}},
    charset: {"enumerable":true,"configurable":true,"get":function(){return "UTF-8"},"set":function(){return true}},
    inputEncoding: {"enumerable":true,"configurable":true,"get":function(){return "UTF-8"},"set":function(){return true}},
    contentType: {"enumerable":true,"configurable":true,"get":function(){return "text/html"},"set":function(){return true}},
    xmlStandalone: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    domain: {"enumerable":true,"configurable":true,"get":function(){return "www.mashangpa.com"},"set":function(){return true}},
    referrer: {"enumerable":true,"configurable":true,"get":function(){return "https://www.mashangpa.com/"},"set":function(){return true}},
    lastModified: {"enumerable":true,"configurable":true,"get":function(){return "09/22/2025 09:40:51"},"set":function(){return true}},
    readyState: {"enumerable":true,"configurable":true,"get":function(){return "complete"},"set":function(){return true}},
    title: {"enumerable":true,"configurable":true,"get":function(){return "题十二：如来神掌 - 码上爬"},"set":function(){return true}},
    dir: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    designMode: {"enumerable":true,"configurable":true,"get":function(){return "off"},"set":function(){return true}},
    fgColor: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    linkColor: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    vlinkColor: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    alinkColor: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    bgColor: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    hidden: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    visibilityState: {"enumerable":true,"configurable":true,"get":function(){return "visible"},"set":function(){return true}},
    wasDiscarded: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    prerendering: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    webkitVisibilityState: {"enumerable":true,"configurable":true,"get":function(){return "visible"},"set":function(){return true}},
    webkitHidden: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    fullscreenEnabled: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    fullscreen: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    webkitIsFullScreen: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    webkitFullscreenEnabled: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    pictureInPictureEnabled: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    childElementCount: {"enumerable":true,"configurable":true,"get":function(){return 1},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"Document",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLDocument.prototype, {
    [Symbol.toStringTag]: {value:"HTMLDocument",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Element.prototype, {
    namespaceURI: {"enumerable":true,"configurable":true,"get":function(){return "http://www.w3.org/1999/xhtml"},"set":function(){return true}},
    localName: {"enumerable":true,"configurable":true,"get":function(){return "script"},"set":function(){return true}},
    tagName: {"enumerable":true,"configurable":true,"get":function(){return "SCRIPT"},"set":function(){return true}},
    id: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    className: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    slot: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    scrollTop: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    scrollLeft: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    scrollWidth: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    scrollHeight: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    clientTop: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    clientLeft: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    clientWidth: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    clientHeight: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    elementTiming: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    childElementCount: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    currentCSSZoom: {"enumerable":true,"configurable":true,"get":function(){return 1},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"Element",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLElement.prototype, {
    title: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    lang: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    translate: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    dir: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    hidden: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    inert: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    accessKey: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    draggable: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    spellcheck: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    autocapitalize: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    contentEditable: {"enumerable":true,"configurable":true,"get":function(){return "inherit"},"set":function(){return true}},
    enterKeyHint: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    isContentEditable: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    inputMode: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    virtualKeyboardPolicy: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    offsetTop: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    offsetLeft: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    offsetWidth: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    offsetHeight: {"enumerable":true,"configurable":true,"get":function(){return 0},"set":function(){return true}},
    innerText: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    outerText: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    writingSuggestions: {"enumerable":true,"configurable":true,"get":function(){return "true"},"set":function(){return true}},
    nonce: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    autofocus: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    tabIndex: {"enumerable":true,"configurable":true,"get":function(){return -1},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"HTMLElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLHtmlElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLHtmlElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLHeadElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLHeadElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLBodyElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLBodyElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Navigator.prototype, {
    vendorSub: {"enumerable":true,"configurable":true,"get":function(){return ""},"set":function(){return true}},
    productSub: {"enumerable":true,"configurable":true,"get":function(){return "20030107"},"set":function(){return true}},
    vendor: {"enumerable":true,"configurable":true,"get":function(){return "Google Inc."},"set":function(){return true}},
    maxTouchPoints: {"enumerable":true,"configurable":true,"get":function(){return 10},"set":function(){return true}},
    pdfViewerEnabled: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    hardwareConcurrency: {"enumerable":true,"configurable":true,"get":function(){return 32},"set":function(){return true}},
    cookieEnabled: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    appCodeName: {"enumerable":true,"configurable":true,"get":function(){return "Mozilla"},"set":function(){return true}},
    appName: {"enumerable":true,"configurable":true,"get":function(){return "Netscape"},"set":function(){return true}},
    appVersion: {"enumerable":true,"configurable":true,"get":function(){return "5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"},"set":function(){return true}},
    platform: {"enumerable":true,"configurable":true,"get":function(){return "Win32"},"set":function(){return true}},
    product: {"enumerable":true,"configurable":true,"get":function(){return "Gecko"},"set":function(){return true}},
    userAgent: {"enumerable":true,"configurable":true,"get":function(){return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"},"set":function(){return true}},
    language: {"enumerable":true,"configurable":true,"get":function(){return "zh-CN"},"set":function(){return true}},
    languages: {"enumerable":true,"configurable":true,"get":function(){return ["zh-CN","zh"]},"set":function(){return true}},
    onLine: {"enumerable":true,"configurable":true,"get":function(){return true},"set":function(){return true}},
    webdriver: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    deprecatedRunAdAuctionEnforcesKAnonymity: {"enumerable":true,"configurable":true,"get":function(){return false},"set":function(){return true}},
    deviceMemory: {"enumerable":true,"configurable":true,"get":function(){return 8},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"Navigator",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PluginArray.prototype, {
    [Symbol.toStringTag]: {value:"PluginArray",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Plugin.prototype, {
    [Symbol.toStringTag]: {value:"Plugin",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(MimeTypeArray.prototype, {
    [Symbol.toStringTag]: {value:"MimeTypeArray",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(MimeType.prototype, {
    [Symbol.toStringTag]: {value:"MimeType",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(CSSStyleDeclaration.prototype, {
    [Symbol.toStringTag]: {value:"CSSStyleDeclaration",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Location.prototype, {
    [Symbol.toStringTag]: {value:"Location",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLCanvasElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLCanvasElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(WebGLRenderingContext.prototype, {
    DEPTH_BUFFER_BIT: {"value":256,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BUFFER_BIT: {"value":1024,"writable":false,"enumerable":true,"configurable":false},
    COLOR_BUFFER_BIT: {"value":16384,"writable":false,"enumerable":true,"configurable":false},
    LINES: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    LINE_LOOP: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    LINE_STRIP: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    TRIANGLES: {"value":4,"writable":false,"enumerable":true,"configurable":false},
    TRIANGLE_STRIP: {"value":5,"writable":false,"enumerable":true,"configurable":false},
    TRIANGLE_FAN: {"value":6,"writable":false,"enumerable":true,"configurable":false},
    ONE: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    SRC_COLOR: {"value":768,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_SRC_COLOR: {"value":769,"writable":false,"enumerable":true,"configurable":false},
    SRC_ALPHA: {"value":770,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_SRC_ALPHA: {"value":771,"writable":false,"enumerable":true,"configurable":false},
    DST_ALPHA: {"value":772,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_DST_ALPHA: {"value":773,"writable":false,"enumerable":true,"configurable":false},
    DST_COLOR: {"value":774,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_DST_COLOR: {"value":775,"writable":false,"enumerable":true,"configurable":false},
    SRC_ALPHA_SATURATE: {"value":776,"writable":false,"enumerable":true,"configurable":false},
    FUNC_ADD: {"value":32774,"writable":false,"enumerable":true,"configurable":false},
    BLEND_EQUATION: {"value":32777,"writable":false,"enumerable":true,"configurable":false},
    BLEND_EQUATION_RGB: {"value":32777,"writable":false,"enumerable":true,"configurable":false},
    BLEND_EQUATION_ALPHA: {"value":34877,"writable":false,"enumerable":true,"configurable":false},
    FUNC_SUBTRACT: {"value":32778,"writable":false,"enumerable":true,"configurable":false},
    FUNC_REVERSE_SUBTRACT: {"value":32779,"writable":false,"enumerable":true,"configurable":false},
    BLEND_DST_RGB: {"value":32968,"writable":false,"enumerable":true,"configurable":false},
    BLEND_SRC_RGB: {"value":32969,"writable":false,"enumerable":true,"configurable":false},
    BLEND_DST_ALPHA: {"value":32970,"writable":false,"enumerable":true,"configurable":false},
    BLEND_SRC_ALPHA: {"value":32971,"writable":false,"enumerable":true,"configurable":false},
    CONSTANT_COLOR: {"value":32769,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_CONSTANT_COLOR: {"value":32770,"writable":false,"enumerable":true,"configurable":false},
    CONSTANT_ALPHA: {"value":32771,"writable":false,"enumerable":true,"configurable":false},
    ONE_MINUS_CONSTANT_ALPHA: {"value":32772,"writable":false,"enumerable":true,"configurable":false},
    BLEND_COLOR: {"value":32773,"writable":false,"enumerable":true,"configurable":false},
    ARRAY_BUFFER: {"value":34962,"writable":false,"enumerable":true,"configurable":false},
    ELEMENT_ARRAY_BUFFER: {"value":34963,"writable":false,"enumerable":true,"configurable":false},
    ARRAY_BUFFER_BINDING: {"value":34964,"writable":false,"enumerable":true,"configurable":false},
    ELEMENT_ARRAY_BUFFER_BINDING: {"value":34965,"writable":false,"enumerable":true,"configurable":false},
    STREAM_DRAW: {"value":35040,"writable":false,"enumerable":true,"configurable":false},
    STATIC_DRAW: {"value":35044,"writable":false,"enumerable":true,"configurable":false},
    DYNAMIC_DRAW: {"value":35048,"writable":false,"enumerable":true,"configurable":false},
    BUFFER_SIZE: {"value":34660,"writable":false,"enumerable":true,"configurable":false},
    BUFFER_USAGE: {"value":34661,"writable":false,"enumerable":true,"configurable":false},
    CURRENT_VERTEX_ATTRIB: {"value":34342,"writable":false,"enumerable":true,"configurable":false},
    FRONT: {"value":1028,"writable":false,"enumerable":true,"configurable":false},
    BACK: {"value":1029,"writable":false,"enumerable":true,"configurable":false},
    FRONT_AND_BACK: {"value":1032,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_2D: {"value":3553,"writable":false,"enumerable":true,"configurable":false},
    CULL_FACE: {"value":2884,"writable":false,"enumerable":true,"configurable":false},
    BLEND: {"value":3042,"writable":false,"enumerable":true,"configurable":false},
    DITHER: {"value":3024,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_TEST: {"value":2960,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_TEST: {"value":2929,"writable":false,"enumerable":true,"configurable":false},
    SCISSOR_TEST: {"value":3089,"writable":false,"enumerable":true,"configurable":false},
    POLYGON_OFFSET_FILL: {"value":32823,"writable":false,"enumerable":true,"configurable":false},
    SAMPLE_ALPHA_TO_COVERAGE: {"value":32926,"writable":false,"enumerable":true,"configurable":false},
    SAMPLE_COVERAGE: {"value":32928,"writable":false,"enumerable":true,"configurable":false},
    INVALID_ENUM: {"value":1280,"writable":false,"enumerable":true,"configurable":false},
    INVALID_VALUE: {"value":1281,"writable":false,"enumerable":true,"configurable":false},
    INVALID_OPERATION: {"value":1282,"writable":false,"enumerable":true,"configurable":false},
    OUT_OF_MEMORY: {"value":1285,"writable":false,"enumerable":true,"configurable":false},
    CW: {"value":2304,"writable":false,"enumerable":true,"configurable":false},
    CCW: {"value":2305,"writable":false,"enumerable":true,"configurable":false},
    LINE_WIDTH: {"value":2849,"writable":false,"enumerable":true,"configurable":false},
    ALIASED_POINT_SIZE_RANGE: {"value":33901,"writable":false,"enumerable":true,"configurable":false},
    ALIASED_LINE_WIDTH_RANGE: {"value":33902,"writable":false,"enumerable":true,"configurable":false},
    CULL_FACE_MODE: {"value":2885,"writable":false,"enumerable":true,"configurable":false},
    FRONT_FACE: {"value":2886,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_RANGE: {"value":2928,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_WRITEMASK: {"value":2930,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_CLEAR_VALUE: {"value":2931,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_FUNC: {"value":2932,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_CLEAR_VALUE: {"value":2961,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_FUNC: {"value":2962,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_FAIL: {"value":2964,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_PASS_DEPTH_FAIL: {"value":2965,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_PASS_DEPTH_PASS: {"value":2966,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_REF: {"value":2967,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_VALUE_MASK: {"value":2963,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_WRITEMASK: {"value":2968,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_FUNC: {"value":34816,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_FAIL: {"value":34817,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_PASS_DEPTH_FAIL: {"value":34818,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_PASS_DEPTH_PASS: {"value":34819,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_REF: {"value":36003,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_VALUE_MASK: {"value":36004,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BACK_WRITEMASK: {"value":36005,"writable":false,"enumerable":true,"configurable":false},
    VIEWPORT: {"value":2978,"writable":false,"enumerable":true,"configurable":false},
    SCISSOR_BOX: {"value":3088,"writable":false,"enumerable":true,"configurable":false},
    COLOR_CLEAR_VALUE: {"value":3106,"writable":false,"enumerable":true,"configurable":false},
    COLOR_WRITEMASK: {"value":3107,"writable":false,"enumerable":true,"configurable":false},
    UNPACK_ALIGNMENT: {"value":3317,"writable":false,"enumerable":true,"configurable":false},
    PACK_ALIGNMENT: {"value":3333,"writable":false,"enumerable":true,"configurable":false},
    MAX_TEXTURE_SIZE: {"value":3379,"writable":false,"enumerable":true,"configurable":false},
    MAX_VIEWPORT_DIMS: {"value":3386,"writable":false,"enumerable":true,"configurable":false},
    SUBPIXEL_BITS: {"value":3408,"writable":false,"enumerable":true,"configurable":false},
    RED_BITS: {"value":3410,"writable":false,"enumerable":true,"configurable":false},
    GREEN_BITS: {"value":3411,"writable":false,"enumerable":true,"configurable":false},
    BLUE_BITS: {"value":3412,"writable":false,"enumerable":true,"configurable":false},
    ALPHA_BITS: {"value":3413,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_BITS: {"value":3414,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_BITS: {"value":3415,"writable":false,"enumerable":true,"configurable":false},
    POLYGON_OFFSET_UNITS: {"value":10752,"writable":false,"enumerable":true,"configurable":false},
    POLYGON_OFFSET_FACTOR: {"value":32824,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_BINDING_2D: {"value":32873,"writable":false,"enumerable":true,"configurable":false},
    SAMPLE_BUFFERS: {"value":32936,"writable":false,"enumerable":true,"configurable":false},
    SAMPLES: {"value":32937,"writable":false,"enumerable":true,"configurable":false},
    SAMPLE_COVERAGE_VALUE: {"value":32938,"writable":false,"enumerable":true,"configurable":false},
    SAMPLE_COVERAGE_INVERT: {"value":32939,"writable":false,"enumerable":true,"configurable":false},
    COMPRESSED_TEXTURE_FORMATS: {"value":34467,"writable":false,"enumerable":true,"configurable":false},
    DONT_CARE: {"value":4352,"writable":false,"enumerable":true,"configurable":false},
    FASTEST: {"value":4353,"writable":false,"enumerable":true,"configurable":false},
    NICEST: {"value":4354,"writable":false,"enumerable":true,"configurable":false},
    GENERATE_MIPMAP_HINT: {"value":33170,"writable":false,"enumerable":true,"configurable":false},
    BYTE: {"value":5120,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_BYTE: {"value":5121,"writable":false,"enumerable":true,"configurable":false},
    SHORT: {"value":5122,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_SHORT: {"value":5123,"writable":false,"enumerable":true,"configurable":false},
    INT: {"value":5124,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_INT: {"value":5125,"writable":false,"enumerable":true,"configurable":false},
    FLOAT: {"value":5126,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_COMPONENT: {"value":6402,"writable":false,"enumerable":true,"configurable":false},
    ALPHA: {"value":6406,"writable":false,"enumerable":true,"configurable":false},
    RGB: {"value":6407,"writable":false,"enumerable":true,"configurable":false},
    RGBA: {"value":6408,"writable":false,"enumerable":true,"configurable":false},
    LUMINANCE: {"value":6409,"writable":false,"enumerable":true,"configurable":false},
    LUMINANCE_ALPHA: {"value":6410,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_SHORT_4_4_4_4: {"value":32819,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_SHORT_5_5_5_1: {"value":32820,"writable":false,"enumerable":true,"configurable":false},
    UNSIGNED_SHORT_5_6_5: {"value":33635,"writable":false,"enumerable":true,"configurable":false},
    FRAGMENT_SHADER: {"value":35632,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_SHADER: {"value":35633,"writable":false,"enumerable":true,"configurable":false},
    MAX_VERTEX_ATTRIBS: {"value":34921,"writable":false,"enumerable":true,"configurable":false},
    MAX_VERTEX_UNIFORM_VECTORS: {"value":36347,"writable":false,"enumerable":true,"configurable":false},
    MAX_VARYING_VECTORS: {"value":36348,"writable":false,"enumerable":true,"configurable":false},
    MAX_COMBINED_TEXTURE_IMAGE_UNITS: {"value":35661,"writable":false,"enumerable":true,"configurable":false},
    MAX_VERTEX_TEXTURE_IMAGE_UNITS: {"value":35660,"writable":false,"enumerable":true,"configurable":false},
    MAX_TEXTURE_IMAGE_UNITS: {"value":34930,"writable":false,"enumerable":true,"configurable":false},
    MAX_FRAGMENT_UNIFORM_VECTORS: {"value":36349,"writable":false,"enumerable":true,"configurable":false},
    SHADER_TYPE: {"value":35663,"writable":false,"enumerable":true,"configurable":false},
    DELETE_STATUS: {"value":35712,"writable":false,"enumerable":true,"configurable":false},
    LINK_STATUS: {"value":35714,"writable":false,"enumerable":true,"configurable":false},
    VALIDATE_STATUS: {"value":35715,"writable":false,"enumerable":true,"configurable":false},
    ATTACHED_SHADERS: {"value":35717,"writable":false,"enumerable":true,"configurable":false},
    ACTIVE_UNIFORMS: {"value":35718,"writable":false,"enumerable":true,"configurable":false},
    ACTIVE_ATTRIBUTES: {"value":35721,"writable":false,"enumerable":true,"configurable":false},
    SHADING_LANGUAGE_VERSION: {"value":35724,"writable":false,"enumerable":true,"configurable":false},
    CURRENT_PROGRAM: {"value":35725,"writable":false,"enumerable":true,"configurable":false},
    NEVER: {"value":512,"writable":false,"enumerable":true,"configurable":false},
    LESS: {"value":513,"writable":false,"enumerable":true,"configurable":false},
    EQUAL: {"value":514,"writable":false,"enumerable":true,"configurable":false},
    LEQUAL: {"value":515,"writable":false,"enumerable":true,"configurable":false},
    GREATER: {"value":516,"writable":false,"enumerable":true,"configurable":false},
    NOTEQUAL: {"value":517,"writable":false,"enumerable":true,"configurable":false},
    GEQUAL: {"value":518,"writable":false,"enumerable":true,"configurable":false},
    ALWAYS: {"value":519,"writable":false,"enumerable":true,"configurable":false},
    KEEP: {"value":7680,"writable":false,"enumerable":true,"configurable":false},
    REPLACE: {"value":7681,"writable":false,"enumerable":true,"configurable":false},
    INCR: {"value":7682,"writable":false,"enumerable":true,"configurable":false},
    DECR: {"value":7683,"writable":false,"enumerable":true,"configurable":false},
    INVERT: {"value":5386,"writable":false,"enumerable":true,"configurable":false},
    INCR_WRAP: {"value":34055,"writable":false,"enumerable":true,"configurable":false},
    DECR_WRAP: {"value":34056,"writable":false,"enumerable":true,"configurable":false},
    VENDOR: {"value":7936,"writable":false,"enumerable":true,"configurable":false},
    RENDERER: {"value":7937,"writable":false,"enumerable":true,"configurable":false},
    VERSION: {"value":7938,"writable":false,"enumerable":true,"configurable":false},
    NEAREST: {"value":9728,"writable":false,"enumerable":true,"configurable":false},
    LINEAR: {"value":9729,"writable":false,"enumerable":true,"configurable":false},
    NEAREST_MIPMAP_NEAREST: {"value":9984,"writable":false,"enumerable":true,"configurable":false},
    LINEAR_MIPMAP_NEAREST: {"value":9985,"writable":false,"enumerable":true,"configurable":false},
    NEAREST_MIPMAP_LINEAR: {"value":9986,"writable":false,"enumerable":true,"configurable":false},
    LINEAR_MIPMAP_LINEAR: {"value":9987,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_MAG_FILTER: {"value":10240,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_MIN_FILTER: {"value":10241,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_WRAP_S: {"value":10242,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_WRAP_T: {"value":10243,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE: {"value":5890,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP: {"value":34067,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_BINDING_CUBE_MAP: {"value":34068,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_POSITIVE_X: {"value":34069,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_NEGATIVE_X: {"value":34070,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_POSITIVE_Y: {"value":34071,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_NEGATIVE_Y: {"value":34072,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_POSITIVE_Z: {"value":34073,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE_CUBE_MAP_NEGATIVE_Z: {"value":34074,"writable":false,"enumerable":true,"configurable":false},
    MAX_CUBE_MAP_TEXTURE_SIZE: {"value":34076,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE0: {"value":33984,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE1: {"value":33985,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE2: {"value":33986,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE3: {"value":33987,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE4: {"value":33988,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE5: {"value":33989,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE6: {"value":33990,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE7: {"value":33991,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE8: {"value":33992,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE9: {"value":33993,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE10: {"value":33994,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE11: {"value":33995,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE12: {"value":33996,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE13: {"value":33997,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE14: {"value":33998,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE15: {"value":33999,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE16: {"value":34000,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE17: {"value":34001,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE18: {"value":34002,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE19: {"value":34003,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE20: {"value":34004,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE21: {"value":34005,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE22: {"value":34006,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE23: {"value":34007,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE24: {"value":34008,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE25: {"value":34009,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE26: {"value":34010,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE27: {"value":34011,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE28: {"value":34012,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE29: {"value":34013,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE30: {"value":34014,"writable":false,"enumerable":true,"configurable":false},
    TEXTURE31: {"value":34015,"writable":false,"enumerable":true,"configurable":false},
    ACTIVE_TEXTURE: {"value":34016,"writable":false,"enumerable":true,"configurable":false},
    REPEAT: {"value":10497,"writable":false,"enumerable":true,"configurable":false},
    CLAMP_TO_EDGE: {"value":33071,"writable":false,"enumerable":true,"configurable":false},
    MIRRORED_REPEAT: {"value":33648,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_VEC2: {"value":35664,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_VEC3: {"value":35665,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_VEC4: {"value":35666,"writable":false,"enumerable":true,"configurable":false},
    INT_VEC2: {"value":35667,"writable":false,"enumerable":true,"configurable":false},
    INT_VEC3: {"value":35668,"writable":false,"enumerable":true,"configurable":false},
    INT_VEC4: {"value":35669,"writable":false,"enumerable":true,"configurable":false},
    BOOL: {"value":35670,"writable":false,"enumerable":true,"configurable":false},
    BOOL_VEC2: {"value":35671,"writable":false,"enumerable":true,"configurable":false},
    BOOL_VEC3: {"value":35672,"writable":false,"enumerable":true,"configurable":false},
    BOOL_VEC4: {"value":35673,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_MAT2: {"value":35674,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_MAT3: {"value":35675,"writable":false,"enumerable":true,"configurable":false},
    FLOAT_MAT4: {"value":35676,"writable":false,"enumerable":true,"configurable":false},
    SAMPLER_2D: {"value":35678,"writable":false,"enumerable":true,"configurable":false},
    SAMPLER_CUBE: {"value":35680,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_ENABLED: {"value":34338,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_SIZE: {"value":34339,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_STRIDE: {"value":34340,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_TYPE: {"value":34341,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_NORMALIZED: {"value":34922,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_POINTER: {"value":34373,"writable":false,"enumerable":true,"configurable":false},
    VERTEX_ATTRIB_ARRAY_BUFFER_BINDING: {"value":34975,"writable":false,"enumerable":true,"configurable":false},
    IMPLEMENTATION_COLOR_READ_TYPE: {"value":35738,"writable":false,"enumerable":true,"configurable":false},
    IMPLEMENTATION_COLOR_READ_FORMAT: {"value":35739,"writable":false,"enumerable":true,"configurable":false},
    COMPILE_STATUS: {"value":35713,"writable":false,"enumerable":true,"configurable":false},
    LOW_FLOAT: {"value":36336,"writable":false,"enumerable":true,"configurable":false},
    MEDIUM_FLOAT: {"value":36337,"writable":false,"enumerable":true,"configurable":false},
    HIGH_FLOAT: {"value":36338,"writable":false,"enumerable":true,"configurable":false},
    LOW_INT: {"value":36339,"writable":false,"enumerable":true,"configurable":false},
    MEDIUM_INT: {"value":36340,"writable":false,"enumerable":true,"configurable":false},
    HIGH_INT: {"value":36341,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER: {"value":36160,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER: {"value":36161,"writable":false,"enumerable":true,"configurable":false},
    RGBA4: {"value":32854,"writable":false,"enumerable":true,"configurable":false},
    RGB5_A1: {"value":32855,"writable":false,"enumerable":true,"configurable":false},
    RGB565: {"value":36194,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_COMPONENT16: {"value":33189,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_INDEX8: {"value":36168,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_STENCIL: {"value":34041,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_WIDTH: {"value":36162,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_HEIGHT: {"value":36163,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_INTERNAL_FORMAT: {"value":36164,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_RED_SIZE: {"value":36176,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_GREEN_SIZE: {"value":36177,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_BLUE_SIZE: {"value":36178,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_ALPHA_SIZE: {"value":36179,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_DEPTH_SIZE: {"value":36180,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_STENCIL_SIZE: {"value":36181,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE: {"value":36048,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_ATTACHMENT_OBJECT_NAME: {"value":36049,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL: {"value":36050,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE: {"value":36051,"writable":false,"enumerable":true,"configurable":false},
    COLOR_ATTACHMENT0: {"value":36064,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_ATTACHMENT: {"value":36096,"writable":false,"enumerable":true,"configurable":false},
    STENCIL_ATTACHMENT: {"value":36128,"writable":false,"enumerable":true,"configurable":false},
    DEPTH_STENCIL_ATTACHMENT: {"value":33306,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_COMPLETE: {"value":36053,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_INCOMPLETE_ATTACHMENT: {"value":36054,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: {"value":36055,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_INCOMPLETE_DIMENSIONS: {"value":36057,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_UNSUPPORTED: {"value":36061,"writable":false,"enumerable":true,"configurable":false},
    FRAMEBUFFER_BINDING: {"value":36006,"writable":false,"enumerable":true,"configurable":false},
    RENDERBUFFER_BINDING: {"value":36007,"writable":false,"enumerable":true,"configurable":false},
    MAX_RENDERBUFFER_SIZE: {"value":34024,"writable":false,"enumerable":true,"configurable":false},
    INVALID_FRAMEBUFFER_OPERATION: {"value":1286,"writable":false,"enumerable":true,"configurable":false},
    UNPACK_FLIP_Y_WEBGL: {"value":37440,"writable":false,"enumerable":true,"configurable":false},
    UNPACK_PREMULTIPLY_ALPHA_WEBGL: {"value":37441,"writable":false,"enumerable":true,"configurable":false},
    CONTEXT_LOST_WEBGL: {"value":37442,"writable":false,"enumerable":true,"configurable":false},
    UNPACK_COLORSPACE_CONVERSION_WEBGL: {"value":37443,"writable":false,"enumerable":true,"configurable":false},
    BROWSER_DEFAULT_WEBGL: {"value":37444,"writable":false,"enumerable":true,"configurable":false},
    RGB8: {"value":32849,"writable":false,"enumerable":true,"configurable":false},
    RGBA8: {"value":32856,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"WebGLRenderingContext",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(CanvasRenderingContext2D.prototype, {
    [Symbol.toStringTag]: {value:"CanvasRenderingContext2D",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Performance.prototype, {
    timeOrigin: {"enumerable":true,"configurable":true,"get":function(){return 1758505189622.1},"set":function(){return true}},
    [Symbol.toStringTag]: {value:"Performance",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceEntry.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceEntry",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceElementTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceElementTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceEventTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceEventTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceLongTaskTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceLongTaskTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceMark.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceMark",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceMeasure.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceMeasure",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceNavigation.prototype, {
    TYPE_RELOAD: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    TYPE_BACK_FORWARD: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    TYPE_RESERVED: {"value":255,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"PerformanceNavigation",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceResourceTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceResourceTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceNavigationTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceNavigationTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceObserver.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceObserver",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceObserverEntryList.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceObserverEntryList",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformancePaintTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformancePaintTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceServerTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceServerTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PerformanceTiming.prototype, {
    [Symbol.toStringTag]: {value:"PerformanceTiming",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Image.prototype, {
    [Symbol.toStringTag]: {value:"Image",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLImageElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLImageElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLMediaElement.prototype, {
    NETWORK_IDLE: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    NETWORK_LOADING: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    NETWORK_NO_SOURCE: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    HAVE_METADATA: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    HAVE_CURRENT_DATA: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    HAVE_FUTURE_DATA: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    HAVE_ENOUGH_DATA: {"value":4,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"HTMLMediaElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(HTMLUnknownElement.prototype, {
    [Symbol.toStringTag]: {value:"HTMLUnknownElement",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(XMLHttpRequestEventTarget.prototype, {
    [Symbol.toStringTag]: {value:"XMLHttpRequestEventTarget",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(XMLHttpRequest.prototype, {
    OPENED: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    HEADERS_RECEIVED: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    LOADING: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    DONE: {"value":4,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"XMLHttpRequest",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Storage.prototype, {
    [Symbol.toStringTag]: {value:"Storage",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(DOMTokenList.prototype, {
    [Symbol.toStringTag]: {value:"DOMTokenList",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Touch.prototype, {
    [Symbol.toStringTag]: {value:"Touch",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(UIEvent.prototype, {
    [Symbol.toStringTag]: {value:"UIEvent",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(TouchEvent.prototype, {
    [Symbol.toStringTag]: {value:"TouchEvent",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(Event.prototype, {
    CAPTURING_PHASE: {"value":1,"writable":false,"enumerable":true,"configurable":false},
    AT_TARGET: {"value":2,"writable":false,"enumerable":true,"configurable":false},
    BUBBLING_PHASE: {"value":3,"writable":false,"enumerable":true,"configurable":false},
    [Symbol.toStringTag]: {value:"Event",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(MouseEvent.prototype, {
    [Symbol.toStringTag]: {value:"MouseEvent",writable:false,enumerable:false,configurable:true},
  })
  Object.defineProperties(PointerEvent.prototype, {
    [Symbol.toStringTag]: {value:"PointerEvent",writable:false,enumerable:false,configurable:true},
  })




  if (typeof __dirname != 'undefined'){ __dirname = undefined }
  if (typeof __filename != 'undefined'){ __filename = undefined }
  if (typeof require != 'undefined'){ require = undefined }
  if (typeof exports != 'undefined'){ exports = undefined }
  if (typeof module != 'undefined'){ module = undefined }
  if (typeof Buffer != 'undefined'){ Buffer = undefined }
  var avoid_log = ['Symbol','Object','Number','RegExp','Boolean','String','constructor']
  var __globalThis__ = typeof global != 'undefined' ? global : this
  var window = new Proxy(v_new(Window), {
    get(a,b){ if(b=='global'){return}
      var r = a[b] || __globalThis__[b]
      if (typeof b !== 'symbol' && avoid_log.indexOf(b) == -1){
        v_console_log('  [*] [window get '+b+'] ==>', r)
      }
      return r
    },
    set(a,b,c){
      if (b == 'onclick' && typeof c == 'function') { window.addEventListener('click', c) }
      if (b == 'onmousedown' && typeof c == 'function') { window.addEventListener('mousedown', c) }
      if (b == 'onmouseup' && typeof c == 'function') { window.addEventListener('mouseup', c) }
      __globalThis__[b] = a[b] = c
      return true
    },
  })
  function v_proxy(obj, name, func){
    return new Proxy(obj, {
      get(a,b){ if(b=='global'){return}
        var r = a[b]
        if (typeof b !== 'symbol' && avoid_log.indexOf(b) == -1){
          v_console_log('  [*] ['+name+' get '+b+'] ==>', r)
        }
        if (func && typeof r == 'undefined'){
          r = func(name)
        }
        return r
      },
      set(a,b,c){
        if (typeof b !== 'symbol' && avoid_log.indexOf(b) == -1){
          v_console_log('  [*] ['+name+' set '+b+'] <--', c)
        }
        a[b] = c
        return true
      },
    })
  }
  var v_hasOwnProperty = Object.prototype.hasOwnProperty
  Object.prototype.hasOwnProperty = v_saf(function hasOwnProperty(){
    var r;
    if (this === window){ r = v_hasOwnProperty.apply(__globalThis__, arguments) }
    else{ r = v_hasOwnProperty.apply(this, arguments) }
    v_console_log('  [*] [hasOwnProperty]', this===window?window:this, [].slice.call(arguments), r)
    return r
  })
  Object.defineProperties(__globalThis__, {[Symbol.toStringTag]:{value:'Window'}})
  Object.defineProperties(__globalThis__, Object.getOwnPropertyDescriptors(window))
  Object.setPrototypeOf(__globalThis__, Object.getPrototypeOf(window))
  window.parent = window
  window.top = window
  window.frames = window
  window.self = window
  window.document = v_proxy(v_new(HTMLDocument), "document")
  window.location = v_proxy(v_new(Location), "location")
  window.navigator = v_proxy(v_new(Navigator), "navigator")
  window.screen = v_proxy(v_new(Screen), "screen")
  window.clientInformation = navigator
  window.performance = v_proxy(v_new(Performance), "performance")
  window.sessionStorage = v_proxy(v_new(Storage), "sessionStorage")
  window.localStorage = v_proxy(v_new(Storage), "localStorage")

  var win = {
    window: window,
    frames: window,
    parent: window,
    self: window,
    top: window,
  }
  function v_repair_this(){
    win = {
      window: __globalThis__,
      frames: __globalThis__,
      parent: __globalThis__,
      self: __globalThis__,
      top: __globalThis__,
    }
  }
  Object.defineProperties(window, {
    window: {get:function(){return win.window},set:function(e){return true}},
    frames: {get:function(){return win.frames},set:function(e){return true}},
    parent: {get:function(){return win.parent},set:function(e){return true}},
    self:   {get:function(){return win.self},  set:function(e){return true}},
    top:    {get:function(){return win.top},   set:function(e){return true}},
  })

  function _createElement(name){
    var htmlmap = {"HTMLElement":["abbr","address","article","aside","b","bdi","bdo","cite","code","dd","dfn","dt","em","figcaption","figure","footer","header","hgroup","i","kbd","main","mark","nav","noscript","rp","rt","ruby","s","samp","section","small","strong","sub","summary","sup","u","var","wbr"],"HTMLAnchorElement":["a"],"HTMLImageElement":["img"],"HTMLFontElement":["font"],"HTMLOutputElement":["output"],"HTMLAreaElement":["area"],"HTMLInputElement":["input"],"HTMLFormElement":["form"],"HTMLParagraphElement":["p"],"HTMLAudioElement":["audio"],"HTMLLabelElement":["label"],"HTMLFrameElement":["frame"],"HTMLParamElement":["param"],"HTMLBaseElement":["base"],"HTMLLegendElement":["legend"],"HTMLFrameSetElement":["frameset"],"HTMLPictureElement":["picture"],"HTMLBodyElement":["body"],"HTMLLIElement":["li"],"HTMLHeadingElement":["h1","h2","h3","h4","h5","h6"],"HTMLPreElement":["listing","pre","xmp"],"HTMLBRElement":["br"],"HTMLLinkElement":["link"],"HTMLHeadElement":["head"],"HTMLProgressElement":["progress"],"HTMLButtonElement":["button"],"HTMLMapElement":["map"],"HTMLHRElement":["hr"],"HTMLQuoteElement":["blockquote","q"],"HTMLCanvasElement":["canvas"],"HTMLMarqueeElement":["marquee"],"HTMLHtmlElement":["html"],"HTMLScriptElement":["script"],"HTMLDataElement":["data"],"HTMLMediaElement":[],"HTMLIFrameElement":["iframe"],"HTMLTimeElement":["time"],"HTMLDataListElement":["datalist"],"HTMLMenuElement":["menu"],"HTMLSelectElement":["select"],"HTMLTitleElement":["title"],"HTMLDetailsElement":["details"],"HTMLMetaElement":["meta"],"HTMLSlotElement":["slot"],"HTMLTableRowElement":["tr"],"HTMLDialogElement":["dialog"],"HTMLMeterElement":["meter"],"HTMLSourceElement":["source"],"HTMLTableSectionElement":["thead","tbody","tfoot"],"HTMLDirectoryElement":["dir"],"HTMLModElement":["del","ins"],"HTMLSpanElement":["span"],"HTMLTemplateElement":["template"],"HTMLDivElement":["div"],"HTMLObjectElement":["object"],"HTMLStyleElement":["style"],"HTMLTextAreaElement":["textarea"],"HTMLDListElement":["dl"],"HTMLOListElement":["ol"],"HTMLTableCaptionElement":["caption"],"HTMLTrackElement":["track"],"HTMLEmbedElement":["embed"],"HTMLOptGroupElement":["optgroup"],"HTMLTableCellElement":["th","td"],"HTMLUListElement":["ul"],"HTMLFieldSetElement":["fieldset"],"HTMLOptionElement":["option"],"HTMLTableColElement":["col","colgroup"],"HTMLUnknownElement":[],"HTMLTableElement":["table"],"HTMLVideoElement":["video"]}
    var ret, htmlmapkeys = Object.keys(htmlmap)
    name = name.toLocaleLowerCase()
    for (var i = 0; i < htmlmapkeys.length; i++) {
      if (htmlmap[htmlmapkeys[i]].indexOf(name) != -1){
        if (!window[htmlmapkeys[i]]){
          break
        }
        ret = v_new(window[htmlmapkeys[i]])
        break
      }
    }
    if (!ret){ ret = v_proxy(v_new(HTMLUnknownElement), 'HTMLUnknownElement', function(a){return function(){v_console_log(a,...arguments)}}) }
    if (typeof CSSStyleDeclaration != 'undefined') { ret.v_style = v_proxy(v_new(CSSStyleDeclaration), 'style') }
    ret.v_tagName = name.toUpperCase()
    return ret
  }
  function init_cookie(cookie){
    var cache = (cookie || "").trim();
    if (!cache){
      cache = ''
    }else if (cache.charAt(cache.length-1) != ';'){
      cache += '; '
    }else{
      cache += ' '
    }
    Object.defineProperty(Document.prototype, 'cookie', {
      get: function() {
        var r = cache.slice(0,cache.length-2);
        v_console_log('  [*] document -> cookie[get]', r)
        return r
      },
      set: function(c) {
        v_console_log('  [*] document -> cookie[set]', c)
        var ncookie = c.split(";")[0].split("=");
        if (!ncookie.slice(1).join('')){
          return c
        }
        var key = ncookie[0].trim()
        var val = ncookie.slice(1).join('').trim()
        var newc = key+'='+val
        var flag = false;
        var temp = cache.split("; ").map(function(a) {
          if (a.split("=")[0] === key) {
            flag = true;
            return newc;
          }
          return a;
        })
        cache = temp.join("; ");
        if (!flag) {
          cache += newc + "; ";
        }
        return cache;
      }
    });
  }
  function v_hook_href(obj, name, initurl){
    var r = Object.defineProperty(obj, 'href', {
      get: function(){
        if (!(this.protocol) && !(this.hostname)){
          r = ''
        }else{
          r = this.protocol + "//" + this.hostname + (this.port ? ":" + this.port : "") + this.pathname + this.search + this.hash;
        }
        v_console_log(`  [*] ${name||obj.constructor.name} -> href[get]:`, JSON.stringify(r))
        return r
      },
      set: function(href){
        href = href.trim()
        v_console_log(`  [*] ${name||obj.constructor.name} -> href[set]:`, JSON.stringify(href))
        if (href.startsWith("http://") || href.startsWith("https://")){/*ok*/}
        else if(href.startsWith("//")){ href = (this.protocol?this.protocol:'http:') + href}
        else{ href = this.protocol+"//"+this.hostname + (this.port?":"+this.port:"") + '/' + ((href[0]=='/')?href.slice(1):href) }
        var a = href.match(/([^:]+:)\/\/([^/:?#]+):?(\d+)?([^?#]*)?(\?[^#]*)?(#.*)?/);
        this.protocol = a[1] ? a[1] : "";
        this.hostname = a[2] ? a[2] : "";
        this.port     = a[3] ? a[3] : "";
        this.pathname = a[4] ? a[4] : "";
        this.search   = a[5] ? a[5] : "";
        this.hash     = a[6] ? a[6] : "";
        this.host     = this.hostname + (this.port?":"+this.port:"") ;
        this.origin   = this.protocol + "//" + this.hostname + (this.port ? ":" + this.port : "");
      }
    });
    if (initurl && initurl.trim()){ var temp=v_new_toggle; v_new_toggle = true; r.href = initurl; v_new_toggle = temp; }
    return r
  }
  function v_hook_storage(){
    Storage.prototype.clear      = v_saf(function(){          v_console_log(`  [*] Storage -> clear[func]:`); var self=this;Object.keys(self).forEach(function (key) { delete self[key]; }); }, 'clear')
    Storage.prototype.getItem    = v_saf(function(key){       v_console_log(`  [*] Storage -> getItem[func]:`, key); var r = (this.hasOwnProperty(key)?String(this[key]):null); return r}, 'getItem')
    Storage.prototype.setItem    = v_saf(function(key, val){  v_console_log(`  [*] Storage -> setItem[func]:`, key, val); this[key] = (val === undefined)?null:String(val) }, 'setItem')
    Storage.prototype.key        = v_saf(function(key){       v_console_log(`  [*] Storage -> key[func]:`, key); return Object.keys(this)[key||0];} , 'key')
    Storage.prototype.removeItem = v_saf(function(key){       v_console_log(`  [*] Storage -> removeItem[func]:`, key); delete this[key];}, 'removeItem')
    Object.defineProperty(Storage.prototype, 'length', {get: function(){
      if(this===Storage.prototype){ throw TypeError('Illegal invocation') }return Object.keys(this).length
    }})
    window.sessionStorage = new Proxy(sessionStorage,{ set:function(a,b,c){ v_console_log(`  [*] Storage -> [set]:`, b, c); return a[b]=String(c)}, get:function(a,b){ v_console_log(`  [*] Storage -> [get]:`, b, a[b]); return a[b]},})
    window.localStorage = new Proxy(localStorage,{ set:function(a,b,c){ v_console_log(`  [*] Storage -> [set]:`, b, c); return a[b]=String(c)}, get:function(a,b){ v_console_log(`  [*] Storage -> [get]:`, b, a[b]); return a[b]},})
  }
  function v_init_document(){
    Document.prototype.documentElement = v_proxy(_createElement('html'), 'documentElement')
    Document.prototype.createElement = v_saf(function createElement(){ return v_proxy(_createElement(arguments[0]), 'createElement '+arguments[0]) })
    Document.prototype.getElementById = v_saf(function getElementById(name){ var r = v_getele(name, 'getElementById'); v_console_log('  [*] Document -> getElementById', name, r); return r })
    Document.prototype.querySelector = v_saf(function querySelector(name){ var r = v_getele(name, 'querySelector'); v_console_log('  [*] Document -> querySelector', name, r); return r })
    Document.prototype.getElementsByClassName = v_saf(function getElementsByClassName(name){ var r = v_geteles(name, 'getElementsByClassName'); v_console_log('  [*] Document -> getElementsByClassName', name, r); return r })
    Document.prototype.getElementsByName = v_saf(function getElementsByName(name){ var r = v_geteles(name, 'getElementsByName'); v_console_log('  [*] Document -> getElementsByName', name, r); return r })
    Document.prototype.getElementsByTagName = v_saf(function getElementsByTagName(name){ var r = v_geteles(name, 'getElementsByTagName'); v_console_log('  [*] Document -> getElementsByTagName', name, r); return r })
    Document.prototype.getElementsByTagNameNS = v_saf(function getElementsByTagNameNS(name){ var r = v_geteles(name, 'getElementsByTagNameNS'); v_console_log('  [*] Document -> getElementsByTagNameNS', name, r); return r })
    Document.prototype.querySelectorAll = v_saf(function querySelectorAll(name){ var r = v_geteles(name, 'querySelectorAll'); v_console_log('  [*] Document -> querySelectorAll', name, r); return r })
    var v_head = v_new(HTMLHeadElement)
    var v_body = v_new(HTMLBodyElement)
    Object.defineProperties(Document.prototype, {
      head: {get(){ v_console_log("  [*] Document -> head[get]", v_head);return v_proxy(v_head, 'document.head') }},
      body: {get(){ v_console_log("  [*] Document -> body[get]", v_body);return v_proxy(v_body, 'document.body') }},
    })
  }
  function v_init_canvas(){
    HTMLCanvasElement.prototype.getContext = function(){
      if (arguments[0]=='2d'){var r = v_proxy(v_new(CanvasRenderingContext2D), 'canvas2d', function(a){return function(){v_console_log(a,...arguments)}}); return r};
      if (arguments[0]=='webgl' || arguments[0]=='experimental-webgl'){var r = v_proxy(v_new(WebGLRenderingContext), 'webgl', function(a){return function(){v_console_log(a,...arguments)}}); r._canvas = this; return r};
      return null
    }
    HTMLCanvasElement.prototype.toDataURL = function(){return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAACWCAYAAABkW7XSAAAEYklEQVR4Xu3UAQkAAAwCwdm/9HI83BLIOdw5AgQIRAQWySkmAQIEzmB5AgIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlAABg+UHCBDICBisTFWCEiBgsPwAAQIZAYOVqUpQAgQMlh8gQCAjYLAyVQlKgIDB8gMECGQEDFamKkEJEDBYfoAAgYyAwcpUJSgBAgbLDxAgkBEwWJmqBCVAwGD5AQIEMgIGK1OVoAQIGCw/QIBARsBgZaoSlACBB1YxAJfjJb2jAAAAAElFTkSuQmCC"}
  }
  var v_start_stamp = +new Date
  var v_fake_stamp = +new Date
  function v_init_event_target(){
    v_events = {}
    function add_event(_this, x){
      if (!v_events[x[0]]){
        v_events[x[0]] = []
      }
      v_events[x[0]].push([_this, x[1].bind(_this)])
    }
    function _mk_mouse_event(type, canBubble, cancelable, view, detail, screenX, screenY, clientX, clientY, ctrlKey, altKey, shiftKey, metaKey, button, relatedTarget){
      if (type == 'click'){
        var m = new v_saf(function PointerEvent(){})
        m.pointerType = "mouse"
      }else{
        var m = new v_saf(function MouseEvent(){})
      }
      m.isTrusted = true
      m.type = type
      m.canBubble = canBubble
      m.cancelable = cancelable
      m.view = view
      m.detail = detail
      m.screenX = screenX; m.movementX = screenX
      m.screenY = screenY; m.movementY = screenY
      m.clientX = clientX; m.layerX = clientX; m.offsetX = clientX; m.pageX = clientX; m.x = clientX;
      m.clientY = clientY; m.layerY = clientY; m.offsetY = clientY; m.pageY = clientY; m.y = clientY;
      m.ctrlKey = ctrlKey
      m.altKey = altKey
      m.shiftKey = shiftKey
      m.metaKey = metaKey
      m.button = button
      m.relatedTarget = relatedTarget
      return m
    }
    function make_mouse(type, x, y){
      return _mk_mouse_event(type, true, true, window, 0, 0, 0, x, y, false, false, false, false, 0, null)
    }
    function mouse_click(x, y){
      for (var i = 0; i < (v_events['click'] || []).length; i++) { v_events['click'][i][1](make_mouse('click', x, y)) }
      for (var i = 0; i < (v_events['mousedown'] || []).length; i++) { v_events['mousedown'][i][1](make_mouse('mousedown', x, y)) }
      for (var i = 0; i < (v_events['mouseup'] || []).length; i++) { v_events['mouseup'][i][1](make_mouse('mouseup', x, y)) }
    }
    var offr = Math.random()
    function make_touch(_this, type, x, y, timeStamp){
      var offx = Math.random()
      var offy = Math.random()
      var t = v_new(new v_saf(function Touch(){}))
      t = clientX = offx + x
      t = clientY = offy + y
      t = force = 1
      t = identifier = 0
      t = pageX = offx + x
      t = pageY = offy + y
      t = radiusX = 28 + offr
      t = radiusY = 28 + offr
      t = rotationAngle = 0
      t = screenX = 0
      t = screenY = 0
      var e = v_new(new v_saf(function TouchEvent(){}))
      e.isTrusted = true
      e.altKey = false
      e.bubbles = true
      e.cancelBubble = false
      e.cancelable = false
      e.changedTouches = e.targetTouches = e.touches = [t]
      e.composed = true
      e.ctrlKey = false
      e.currentTarget = null
      e.defaultPrevented = false
      e.detail = 0
      e.eventPhase = 0
      e.metaKey = false
      e.path = _this == window ? [window] : [_this, window]
      e.returnValue = true
      e.shiftKey = false
      e.sourceCapabilities = new v_saf(function InputDeviceCapabilities(){this.firesTouchEvents = true})
      e.srcElement = _this
      e.target = _this
      e.type = type
      e.timeStamp = timeStamp == undefined ? (new Date - v_start_stamp) : ((v_fake_stamp += Math.random()*20) - v_start_stamp)
      e.view = window
      e.which = 0
      return e
    }
    function make_trace(x1, y1, x2, y2){
      // 贝塞尔曲线
      function step_len(x1, y1, x2, y2){
        var ln = ((x2 - x1) ** 2 + (y2 - y1) ** 2) ** 0.5
        return (ln / 10) ^ 0
      }
      var slen = step_len(x1, y1, x2, y2)
      if (slen < 3){
        return []
      }
      function factorial(x){
        for(var y = 1; x > 1;  x--) {
          y *= x
        }
        return y;
      }
      var lp = Math.random()
      var rp = Math.random()
      var xx1 = (x1 + (x2 - x1) / 12 * (4-lp*4)) ^ 0
      var yy1 = (y1 + (y2 - y1) / 12 * (8+lp*4)) ^ 0
      var xx2 = (x1 + (x2 - x1) / 12 * (8+rp*4)) ^ 0
      var yy2 = (y1 + (y2 - y1) / 12 * (4-rp*4)) ^ 0
      var points = [[x1, y1], [xx1, yy1], [xx2, yy2], [x2, y2]]
      var N = points.length
      var n = N - 1
      var traces = []
      var step = slen
      for (var T = 0; T < step+1; T++) {
        var t = T*(1/step)
        var x = 0
        var y = 0
        for (var i = 0; i < N; i++) {
          var B = factorial(n)*t**i*(1-t)**(n-i)/(factorial(i)*factorial(n-i))
          x += points[i][0]*B
          y += points[i][1]*B
        }
        traces.push([x^0, y^0])
      }
      return traces
    }
    function touch(x1, y1, x2, y2){
      if (x2 == undefined && y2 == undefined){
        x2 = x1
        y2 = y1
      }
      var traces = make_trace(x1, y1, x2, y2)
      v_console_log('traces:', traces)
      for (var i = 0; i < (v_events['touchstart'] || []).length; i++) { v_events['touchstart'][i][1](make_touch(v_events['touchstart'][i][0], 'touchstart', x1, y1)) }
      for (var j = 0; j < traces.length; j++) {
        var x = traces[j][0]
        var y = traces[j][0]
        for (var i = 0; i < (v_events['touchmove'] || []).length; i++) { v_events['touchmove'][i][1](make_touch(v_events['touchmove'][i][0], 'touchmove', x, y)) }
      }
      for (var i = 0; i < (v_events['touchend'] || []).length; i++) { v_events['touchend'][i][1](make_touch(v_events['touchend'][i][0], 'touchend', x2, y2)) }
    }
    function mouse_move(x1, y1, x2, y2){
      if (x2 == undefined && y2 == undefined){
        x2 = x1
        y2 = y1
      }
      var traces = make_trace(x1, y1, x2, y2)
      v_console_log('traces:', traces)
      for (var j = 0; j < traces.length; j++) {
        var x = traces[j][0]
        var y = traces[j][0]
        for (var i = 0; i < (v_events['mousemove'] || []).length; i++) { v_events['mousemove'][i][1](make_touch(v_events['mousemove'][i][0], 'mousemove', x, y)) }
      }
    }
    window.make_mouse = make_mouse
    window.mouse_click = mouse_click
    window.mouse_move = mouse_move
    window.touch = touch
    EventTarget.prototype.addEventListener = function(){v_console_log('  [*] EventTarget -> addEventListener[func]', this===window?'[Window]':this===document?'[Document]':this, [].slice.call(arguments)); add_event(this, [].slice.call(arguments)); return null}
    EventTarget.prototype.dispatchEvent = function(){v_console_log('  [*] EventTarget -> dispatchEvent[func]', this===window?'[Window]':this===document?'[Document]':this, [].slice.call(arguments)); add_event(this, [].slice.call(arguments)); return null}
    EventTarget.prototype.removeEventListener = function(){v_console_log('  [*] EventTarget -> removeEventListener[func]', this===window?'[Window]':this===document?'[Document]':this, [].slice.call(arguments)); add_event(this, [].slice.call(arguments)); return null}
  }
  function v_init_Element_prototype(){
    Element.prototype.appendChild            = Element.prototype.appendChild            || v_saf(function appendChild(){v_console_log("  [*] Element -> appendChild[func]", [].slice.call(arguments));})
    Element.prototype.removeChild            = Element.prototype.removeChild            || v_saf(function removeChild(){v_console_log("  [*] Element -> removeChild[func]", [].slice.call(arguments));})
    Element.prototype.getAnimations          = Element.prototype.getAnimations          || v_saf(function getAnimations(){v_console_log("  [*] Element -> getAnimations[func]", [].slice.call(arguments));})
    Element.prototype.getAttribute           = Element.prototype.getAttribute           || v_saf(function getAttribute(){v_console_log("  [*] Element -> getAttribute[func]", [].slice.call(arguments));})
    Element.prototype.getAttributeNS         = Element.prototype.getAttributeNS         || v_saf(function getAttributeNS(){v_console_log("  [*] Element -> getAttributeNS[func]", [].slice.call(arguments));})
    Element.prototype.getAttributeNames      = Element.prototype.getAttributeNames      || v_saf(function getAttributeNames(){v_console_log("  [*] Element -> getAttributeNames[func]", [].slice.call(arguments));})
    Element.prototype.getAttributeNode       = Element.prototype.getAttributeNode       || v_saf(function getAttributeNode(){v_console_log("  [*] Element -> getAttributeNode[func]", [].slice.call(arguments));})
    Element.prototype.getAttributeNodeNS     = Element.prototype.getAttributeNodeNS     || v_saf(function getAttributeNodeNS(){v_console_log("  [*] Element -> getAttributeNodeNS[func]", [].slice.call(arguments));})
    Element.prototype.getBoundingClientRect  = Element.prototype.getBoundingClientRect  || v_saf(function getBoundingClientRect(){v_console_log("  [*] Element -> getBoundingClientRect[func]", [].slice.call(arguments));})
    Element.prototype.getClientRects         = Element.prototype.getClientRects         || v_saf(function getClientRects(){v_console_log("  [*] Element -> getClientRects[func]", [].slice.call(arguments));})
    Element.prototype.getElementsByClassName = Element.prototype.getElementsByClassName || v_saf(function getElementsByClassName(){v_console_log("  [*] Element -> getElementsByClassName[func]", [].slice.call(arguments));})
    Element.prototype.getElementsByTagName   = Element.prototype.getElementsByTagName   || v_saf(function getElementsByTagName(){v_console_log("  [*] Element -> getElementsByTagName[func]", [].slice.call(arguments));})
    Element.prototype.getElementsByTagNameNS = Element.prototype.getElementsByTagNameNS || v_saf(function getElementsByTagNameNS(){v_console_log("  [*] Element -> getElementsByTagNameNS[func]", [].slice.call(arguments));})
    Element.prototype.getInnerHTML           = Element.prototype.getInnerHTML           || v_saf(function getInnerHTML(){v_console_log("  [*] Element -> getInnerHTML[func]", [].slice.call(arguments));})
    Element.prototype.hasAttribute           = Element.prototype.hasAttribute           || v_saf(function hasAttribute(){v_console_log("  [*] Element -> hasAttribute[func]", [].slice.call(arguments));})
    Element.prototype.hasAttributeNS         = Element.prototype.hasAttributeNS         || v_saf(function hasAttributeNS(){v_console_log("  [*] Element -> hasAttributeNS[func]", [].slice.call(arguments));})
    Element.prototype.hasAttributes          = Element.prototype.hasAttributes          || v_saf(function hasAttributes(){v_console_log("  [*] Element -> hasAttributes[func]", [].slice.call(arguments));})
    Element.prototype.hasPointerCapture      = Element.prototype.hasPointerCapture      || v_saf(function hasPointerCapture(){v_console_log("  [*] Element -> hasPointerCapture[func]", [].slice.call(arguments));})
    Element.prototype.webkitMatchesSelector  = Element.prototype.webkitMatchesSelector  || v_saf(function webkitMatchesSelector(){v_console_log("  [*] Element -> webkitMatchesSelector[func]", [].slice.call(arguments));})
  }
  function v_init_HTMLElement(){
    try{
      Object.defineProperties(HTMLElement.prototype, {
        style: {set: undefined, enumerable: true, configurable: true, get: v_saf(function style(){v_console_log("  [*] HTMLElement -> style[get]", [].slice.call(arguments)); return this.v_style })},
      })
    }catch(e){
      v_console_log(e)
    }
  }
  function v_init_DOMTokenList_prototype(){
    DOMTokenList.prototype.add = DOMTokenList.prototype.add || v_saf(function add(){v_console_log("  [*] DOMTokenList -> add[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.contains = DOMTokenList.prototype.contains || v_saf(function contains(){v_console_log("  [*] DOMTokenList -> contains[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.entries = DOMTokenList.prototype.entries || v_saf(function entries(){v_console_log("  [*] DOMTokenList -> entries[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.forEach = DOMTokenList.prototype.forEach || v_saf(function forEach(){v_console_log("  [*] DOMTokenList -> forEach[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.item = DOMTokenList.prototype.item || v_saf(function item(){v_console_log("  [*] DOMTokenList -> item[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.keys = DOMTokenList.prototype.keys || v_saf(function keys(){v_console_log("  [*] DOMTokenList -> keys[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.length = DOMTokenList.prototype.length || v_saf(function length(){v_console_log("  [*] DOMTokenList -> length[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.remove = DOMTokenList.prototype.remove || v_saf(function remove(){v_console_log("  [*] DOMTokenList -> remove[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.replace = DOMTokenList.prototype.replace || v_saf(function replace(){v_console_log("  [*] DOMTokenList -> replace[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.supports = DOMTokenList.prototype.supports || v_saf(function supports(){v_console_log("  [*] DOMTokenList -> supports[func]", [].slice.call(arguments));})
    DOMTokenList.prototype.toggle = DOMTokenList.prototype.toggle || v_saf(function toggle(){v_console_log("  [*] DOMTokenList -> toggle[func]", [].slice.call(arguments));})
  }
  function v_init_CSSStyleDeclaration_prototype(){
    CSSStyleDeclaration.prototype["zoom"] = ''
    CSSStyleDeclaration.prototype["resize"] = ''
    CSSStyleDeclaration.prototype["text-rendering"] = ''
    CSSStyleDeclaration.prototype["text-align-last"] = ''
  }
  function v_init_PointerEvent_prototype(){
    PointerEvent.prototype.getCoalescedEvents = v_saf(function getCoalescedEvents(){v_console_log("  [*] PointerEvent -> getCoalescedEvents[func]", [].slice.call(arguments));})
    PointerEvent.prototype.getPredictedEvents = v_saf(function getPredictedEvents(){v_console_log("  [*] PointerEvent -> getPredictedEvents[func]", [].slice.call(arguments));})
  }
  function v_init_PerformanceTiming_prototype(){
    try{
      Object.defineProperties(PerformanceTiming.prototype, {
        connectEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function connectEnd(){v_console_log("  [*] PerformanceTiming -> connectEnd[get]", [].slice.call(arguments));})},
        connectStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function connectStart(){v_console_log("  [*] PerformanceTiming -> connectStart[get]", [].slice.call(arguments));})},
        domComplete: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domComplete(){v_console_log("  [*] PerformanceTiming -> domComplete[get]", [].slice.call(arguments));})},
        domContentLoadedEventEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domContentLoadedEventEnd(){v_console_log("  [*] PerformanceTiming -> domContentLoadedEventEnd[get]", [].slice.call(arguments));})},
        domContentLoadedEventStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domContentLoadedEventStart(){v_console_log("  [*] PerformanceTiming -> domContentLoadedEventStart[get]", [].slice.call(arguments));})},
        domInteractive: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domInteractive(){v_console_log("  [*] PerformanceTiming -> domInteractive[get]", [].slice.call(arguments));})},
        domLoading: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domLoading(){v_console_log("  [*] PerformanceTiming -> domLoading[get]", [].slice.call(arguments));})},
        domainLookupEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domainLookupEnd(){v_console_log("  [*] PerformanceTiming -> domainLookupEnd[get]", [].slice.call(arguments));})},
        domainLookupStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function domainLookupStart(){v_console_log("  [*] PerformanceTiming -> domainLookupStart[get]", [].slice.call(arguments));})},
        fetchStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function fetchStart(){v_console_log("  [*] PerformanceTiming -> fetchStart[get]", [].slice.call(arguments));})},
        loadEventEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function loadEventEnd(){v_console_log("  [*] PerformanceTiming -> loadEventEnd[get]", [].slice.call(arguments));})},
        loadEventStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function loadEventStart(){v_console_log("  [*] PerformanceTiming -> loadEventStart[get]", [].slice.call(arguments));})},
        navigationStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function navigationStart(){v_console_log("  [*] PerformanceTiming -> navigationStart[get]", [].slice.call(arguments));})},
        redirectEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function redirectEnd(){v_console_log("  [*] PerformanceTiming -> redirectEnd[get]", [].slice.call(arguments));})},
        redirectStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function redirectStart(){v_console_log("  [*] PerformanceTiming -> redirectStart[get]", [].slice.call(arguments));})},
        requestStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function requestStart(){v_console_log("  [*] PerformanceTiming -> requestStart[get]", [].slice.call(arguments));})},
        responseEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function responseEnd(){v_console_log("  [*] PerformanceTiming -> responseEnd[get]", [].slice.call(arguments));})},
        responseStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function responseStart(){v_console_log("  [*] PerformanceTiming -> responseStart[get]", [].slice.call(arguments));})},
        secureConnectionStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function secureConnectionStart(){v_console_log("  [*] PerformanceTiming -> secureConnectionStart[get]", [].slice.call(arguments));})},
        unloadEventEnd: {set: undefined, enumerable: true, configurable: true, get: v_saf(function unloadEventEnd(){v_console_log("  [*] PerformanceTiming -> unloadEventEnd[get]", [].slice.call(arguments));})},
        unloadEventStart: {set: undefined, enumerable: true, configurable: true, get: v_saf(function unloadEventStart(){v_console_log("  [*] PerformanceTiming -> unloadEventStart[get]", [].slice.call(arguments));})},
      })
    }catch(e){}
  }
  function mk_atob_btoa(r){var a="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",t=new Array(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,-1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1);return{atob:function(r){var a,e,o,h,c,i,n;for(i=r.length,c=0,n="";c<i;){do{a=t[255&r.charCodeAt(c++)]}while(c<i&&-1==a);if(-1==a)break;do{e=t[255&r.charCodeAt(c++)]}while(c<i&&-1==e);if(-1==e)break;n+=String.fromCharCode(a<<2|(48&e)>>4);do{if(61==(o=255&r.charCodeAt(c++)))return n;o=t[o]}while(c<i&&-1==o);if(-1==o)break;n+=String.fromCharCode((15&e)<<4|(60&o)>>2);do{if(61==(h=255&r.charCodeAt(c++)))return n;h=t[h]}while(c<i&&-1==h);if(-1==h)break;n+=String.fromCharCode((3&o)<<6|h)}return n},btoa:function(r){var t,e,o,h,c,i;for(o=r.length,e=0,t="";e<o;){if(h=255&r.charCodeAt(e++),e==o){t+=a.charAt(h>>2),t+=a.charAt((3&h)<<4),t+="==";break}if(c=r.charCodeAt(e++),e==o){t+=a.charAt(h>>2),t+=a.charAt((3&h)<<4|(240&c)>>4),t+=a.charAt((15&c)<<2),t+="=";break}i=r.charCodeAt(e++),t+=a.charAt(h>>2),t+=a.charAt((3&h)<<4|(240&c)>>4),t+=a.charAt((15&c)<<2|(192&i)>>6),t+=a.charAt(63&i)}return t}}}
  var atob_btoa = mk_atob_btoa()
  window.btoa = window.btoa || v_saf(atob_btoa.btoa, 'btoa')
  window.atob = window.atob || v_saf(atob_btoa.atob, 'atob')
  window.postMessage = v_saf(function(){v_console_log('  [*] [postMessage]', arguments)}, 'postMessage')
  window.matchMedia = v_saf(function(){v_console_log('  [*] [matchMedia]', arguments); return v_proxy({}, 'matchMedia{}')}, 'matchMedia')

  init_cookie("v=QThpRG1lZmNPMmpaaFZpb1h1Mk8xNElobVQzZmNTd2JEdFFBLVlKNUZuODJsbVpqS29IOEMxN2wwSXZSMTc1Nzg5OTkwODQ4Nw==; _nano_fp=XpmynqCblpUxn0EYlT_squM__YIjvP6lMIRryTtj; Hm_lvt_0d2227abf9548feda3b9cb6fddee26c0=1758017588,1758075595,1758261042,1758505179; HMACCOUNT=5ADB897338667860; Hm_lpvt_0d2227abf9548feda3b9cb6fddee26c0=1758505190")
  v_hook_href(window.location, 'location', "https://www.mashangpa.com/problem-detail/12/")
  Location.prototype.toString = v_saf(function toString(){ return "https://www.mashangpa.com/problem-detail/12/" })
  window.alert = v_saf(function alert(){})
  v_hook_storage()
  v_init_HTMLElement()
  v_init_document()
  v_init_canvas()
  v_init_event_target()
  v_init_Element_prototype()
  v_init_DOMTokenList_prototype()
  v_init_CSSStyleDeclaration_prototype()
  v_init_PointerEvent_prototype()
  v_init_PerformanceTiming_prototype()
  window.innerWidth = 1707
  window.innerHeight = 898
  window.outerHeight = 1019
  window.outerWidth = 1707
  window.isSecureContext = true
  window.origin = location.origin
  function v_getele(name, func){
    return null
  }
  function v_geteles(name, func){
    return null
  }
  var v_Date = Date;
  var v_base_time = +new Date;
  (function(){
    function ftime(){
      return new v_Date() - v_base_time + v_to_time
    }
    Date = function(_Date) {
      var bind = Function.bind;
      var unbind = bind.bind(bind);
      function instantiate(constructor, args) {
        return new (unbind(constructor, null).apply(null, args));
      }
      var names = Object.getOwnPropertyNames(_Date);
      for (var i = 0; i < names.length; i++) {
        if (names[i]in Date)
          continue;
        var desc = Object.getOwnPropertyDescriptor(_Date, names[i]);
        Object.defineProperty(Date, names[i], desc);
      }
      function Date() {
        var date = instantiate(_Date, [ftime()]);
        return date;
      }
      Date.prototype = _Date.prototype
      return v_saf(Date);
    }(Date);
    Date.now = v_saf(function now(){ return ftime() })
  })();
  var v_to_time = +new v_Date
  // var v_to_time = +new v_Date('Sat Sep 03 2022 11:11:58 GMT+0800') // 自定义起始时间

  v_repair_this() // 修复 window 指向global
  v_new_toggle = false




  // v_console_log = function(){} // 关闭日志输出
  // setTimeout = function(){} // 关闭定时器
  // setInterval = function(){} // 关闭定时器
  return window
})();

const Q0Q00OQ = QOQQ;
var OＯ0$ = 'jsjiami.com.v7';


// TODO:补ajax
var diy_$ = {
    'ajax': function () {
        console.log(arguments)
    }
}

function QQQO() {
    const OQO0QOO = (function () {
        return [OＯ0$, 'gIRjJsTEjeHibVEamQiNOl.FRcyYoEkFmNI.gKv7==', 'pG7dJ8oOWQu', 'AcRdMq', 'W7P0WPNcKmot', 'WP/cSCktzSou', 'W7VcNKldJqa', 'WPvDB8kMua', 'fmkurCo8W6ldNuOrxuldPmorWOe', 'j8k4W5NdM38', 'd8kWW5jECW', 'uCoobW', 'WR9hWRO', 'dCk8WQ/cNq', 'FCo+WRhcMaKYWQZdK8oDW4WoWQO', 'WQJdT3NdSra', 'WPb7E8o+W6tcSau', 'hGpdNmosWRK', 'WP/cH8o1W6nQ', 'WObrW4ddHa', 'vSocerVdNG', 'lSo/E8kgwa', 'WR3dOmoS', 'phKufSk6', 'g8oOW5zzfG', 'q8khWOrLkrCNWPmgu8oO', 'WRDEW4/dOq', 'WPneW4hcUZ4', 'W7JcGv4', 'W6JcS0FdMW', 'WRiiW5pdLrC', 'f8kFW5flFHPIWOPgw8kvW7dcG8oLn8oNimogWQTsFq', 'cXpdLmoNWOu', 'W7FcGCkDW7pcTG', 'kYbuWQxdIxS', 'jtldP8o1WPy', 'DLFdU30', 'WQ8DW77dHSkd', 'WQ8QW5u', 'bSkuW4u', 'WRNcVCosW4TU', 'W67cMSk4W5S', 'W5KJW5ddL0i', 'W7xcU8kUjLhcLmkmWRRdLmkFW6XH', 'sXhdOr3dGG', 'WR/dUNJdHcSIWRC', 'W7BdSSo8W68GWQK', 'nSoNW712oW', 'W7ukW4dcGmok', 'W6xdJCoaW7WK', 'WR95wmklEG', 'jSk9W5VdPM4', 'WPNcPCk7smoI', 'W5xdLmoHBxm', 'WQ0hW7/dRCk0', 'Be3dOxS', 'WQZdShW', 'WRb5qmoMW7W', 'rCknWOVdHSkh', 'W68JW77dVwS', 'Dmorg8kcWP0', 'j8ogCSk8zq', 'iN4EpmkH', 'kdzvWQG', 'WQO8W7pdKZa', 'WRJdO23dPSkk', 'zCoklSkPi8kNWQtcMwrDW7Hpsq', 'W6BdRCo+W6u1', 'W6xdQmoHW6q', 'WQRdQSoOraRdGSkCW4lcNG', 'CfldLL3cVW', 'WQ10W7NcOJ0', 'BmkVW7ZcRfm', 'W53dQmo0', 'fSo5WPdcQGC', 'EmkqWPZdQmkZ', 'WODFwCkjyG', 'WQFdKmo8W7qNyCkhWQFcSIJcGb/cNYW', 'WQhdVhZdQ8kx', 'nxChAXy', 'WPNdPMtdMXC', 'WRRdPXzRW7W', 'W6ldR8o9W7SQ', 'WPiYW6O', 'W4VcRvldO18', 'W7NcTKtdN3a', 'jmozBG', 'emkkW5jHuW', 'AIWQrG', 'W7BcU8k+W6tcNa', 'C8ogiXJdPG', 'pqZdG1hcLG', 'aCoDt8kpvG', 'W57dOSoWr1FdGapcGwXcrrK', 'WQf0Fmo3W5VcVXq9', 'W4dcOSkQW7VcSq', 'WOyieq', 'sW4QEYKXycG', 'jmomW4vvkW', 'p8ktWPVcVfW', 'WPOhW7VdJCkRFxlcICkhWOvC', 'WQa8s8oN', 'x13dU0BcTq', 'WRZdMgtdUWW', 'WPqYW4ldMHCwzWNdMq', 'W6BcLmk8WQS', 'WRuUWRpcQCkUxYnpjq', 'eX7dSSoFWPq', 'AJqaASkQ', 'WPNcNSoyW4T0', 'WPCrW4tdRte', 'WQpcRSoKWQas', 'W6LZWPm', 'W6mdW5VdSw8', 'fmkaW5hdH0K', 'gJRdNvBcHa', 'oatdHq', 'WOxdHb1iW50', 'iCoZWPRcHH8', 'WPzLzmo/W7K', 'jSkrW4DTAG', 'dSkkW7fjwq', 'WPeYW5xdQJO', 'WQxcISogWRy7', 'grvlWQldVa', 'WOVdNCoREqC'].concat((function () {
            return ['WO9eFCoZW7/cRW', 'W49WWRBcVSoV', 'tcaEt8kK', 'WQZdM3/dMmkL', 'WRNcLCoJWO8N', 'zCk1W5/cUv8', 'WOVcO8otW7LY', 'uZBdNbFdQW', 'caxdLCovWOO6W78', 'WO10Ba', 'WP7dR0JdLc0', 'W4JcGg3dPvO', 'W7a9W5tcOCoUsN5u', 'WRv8CSkuf8khW5DZdZq', 'W7NdPCoRW6O7', 'oYTCWQ/dTG', 'WQddHHPgW44', 'WOLWzSoXW7NcVG', 'WOOmW6JdMSknFehcHCkIWOu', 'W6FcLCkWW4tcNmkh', 'W7JcM13dJa', 'WOVcO8orWPiqWQLca8ogCW', 'kcXw', 'W7/dKCobW6eB', 'W5NdOSo8CMhdLqlcJwjExXK', 'WRRcVmolW5PB', 'W4FcOepdSIi', 'eLatgCkRbW', 't0PYze4KeX7cOCodxfvBW5ei', 'ySoLpSkxWR4', 'WQlcMmkk', 'WRvOW6BdH8ou', 'WOJdJtrRW5y', 'WP7dMJvsW7i', 'WOepFSopWRi', 'WPzrW54', 'oXVdMv7cUa', 'WONdHv3dQG', 'WOldMXr9W50', 'dHVdISouWPC', 'k8k7W63dJ0S', 'W6qbWOK', 'WR5NW6/dUmov', 'sCkjWONdRSk6', 'l8oMW5vdhW', 'W57cG8kUW7VcMa', 'W7/cHCkQW4S', 'WP3dNCokqI0', 'WQe8sCodWPi', 'lHldULNcJW', 'W6alWOhcRa', 'WQDwvSkFrq', 'uCoubmkT', 'W47cVKZdVKW', 'r8opW4vHo0XwWP1CtSkWWRK', 'WRFdJJDDW7m', 'naRdO8oNWOa', 'WPOrW6RdI8kRyfy', 'WQi2WPZdPbldTtJcT8kLWRfRW4q7', 'WRVcKHZdMeaaWQOdBSoSvCoWBbxdQa', 'WRxdSdD3W5C', 'rCo9W67dGIeUWP3cS8ohWO8Xaq', 'W7/cUmkQkfRcMSoDWR7dG8k8W65yaa', 'FCo0W6ZdJ39HWQFdMG', 'W6VcQL3dKgW', 'W4NcVSk/WRf+j8orW4hcHq', 'avan', 'WPPwW6FcVHW', 'srhdNIldHG', 'k2TRgmowWPxdTaCtEsGL', 'wCo4fCkaWR0', 'W6BcJSk/', 'jmodBCk9', 'p8oXWOZcIWm', 'pSkPW7ldM1L3WRVdUmoEW5WhWPdcNmkLDSohz8kiWRi', 'dCkKWOxcSvO', 'tCoofmkKWQpcJruD', 'WQrXsG', 'WQJcPSk2u8oS', 'AIldIYxcHZxcI8o2k8kIW79ZW608W5xdT2i2bmkemCkH', 'WPRcUSosWPi', 'kbLtWOhdNa', 'W7SpW6JdNey', 'W6PEWQ7cTSok', 'W4ldImovW7uV', 'mCoxBmkIzG', 'WR7dGSkAW4JcM8krW4NdUG', 'WRPZW6lcGdG', 'W6xdSSoI', 'C8oOpq', 'WRRcMSoo', 'W43cR1RdHZRcGclcHWvWWQi', 'WOaYxmo3WO84bt4', 'eLuEaSkG', 'W5ZcOCkJW6ZcSq', 'WPv6Ea', 'nMGB', 'WPLRW77cQca', 'WRPWWOrvW6a', 'kwy8mSk/', 'udrUW7BdLmo7WPZdNCkL', 'WPJcQSosWOOEWQvueSoMFNNdQeGTwahdGW7dRwq', 'WQtdLSo5W7OOzCklW5hcTtBcVHVcQa', 'W7/dGmkmWQixW7VcVSofWQ0Xi8o7mG', 'AMhdKxtcHW', 'WOX3W7RdUSok', 'W77cR1JdN13dLXVcGq', 'W5ldLCoRW6KP', 'ymo1iYtdRG', 'qmofe8kxWRlcLWOCEvVdISoYWQrAjSkxWQ5KyNrKja', 'W53dL8owDKy', 'WPbGB8ki', 'WQrRsCkN', 'lG8fW5lcHW', 'W6tcIgRdQIC', 'WQBdKX51W4m', 'WQddQSoVyGldI8kn', 'W5RdR8oyW6mn', 'vGWDrSkk', 'pmk7WQ7cLgO', 'W6JcQuq', 'WO0MW6ldOSk7', 'lmoTWPlcRtG', 'W4JcLSkRW7xcTq', 'WQldO8oqvHS', 'WOhdUCowvI8'].concat((function () {
                return ['BXaOr8kT', 'WRVcMCkoB8owuSoFW6SjiKe', 'WQBdU8ooqIG', 'n3aVDa0', 'WP3dLvJdNXO', 'vCksW7JcVxu', 'vs8yqmk5', 'ESkoWOVdQmktkSoKW4dcVbe', 'WPaYW7y', 'dtruWO7dMq', 'rmoKpCknWO4', 'W4VdLSoQtvy', 'WQxcMCoYW5XA', 'nCoMWRhdRsPMnqlcUSkpWRJdK8oa', 'p3WsvYu', 'w8o4W6pcN3JdVMNdRSkhd8kJ', 'WQaiE8oyWQu', 'WQxcNSo1WRGq', 'lI8YW6VcPW', 'WRFdQHTyW4m', 'WQngWQvTW4G', 'WQlcLmonW5rvWQZdNSovWOGRdCoJdrG', 'W6PSWO/cLSo9', 'zSkSWR3dG8k9', 'WOdcT8ouWRSG', 'i8o0WOFcVsm', 'WOjtW73cHJpdTXK', 'WOf5ECkswW', 'WR3dK1JdH8ky', 'W6NcVfZdOHC', 'obNdLmosWPf8W60jWPK2jmojWPqIW5qnB37cVW', 'iCoyESkWD8kYW7NcGdy', 'et0UW6i', 'oeqCyJ8', 'WQhcKSko', 'W7ZcGmk1W4RcGa', 'WQJdVrm', 'W6VdRLDnWOPiW4vctW', 'WPrYW7BdGmoj', 'WOneWRT4W5q', 'kcTtWQhdVG', 'EmoXpXRdH2pcRa', 'W6pdHSojpCksa8kdWQivkMmzWORdQq', 'WOVcNCotWRWs', 'pmkHW5RdQNu', 'kx3dHIxdRghdK8kV', 'kqLFWOVdOa', 'oSowW4T1jG', 'WRFdJb4', 'ew4nFrW', 'W5/dQCovAKq', 'WONdOL7dK8kj', 'dCkMWQW', 'cLK2', 'W7BdQCoQ', 'WRxdSdzVW5G', 'n8kXW5LDsq', 'qmotemkWWRRcGrqhza', 'WQz7ACknAG', 'W6VcQZ7cOxr/W6lcQmomWRi5cWW', '5BYP5yM75O2b5lU85lMW5AYa5zYm772U6k2N5Qg/5P6j5OYU5lQj6zMRgW', 'sCoteSkJ', 'WRawW7RdUCk/', 'vWRdPXFdHq', 'WP8dW5VdKSkb', 'B8kpWPG', 'WQNdMLZdTSkt', 'WRzwvmk1FW', 'W6y/W5NdSa', 'W7m7W6pcOCo0', 'dcTiWRxdHq', 'fSoermkVuW', 'DSoSg8kKWQe', 'jgWcsXi', 'lqddVNdcPW/dJfe', 'WQf2xSkRFG', 'WQGWASoiWRu', 'WPzwFCkHwG', 'xbldMcRdQG', 'oYzOWPddIa', 'WP7dLvRdGCknpmoBggVdLa', 'WPzHACoIW7JcPq', 'o8oqW6XBogBdIa', 'WRldMZD7W5a', 'WQxcGmoVWQKB', 'WRVdT3BdVXG', 'z8o0obS', 'm8otASkfy8k0W6pcIIzyWR1wmGq', 'WRb1W7tdV8ok', 'WQJcQmosWQSw', 'lmkwW5zyAW', 'WRuUWRpcQCkPxYbpj8ojmW', 'bmooWRa', 'WPzlW53dMa', 'D3/dLL7cGa', 'Ex3dI3FcOa', 'W6yLW5O', 'ymokl8kPi8kNWQFcMwe', 'pSomW7bhpNtdHu4Y', 'WQDKW4lcTbi', 'WO8ez8ouWRG', 'ed3dOmoUWPm', 'qmoudaRdHG', 'EfNdPNi', 'hKS5AXC', 'WQjZESoaW4W', 'f18bCWa', 'WOf7W4RdGSoN', 'WORdLcjsW6W', 'W50iWQdcQmkz', 'cmkhW6NdVgK', 'WQvZACo1W6JcUHq5W5e', 'W53dSSo3BG', 'bmkmWORcRgi', 'WOX3FmkcrG', 'W75RWOHSW5iqW7S', 'W6CZW6VcUq', 'WPGuW6FdRcK', 'C8kjWOxdJ8kg', 'lsRdV3tcTa', 'gmoRWQe', 'WORdTYnOW5S', 'cue2uYC', 'ddi3W4NcIG', 'smkUWRRdVCkU'];
            }()));
        }()));
    }());
    QQQO = function () {
        return OQO0QOO;
    }
    ;
    return QQQO();
}

function QOQQ(_0x4aea86, _0x36e8fe) {
    const _0x576afd = QQQO();
    return QOQQ = function (_0x274772, _0x4d12b9) {
        _0x274772 = _0x274772 - 0x1e7;
        let _0x2e71a9 = _0x576afd[_0x274772];
        if (QOQQ['gYcXjc'] === undefined) {
            var _0x351913 = function (_0x5e79fc) {
                const _0x1ec579 = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/=';
                let _0x3460f6 = ''
                    , _0x56eb7a = '';
                for (let _0x5d09b1 = 0x0, _0x3d27b0, _0x3baf31, _0x5b9f69 = 0x0; _0x3baf31 = _0x5e79fc['charAt'](_0x5b9f69++); ~_0x3baf31 && (_0x3d27b0 = _0x5d09b1 % 0x4 ? _0x3d27b0 * 0x40 + _0x3baf31 : _0x3baf31,
                _0x5d09b1++ % 0x4) ? _0x3460f6 += String['fromCharCode'](0xff & _0x3d27b0 >> (-0x2 * _0x5d09b1 & 0x6)) : 0x0) {
                    _0x3baf31 = _0x1ec579['indexOf'](_0x3baf31);
                }
                for (let _0x4e3858 = 0x0, _0x13215a = _0x3460f6['length']; _0x4e3858 < _0x13215a; _0x4e3858++) {
                    _0x56eb7a += '%' + ('00' + _0x3460f6['charCodeAt'](_0x4e3858)['toString'](0x10))['slice'](-0x2);
                }
                return decodeURIComponent(_0x56eb7a);
            };
            const _0xa4b335 = function (_0xe47c76, _0x35d1b5) {
                let _0xe2f071 = [], _0x1ec36b = 0x0, _0x1eeb4c, _0x24749e = '';
                _0xe47c76 = _0x351913(_0xe47c76);
                let _0x216b13;
                for (_0x216b13 = 0x0; _0x216b13 < 0x100; _0x216b13++) {
                    _0xe2f071[_0x216b13] = _0x216b13;
                }
                for (_0x216b13 = 0x0; _0x216b13 < 0x100; _0x216b13++) {
                    _0x1ec36b = (_0x1ec36b + _0xe2f071[_0x216b13] + _0x35d1b5['charCodeAt'](_0x216b13 % _0x35d1b5['length'])) % 0x100,
                        _0x1eeb4c = _0xe2f071[_0x216b13],
                        _0xe2f071[_0x216b13] = _0xe2f071[_0x1ec36b],
                        _0xe2f071[_0x1ec36b] = _0x1eeb4c;
                }
                _0x216b13 = 0x0,
                    _0x1ec36b = 0x0;
                for (let _0x53357e = 0x0; _0x53357e < _0xe47c76['length']; _0x53357e++) {
                    _0x216b13 = (_0x216b13 + 0x1) % 0x100,
                        _0x1ec36b = (_0x1ec36b + _0xe2f071[_0x216b13]) % 0x100,
                        _0x1eeb4c = _0xe2f071[_0x216b13],
                        _0xe2f071[_0x216b13] = _0xe2f071[_0x1ec36b],
                        _0xe2f071[_0x1ec36b] = _0x1eeb4c,
                        _0x24749e += String['fromCharCode'](_0xe47c76['charCodeAt'](_0x53357e) ^ _0xe2f071[(_0xe2f071[_0x216b13] + _0xe2f071[_0x1ec36b]) % 0x100]);
                }
                return _0x24749e;
            };
            QOQQ['tWYplk'] = _0xa4b335,
                _0x4aea86 = arguments,
                QOQQ['gYcXjc'] = !![];
        }
        const _0x3264a8 = _0x576afd[0x0]
            , _0x3b58ab = _0x274772 + _0x3264a8
            , _0x39cd3b = _0x4aea86[_0x3b58ab];
        return !_0x39cd3b ? (QOQQ['NVFzSW'] === undefined && (QOQQ['NVFzSW'] = !![]),
            _0x2e71a9 = QOQQ['tWYplk'](_0x2e71a9, _0x4d12b9),
            _0x4aea86[_0x3b58ab] = _0x2e71a9) : _0x2e71a9 = _0x39cd3b,
            _0x2e71a9;
    }
        ,
        QOQQ(_0x4aea86, _0x36e8fe);
}
;(function (OO0QQ0Q, OO00O00, QQQOOQ0, O0OO0QQ, QQQQQO0, O0OO0QO, O0OOQ0Q) {
    return OO0QQ0Q = OO0QQ0Q >> 0x5,
        O0OO0QO = 'hs',
        O0OOQ0Q = 'hs',
        function (QQQQQQO, QO0O0OO, OQO0QOQ, QO0OQ00, QO0O0OQ) {
            const QQQ000Q = QOQQ;
            QO0OQ00 = 'tfi',
                O0OO0QO = QO0OQ00 + O0OO0QO,
                QO0O0OQ = 'up',
                O0OOQ0Q += QO0O0OQ,
                O0OO0QO = OQO0QOQ(O0OO0QO),
                O0OOQ0Q = OQO0QOQ(O0OOQ0Q),
                OQO0QOQ = 0x0;
            const OO00O0Q = QQQQQQO();
            while (!![] && --O0OO0QQ + QO0O0OO) {
                try {
                    QO0OQ00 = -parseInt(QQQ000Q(0x33f, '8k$&')) / 0x1 * (parseInt(QQQ000Q(0x28b, 'G]Ue')) / 0x2) + parseInt(QQQ000Q(0x28d, ']x14')) / 0x3 * (parseInt(QQQ000Q(0x355, ']x14')) / 0x4) + -parseInt(QQQ000Q(0x2b4, 'SEv2')) / 0x5 * (parseInt(QQQ000Q(0x2a4, 'Ecwj')) / 0x6) + -parseInt(QQQ000Q(0x307, 'TUGE')) / 0x7 + parseInt(QQQ000Q(0x2d9, 'K@E7')) / 0x8 + -parseInt(QQQ000Q(0x316, '03K!')) / 0x9 * (-parseInt(QQQ000Q(0x2b5, 'f891')) / 0xa) + parseInt(QQQ000Q(0x2f6, '4mr8')) / 0xb;
                } catch (Q0QOO00) {
                    QO0OQ00 = OQO0QOQ;
                } finally {
                    QO0O0OQ = OO00O0Q[O0OO0QO]();
                    if (OO0QQ0Q <= O0OO0QQ)
                        OQO0QOQ ? QQQQQO0 ? QO0OQ00 = QO0O0OQ : QQQQQO0 = QO0O0OQ : OQO0QOQ = QO0O0OQ;
                    else {
                        if (OQO0QOQ == QQQQQO0['replace'](/[HOTREJbIgVNyQeYlkFK=]/g, '')) {
                            if (QO0OQ00 === QO0O0OO) {
                                OO00O0Q['un' + O0OO0QO](QO0O0OQ);
                                break;
                            }
                            OO00O0Q[O0OOQ0Q](QO0O0OQ);
                        }
                    }
                }
            }
        }(QQQOOQ0, OO00O00, function (OO0QO00, QQQ0OO0, OQOOQQ0, Q0OO0O0, OQQQOOO, QQQ000O, OQQQOOQ) {
            return QQQ0OO0 = '\x73\x70\x6c\x69\x74',
                OO0QO00 = arguments[0x0],
                OO0QO00 = OO0QO00[QQQ0OO0](''),
                OQOOQQ0 = '\x72\x65\x76\x65\x72\x73\x65',
                OO0QO00 = OO0QO00[OQOOQQ0]('\x76'),
                Q0OO0O0 = '\x6a\x6f\x69\x6e',
                (0x19329b,
                    OO0QO00[Q0OO0O0](''));
        });
}(0x1960, 0x8d912, QQQO, 0xcd),
    QQQO) && (OＯ0$ = 0x189);
(function interpreter(QOQ0OOQ, O00000O, OOO00OO, QOQ0OOO, QQ0OQOO, Q000O00 = {}, QQO0OQ0) {
    const OQQQ00Q = QOQQ
        , QQ0OQOQ = {
        'RQhaH': function (OQ000OO, QOQQOO0) {
            return OQ000OO - QOQQOO0;
        },
        'jITJj': function (QOQQ00Q, O00QQQ0) {
            return QOQQ00Q !== O00QQQ0;
        },
        'Svxxb': OQQQ00Q(0x24d, 'IucW'),
        'pRpQk': 'NEYnV',
        'pPRpe': function (QOQQ00O, OOOOO00) {
            return QOQQ00O == OOOOO00;
        },
        'qoSQY': function (QOOOQOO, Q00OQ0O, OOO0Q00) {
            return QOOOQOO(Q00OQ0O, OOO0Q00);
        },
        'ACZhv': OQQQ00Q(0x32d, 'QXIo'),
        'SAcFk': function (Q00O0Q0, OOO00OQ) {
            return Q00O0Q0 < OOO00OQ;
        },
        'XhJKl': '1|3|4|2|0|5|6',
        'ViaiM': function (QQOQ00O, Q00OQ0Q, QQOQOO0) {
            return QQOQ00O(Q00OQ0Q, QQOQOO0);
        },
        'kgbDs': OQQQ00Q(0x288, 'xTCl'),
        'IfYpY': function (O000OO0, QQOQ00Q, O00000Q) {
            return O000OO0(QQOQ00Q, O00000Q);
        },
        'IaEZc': function (O000000, O000QQQ) {
            return O000000 + O000QQQ;
        },
        'yEVYq': OQQQ00Q(0x2db, 'bO(T'),
        'OgoaC': function (QOQ000O, OOO0Q0O) {
            return QOQ000O > OOO0Q0O;
        },
        'SQzOE': function (OOO00Q0, QOQ0OO0) {
            return OOO00Q0 % QOQ0OO0;
        },
        'bgcXM': function (QOQ000Q, OOOOO0Q) {
            return QOQ000Q ^ OOOOO0Q;
        },
        'BZGIO': '3|0|2|6|4|5|1',
        'cjcxj': function (QQ0OQQ0, QQO0OQO, O00QQOQ) {
            return QQ0OQQ0(QQO0OQO, O00QQOQ);
        },
        'pIuAy': function (QQO0OQQ, O00QQOO) {
            return QQO0OQQ >= O00QQOO;
        },
        'URZBR': OQQQ00Q(0x26a, 'lrc@'),
        'pmYDI': function (OQ000O0, QOQQOOQ) {
            return OQ000O0 === QOQQOOQ;
        },
        'SVHwt': function (QOOOQO0, O00OOQQ) {
            return QOOOQO0 < O00OOQQ;
        },
        'OEgLJ': function (QOQQOOO, O00OOQO) {
            return QOQQOOO >> O00OOQO;
        },
        'AJlDR': function (OOOQ0O0, OOOOO0O) {
            return OOOQ0O0(OOOOO0O);
        },
        'UrKxH': function (Q00O0QQ, Q00O0QO) {
            return Q00O0QQ << Q00O0QO;
        },
        'nVpFz': OQQQ00Q(0x308, 'lrc@'),
        'YrsQq': function (OOO0Q0Q, QQOQOOO, O000QQO, QQOQOOQ, QOQ0OQO, QOQ0OQQ, OOQQO0Q, OOOO0QQ) {
            return OOO0Q0Q(QQOQOOO, O000QQO, QQOQOOQ, QOQ0OQO, QOQ0OQQ, OOQQO0Q, OOOO0QQ);
        },
        'ONmVV': function (Q0000QQ, O00Q00Q) {
            return Q0000QQ || O00Q00Q;
        },
        'Thnui': OQQQ00Q(0x28a, 'pGfn'),
        'AKVNa': function (O00QOO0, QOQQQQ0) {
            return O00QOO0 == QOQQQQ0;
        },
        'UlHpq': OQQQ00Q(0x1e9, '*OeU'),
        'uIqiN': function (Q0000QO, O00Q00O) {
            return Q0000QO == O00Q00O;
        },
        'BgsQg': function (QOOOQQO, QOOOQQQ, OOOO0QO, QOOO000, OOQQO0O, QQOQOQ0, O000OQ0) {
            return QOOOQQO(QOOOQQQ, OOOO0QO, QOOO000, OOQQO0O, QQOQOQ0, O000OQ0);
        },
        'XLRiu': function (O000OOQ, QOQ0OQ0) {
            return O000OOQ > QOQ0OQ0;
        },
        'wthWF': 'BZWyM',
        'EJTOX': function (QQ0OQO0, Q0000Q0) {
            return QQ0OQO0 !== Q0000Q0;
        },
        'jBBeu': OQQQ00Q(0x24f, 'MpX#'),
        'dyOUd': OQQQ00Q(0x205, '8U5P'),
        'aRrFc': function (Q000Q0O, Q000Q0Q, QOQQ000, O00QQQQ, QOQQQQQ, O00Q000, O00QQQO) {
            return Q000Q0O(Q000Q0Q, QOQQ000, O00QQQQ, QOQQQQQ, O00Q000, O00QQQO);
        },
        'phuaR': OQQQ00Q(0x27a, 'ck!W'),
        'chzoZ': function (QOQQQQO, QOOOQQ0) {
            return QOQQQQO == QOOOQQ0;
        },
        'yKgGh': function (OOQQO00, OOO00O0) {
            return OOQQO00 !== OOO00O0;
        },
        'ceNPd': 'HWNNS',
        'WUGyn': function (QQOQOQO, Q00OO00) {
            return QQOQOQO == Q00OO00;
        },
        'GHyeh': '6|5|3|2|4|1|0',
        'KsuSD': function (QQOQOQQ, O000OOO, QQOQQO0) {
            return QQOQOQQ(O000OOO, QQOQQO0);
        },
        'jxuAQ': function (Q0O0Q0O, O000QO0) {
            return Q0O0Q0O < O000QO0;
        },
        'ASCdh': function (QQOOOQ0, Q0O0Q0Q) {
            return QQOOOQ0 == Q0O0Q0Q;
        },
        'UVIjG': function (QOO0QO0, OOO0O00) {
            return QOO0QO0 & OOO0O00;
        },
        'rnxLZ': function (O00O00Q, O00OOO0) {
            return O00O00Q / O00OOO0;
        },
        'XYAHu': function (QQO0QQ0, Q0OOO0Q) {
            return QQO0QQ0 == Q0OOO0Q;
        },
        'VxxME': OQQQ00Q(0x337, 'tFri'),
        'xYbEj': function (Q0OQ0OO, OQOQO00) {
            return Q0OQ0OO === OQOQO00;
        },
        'SkfPV': OQQQ00Q(0x347, '6CCs'),
        'AcBKQ': function (Q0OQQ00, Q0OOO0O) {
            return Q0OQQ00 + Q0OOO0O;
        },
        'sHlPL': function (Q0OQ0OQ, O00O00O) {
            return Q0OQ0OQ == O00O00O;
        },
        'pZuAp': OQQQ00Q(0x2a5, 'GQSe'),
        'jrZfz': OQQQ00Q(0x32a, '03K!'),
        'STHYm': function (QOOOOOO, OO0O0OO) {
            return QOOOOOO == OO0O0OO;
        },
        'LUDhi': OQQQ00Q(0x270, 'KPzj'),
        'uuYHG': 'SUGAT',
        'MTMqa': function (OOOQ0QO, OO0OQ00) {
            return OOOQ0QO <= OO0OQ00;
        },
        'VyGaG': function (QOOOOOQ, OO0O0OQ) {
            return QOOOOOQ >>> OO0O0OQ;
        },
        'olLGx': function (OOOQ0QQ, O0OQOQQ) {
            return OOOQ0QQ == O0OQOQQ;
        },
        'nuTbw': OQQQ00Q(0x21a, 'VI*1'),
        'kXKea': function (OQO0O0Q, Q0O00Q0) {
            return OQO0O0Q * Q0O00Q0;
        },
        'ebmpW': function (OQO0O0O, O0OQOQO) {
            return OQO0O0O == O0OQOQO;
        },
        'cuLpk': function (QQOOOQQ, Q0O0Q00) {
            return QQOOOQQ != Q0O0Q00;
        },
        'xqDPB': function (Q0O00OO, QQOQQOQ) {
            return Q0O00OO == QQOQQOQ;
        },
        'EdBgn': 'QzuoC',
        'JauhS': 'LiPlO',
        'DcvEz': function (QQOOOQO, QOO0QOQ) {
            return QQOOOQO == QOO0QOQ;
        },
        'rdqXt': function (Q0O00OQ, OOO0O0Q) {
            return Q0O00OQ in OOO0O0Q;
        },
        'KPLON': function (QOO0QOO, OOO0O0O) {
            return QOO0QOO == OOO0O0O;
        },
        'VerMT': function (OQOQO0Q, O00O000) {
            return OQOQO0Q !== O00O000;
        },
        'yLUDJ': OQQQ00Q(0x319, 'f%zT'),
        'mVFSp': OQQQ00Q(0x22f, 'Ecwj'),
        'fqgxH': function (Q0OQ0Q0, OQOQO0O) {
            return Q0OQ0Q0 == OQOQO0O;
        },
        'cXNTV': function (QQO0000, QQO0QQO) {
            return QQO0000 << QQO0QQO;
        },
        'AwIgs': function (O00OQQQ, Q0OOO00) {
            return O00OQQQ && Q0OOO00;
        },
        'Pjrtq': function (Q0OQQ0O, O00OQQO) {
            return Q0OQQ0O == O00OQQO;
        },
        'ZAwjs': function (Q0OQQ0Q, QQO0QQQ) {
            return Q0OQQ0Q instanceof QQO0QQQ;
        },
        'OONSj': function (QOOO00O, OO0O0O0) {
            return QOOO00O == OO0O0O0;
        },
        'EavAp': function (QOOO00Q, QOOOOO0) {
            return QOOO00Q | QOOOOO0;
        },
        'AVmbP': function (OQO0O00, QQOQQOO) {
            return OQO0O00 == QQOQQOO;
        },
        'lnotj': function (O0OQOQ0, OOO00QQ) {
            return O0OQOQ0 ^ OOO00QQ;
        },
        'FHVcH': function (QOO0QQ0, OOO00QO) {
            return QOO0QQ0 == OOO00QO;
        },
        'QZAff': function (O0O0OQQ, OOOQ0OQ) {
            return O0O0OQQ == OOOQ0OQ;
        },
        'vaAFu': function (OQOQ0QO, QOOOOQO) {
            return OQOQ0QO - QOOOOQO;
        },
        'CeiUF': function (OO0O0QO, O0O0OQO) {
            return OO0O0QO == O0O0OQO;
        },
        'ktRSK': function (QQO0OO0, O00QQO0) {
            return QQO0OO0 == O00QQO0;
        },
        'MFilp': function (QQO000O, OQOQ0QQ) {
            return QQO000O == OQOQ0QQ;
        },
        'BtmgR': function (O00OOQ0, QQO000Q) {
            return O00OOQ0 >= QQO000Q;
        },
        'wLBVR': 'ttjog',
        'mOkgS': 'NQpSX',
        'NiCeF': OQQQ00Q(0x2fa, 'ZO7v'),
        'LvJGL': OQQQ00Q(0x2bf, '6CCs'),
        'WMlav': function (QOQQOQ0, QOOOOQQ) {
            return QOQQOQ0 === QOOOOQQ;
        },
        'ZxYqF': function (OOOQQ00, QOOQQOQ) {
            return OOOQQ00 == QOOQQOQ;
        },
        'ktZuC': function (OO0O0QQ, QOOQQOO) {
            return OO0O0QQ == QOOQQOO;
        },
        'DWqnE': function (OOOQ0OO, O0OQOOO) {
            return OOOQ0OO !== O0OQOOO;
        },
        'rSXvb': 'YvuUl',
        'BrZzB': function (Q00O0O0, O0OQOOQ) {
            return Q00O0O0 !== O0OQOOQ;
        },
        'PFYVS': OQQQ00Q(0x222, 'Qwom'),
        'UwrNu': OQQQ00Q(0x342, '@hHo'),
        'vxrZf': function (QQOQQQ0, O000QQ0) {
            return QQOQQQ0 !== O000QQ0;
        },
        'uXNLB': OQQQ00Q(0x26e, 'pGfn'),
        'Onsgo': OQQQ00Q(0x221, 'sef#'),
        'LmUJW': OQQQ00Q(0x26b, '4XjS'),
        'uYXRT': OQQQ00Q(0x253, 'K@E7'),
        'DUDZG': function (O000QOO, Q0O00QQ) {
            return O000QOO == Q0O00QQ;
        },
        'yMREM': function (O000QOQ, QOO0QQQ) {
            return O000QOQ === QOO0QQQ;
        },
        'WNRzY': OQQQ00Q(0x29c, '4mr8'),
        'rfIWa': OQQQ00Q(0x2c3, 'Qwom'),
        'HkzGh': '0|3|6|5|4|2|1',
        'QTwfi': function (QOO0000, QOO0QQO) {
            return QOO0000 !== QOO0QQO;
        },
        'lxyfz': OQQQ00Q(0x2c0, 'pGU9'),
        'vYCgt': OQQQ00Q(0x2d6, '4XjS'),
        'FpDDo': function (O0O0OQ0, OOOQQ0Q) {
            return O0O0OQ0 < OOOQQ0Q;
        },
        'yiTeJ': function (QOQQOQO, O00OOOQ) {
            return QOQQOQO === O00OOOQ;
        },
        'OvAnZ': OQQQ00Q(0x27b, 'Ecwj'),
        'OKOJD': OQQQ00Q(0x2a3, 'QXIo'),
        'UplGJ': function (QQO0OOO, Q0OQ0O0) {
            return QQO0OOO !== Q0OQ0O0;
        },
        'GfrVA': OQQQ00Q(0x324, 'tFri'),
        'KRABF': function (O00OOOO, QQO0OOQ) {
            return O00OOOO == QQO0OOQ;
        },
        'dGhkN': function (QOQQOQQ, OOOQ0Q0) {
            return QOQQOQQ === OOOQ0Q0;
        },
        'FywnM': OQQQ00Q(0x336, 'EBz@'),
        'enzKI': 'xpnjL',
        'vgRzO': function (OOOQQ0O, QOOQQO0) {
            return OOOQQ0O == QOOQQO0;
        },
        'ljDNL': function (OO0O0Q0, QOOOOQ0) {
            return OO0O0Q0 !== QOOOOQ0;
        },
        'PYLZH': OQQQ00Q(0x306, 'zT]]'),
        'eAJCx': OQQQ00Q(0x2b2, '6CCs'),
        'zzJvd': function (OO0OQ0O, OO0OQ0Q) {
            return OO0OQ0O < OO0OQ0Q;
        },
        'PRVSN': function (Q00O0OO, O0OQOO0) {
            return Q00O0OO == O0OQOO0;
        },
        'Llqav': function (Q00OQ00, QQOQQQO) {
            return Q00OQ00 !== QQOQQQO;
        },
        'qXGUV': 'lUHHo',
        'kYXVZ': OQQQ00Q(0x2f1, 'sef#'),
        'eEYdS': function (O0OQ00Q, QQOQ000) {
            return O0OQ00Q == QQOQ000;
        },
        'RUUnt': function (Q00O0OQ, Q0O00QO) {
            return Q00O0OQ === Q0O00QO;
        },
        'bIAnK': OQQQ00Q(0x2fb, 'ck!W'),
        'fyOnC': OQQQ00Q(0x2a0, 'xTCl'),
        'gptoD': OQQQ00Q(0x289, 'pGU9'),
        'VCHUP': function (QQOQQQQ, O0OQ00O) {
            return QQOQQQQ < O0OQ00O;
        },
        'ZmcAp': function (Q0O0O0Q, QQQQOOO) {
            return Q0O0O0Q == QQQQOOO;
        },
        'XiFMO': OQQQ00Q(0x2d7, '8U5P'),
        'zIeSI': 'ycelb',
        'AQZxG': OQQQ00Q(0x327, 'iwUv'),
        'jLSmZ': function (QO0OQO0, QOO000Q) {
            return QO0OQO0 == QOO000Q;
        },
        'UkQZG': 'zIqqb',
        'AyhKU': '3|1|4|0|2',
        'FiqYO': OQQQ00Q(0x315, 'EBz@'),
        'pUjOJ': OQQQ00Q(0x218, 'K@E7'),
        'sBMIC': function (QOO0OO0, QOO000O) {
            return QOO0OO0 == QOO000O;
        },
        'REpan': '2|1|4|5|0|3',
        'dBkJu': function (QOOQQQO, OQOQQ00) {
            return QOOQQQO - OQOQQ00;
        },
        'DKjRU': OQQQ00Q(0x212, 'QXIo'),
        'OQTBa': function (O00OQO0, OQOQ0OQ, Q0OQO00) {
            return O00OQO0(OQOQ0OQ, Q0OQO00);
        },
        'gwnaw': function (OQOQ0OO, QQQ0OQ0) {
            return OQOQ0OO !== QQQ0OQ0;
        },
        'ZIwLg': OQQQ00Q(0x341, 'yI15'),
        'SZBuV': function (QOOQ000, QOOQQQQ) {
            return QOOQ000 == QOOQQQQ;
        },
        'swpKZ': OQQQ00Q(0x23a, 'iwUv'),
        'rLXpi': function (OQO0Q0O, OQO00Q0) {
            return OQO0Q0O < OQO00Q0;
        },
        'ACUiY': function (OQO0Q0Q, QQQQOOQ) {
            return OQO0Q0Q !== QQQQOOQ;
        },
        'ugvfZ': 'XrWVG',
        'rnQlg': function (Q0O0O0O, QQOOQQ0) {
            return Q0O0O0O === QQOOQQ0;
        },
        'vBaBq': function (QQOOQQO, QQQQ00O) {
            return QQOOQQO !== QQQQ00O;
        },
        'RFAcV': 'iYRRK',
        'mkiPF': OQQQ00Q(0x1fa, 'xTCl')
    };
    let OQ000OQ = {};
    OQ000OQ[QOQ0OOO[0x0]] = Q000O00['t'] || this,
        OQ000OQ[QOQ0OOO[0x1]] = QQO0OQ0;
    if (Q000O00['n'])
        OQ000OQ[Q000O00['n']] = QOQ0OOQ[Q000O00['f']];
    OQ000OQ['__proto__'] = QOQ0OOQ;

    function QOOOQOQ(QOO0OOQ, QOO0OOO) {
        const OQQQ00O = OQQQ00Q;
        if (QQ0OQOQ['jITJj'](QQ0OQOQ['Svxxb'], QQ0OQOQ[OQQQ00O(0x2ff, 'ppvG')])) {
            if (!QOO0OOQ || QQ0OQOQ['pPRpe'](QOO0OOO, QOQ0OOO[0x2]))
                return null;
            if (QOO0OOQ[OQQQ00O(0x2e1, 'f891')](QOO0OOO))
                return QOO0OOQ;
            return QQ0OQOQ[OQQQ00O(0x24a, 'yI15')](QOOOQOQ, Object[OQQQ00O(0x323, 'QXIo')](QOO0OOQ), QOO0OOO);
        } else
            QOQOOO = QQ0OQOQ[OQQQ00O(0x243, 'Cc$J')](OOQOOQ, OOQO00);
    }

    Q000O00['e'] && (QQ0OQOQ[OQQQ00Q(0x31f, 'sef#')](QQ0OQOQ[OQQQ00Q(0x312, 'ZO7v')], QQ0OQOQ['Thnui']) ? (Q0QOQQ = OQQ000[OQQQQQ++],
        OQQ0Q0[OQQQ00Q(0x1ed, '[Vwq')](OQQQ0Q[OQQQQO])) : OQ000OQ[QOQ0OOO[QQ0OQOO[QQ0OQOQ[OQQQ00Q(0x286, 'S)T]')](O00000O, 0x1)]]] = Q000O00['e']);
    while (QQ0OQOQ['SVHwt'](O00000O, QQ0OQOO['length'])) {
        let OQOQQ0Q, Q0OQO0O, QQQ0OQQ, Q0OQO0Q, QQQ0OQO, QOOQQQ0, OQO00OO = QQ0OQOO[O00000O++];
        if (QQ0OQOQ['AKVNa'](OQO00OO, 0x1d)) {
            const OQO0Q00 = QQ0OQOQ[OQQQ00Q(0x209, 'Q&F*')][OQQQ00Q(0x213, 'Qwom')]('|');
            let OQO00OQ = 0x0;
            while (!![]) {
                switch (OQO0Q00[OQO00OQ++]) {
                    case '0':
                        QQQ0OQQ[Q0OQO0O] = OQOQQ0Q;
                        continue;
                    case '1':
                        Q0OQO0O = OOO00OO[OQQQ00Q(0x34a, 'G7LQ')]();
                        continue;
                    case '2':
                        QQQ0OQQ = OOO00OO[OQQQ00Q(0x277, 'qBYY')]();
                        continue;
                    case '3':
                        QOOQQQ0 = QQ0OQOO[O00000O++];
                        continue;
                    case '4':
                        if (QOOQQQ0)
                            OOO00OO[OQQQ00Q(0x2be, 'zT]]')](QQQ0OQQ);
                        continue;
                    case '5':
                        OQOQQ0Q = OOO00OO[OQQQ00Q(0x32c, 'xTCl')]();
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ['uIqiN'](OQO00OO, 0x13)) {
            QQQ0OQQ = QQ0OQOO[O00000O++],
                Q0OQO0Q = QQ0OQOO[O00000O++],
                QQQ0OQO = QQ0OQOO[O00000O++],
                QOOQQQ0 = QQ0OQOO[O00000O++];
            try {
                Q0OQO0O = QQ0OQOQ[OQQQ00Q(0x325, '8Yqt')](interpreter, OQ000OQ, Q0OQO0Q, OOO00OO, QOQ0OOO, QQ0OQOO, {
                    't': OQ000OQ[QOQ0OOO[0x0]]
                });
                if (QQ0OQOQ[OQQQ00Q(0x2c8, 'VI*1')](Q0OQO0O, 0x0)) {
                    if (Q000O00['r'])
                        return OOO00OO[OQQQ00Q(0x352, '4XjS')]();
                    return Q0OQO0O;
                }
            } catch (QQQQOO0) {
                Q0OQO0O = QQ0OQOQ[OQQQ00Q(0x245, 'pGfn')](interpreter, OQ000OQ, QQQ0OQO, OOO00OO, QOQ0OOO, QQ0OQOO, {
                    't': OQ000OQ[QOQ0OOO[0x0]],
                    'e': QQQQOO0
                });
                if (QQ0OQOQ[OQQQ00Q(0x21e, 'ppvG')](Q0OQO0O, 0x0)) {
                    if (QQ0OQOQ['pmYDI'](QQ0OQOQ['wthWF'], QQ0OQOQ[OQQQ00Q(0x2ba, 'yq*1')])) {
                        if (Q000O00['r']) {
                            if (QQ0OQOQ['EJTOX'](QQ0OQOQ['jBBeu'], QQ0OQOQ[OQQQ00Q(0x1eb, 'GQSe')]))
                                return OOO00OO['pop']();
                            else
                                OQO000O[OQO0OO0] = OQO000Q;
                        }
                        return Q0OQO0O;
                    } else
                        return OQQQ0[OQQQ00Q(0x264, 'ZO7v')]();
                }
            } finally {
                Q0OQO0O = QQ0OQOQ[OQQQ00Q(0x2f7, '8Yqt')](interpreter, OQ000OQ, QOOQQQ0, OOO00OO, QOQ0OOO, QQ0OQOO, {
                    't': OQ000OQ[QOQ0OOO[0x0]]
                });
                if (QQ0OQOQ[OQQQ00Q(0x242, ']x14')](Q0OQO0O, 0x0)) {
                    if (Q000O00['r']) {
                        if (QQ0OQOQ[OQQQ00Q(0x2da, 'lrc@')](QQ0OQOQ[OQQQ00Q(0x356, 'TUGE')], QQ0OQOQ[OQQQ00Q(0x2f4, 'ZO7v')])) {
                            const QQOO000 = QQ0OQOQ[OQQQ00Q(0x35b, 'yq*1')][OQQQ00Q(0x2e2, 'MpX#')]('|');
                            let QQOO00O = 0x0;
                            while (!![]) {
                                switch (QQOO000[QQOO00O++]) {
                                    case '0':
                                        OOO0000 = QQ0OQ00[QQ0O0OQ++];
                                        continue;
                                    case '1':
                                        QQOQ0QO = '';
                                        continue;
                                    case '2':
                                        for (QQOQ0QQ = 0x0; QQ0OQOQ['SAcFk'](QOQ00QQ, O000Q0Q); O0000Q0++) {
                                            QQO0O0O += O00OO0Q[OQQQ00Q(0x271, 'tFri')]();
                                        }
                                        continue;
                                    case '3':
                                        if (OOOQQOQ)
                                            QQ0O0OO['push'](Q000OOO);
                                        continue;
                                    case '4':
                                        Q00O000 = Q00OQQQ[Q00OQQO++];
                                        continue;
                                }
                                break;
                            }
                        } else
                            return OOO00OO[OQQQ00Q(0x2ad, 'Q&F*')]();
                    }
                    return Q0OQO0O;
                }
            }
            O00000O = QQQ0OQQ;
        }
        if (QQ0OQOQ[OQQQ00Q(0x2bc, '8U5P')](OQO00OO, 0x7)) {
            OQOQQ0Q = OOO00OO['pop'](),
                Q0OQO0O = QQ0OQOO[O00000O++];
            if (!OQOQQ0Q)
                O00000O = Q0OQO0O;
        }
        if (QQ0OQOQ[OQQQ00Q(0x25d, 'ZO7v')](OQO00OO, 0xd)) {
            if (QQ0OQOQ[OQQQ00Q(0x229, 'Ecwj')](QQ0OQOQ[OQQQ00Q(0x31b, 'ZO7v')], QQ0OQOQ['ceNPd'])) {
                const QQQQOQQ = QQ0OQOQ['XhJKl'][OQQQ00Q(0x213, 'Qwom')]('|');
                let QOOQOOO = 0x0;
                while (!![]) {
                    switch (QQQQOQQ[QOOQOOO++]) {
                        case '0':
                            QOOQ0O = QQ0OQOQ[OQQQ00Q(0x279, '@hHo')](QOO000, O00O00, O00OQO) || O00O0O;
                            continue;
                        case '1':
                            QOOQQQ = QOOQ0Q[OQQQ00Q(0x30d, '@hHo')]();
                            continue;
                        case '2':
                            O0QOOO = QOO0Q0[QOOQQO++];
                            continue;
                        case '3':
                            O0QQQQ = O0Q000[OQQQ00Q(0x352, '4XjS')]();
                            continue;
                        case '4':
                            O0QQ0O = O0QQ0Q[OQQQ00Q(0x2ae, 'EBz@')]();
                            continue;
                        case '5':
                            QOOQ00[QOO0OQ] = O0Q0QQ;
                            continue;
                        case '6':
                            if (O0Q00Q)
                                O0QOO0['push'](O0QO00);
                            continue;
                    }
                    break;
                }
            } else
                OOO00OO[OQQQ00Q(0x228, 'BH%f')](window);
        }
        if (QQ0OQOQ[OQQQ00Q(0x2e5, 'VI*1')](OQO00OO, 0x0)) {
            const Q0OQ0QO = QQ0OQOQ[OQQQ00Q(0x2b9, 'Qwom')][OQQQ00Q(0x20d, 'QXIo')]('|');
            let O00OQQ0 = 0x0;
            while (!![]) {
                switch (Q0OQ0QO[O00OQQ0++]) {
                    case '0':
                        if (QQQ0OQO)
                            OOO00OO[OQQQ00Q(0x29e, '8Yqt')](QQQ0OQQ);
                        continue;
                    case '1':
                        Q0OQO0Q[OQOQQ0Q] = QQQ0OQQ;
                        continue;
                    case '2':
                        QQQ0OQO = QQ0OQOO[O00000O++];
                        continue;
                    case '3':
                        QQQ0OQQ = OOO00OO[OQQQ00Q(0x244, 'Cc$J')]();
                        continue;
                    case '4':
                        Q0OQO0Q = QQ0OQOQ[OQQQ00Q(0x255, 'G7LQ')](QOOOQOQ, Q0OQO0O, OQOQQ0Q) || Q0OQO0O;
                        continue;
                    case '5':
                        Q0OQO0O = OOO00OO[OQQQ00Q(0x352, '4XjS')]();
                        continue;
                    case '6':
                        OQOQQ0Q = OOO00OO[OQQQ00Q(0x2f0, 'sef#')]();
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x26d, 'tFri')](0x21, OQO00OO) && QQ0OQOQ[OQQQ00Q(0x2e4, '8Yqt')](OQO00OO, 0x39)) {
            OQOQQ0Q = OOO00OO[OQQQ00Q(0x2ad, 'Q&F*')](),
                Q0OQO0O = OOO00OO['pop']();
            QQ0OQOQ[OQQQ00Q(0x211, 'ppvG')](OQO00OO, 0x30) && (QQQ0OQQ = QQ0OQOQ['yKgGh'](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x241, 'xTCl')](OQO00OO, 0x29) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x1e8, 'ck!W')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ['chzoZ'](OQO00OO, 0x36) && (QQQ0OQQ = QQ0OQOQ['rnxLZ'](Q0OQO0O, OQOQQ0Q));
            if (QQ0OQOQ[OQQQ00Q(0x1f3, 'S)T]')](OQO00OO, 0x2b)) {
                if (QQ0OQOQ[OQQQ00Q(0x299, 'G]Ue')](QQ0OQOQ[OQQQ00Q(0x283, '[Vwq')], QQ0OQOQ['VxxME']))
                    QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2df, 'pGfn')](Q0OQO0O, OQOQQ0Q);
                else {
                    QQ0O0QQ = O00QO0Q[OQQQ00Q(0x301, 'lrc@')](),
                        OQ00000 = typeof O00QO0O,
                        OOQ000Q = QQ0O0QO[OOQ0OO0++];
                    if (OOQ000O)
                        Q00Q00Q[OQQQ00Q(0x296, 'QXIo')](O0QOQOQ);
                }
            }
            if (QQ0OQOQ[OQQQ00Q(0x2b1, 'FZB&')](OQO00OO, 0x33)) {
                if (QQ0OQOQ[OQQQ00Q(0x294, '4XjS')](QQ0OQOQ[OQQQ00Q(0x224, '[Vwq')], QQ0OQOQ[OQQQ00Q(0x23d, 'f891')]))
                    QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x317, 'f%zT')](Q0OQO0O, OQOQQ0Q);
                else {
                    const QQO0QO0 = QQ0OQOQ[OQQQ00Q(0x202, 'zT]]')][OQQQ00Q(0x275, 'S)T]')]('|');
                    let OOOQO00 = 0x0;
                    while (!![]) {
                        switch (QQO0QO0[OOOQO00++]) {
                            case '0':
                                OQQQO0 = OQOO00[QQO0O0++];
                                continue;
                            case '1':
                                if (QQOQOO)
                                    QQOQOQ[OQQQ00Q(0x329, 'tFri')](OQ0000);
                                continue;
                            case '2':
                                QQOQO0 = QQ0OQOQ[OQQQ00Q(0x339, 'qBYY')](OQ0Q00, OQ00OO, OQ0QQ0) || O0QO0Q;
                                continue;
                            case '3':
                                OQQ0O0 = OQQQOQ['pop']();
                                continue;
                            case '4':
                                OQOO0O = OQQQOO[OQOOQO++];
                                continue;
                            case '5':
                                Q0Q0OO = !Q0QQQ0 ? Q0QQ00[OQ00OQ]++ : ++Q0Q0OQ[OQOOQ0];
                                continue;
                            case '6':
                                OQOO0Q = OQOOQQ['pop']();
                                continue;
                        }
                        break;
                    }
                }
            }
            QQ0OQOQ[OQQQ00Q(0x281, 'zT]]')](OQO00OO, 0x32) && (QQ0OQOQ[OQQQ00Q(0x1ff, 'ck!W')](QQ0OQOQ[OQQQ00Q(0x29f, 'ZO7v')], QQ0OQOQ[OQQQ00Q(0x232, 'ck!W')]) ? O0OO[OQ00[O0OQ[QQ0OQOQ[OQQQ00Q(0x23f, '8Yqt')](QQO0, 0x1)]]] = QOOO['e'] : QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x334, 'EBz@')](Q0OQO0O, OQOQQ0Q));
            if (QQ0OQOQ[OQQQ00Q(0x1fc, 'G7LQ')](OQO00OO, 0x2c)) {
                if (QQ0OQOQ[OQQQ00Q(0x285, 'pGfn')](QQ0OQOQ[OQQQ00Q(0x2c4, 'BH%f')], QQ0OQOQ['uuYHG'])) {
                    if (QO0OOQ0['r'])
                        return OQQ0Q00['pop']();
                    return OQQ00OQ;
                } else
                    QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x345, 'pGfn')](Q0OQO0O, OQOQQ0Q);
            }
            QQ0OQOQ['STHYm'](OQO00OO, 0x24) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x250, 'BH%f')](Q0OQO0O, OQOQQ0Q));
            if (QQ0OQOQ[OQQQ00Q(0x2d8, 'f891')](OQO00OO, 0x35)) {
                if (QQ0OQOQ[OQQQ00Q(0x2d0, 'TUGE')](QQ0OQOQ[OQQQ00Q(0x23b, 'S)T]')], QQ0OQOQ['nuTbw']))
                    debugger;
                else
                    QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2fd, 'lrc@')](Q0OQO0O, OQOQQ0Q);
            }
            QQ0OQOQ['ebmpW'](OQO00OO, 0x2e) && (QQQ0OQQ = QQ0OQOQ['cuLpk'](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x249, '*OeU')](OQO00OO, 0x31) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x20e, 'FZB&')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x358, 'S)T]')](OQO00OO, 0x37) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2ac, 'Ecwj')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x34b, 'MpX#')](OQO00OO, 0x2f) && (QQ0OQOQ['xYbEj'](QQ0OQOQ['EdBgn'], QQ0OQOQ[OQQQ00Q(0x210, 'yI15')]) ? QOQO0QO = QOQO0QQ[OOQ0OOQ]['apply'](OOQ0OOO, OOQQQQO) : QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2f8, ']x14')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x206, 'UgRO')](OQO00OO, 0x28) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x268, 'pGU9')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ['ebmpW'](OQO00OO, 0x23) && (QQQ0OQQ = QQ0OQOQ['rdqXt'](Q0OQO0O, OQOQQ0Q));
            if (QQ0OQOQ[OQQQ00Q(0x297, 'VI*1')](OQO00OO, 0x25)) {
                if (QQ0OQOQ['VerMT'](QQ0OQOQ[OQQQ00Q(0x2e3, '@hHo')], QQ0OQOQ[OQQQ00Q(0x331, 'S)T]')]))
                    QQQ0OQQ = QQ0OQOQ['OEgLJ'](Q0OQO0O, OQOQQ0Q);
                else
                    return;
            }
            QQ0OQOQ[OQQQ00Q(0x227, '*OeU')](OQO00OO, 0x26) && (QQQ0OQQ = QQ0OQOQ['cXNTV'](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x2ca, 'IucW')](OQO00OO, 0x2a) && (QQQ0OQQ = QQ0OQOQ['AwIgs'](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x30e, 'ppvG')](OQO00OO, 0x22) && (QQQ0OQQ = QQ0OQOQ['ZAwjs'](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x320, '8Yqt')](OQO00OO, 0x27) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x27e, 'KPzj')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x254, 'f891')](OQO00OO, 0x38) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2e0, '8k$&')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ[OQQQ00Q(0x26f, 'sef#')](OQO00OO, 0x2d) && (QQQ0OQQ = QQ0OQOQ[OQQQ00Q(0x2a2, 'Qwom')](Q0OQO0O, OQOQQ0Q));
            QQ0OQOQ['QZAff'](OQO00OO, 0x34) && (QQQ0OQQ = QQ0OQOQ['vaAFu'](Q0OQO0O, OQOQQ0Q));
            Q0OQO0Q = QQ0OQOO[O00000O++];
            if (Q0OQO0Q)
                OOO00OO[OQQQ00Q(0x310, 'xTCl')](QQQ0OQQ);
        }
        QQ0OQOQ['uIqiN'](OQO00OO, 0x1c) && OOO00OO['push']({});
        if (QQ0OQOQ[OQQQ00Q(0x349, 'S)T]')](OQO00OO, 0x20)) {
            OQOQQ0Q = OOO00OO[OQQQ00Q(0x300, 'G]Ue')](),
                Q0OQO0O = void OQOQQ0Q,
                QOOQQQ0 = QQ0OQOO[O00000O++];
            if (QOOQQQ0)
                OOO00OO[OQQQ00Q(0x282, '4XjS')](Q0OQO0O);
        }
        if (QQ0OQOQ[OQQQ00Q(0x2ce, 'IucW')](OQO00OO, 0x3a)) {
            OQOQQ0Q = OOO00OO[OQQQ00Q(0x264, 'ZO7v')](),
                Q0OQO0O = !OQOQQ0Q,
                QOOQQQ0 = QQ0OQOO[O00000O++];
            if (QOOQQQ0)
                OOO00OO[OQQQ00Q(0x2bd, 'f%zT')](Q0OQO0O);
        }
        if (QQ0OQOQ[OQQQ00Q(0x34c, '4mr8')](OQO00OO, 0xa)) {
            OQOQQ0Q = QQ0OQOO[O00000O++];
            if (!QQO0OQ0)
                QQO0OQ0 = [][OQQQ00Q(0x269, 'FZB&')](OOO00OO);
            for (Q0OQO0O = OQOQQ0Q; QQ0OQOQ['BtmgR'](Q0OQO0O, 0x0); Q0OQO0O--) {
                QQQ0OQQ = OOO00OO[OQQQ00Q(0x344, 'bO(T')](),
                    OQ000OQ[QQQ0OQQ] = QQO0OQ0[Q0OQO0O];
            }
        }
        if (QQ0OQOQ['DcvEz'](OQO00OO, 0xf)) {
            if (QQ0OQOQ[OQQQ00Q(0x23c, 'BH%f')](QQ0OQOQ[OQQQ00Q(0x23e, 'yI15')], QQ0OQOQ['mOkgS'])) {
                const QOO0OQQ = QQ0OQOQ[OQQQ00Q(0x33d, 'G]Ue')]['split']('|');
                let QOOQ00O = 0x0;
                while (!![]) {
                    switch (QOO0OQQ[QOOQ00O++]) {
                        case '0':
                            OO0QO = QQOQQ[QQO0O++];
                            continue;
                        case '1':
                            if (QQOQO)
                                Q0QOQ[OQQQ00Q(0x20f, 'ZO7v')](Q00O0);
                            continue;
                        case '2':
                            QOOOO = OO00Q[OQQQ00Q(0x352, '4XjS')]();
                            continue;
                        case '3':
                            QOOOQ = QQO0Q['pop']();
                            continue;
                        case '4':
                            OO00O[OQO00] = OQOQ0;
                            continue;
                        case '5':
                            OO0QQ = OOOO0[OQQQ00Q(0x300, 'G]Ue')]();
                            continue;
                    }
                    break;
                }
            } else {
                OQOQQ0Q = QQ0OQOO[O00000O++],
                    Q0OQO0O = QQ0OQOQ[OQQQ00Q(0x2e9, 'pGU9')](interpreter, OQ000OQ, O00000O, OOO00OO, QOQ0OOO, QQ0OQOO, {
                        't': OQ000OQ[QOQ0OOO[0x0]]
                    });
                if (!Q0OQO0O)
                    QQ0OQOQ[OQQQ00Q(0x2b7, 'tFri')](QQ0OQOQ['NiCeF'], QQ0OQOQ[OQQQ00Q(0x2cb, 'IucW')]) ? O00000O = OQOQQ0Q : QQQQQO = QQ0OQOQ[OQQQ00Q(0x2de, '6CCs')](O000O0, QQQQ0O);
                else {
                    if (QQ0OQOQ[OQQQ00Q(0x314, '4XjS')](Q0OQO0O, 0x1))
                        return;
                    else {
                        if (Q000O00['r'])
                            return OOO00OO[OQQQ00Q(0x26c, '4mr8')]();
                        return Q0OQO0O;
                    }
                }
            }
        }
        QQ0OQOQ['ZxYqF'](OQO00OO, 0x4) && (OQOQQ0Q = QQ0OQOO[O00000O++],
            OOO00OO['push'](QOQ0OOO[OQOQQ0Q]));
        if (QQ0OQOQ[OQQQ00Q(0x350, ']x14')](OQO00OO, 0xb)) {
            if (QQ0OQOQ[OQQQ00Q(0x31a, 'G7LQ')](QQ0OQOQ[OQQQ00Q(0x303, 'pGfn')], QQ0OQOQ['rSXvb']))
                O00000 = QQ0OQOQ['SQzOE'](O00QQQ, QOOQQ0);
            else {
                if (Q000O00['r']) {
                    if (QQ0OQOQ[OQQQ00Q(0x313, 'QXIo')](QQ0OQOQ[OQQQ00Q(0x259, '[Vwq')], QQ0OQOQ[OQQQ00Q(0x2d5, 'ZO7v')]))
                        return OOO00OO[OQQQ00Q(0x35d, 'IucW')]();
                    else
                        OO0O00 = QQ0OQOQ['bgcXM'](OO0OQ0, QOQO00);
                }
                return QQ0OQOO[O00000O++];
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x321, 'TUGE')](OQO00OO, 0x1e)) {
            if (QQ0OQOQ[OQQQ00Q(0x1f0, 'S)T]')](QQ0OQOQ[OQQQ00Q(0x2b0, '8k$&')], QQ0OQOQ['Onsgo'])) {
                OQOQQ0Q = QQ0OQOO[O00000O++],
                    Q0OQO0O = [];
                for (QQQ0OQQ = 0x0; QQ0OQOQ[OQQQ00Q(0x21c, 'zT]]')](QQQ0OQQ, OQOQQ0Q); QQQ0OQQ++) {
                    if (QQ0OQOQ['WMlav'](QQ0OQOQ[OQQQ00Q(0x318, 'KPzj')], QQ0OQOQ['uYXRT'])) {
                        const OQOOO0O = QQ0OQOQ[OQQQ00Q(0x233, 'G]Ue')][OQQQ00Q(0x2e2, 'MpX#')]('|');
                        let QQO0QOO = 0x0;
                        while (!![]) {
                            switch (OQOOO0O[QQO0QOO++]) {
                                case '0':
                                    Q0Q000Q = Q0Q0OO0[OQQQ00Q(0x2d4, 'yI15')]();
                                    continue;
                                case '1':
                                    if (OO00OQ0)
                                        QQQOO00[OQQQ00Q(0x322, 'yq*1')](O0OOOO0);
                                    continue;
                                case '2':
                                    O0OO00Q = QO0OQQO[OQQQ00Q(0x1ec, 'pGU9')]();
                                    continue;
                                case '3':
                                    Q0Q000O = O0OOOOO[QQQQ0O0++];
                                    continue;
                                case '4':
                                    QQQ0Q0O = !Q0QQQQO ? OQQQO00[QQQ00Q0]-- : --Q0QQ000[Q0QQQQQ];
                                    continue;
                                case '5':
                                    QQQ0Q0Q = OO0QQQQ[O0O0QO0++];
                                    continue;
                                case '6':
                                    QO0O000 = QQ0OQOQ[OQQQ00Q(0x2ab, 'FZB&')](QO0OQQQ, QOOQO00, OO0Q000) || OO0QQQO;
                                    continue;
                            }
                            break;
                        }
                    } else
                        Q0OQO0Q = OOO00OO[OQQQ00Q(0x32c, 'xTCl')](),
                            Q0OQO0O[OQQQ00Q(0x256, 'S)T]')](Q0OQO0Q);
                }
                QOOQQQ0 = QQ0OQOO[O00000O++];
                if (QOOQQQ0)
                    OOO00OO[OQQQ00Q(0x27c, 'Ecwj')](Q0OQO0O);
            } else
                OOOOOQ = OOOOQ0[OQQOQO++],
                    OQQO0O = OO00OQ[OO0Q00],
                    OO00OO[OO0QQ0] = Q0OQ0Q;
        }
        QQ0OQOQ[OQQQ00Q(0x30b, 'G7LQ')](OQO00OO, 0x3) && (OQOQQ0Q = QQ0OQOO[O00000O++],
            Q0OQO0O = QOQ0OOO[OQOQQ0Q],
            OQ000OQ[Q0OQO0O] = undefined);
        QQ0OQOQ[OQQQ00Q(0x1e7, 'FZB&')](OQO00OO, 0x6) && (OQOQQ0Q = QQ0OQOO[O00000O++],
            O00000O = OQOQQ0Q);
        if (QQ0OQOQ[OQQQ00Q(0x2dc, 'KPzj')](OQO00OO, 0x1)) {
            OQOQQ0Q = OOO00OO[OQQQ00Q(0x353, '8k$&')](),
                Q0OQO0O = OOO00OO[OQQQ00Q(0x240, 'MpX#')](),
                QOOQQQ0 = QQ0OQOO[O00000O++];
            if (QOOQQQ0)
                if (OQOQQ0Q === '$') {
                    Q0OQO0O[OQOQQ0Q] = diy_$
                }
            OOO00OO['push'](Q0OQO0O[OQOQQ0Q]);
        }
        if (QQ0OQOQ['pPRpe'](OQO00OO, 0x19)) {
            if (QQ0OQOQ['yMREM'](QQ0OQOQ[OQQQ00Q(0x348, '@hHo')], QQ0OQOQ['rfIWa']))
                O00OO0O += O00Q0O0['pop']();
            else {
                const OOOQO0O = QQ0OQOQ['HkzGh']['split']('|');
                let QOOQOO0 = 0x0;
                while (!![]) {
                    switch (OOOQO0O[QOOQOO0++]) {
                        case '0':
                            OQOQQ0Q = QQ0OQOO[O00000O++];
                            continue;
                        case '1':
                            if (QOOQQQ0)
                                OOO00OO['push'](QQQ0OQO);
                            continue;
                        case '2':
                            QOOQQQ0 = QQ0OQOO[O00000O++];
                            continue;
                        case '3':
                            Q0OQO0O = OOO00OO[OQQQ00Q(0x29b, 'zT]]')]();
                            continue;
                        case '4':
                            QQQ0OQO = !OQOQQ0Q ? Q0OQO0Q[Q0OQO0O]++ : ++Q0OQO0Q[Q0OQO0O];
                            continue;
                        case '5':
                            Q0OQO0Q = QQ0OQOQ['qoSQY'](QOOOQOQ, QQQ0OQQ, Q0OQO0O) || QQQ0OQQ;
                            continue;
                        case '6':
                            QQQ0OQQ = OOO00OO[OQQQ00Q(0x240, 'MpX#')]();
                            continue;
                    }
                    break;
                }
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x2e8, 'ppvG')](OQO00OO, 0x2)) {
            if (QQ0OQOQ[OQQQ00Q(0x292, 'G7LQ')](QQ0OQOQ[OQQQ00Q(0x25c, 'Qwom')], QQ0OQOQ[OQQQ00Q(0x2b6, '03K!')])) {
                OQOQQ0Q = QQ0OQOO[O00000O++],
                    Q0OQO0O = OOO00OO[OQQQ00Q(0x2fc, 'pGfn')](),
                    QQQ0OQQ = OOO00OO[OQQQ00Q(0x34a, 'G7LQ')](),
                    QQO0OQ0 = [];
                for (Q0OQO0Q = 0x0; QQ0OQOQ[OQQQ00Q(0x258, 'TUGE')](Q0OQO0Q, OQOQQ0Q); Q0OQO0Q++)
                    QQO0OQ0[OQQQ00Q(0x31e, 'ck!W')](OOO00OO[OQQQ00Q(0x295, 'SEv2')]());
                QQ0OQOQ['pmYDI'](QQQ0OQQ, 0x0) ? QQ0OQOQ[OQQQ00Q(0x20b, 'xTCl')](QQ0OQOQ[OQQQ00Q(0x2d2, 'BH%f')], QQ0OQOQ['OKOJD']) ? OOQQQQQ = QOQ0Q0O[OQQQ00Q(0x2c1, 'pGfn')](QQ0OQ0Q, QQ0O0Q0) : QQQ0OQO = Q0OQO0O['apply'](OQ000OQ, QQO0OQ0) : QQQ0OQO = QQQ0OQQ[Q0OQO0O][OQQQ00Q(0x225, '[Vwq')](QQQ0OQQ, QQO0OQ0);
                QOOQQQ0 = QQ0OQOO[O00000O++];
                if (QOOQQQ0)
                    OOO00OO['push'](QQQ0OQO);
            } else
                OQOQ0O = OQO000[OQOQQO++],
                    OQOQ0Q = OQOQQQ;
        }
        if (QQ0OQOQ[OQQQ00Q(0x1f1, 'Ecwj')](OQO00OO, 0x1b)) {
            if (QQ0OQOQ[OQQQ00Q(0x20c, '4XjS')](QQ0OQOQ[OQQQ00Q(0x2c9, 'Ecwj')], QQ0OQOQ[OQQQ00Q(0x335, 'Q&F*')])) {
                QOOO0Q = QOQQOO[QOOOQO++];
                if (!QOOO0O)
                    QO0QQO = [][OQQQ00Q(0x1fe, 'Qwom')](QO00Q0);
                for (QO0Q0O = QO0000; QQ0OQOQ[OQQQ00Q(0x267, 'f891')](QO0QQQ, 0x0); QOQ00Q--) {
                    OOQOQO = OOQO0O[OQQQ00Q(0x35d, 'IucW')](),
                        QO0OQ0[QO0O00] = QOQQ0Q[QOQQQQ];
                }
            } else {
                OQOQQ0Q = OOO00OO['pop'](),
                    Q0OQO0O = typeof OQOQQ0Q,
                    QOOQQQ0 = QQ0OQOO[O00000O++];
                if (QOOQQQ0)
                    OOO00OO[OQQQ00Q(0x310, 'xTCl')](Q0OQO0O);
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x204, '4mr8')](OQO00OO, 0x18)) {
            if (QQ0OQOQ[OQQQ00Q(0x22a, 'yq*1')](QQ0OQOQ[OQQQ00Q(0x1f5, 'UgRO')], QQ0OQOQ['enzKI'])) {
                const OQO00QO = QQ0OQOQ[OQQQ00Q(0x246, 'VI*1')]['split']('|');
                let QO0O0QO = 0x0;
                while (!![]) {
                    switch (OQO00QO[QO0O0QO++]) {
                        case '0':
                            QQ0OQOQ['pmYDI'](Q00QQOO, 0x0) ? OOQQ00O = Q00QQQ0['apply'](QQ000OO, QQ00Q00) : O000O0O = QQ000OQ[O000O0Q]['apply'](QOQ0Q0Q, QOQ00Q0);
                            continue;
                        case '1':
                            for (OQ00OQQ = 0x0; QQ0OQOQ[OQQQ00Q(0x26d, 'tFri')](QOQOO00, O00QO00); OOQ0OQQ++)
                                Q00QQOQ['unshift'](OOQQOOO[OQQQ00Q(0x271, 'tFri')]());
                            continue;
                        case '2':
                            if (QOQQ0O0)
                                QOQOO0Q[OQQQ00Q(0x1ed, '[Vwq')](OOOOQQO);
                            continue;
                        case '3':
                            OOOOQQ0 = OQ00OQO[OQQQ00Q(0x244, 'Cc$J')]();
                            continue;
                        case '4':
                            OOQQOOQ = OOQ0OQO[OQQQ00Q(0x26c, '4mr8')]();
                            continue;
                        case '5':
                            OQ0QOOO = Q00OOQO[QOQ00O0++];
                            continue;
                        case '6':
                            QQ0OO00 = Q000QOO[QOQOO0O++];
                            continue;
                        case '7':
                            Q000QQ0 = [];
                            continue;
                    }
                    break;
                }
            } else
                debugger;
        }
        if (QQ0OQOQ[OQQQ00Q(0x30c, 'UgRO')](OQO00OO, 0x16)) {
            if (QQ0OQOQ[OQQQ00Q(0x216, '03K!')](QQ0OQOQ[OQQQ00Q(0x32f, 'GQSe')], QQ0OQOQ['PYLZH']))
                Q0O0OO = QQ0OQOQ[OQQQ00Q(0x24b, '8Yqt')](Q0OQQ0, Q0O0OQ);
            else {
                const QQQOOQO = QQ0OQOQ[OQQQ00Q(0x343, 'Cc$J')]['split']('|');
                let OO0Q0QO = 0x0;
                while (!![]) {
                    switch (QQQOOQO[OO0Q0QO++]) {
                        case '0':
                            for (Q0OQO0O = 0x0; QQ0OQOQ['zzJvd'](Q0OQO0O, OQOQQ0Q); Q0OQO0O++) {
                                QQQ0OQQ += OOO00OO[OQQQ00Q(0x26c, '4mr8')]();
                            }
                            continue;
                        case '1':
                            OQOQQ0Q = QQ0OQOO[O00000O++];
                            continue;
                        case '2':
                            if (QOOQQQ0)
                                OOO00OO[OQQQ00Q(0x214, 'Qwom')](QQQ0OQQ);
                            continue;
                        case '3':
                            QQQ0OQQ = '';
                            continue;
                        case '4':
                            QOOQQQ0 = QQ0OQOO[O00000O++];
                            continue;
                    }
                    break;
                }
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x27d, 'IucW')](OQO00OO, 0x5)) {
            if (QQ0OQOQ[OQQQ00Q(0x2f3, '8k$&')](QQ0OQOQ[OQQQ00Q(0x200, 'iwUv')], QQ0OQOQ['qXGUV'])) {
                if (OOOQQO['r'])
                    return OOOQQQ['pop']();
                return Q00OQ0;
            } else {
                const OO0Q0QQ = QQ0OQOQ[OQQQ00Q(0x330, 'KPzj')][OQQQ00Q(0x247, 'Q&F*')]('|');
                let OQQQOQO = 0x0;
                while (!![]) {
                    switch (OO0Q0QQ[OQQQOQO++]) {
                        case '0':
                            QQQ0OQQ = Q0OQO0O[OQOQQ0Q];
                            continue;
                        case '1':
                            QOOQQQ0 = QQ0OQOO[O00000O++];
                            continue;
                        case '2':
                            if (QOOQQQ0)
                                OOO00OO['push'](QQQ0OQQ);
                            continue;
                        case '3':
                            OQOQQ0Q = QOQ0OOO[QQ0OQOO[O00000O++]];
                            continue;
                        case '4':
                            Q0OQO0O = OQ000OQ;
                            continue;
                    }
                    break;
                }
            }
        }
        if (QQ0OQOQ['eEYdS'](OQO00OO, 0x10)) {
            if (QQ0OQOQ[OQQQ00Q(0x1ee, 'yI15')](QQ0OQOQ['bIAnK'], QQ0OQOQ[OQQQ00Q(0x27f, 'Cc$J')])) {
                QOQOQO = OOQQQQ[OQQQ00Q(0x226, 'QXIo')](),
                    OOQQ0Q = !OOQ0QO,
                    OOQOO0 = OOQ0QQ[OOQ00O++];
                if (O0O00Q)
                    O0O0QQ[OQQQ00Q(0x228, 'BH%f')](O0O00O);
            } else
                return;
        }
        if (QQ0OQOQ[OQQQ00Q(0x236, '03K!')](OQO00OO, 0x1f)) {
            const O0O00O0 = QQ0OQOQ[OQQQ00Q(0x21b, '@hHo')][OQQQ00Q(0x2ef, 'Ecwj')]('|');
            let QQQ0QQ0 = 0x0;
            while (!![]) {
                switch (O0O00O0[QQQ0QQ0++]) {
                    case '0':
                        if (QOOQQQ0)
                            OOO00OO['push'](QQQ0OQO);
                        continue;
                    case '1':
                        for (Q0OQO0Q = 0x0; QQ0OQOQ[OQQQ00Q(0x30f, 'f%zT')](Q0OQO0Q, QQQ0OQQ); Q0OQO0Q++)
                            QQO0OQ0[OQQQ00Q(0x2e6, 'GQSe')](OOO00OO[OQQQ00Q(0x244, 'Cc$J')]());
                        continue;
                    case '2':
                        Q0OQO0O = OOO00OO[OQQQ00Q(0x2c6, '[Vwq')]();
                        continue;
                    case '3':
                        OQOQQ0Q = OOO00OO['pop']();
                        continue;
                    case '4':
                        QQQ0OQQ = QQ0OQOO[O00000O++];
                        continue;
                    case '5':
                        QQO0OQ0 = [];
                        continue;
                    case '6':
                        QQQ0OQO = new Q0OQO0O[OQOQQ0Q](...QQO0OQ0);
                        continue;
                    case '7':
                        QOOQQQ0 = QQ0OQOO[O00000O++];
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x326, '*OeU')](OQO00OO, 0x8)) {
            if (QQ0OQOQ[OQQQ00Q(0x351, '*OeU')](QQ0OQOQ[OQQQ00Q(0x33e, 'zT]]')], QQ0OQOQ[OQQQ00Q(0x35c, 'QXIo')])) {
                const Q0QQ0OO = QQ0OQOQ['AQZxG']['split']('|');
                let OQQQOQQ = 0x0;
                while (!![]) {
                    switch (Q0QQ0OO[OQQQOQQ++]) {
                        case '0':
                            if (QQ0OQOQ[OQQQ00Q(0x24c, 'ZO7v')](Q0OQO0O, -0x1))
                                Q0OQO0O = QQQ0OQQ;
                            continue;
                        case '1':
                            OQOQQ0Q = OOO00OO[OQQQ00Q(0x344, 'bO(T')]();
                            continue;
                        case '2':
                            Q0OQO0O = OQOQQ0Q[Q0OQO0O];
                            continue;
                        case '3':
                            O00000O = Q0OQO0O;
                            continue;
                        case '4':
                            Q0OQO0O = OOO00OO['pop']();
                            continue;
                        case '5':
                            QQQ0OQQ = QQ0OQOO[O00000O++];
                            continue;
                    }
                    break;
                }
            } else {
                O0OQQOO = Q0Q0OQ0[OQQQ00Q(0x328, 'VI*1')](),
                    OQ0O0O0 = QQ0OQOQ[OQQQ00Q(0x304, '*OeU')](O0OOOQQ, O0QQOQ0);
                if (QQ0OQOQ[OQQQ00Q(0x201, 'Qwom')](OQQ00OO, O0OOOQO))
                    OQQ0Q0Q = -0x1;
                OQ0O0OO[OQQQ00Q(0x207, '03K!')](Q0Q0OQO);
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x265, 'Qwom')](OQO00OO, 0x21)) {
            if (QQ0OQOQ[OQQQ00Q(0x2ed, 'lrc@')](QQ0OQOQ[OQQQ00Q(0x22c, 'QXIo')], QQ0OQOQ[OQQQ00Q(0x251, 'ppvG')]))
                QQ0Q0O = QQ0OQOQ[OQQQ00Q(0x359, 'f891')](O0QQQO, O0Q0Q0);
            else {
                const Q0QQQ00 = QQ0OQOQ[OQQQ00Q(0x220, 'TUGE')][OQQQ00Q(0x34d, 'pGU9')]('|');
                let OQOO00O = 0x0;
                while (!![]) {
                    switch (Q0QQQ00[OQOO00O++]) {
                        case '0':
                            QOOQQQ0 = QQ0OQOO[O00000O++];
                            continue;
                        case '1':
                            Q0OQO0O = OOO00OO[OQQQ00Q(0x2ad, 'Q&F*')]();
                            continue;
                        case '2':
                            if (QOOQQQ0)
                                OOO00OO['push'](QQQ0OQQ);
                            continue;
                        case '3':
                            OQOQQ0Q = OOO00OO[OQQQ00Q(0x2a8, 'f891')]();
                            continue;
                        case '4':
                            QQQ0OQQ = delete Q0OQO0O[OQOQQ0Q];
                            continue;
                    }
                    break;
                }
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x2cc, 'BH%f')](OQO00OO, undefined)) {
            if (QQ0OQOQ[OQQQ00Q(0x237, 'TUGE')](QQ0OQOQ['FiqYO'], QQ0OQOQ['pUjOJ']))
                throw new Q00OQOQ(QQ0OQOQ[OQQQ00Q(0x2af, 'GQSe')]);
            else
                throw new Error(QQ0OQOQ['nVpFz']);
        }
        if (QQ0OQOQ[OQQQ00Q(0x278, 'tFri')](OQO00OO, 0x14))
            throw OOO00OO[OQQQ00Q(0x2d4, 'yI15')]();
        if (QQ0OQOQ[OQQQ00Q(0x2a1, 'MpX#')](OQO00OO, 0x9)) {
            const QQQOOQQ = QQ0OQOQ[OQQQ00Q(0x2d1, 'K@E7')][OQQQ00Q(0x272, 'Cc$J')]('|');
            let QQQQQOO = 0x0;
            while (!![]) {
                switch (QQQOOQQ[QQQQQOO++]) {
                    case '0':
                        QQQ0OQO = function () {
                            const OO00O0O = OQQQ00Q;
                            return QQ0OQOQ[OO00O0O(0x30a, 'UgRO')](interpreter, OQ000OQ, Q0OQO0O, OOO00OO, QOQ0OOO, QQ0OQOO, {
                                't': this,
                                'n': OQOQQ0Q,
                                'f': QQ0OQOQ['ONmVV'](OQOQQ0Q, Q0OQO0Q),
                                'r': 0x1
                            }, arguments);
                        }
                        ;
                        continue;
                    case '1':
                        Q0OQO0O = QQ0OQOO[O00000O++];
                        continue;
                    case '2':
                        OQOQQ0Q = OOO00OO[OQQQ00Q(0x2ad, 'Q&F*')]();
                        continue;
                    case '3':
                        QQQ0OQQ ? OOO00OO['push'](QQQ0OQO) : OQ000OQ[OQOQQ0Q] = QQQ0OQO;
                        continue;
                    case '4':
                        QQQ0OQQ = QQ0OQOO[O00000O++];
                        continue;
                    case '5':
                        Q0OQO0Q = OOO00OO[QQ0OQOQ[OQQQ00Q(0x2c7, 'UgRO')](OOO00OO['length'], 0x1)];
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x248, '*OeU')](OQO00OO, 0x1a)) {
            const Q0Q00QO = QQ0OQOQ[OQQQ00Q(0x33a, ']x14')]['split']('|');
            let QQQQQOQ = 0x0;
            while (!![]) {
                switch (Q0Q00QO[QQQQQOQ++]) {
                    case '0':
                        Q0OQO0O = OOO00OO[OQQQ00Q(0x290, 'FZB&')]();
                        continue;
                    case '1':
                        OQOQQ0Q = QQ0OQOO[O00000O++];
                        continue;
                    case '2':
                        QQQ0OQQ = OOO00OO['pop']();
                        continue;
                    case '3':
                        Q0OQO0Q = QQ0OQOQ[OQQQ00Q(0x2dd, '8Yqt')](QOOOQOQ, QQQ0OQQ, Q0OQO0O) || QQQ0OQQ;
                        continue;
                    case '4':
                        if (QOOQQQ0)
                            OOO00OO[OQQQ00Q(0x239, 'SEv2')](QQQ0OQO);
                        continue;
                    case '5':
                        QOOQQQ0 = QQ0OQOO[O00000O++];
                        continue;
                    case '6':
                        QQQ0OQO = !OQOQQ0Q ? Q0OQO0Q[Q0OQO0O]-- : --Q0OQO0Q[Q0OQO0O];
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ[OQQQ00Q(0x1f8, 'f891')](OQO00OO, 0x39)) {
            if (QQ0OQOQ[OQQQ00Q(0x276, ']x14')](QQ0OQOQ[OQQQ00Q(0x217, 'GQSe')], QQ0OQOQ[OQQQ00Q(0x274, 'sef#')]))
                O0O0OQ[OQQQ00Q(0x354, 'G]Ue')]({});
            else {
                OQOQQ0Q = OOO00OO[OQQQ00Q(0x2c6, '[Vwq')](),
                    Q0OQO0O = ~OQOQQ0Q,
                    QOOQQQ0 = QQ0OQOO[O00000O++];
                if (QOOQQQ0)
                    OOO00OO[OQQQ00Q(0x33c, '8U5P')](Q0OQO0O);
            }
        }
        QQ0OQOQ[OQQQ00Q(0x2cf, 'lrc@')](OQO00OO, 0xc) && OOO00OO['push'](OQ000OQ);
        if (QQ0OQOQ[OQQQ00Q(0x252, '8Yqt')](OQO00OO, 0x11)) {
            const QO0OQ0O = QQ0OQOQ[OQQQ00Q(0x346, 'lrc@')][OQQQ00Q(0x21f, 'EBz@')]('|');
            let QO0O0Q0 = 0x0;
            while (!![]) {
                switch (QO0OQ0O[QO0O0Q0++]) {
                    case '0':
                        if (QOOQQQ0)
                            OOO00OO[OQQQ00Q(0x33c, '8U5P')](Q0OQO0O);
                        continue;
                    case '1':
                        OQOQQ0Q = QQ0OQOO[O00000O++];
                        continue;
                    case '2':
                        for (QQQ0OQQ = 0x0; QQ0OQOQ[OQQQ00Q(0x2f2, 'tFri')](QQQ0OQQ, OQOQQ0Q); QQQ0OQQ++)
                            OOO00OO['pop']();
                        continue;
                    case '3':
                        QOOQQQ0 = QQ0OQOO[O00000O++];
                        continue;
                    case '4':
                        Q0OQO0O = OOO00OO[OQQQ00Q(0x1f7, '*OeU')]();
                        continue;
                }
                break;
            }
        }
        if (QQ0OQOQ['uIqiN'](OQO00OO, 0xe)) {
            if (QQ0OQOQ[OQQQ00Q(0x34e, 'zT]]')](QQ0OQOQ[OQQQ00Q(0x22b, 'Cc$J')], QQ0OQOQ['ugvfZ']))
                Q0QQOOQ = OQQOO0Q[OQQQ00Q(0x219, '8U5P')](),
                    QQQ00O0 = Q0OO00O[OQQQ00Q(0x2a6, 'Qwom')](),
                    OQQQ0O0 = new QO00QOO(OQQOO0O, OO0OOQQ),
                    OO0QQOO[OQQQ00Q(0x262, 'pGU9')](QO00QOQ);
            else {
                OQOQQ0Q = QQ0OQOO[O00000O++],
                    Q0OQO0O = QQ0OQOQ['BgsQg'](interpreter, OQ000OQ, O00000O, OOO00OO, QOQ0OOO, QQ0OQOO, {
                        't': OQ000OQ[QOQ0OOO[0x0]]
                    });
                if (QQ0OQOQ[OQQQ00Q(0x2fe, '8U5P')](Q0OQO0O, undefined))
                    O00000O = OQOQQ0Q;
                else {
                    if (Q000O00['r'])
                        return QQ0OQOQ['vBaBq'](QQ0OQOQ[OQQQ00Q(0x338, 'sef#')], QQ0OQOQ[OQQQ00Q(0x291, 'GQSe')]) ? OOO00OO[OQQQ00Q(0x2a8, 'f891')]() : OQQ0OO[OQQQ00Q(0x230, '@zXt')]();
                    return Q0OQO0O;
                }
            }
        }
        QQ0OQOQ[OQQQ00Q(0x32b, '03K!')](OQO00OO, 0x15) && OOO00OO[OQQQ00Q(0x2ec, '6CCs')]('' + OOO00OO[OQQQ00Q(0x219, '8U5P')]());
        if (QQ0OQOQ['xqDPB'](OQO00OO, 0x12)) {
            OQOQQ0Q = OOO00OO['pop'](),
                OQOQQ0Q = QQ0OQOQ[OQQQ00Q(0x304, '*OeU')](Number, OQOQQ0Q);
            if (QQ0OQOQ[OQQQ00Q(0x332, 'yq*1')](OQOQQ0Q, NaN))
                OQOQQ0Q = -0x1;
            OOO00OO['push'](OQOQQ0Q);
        }
        QQ0OQOQ[OQQQ00Q(0x20a, '@hHo')](OQO00OO, 0x17) && (OQOQQ0Q = OOO00OO[OQQQ00Q(0x328, 'VI*1')](),
            Q0OQO0O = OOO00OO['pop'](),
            Q0OQO0Q = new RegExp(Q0OQO0O, OQOQQ0Q),
            OOO00OO['push'](Q0OQO0Q));
    }
}(typeof window !== Q0Q00OQ(0x2eb, 'QXIo') ? window : (window = global,
    window), 0x0, [], [Q0Q00OQ(0x33b, 'Q&F*'), Q0Q00OQ(0x305, '4XjS'), Q0Q00OQ(0x28f, 'SEv2'), Q0Q00OQ(0x25e, 'pGfn'), Q0Q00OQ(0x235, 'KPzj'), Q0Q00OQ(0x1fd, 'TUGE'), 'Q', 's', Q0Q00OQ(0x333, '03K!'), 'i', 'j', 't', 'l', 'w', 'f', Q0Q00OQ(0x223, 'yI15'), 'k', 'm', 'o', 'hex', Q0Q00OQ(0x2d3, '@hHo'), Q0Q00OQ(0x25b, 'f%zT'), Q0Q00OQ(0x25f, 'Q&F*'), 0x8, 0x6, 0x4, 0x10, 0x2, 'buffer', Q0Q00OQ(0x2ee, '4mr8'), Q0Q00OQ(0x2a9, '[Vwq'), Q0Q00OQ(0x22e, 'Q&F*'), 0x0, Q0Q00OQ(0x215, 'IucW'), 0x80, 0x18, 0x3, 0x1, 'n', 'c', 0x20, 0x5a827999, 0x6ed9eba1, 0x70e44324, 0x359d3e2a, 0x67452301, 0x10325477, null, 0x3c2d1e10, Q0Q00OQ(0x28e, '[Vwq'), 0x50, 0xe, 0x5, 0x14, 0x1e, 'pop', Q0Q00OQ(0x357, 'Q&F*'), '', 'e', '0', Q0Q00OQ(0x25a, 'iwUv'), Q0Q00OQ(0x2c5, 'G]Ue'), Q0Q00OQ(0x32e, 'ck!W'), Q0Q00OQ(0x257, 'f%zT'), Q0Q00OQ(0x340, 'iwUv'), Q0Q00OQ(0x1f4, '03K!'), 'r', 'x', Q0Q00OQ(0x260, 'UgRO'), Q0Q00OQ(0x273, 'ppvG'), 0x800, 0xc0, 0x1f, 0x3f, 0xd800, 0xa, 0xdc00, 0x10000, 0xf0, 0x12, 0x7, 0xc, 0xe0, 0xf, Q0Q00OQ(0x24e, 'Q&F*'), '$', 'originalAjax', Q0Q00OQ(0x1f9, 'Ecwj'), Q0Q00OQ(0x298, ']x14'), Q0Q00OQ(0x2b3, '8Yqt'), Q0Q00OQ(0x2cd, '4mr8'), Q0Q00OQ(0x1ef, '*OeU'), Q0Q00OQ(0x2bb, '4XjS'), Q0Q00OQ(0x2f5, 'yq*1'), 'forEach', !![], Q0Q00OQ(0x287, 'UgRO'), 'textStatus', Q0Q00OQ(0x203, ']x14'), 'error', 'errorThrown', 'extend', 'tt', 'url', '&', '=', 'fu', 'aa', Q0Q00OQ(0x31d, 'Q&F*'), Q0Q00OQ(0x2ea, 'S)T]'), Q0Q00OQ(0x2c2, 'IucW'), Q0Q00OQ(0x2e7, 'f%zT'), Q0Q00OQ(0x234, 'UgRO'), 'screenHeight', 'canvas', Q0Q00OQ(0x302, 'Qwom'), Q0Q00OQ(0x280, 'qBYY'), 'canvasData', Q0Q00OQ(0x2a7, 'yq*1'), Q0Q00OQ(0x1f2, 'ZO7v'), Q0Q00OQ(0x311, 'iwUv'), Q0Q00OQ(0x261, 'Ecwj'), Q0Q00OQ(0x2aa, 'KPzj'), 'createElement', '2d', Q0Q00OQ(0x31c, 'ppvG'), 'fingerprint', Q0Q00OQ(0x208, 'TUGE'), Q0Q00OQ(0x266, '8U5P'), Q0Q00OQ(0x284, '*OeU'), Q0Q00OQ(0x35a, 'tFri'), Q0Q00OQ(0x263, '8Yqt'), '#f60', 'fillStyle', 0x7d, 0x3e, 'fillRect', Q0Q00OQ(0x1ea, 'qBYY'), Q0Q00OQ(0x2b8, '[Vwq'), Q0Q00OQ(0x29d, 'G7LQ'), 0x11, Q0Q00OQ(0x238, 'yI15'), 'ss', Q0Q00OQ(0x1f6, 'MpX#'), Q0Q00OQ(0x22d, '8U5P'), 'uuu', Q0Q00OQ(0x29a, '4XjS'), Q0Q00OQ(0x309, '4XjS')], [0x4, 0x3, 0x9, 0x7, 0x0, 0x6, 0x17, 0xa, -0x1, 0xc, 0x4, 0x4, 0x1f, 0x0, 0x1, 0x4, 0x5, 0x2, 0x0, 0x1, 0xb, 0x2, 0x10, 0x4, 0x6, 0x9, 0x1e, 0x0, 0x6, 0x545, 0x4, 0x7, 0xa, 0x0, 0x3, 0x8, 0x3, 0x9, 0x3, 0xa, 0x3, 0xb, 0x3, 0xc, 0x3, 0xd, 0x3, 0xe, 0x3, 0xf, 0x3, 0x10, 0x3, 0x11, 0x3, 0x12, 0x3, 0x9, 0x3, 0x13, 0x5, 0x7, 0x1, 0xc, 0x4, 0x14, 0x2, 0x1, 0x1, 0xc, 0x4, 0x15, 0x1f, 0x1, 0x1, 0xc, 0x4, 0x8, 0x0, 0x1, 0x11, 0x0, 0x0, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x17, 0x33, 0x1, 0x4, 0x18, 0x24, 0x1, 0x4, 0x19, 0x26, 0x1, 0x4, 0x1a, 0x33, 0x1, 0xc, 0x4, 0xc, 0x0, 0x1, 0x5, 0xc, 0x1, 0x4, 0x1b, 0x26, 0x1, 0xc, 0x4, 0x15, 0x1f, 0x1, 0x1, 0xc, 0x4, 0x7, 0x0, 0x1, 0x11, 0x1, 0x0, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x1c, 0x1, 0x1, 0xc, 0x4, 0x15, 0x1f, 0x1, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x1d, 0x2, 0x1, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x1c, 0x1, 0x1, 0xc, 0x4, 0x1e, 0x1f, 0x1, 0x1, 0xc, 0x4, 0x7, 0x0, 0x1, 0x11, 0x1, 0x0, 0xe, 0x10b, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x1c, 0x1, 0x1, 0xc, 0x4, 0x1f, 0x1f, 0x1, 0x1, 0xc, 0x4, 0xb, 0x0, 0x1, 0x4, 0x20, 0xc, 0x4, 0x9, 0x0, 0x1, 0x11, 0x1, 0x0, 0xf, 0x102, 0x5, 0x9, 0x1, 0x5, 0xc, 0x1, 0x31, 0x1, 0x7, 0x100, 0x5, 0x9, 0x1, 0x4, 0x1b, 0x26, 0x1, 0xc, 0x4, 0xb, 0x1, 0x1, 0x4, 0x21, 0x2, 0x1, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0xc, 0x4, 0x9, 0x1, 0x1, 0x0, 0x0, 0x10, 0xb, 0x1, 0xc, 0x4, 0x9, 0x19, 0x0, 0x0, 0x6, 0xd6, 0x10, 0xc, 0x4, 0x7, 0x1, 0x1, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x1b, 0x25, 0x1, 0x1, 0x1, 0x4, 0x22, 0x4, 0x23, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x24, 0x29, 0x1, 0x4, 0x17, 0x35, 0x1, 0x34, 0x1, 0x26, 0x1, 0x27, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x1b, 0x25, 0x1, 0x0, 0x0, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x24, 0x26, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x5, 0xc, 0x1, 0x4, 0x25, 0x34, 0x1, 0x0, 0x0, 0x1e, 0x0, 0x1, 0xc, 0x4, 0xd, 0x0, 0x1, 0x4, 0x20, 0x9, 0x178, 0x1, 0x6, 0x1aa, 0xa, -0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x1, 0x1, 0x29, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0x12, 0x39, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x1, 0x1, 0x29, 0x1, 0x27, 0x1, 0xb, 0x2, 0x10, 0x4, 0x20, 0x9, 0x1b1, 0x1, 0x6, 0x1d5, 0xa, -0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x1, 0x1, 0x38, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x1, 0x1, 0x38, 0x1, 0xb, 0x2, 0x10, 0x4, 0x20, 0x9, 0x1dc, 0x1, 0x6, 0x221, 0xa, -0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x1, 0x1, 0x29, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x1, 0x1, 0x29, 0x1, 0x27, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x1, 0x1, 0x29, 0x1, 0x27, 0x1, 0xb, 0x2, 0x10, 0x4, 0x20, 0x9, 0x228, 0x1, 0x6, 0x24c, 0xa, -0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x1, 0x1, 0x38, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x1, 0x1, 0x38, 0x1, 0xb, 0x2, 0x10, 0x1e, 0x4, 0x1, 0xc, 0x4, 0xe, 0x0, 0x1, 0x4, 0x20, 0x9, 0x25b, 0x1, 0x6, 0x27a, 0x4, 0x26, 0x4, 0x27, 0xa, 0x1, 0x5, 0x26, 0x1, 0x5, 0x27, 0x1, 0x26, 0x1, 0x5, 0x26, 0x1, 0x4, 0x28, 0x5, 0x27, 0x1, 0x34, 0x1, 0x24, 0x1, 0x27, 0x1, 0xb, 0x2, 0x10, 0xc, 0x4, 0xf, 0x0, 0x1, 0x4, 0x29, 0x4, 0x2a, 0x4, 0x20, 0x4, 0x2b, 0x34, 0x1, 0x4, 0x20, 0x4, 0x2c, 0x34, 0x1, 0x1e, 0x4, 0x1, 0xc, 0x4, 0x10, 0x0, 0x1, 0x4, 0x2d, 0x4, 0x20, 0x4, 0x2e, 0x34, 0x1, 0x4, 0x2f, 0x4, 0x2f, 0x4, 0x20, 0x4, 0x30, 0x34, 0x1, 0x1e, 0x5, 0x1, 0xc, 0x4, 0x11, 0x0, 0x1, 0x11, 0x4, 0x0, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x20, 0x1, 0x1, 0x12, 0x39, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x1b, 0x0, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0x12, 0x39, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x24, 0x0, 0x1, 0x11, 0x1, 0x0, 0xe, 0x488, 0x4, 0x20, 0xc, 0x4, 0x9, 0x0, 0x0, 0xf, 0x479, 0x5, 0x9, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x31, 0x1, 0x7, 0x477, 0x4, 0x20, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x31, 0x2, 0x1, 0x1, 0xc, 0x4, 0x12, 0x0, 0x1, 0x11, 0x0, 0x0, 0xe, 0x42c, 0x4, 0x20, 0xc, 0x4, 0xa, 0x0, 0x0, 0xf, 0x423, 0x5, 0xa, 0x1, 0x4, 0x32, 0x31, 0x1, 0x7, 0x421, 0x5, 0xa, 0x1, 0x4, 0x1a, 0x31, 0x1, 0x7, 0x33e, 0xc, 0x4, 0x7, 0x1, 0x1, 0x5, 0x9, 0x1, 0x5, 0xa, 0x1, 0x33, 0x1, 0x1, 0x1, 0x6, 0x384, 0xc, 0x4, 0xd, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x24, 0x34, 0x1, 0x1, 0x1, 0xc, 0x4, 0xd, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x17, 0x34, 0x1, 0x1, 0x1, 0x38, 0x1, 0xc, 0x4, 0xd, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x33, 0x34, 0x1, 0x1, 0x1, 0x38, 0x1, 0xc, 0x4, 0xd, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x1a, 0x34, 0x1, 0x1, 0x1, 0x38, 0x1, 0x4, 0x25, 0xc, 0x4, 0xf, 0x2, 0x2, 0x1, 0xc, 0x4, 0xd, 0x1, 0x1, 0xc, 0x4, 0xa, 0x1, 0x1, 0x0, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x20, 0x1, 0x1, 0x4, 0x34, 0xc, 0x4, 0xf, 0x2, 0x2, 0x1, 0xc, 0x4, 0xe, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x35, 0x36, 0x1, 0x4, 0x20, 0x27, 0x1, 0x2, 0x0, 0x1, 0x33, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x19, 0x1, 0x1, 0x33, 0x1, 0xc, 0x4, 0xd, 0x1, 0x1, 0xc, 0x4, 0xa, 0x1, 0x1, 0x1, 0x1, 0x33, 0x1, 0xc, 0x4, 0x10, 0x1, 0x1, 0x5, 0xa, 0x1, 0x4, 0x35, 0x36, 0x1, 0x4, 0x20, 0x27, 0x1, 0x1, 0x1, 0x33, 0x1, 0x4, 0x20, 0x27, 0x1, 0xc, 0x4, 0xb, 0x0, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x1, 0x1, 0x4, 0x36, 0xc, 0x4, 0xf, 0x2, 0x2, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x25, 0x0, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x37, 0x2, 0x0, 0x1, 0x5, 0xb, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0x4, 0x38, 0x2, 0x1, 0x1, 0x11, 0x4, 0x0, 0x10, 0xb, 0x1, 0xc, 0x4, 0xa, 0x19, 0x0, 0x0, 0x6, 0x319, 0x10, 0xe, 0x476, 0x4, 0x20, 0xc, 0x4, 0xa, 0x0, 0x0, 0xf, 0x46d, 0x5, 0xa, 0x1, 0x4, 0x34, 0x31, 0x1, 0x7, 0x46b, 0xc, 0x4, 0x11, 0x1, 0x1, 0xc, 0x4, 0xa, 0x1, 0x1, 0x1, 0x1, 0xc, 0x4, 0x12, 0x1, 0x1, 0xc, 0x4, 0xa, 0x1, 0x1, 0x1, 0x1, 0x33, 0x1, 0x4, 0x20, 0x27, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0xc, 0x4, 0xa, 0x1, 0x1, 0x0, 0x0, 0x10, 0xb, 0x1, 0xc, 0x4, 0xa, 0x19, 0x0, 0x0, 0x6, 0x435, 0x10, 0x10, 0xb, 0x1, 0x5, 0x9, 0x1, 0x4, 0x1a, 0x33, 0x1, 0xc, 0x4, 0x9, 0x0, 0x0, 0x6, 0x2ea, 0x10, 0x5, 0x11, 0x1, 0xc, 0x4, 0x1e, 0x1f, 0x1, 0x1, 0x4, 0x1c, 0x1, 0x1, 0xc, 0x4, 0x1f, 0x1f, 0x1, 0x1, 0xc, 0x4, 0xb, 0x0, 0x0, 0xe, 0x4e0, 0x4, 0x20, 0xc, 0x4, 0x9, 0x0, 0x1, 0x11, 0x0, 0x0, 0xf, 0x4d7, 0x5, 0x9, 0x1, 0x4, 0x34, 0x31, 0x1, 0x7, 0x4d5, 0x5, 0x9, 0x1, 0x4, 0x1b, 0x26, 0x1, 0xc, 0x4, 0xb, 0x1, 0x1, 0x4, 0x21, 0x2, 0x1, 0x1, 0xc, 0x4, 0x11, 0x1, 0x1, 0xc, 0x4, 0x9, 0x1, 0x1, 0x0, 0x0, 0x10, 0xb, 0x1, 0xc, 0x4, 0x9, 0x19, 0x0, 0x0, 0x6, 0x4ac, 0x10, 0x4, 0x39, 0x5, 0x11, 0x1, 0xc, 0x4, 0x1e, 0x1f, 0x1, 0x1, 0x4, 0x1c, 0x1, 0x1, 0xc, 0x4, 0x15, 0x1f, 0x1, 0x1, 0x4, 0x20, 0x9, 0x4fc, 0x1, 0x6, 0x520, 0x4, 0x3a, 0xa, 0x0, 0x5, 0x3a, 0x1, 0x4, 0x1a, 0x31, 0x1, 0x7, 0x50d, 0x4, 0x3b, 0x6, 0x50f, 0x4, 0x39, 0x4, 0x1a, 0xc, 0x4, 0x3a, 0x1, 0x1, 0x4, 0x3c, 0x2, 0x1, 0x1, 0x33, 0x1, 0xb, 0x2, 0x10, 0xc, 0x4, 0x3d, 0x1, 0x1, 0x4, 0x3e, 0x1, 0x1, 0x4, 0x3f, 0x1, 0x1, 0x4, 0x40, 0x2, 0x2, 0x1, 0x4, 0x41, 0x2, 0x1, 0x1, 0xc, 0x4, 0x13, 0x0, 0x1, 0x11, 0x0, 0x0, 0x5, 0x13, 0x1, 0xb, 0x2, 0x10, 0x4, 0x14, 0x9, 0x54c, 0x0, 0x6, 0x68f, 0x4, 0x7, 0xa, 0x0, 0x3, 0x9, 0x3, 0x42, 0x3, 0x27, 0x3, 0x43, 0x1e, 0x0, 0x1, 0xc, 0x4, 0x42, 0x0, 0x1, 0x11, 0x0, 0x0, 0xe, 0x689, 0x4, 0x20, 0xc, 0x4, 0x9, 0x0, 0x0, 0xf, 0x680, 0x5, 0x9, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x31, 0x1, 0x7, 0x67e, 0x5, 0x9, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x44, 0x2, 0x1, 0x1, 0xc, 0x4, 0x27, 0x0, 0x1, 0x4, 0x22, 0x31, 0x1, 0x7, 0x5a5, 0x5, 0x27, 0x1, 0xc, 0x4, 0x42, 0x1, 0x1, 0x4, 0x45, 0x2, 0x1, 0x0, 0x6, 0x67d, 0x5, 0x27, 0x1, 0x4, 0x46, 0x31, 0x1, 0x7, 0x5d4, 0x4, 0x47, 0x5, 0x27, 0x1, 0x4, 0x18, 0x25, 0x1, 0x4, 0x48, 0x29, 0x1, 0x33, 0x1, 0x4, 0x22, 0x5, 0x27, 0x1, 0x4, 0x49, 0x29, 0x1, 0x33, 0x1, 0xc, 0x4, 0x42, 0x1, 0x1, 0x4, 0x45, 0x2, 0x2, 0x0, 0x6, 0x67d, 0xe, 0x67d, 0x5, 0x27, 0x1, 0x4, 0x4a, 0x38, 0x1, 0xc, 0x4, 0x43, 0x0, 0x1, 0x4, 0x4b, 0x25, 0x1, 0x4, 0x20, 0x2d, 0x1, 0x7, 0x63f, 0x5, 0x43, 0x1, 0x4, 0x4b, 0x26, 0x1, 0xc, 0x4, 0x9, 0x19, 0x1, 0x1, 0xc, 0x4, 0x7, 0x1, 0x1, 0x4, 0x44, 0x2, 0x1, 0x1, 0x4, 0x4c, 0x38, 0x1, 0x33, 0x1, 0x4, 0x4d, 0x33, 0x1, 0xc, 0x4, 0x27, 0x0, 0x1, 0x4, 0x4e, 0x5, 0x27, 0x1, 0x4, 0x4f, 0x25, 0x1, 0x4, 0x50, 0x29, 0x1, 0x33, 0x1, 0x4, 0x22, 0x5, 0x27, 0x1, 0x4, 0x51, 0x25, 0x1, 0x4, 0x49, 0x29, 0x1, 0x33, 0x1, 0xc, 0x4, 0x42, 0x1, 0x1, 0x4, 0x45, 0x2, 0x2, 0x1, 0x11, 0x1, 0x0, 0x6, 0x658, 0x4, 0x52, 0x5, 0x27, 0x1, 0x4, 0x51, 0x25, 0x1, 0x4, 0x53, 0x29, 0x1, 0x33, 0x1, 0xc, 0x4, 0x42, 0x1, 0x1, 0x4, 0x45, 0x2, 0x1, 0x0, 0x4, 0x22, 0x5, 0x27, 0x1, 0x4, 0x18, 0x25, 0x1, 0x4, 0x49, 0x29, 0x1, 0x33, 0x1, 0x4, 0x22, 0x5, 0x27, 0x1, 0x4, 0x49, 0x29, 0x1, 0x33, 0x1, 0xc, 0x4, 0x42, 0x1, 0x1, 0x4, 0x45, 0x2, 0x2, 0x0, 0x10, 0x10, 0xb, 0x1, 0xc, 0x4, 0x9, 0x19, 0x0, 0x0, 0x6, 0x56c, 0x10, 0x5, 0x42, 0x1, 0xb, 0x2, 0x10, 0x5, 0x54, 0x1, 0x4, 0x20, 0x4, 0x20, 0x9, 0x69b, 0x1, 0x6, 0x828, 0x4, 0x55, 0xa, 0x0, 0x3, 0x56, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x57, 0x1, 0x1, 0xc, 0x4, 0x56, 0x0, 0x0, 0x3, 0x58, 0x1e, 0x0, 0x1, 0xc, 0x4, 0x58, 0x0, 0x0, 0x3, 0x59, 0x1e, 0x0, 0x1, 0xc, 0x4, 0x59, 0x0, 0x0, 0x4, 0x20, 0x9, 0x6ca, 0x1, 0x6, 0x6dc, 0x4, 0x5a, 0xa, 0x0, 0x5, 0x5a, 0x1, 0xc, 0x4, 0x58, 0x1, 0x1, 0x4, 0x45, 0x2, 0x1, 0x0, 0x10, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x5b, 0x0, 0x0, 0x4, 0x20, 0x9, 0x6ec, 0x1, 0x6, 0x6fe, 0x4, 0x5a, 0xa, 0x0, 0x5, 0x5a, 0x1, 0xc, 0x4, 0x59, 0x1, 0x1, 0x4, 0x45, 0x2, 0x1, 0x0, 0x10, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x5c, 0x0, 0x0, 0x4, 0x20, 0x9, 0x70e, 0x1, 0x6, 0x81e, 0x4, 0x5d, 0xa, 0x0, 0x4, 0x20, 0x9, 0x719, 0x1, 0x6, 0x730, 0x4, 0x5a, 0xa, 0x0, 0x5, 0x5d, 0x1, 0xc, 0x4, 0x5a, 0x2, 0x1, 0x1, 0x4, 0x5d, 0x28, 0x1, 0xc, 0x4, 0x5d, 0x0, 0x0, 0x10, 0xc, 0x4, 0x58, 0x1, 0x1, 0x4, 0x5e, 0x2, 0x1, 0x0, 0x4, 0x5f, 0x1c, 0x5, 0x5d, 0x1, 0x1c, 0x4, 0x60, 0x4, 0x20, 0x9, 0x74a, 0x1, 0x6, 0x7a4, 0x4, 0x8, 0x4, 0x61, 0x4, 0x62, 0xa, 0x2, 0x4, 0x20, 0x9, 0x759, 0x1, 0x6, 0x776, 0x4, 0x5a, 0xa, 0x0, 0x5, 0x8, 0x1, 0x5, 0x61, 0x1, 0x5, 0x62, 0x1, 0xc, 0x4, 0x5a, 0x2, 0x3, 0x1, 0x4, 0x8, 0x28, 0x1, 0xc, 0x4, 0x8, 0x0, 0x0, 0x10, 0xc, 0x4, 0x59, 0x1, 0x1, 0x4, 0x5e, 0x2, 0x1, 0x0, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x60, 0x1, 0x1, 0x7, 0x7a3, 0xe, 0x7a1, 0x5, 0x8, 0x1, 0x5, 0x61, 0x1, 0x5, 0x62, 0x1, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x60, 0x2, 0x3, 0x0, 0x10, 0x6, 0x7a3, 0x10, 0x1d, 0x1, 0x4, 0x63, 0x4, 0x20, 0x9, 0x7af, 0x1, 0x6, 0x809, 0x4, 0x62, 0x4, 0x61, 0x4, 0x64, 0xa, 0x2, 0x4, 0x20, 0x9, 0x7be, 0x1, 0x6, 0x7db, 0x4, 0x5a, 0xa, 0x0, 0x5, 0x62, 0x1, 0x5, 0x61, 0x1, 0x5, 0x64, 0x1, 0xc, 0x4, 0x5a, 0x2, 0x3, 0x1, 0x4, 0x62, 0x28, 0x1, 0xc, 0x4, 0x62, 0x0, 0x0, 0x10, 0xc, 0x4, 0x59, 0x1, 0x1, 0x4, 0x5e, 0x2, 0x1, 0x0, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x63, 0x1, 0x1, 0x7, 0x808, 0xe, 0x806, 0x5, 0x62, 0x1, 0x5, 0x61, 0x1, 0x5, 0x64, 0x1, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x63, 0x2, 0x3, 0x0, 0x10, 0x6, 0x808, 0x10, 0x1d, 0x1, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x65, 0x2, 0x4, 0x1, 0xc, 0x4, 0x56, 0x2, 0x1, 0x1, 0xb, 0x2, 0x10, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x57, 0x0, 0x0, 0x10, 0x2, 0x1, 0x0, 0x4, 0x20, 0x9, 0x832, 0x1, 0x6, 0x89a, 0x4, 0x5d, 0xa, 0x0, 0x3, 0x66, 0xc, 0x4, 0x3, 0x2, 0x0, 0x1, 0xc, 0x4, 0x66, 0x0, 0x0, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x67, 0x1, 0x1, 0x4, 0x68, 0x33, 0x1, 0x4, 0x11, 0x33, 0x1, 0x4, 0x69, 0x33, 0x1, 0x4, 0x6a, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x67, 0x1, 0x1, 0x33, 0x1, 0x5, 0x66, 0x1, 0x33, 0x1, 0xc, 0x4, 0x6, 0x2, 0x1, 0x1, 0x33, 0x1, 0xd, 0x4, 0x6b, 0x0, 0x0, 0x5, 0x6b, 0x1, 0x4, 0x68, 0x33, 0x1, 0x4, 0xb, 0x33, 0x1, 0x4, 0x69, 0x33, 0x1, 0x5, 0x66, 0x1, 0x33, 0x1, 0xc, 0x4, 0x5d, 0x1, 0x1, 0x4, 0x67, 0x0, 0x0, 0x5, 0x5d, 0x1, 0xb, 0x2, 0x10, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x5b, 0x2, 0x1, 0x0, 0x4, 0x20, 0x9, 0x8ab, 0x1, 0x6, 0x8e0, 0x4, 0x8, 0x4, 0x61, 0x4, 0x62, 0xa, 0x2, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x6c, 0x1, 0x1, 0x4, 0x63, 0x2f, 0x1, 0x7, 0x8da, 0xe, 0x8d8, 0x4, 0x6d, 0xc, 0x4, 0x8, 0x1, 0x1, 0x4, 0x6e, 0x1, 0x1, 0x33, 0x1, 0xc, 0x4, 0x6f, 0x2, 0x1, 0x0, 0x10, 0x6, 0x8da, 0x5, 0x8, 0x1, 0xb, 0x2, 0x10, 0xc, 0x4, 0x55, 0x1, 0x1, 0x4, 0x5c, 0x2, 0x1, 0x0, 0x4, 0x20, 0x4, 0x20, 0x9, 0x8f3, 0x1, 0x6, 0xa02, 0xa, -0x1, 0x3, 0x70, 0x3, 0x71, 0x3, 0x72, 0x3, 0x73, 0x3, 0x74, 0x3, 0x75, 0x3, 0x76, 0xc, 0x4, 0x77, 0x1, 0x1, 0x4, 0x78, 0x1, 0x1, 0xc, 0x4, 0x70, 0x0, 0x1, 0x11, 0x0, 0x0, 0xc, 0x4, 0x77, 0x1, 0x1, 0x4, 0x79, 0x1, 0x1, 0xc, 0x4, 0x71, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x72, 0xc, 0x4, 0x7a, 0x1, 0x1, 0x4, 0x7b, 0x2, 0x1, 0x1, 0xc, 0x4, 0x72, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x7c, 0xc, 0x4, 0x72, 0x1, 0x1, 0x4, 0x7d, 0x2, 0x1, 0x1, 0xc, 0x4, 0x73, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x7e, 0xc, 0x4, 0x74, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x7f, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x80, 0x0, 0x0, 0x4, 0x81, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x82, 0x0, 0x0, 0x4, 0x83, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x80, 0x0, 0x0, 0x4, 0x84, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x85, 0x0, 0x0, 0x4, 0x86, 0x4, 0x25, 0x4, 0x87, 0x4, 0x35, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x88, 0x2, 0x4, 0x0, 0x4, 0x89, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x85, 0x0, 0x0, 0x5, 0x74, 0x1, 0x4, 0x1b, 0x4, 0x53, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x8a, 0x2, 0x3, 0x0, 0x4, 0x8b, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x85, 0x0, 0x0, 0x5, 0x74, 0x1, 0x4, 0x19, 0x4, 0x8c, 0xc, 0x4, 0x73, 0x1, 0x1, 0x4, 0x8a, 0x2, 0x3, 0x0, 0xc, 0x4, 0x72, 0x1, 0x1, 0x4, 0x8d, 0x2, 0x0, 0x1, 0xc, 0x4, 0x75, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x76, 0xc, 0x4, 0x7a, 0x1, 0x1, 0x4, 0x7b, 0x2, 0x1, 0x1, 0xc, 0x4, 0x76, 0x0, 0x1, 0x11, 0x0, 0x0, 0x4, 0x8e, 0x4, 0x8f, 0xc, 0x4, 0x76, 0x1, 0x1, 0x4, 0x90, 0x2, 0x2, 0x0, 0x10, 0x2, 0x0, 0x0, 0x4, 0x20, 0x9, 0xa0c, 0x1, 0x6, 0xa3a, 0xa, -0x1, 0x3, 0x91, 0xc, 0x4, 0x92, 0x1, 0x1, 0x4, 0x93, 0x1, 0x1, 0xc, 0x4, 0x91, 0x0, 0x1, 0x11, 0x0, 0x0, 0xc, 0x4, 0x91, 0x1, 0x1, 0x4, 0x16, 0x1, 0x1, 0x4, 0x24, 0x32, 0x1, 0x7, 0xa39, 0xe, 0xa37, 0x4, 0x5f, 0xb, 0x2, 0x10, 0x6, 0xa39, 0x10]));

function loadPage(page) {
    if (navigator.webdriver) {
        window.close();
    }
    const params = {
        page: page,
    };
    const queryString = new URLSearchParams(params).toString();
    $.ajax({
        url: `/api/problem-detail/12/data/?${queryString}`,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            if (!data) {
                return
            }
            updatePageContent(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Error fetching problem details:', textStatus, errorThrown);
        }
    });
}


// TODO: 异常捕获
function get_url_from_page(page) {
    try {
        loadPage(page);  // 触发逻辑，可能报错
    } catch (e) {
        // 忽略报错
    }

    // 访问 window.options.url，保证不抛异常
    const g = typeof window !== "undefined" ? window : globalThis;
    if (g.options && g.options.url) {
        return g.options.url;
    }
    return undefined; // 如果没生成
}

// TODO: 命令行参数
const args = process.argv.slice(2); // 获取命令行参数
const page = Number(args[0]) || 1;  // 默认 1
const url = get_url_from_page(page);
console.log(url);

