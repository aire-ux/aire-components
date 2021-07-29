(function (exports) {
    'use strict';

    function loadText(url, method) {
        if (method === void 0) { method = 'GET'; }
        return new Promise(function (resolve, reject) {
            var request = new XMLHttpRequest();
            request.onload = function () {
                if (request.status >= 200 && request.status < 300) {
                    resolve(request.responseText);
                }
                else {
                    reject(new Error("\n              Failed to load value at " + url + " via method " + method + ". Reason: " + request.statusText + " (code: " + request.status + "\n            "));
                }
            };
            request.onerror = function () {
                reject(new Error("\n              Failed to load value at " + url + " via method " + method + ". Reason: " + request.statusText + " (code: " + request.status + "\n            "));
            };
            request.open(method, url);
            request.send();
        });
    }
    /**
     * walk the dom
     * does not risk a stack overflow error
     * @param from the element to start the traversal at
     * @param f the function to apply to each element
     * @param t a cast/filter function to transform the element to something consumable
     * if t returns null for an element, traversal to its children is not halted
     */
    function walkDom(from, f, t) {
        if (from === void 0) { from = document.documentElement; }
        if (t === void 0) { t = function (e) { return e; }; }
        var stack = [from];
        while (stack.length) {
            var el = stack.pop(), e = t(el);
            if (e) {
                f(e);
            }
            stack.push.apply(stack, Array.from(el.children));
        }
    }
    /**
     *
     * @param properties the properties to load a stylesheet from
     */
    function constructStyleSheetFrom(properties) {
        var _a;
        var textLoader = (_a = properties.urlLoader) !== null && _a !== void 0 ? _a : loadText;
        return new Promise(function (resolve, reject) {
            textLoader(properties.content, 'GET').then(function (styleDefinition) {
                var stylesheet = new CSSStyleSheet();
                stylesheet.replace(styleDefinition).then(function (_) {
                    resolve(stylesheet);
                });
            });
        });
    }
    /**
     *
     * @param installationInstructions the installation instructions to collect the elements over
     */
    function collectElements(installationInstructions) {
        var applicableTo = installationInstructions.applicableTo, elements = [];
        if (!applicableTo) {
            return;
        }
        var predicates = [];
        if (applicableTo.matchingAttributeExistence) {
            var attrs_1 = applicableTo.matchingAttributeExistence;
            predicates.push(function (el) {
                for (var _i = 0, attrs_2 = attrs_1; _i < attrs_2.length; _i++) {
                    var attribute = attrs_2[_i];
                    if (el.hasAttribute(attribute)) {
                        return true;
                    }
                }
                return false;
            });
        }
        if (applicableTo.matchingAttributeValues) {
            var attrs_3 = applicableTo.matchingAttributeValues;
            predicates.push(function (el) {
                for (var _i = 0, attrs_4 = attrs_3; _i < attrs_4.length; _i++) {
                    var _a = attrs_4[_i], key = _a[0], value = _a[1];
                    if (el.getAttribute(key) === value) {
                        return true;
                    }
                }
                return false;
            });
        }
        var matches = function (element) {
            for (var _i = 0, predicates_1 = predicates; _i < predicates_1.length; _i++) {
                var predicate = predicates_1[_i];
                if (predicate(element)) {
                    return true;
                }
            }
            return false;
        };
        walkDom(document.documentElement, function (element) {
            if (matches(element)) {
                elements.push(element);
            }
        });
        if (applicableTo.matchingQuerySelectors) {
            var joinedSelector = applicableTo.matchingQuerySelectors.join(",");
            elements.push.apply(elements, Array.from(document.querySelectorAll(joinedSelector)));
        }
        if (applicableTo.matchingTagNames) {
            var joinedSelector = applicableTo.matchingTagNames.join(",");
            elements.push.apply(elements, Array.from(document.querySelectorAll(joinedSelector)));
        }
        return elements;
    }

    /*! *****************************************************************************
    Copyright (c) Microsoft Corporation.

    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
    REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
    AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
    INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
    LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
    OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
    PERFORMANCE OF THIS SOFTWARE.
    ***************************************************************************** */

    function __awaiter(thisArg, _arguments, P, generator) {
        function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
        return new (P || (P = Promise))(function (resolve, reject) {
            function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
            function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
            function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
            step((generator = generator.apply(thisArg, _arguments || [])).next());
        });
    }

    function __generator(thisArg, body) {
        var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
        return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
        function verb(n) { return function (v) { return step([n, v]); }; }
        function step(op) {
            if (f) throw new TypeError("Generator is already executing.");
            while (_) try {
                if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
                if (y = 0, t) op = [op[0] & 2, t.value];
                switch (op[0]) {
                    case 0: case 1: t = op; break;
                    case 4: _.label++; return { value: op[1], done: false };
                    case 5: _.label++; y = op[1]; op = [0]; continue;
                    case 7: op = _.ops.pop(); _.trys.pop(); continue;
                    default:
                        if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                        if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                        if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                        if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                        if (t[2]) _.ops.pop();
                        _.trys.pop(); continue;
                }
                op = body.call(thisArg, _);
            } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
            if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
        }
    }

    function __spreadArray(to, from, pack) {
        if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
            if (ar || !(i in from)) {
                if (!ar) ar = Array.prototype.slice.call(from, 0, i);
                ar[i] = from[i];
            }
        }
        return to.concat(ar || from);
    }

    /**
     * installs an inline page style,
     * e.g.
     * <style type="text/css">
     *   content
     * </style>
     *
     */
    var InlinePageStyleInstaller = /** @class */ (function () {
        function InlinePageStyleInstaller(manager) {
            this.manager = manager;
        }
        InlinePageStyleInstaller.prototype.install = function (properties) {
            var styleElement = document.createElement('style');
            styleElement.textContent = properties.content;
            return new Promise(function (resolve) {
                document.head.append(styleElement);
                resolve(new LinkStyleRegistration(styleElement));
            });
        };
        return InlinePageStyleInstaller;
    }());
    /**
     * creates a constructablestylesheet
     */
    var RemoteConstructableStyleInstaller = /** @class */ (function () {
        function RemoteConstructableStyleInstaller(manager) {
            this.manager = manager;
        }
        RemoteConstructableStyleInstaller.prototype.install = function (properties) {
            var _this = this;
            return constructStyleSheetFrom(properties)
                .then(function (styleSheet) {
                var manager = _this.manager;
                manager.enqueueConstructableStyleSheet(styleSheet);
                return new ConstructableStyleRegistration(manager, styleSheet);
            });
        };
        return RemoteConstructableStyleInstaller;
    }());
    var ConstructableStyleRegistration = /** @class */ (function () {
        function ConstructableStyleRegistration(manager, styleSheet) {
            this.manager = manager;
            this.styleSheet = styleSheet;
        }
        ConstructableStyleRegistration.prototype.remove = function () {
            this.manager.enqueueConstructableStyleSheetForRemoval(this.styleSheet);
        };
        return ConstructableStyleRegistration;
    }());
    /**
     * installs a global page style to the document's head, e.g.
     *
     * <link rel="stylesheet" type="text/css" href="properties.conent"></link>
     */
    var GlobalPageStyleInstaller = /** @class */ (function () {
        function GlobalPageStyleInstaller(manager) {
            this.manager = manager;
        }
        GlobalPageStyleInstaller.prototype.install = function (properties) {
            var styleElement = document.createElement('link');
            styleElement.rel = 'stylesheet';
            styleElement.type = 'text/css';
            styleElement.href = properties.content;
            return new Promise(function (resolve, reject) {
                document.head.append(styleElement);
                styleElement.onload = function (event) {
                    resolve(new LinkStyleRegistration(styleElement));
                };
                styleElement.onerror = function (event) {
                    reject(new ErrorStyleRegistration(event, styleElement));
                };
            });
        };
        return GlobalPageStyleInstaller;
    }());

    var StyleElementDefinition = /** @class */ (function () {
        /**
         *
         * @param properties the properties to construct this
         * page style definition with
         */
        function StyleElementDefinition(properties) {
            this.properties = properties;
        }
        StyleElementDefinition.initialize = function () {
            var bySource = new Map();
            bySource.set('remote', new Map());
            bySource.set('inline', new Map());
            bySource.get('inline').set('page', InlinePageStyleInstaller);
            bySource.get('remote').set('page', GlobalPageStyleInstaller);
            bySource.get('remote').set('constructable', RemoteConstructableStyleInstaller);
            StyleElementDefinition.installers = bySource;
            // bySource.get('remote').set('constructable', InlinePageStyleInstaller)
            // PageStyleDefinition.installers = new Map();
        };
        StyleElementDefinition.prototype.install = function (manager) {
            var properties = this.properties, installers = StyleElementDefinition.installers, sourceMap = installers.get(properties.source), installer = sourceMap && sourceMap.get(properties.mode);
            if (installer) {
                return new installer(manager).install(properties);
            }
            return Promise.reject(new ErrorStyleRegistration('no available installer registered'));
        };
        /**
         * apply this page style definition to an element. If this is a global
         * style definition.  This takes the content (local or remote) and constructs
         * a constructable stylesheet with it, then applies that stylesheet to this element
         * @param themeManager the manager to use
         * @param element the element to apply this to
         *
         */
        StyleElementDefinition.prototype.applyToElement = function (themeManager, element) {
            if (!element) {
                throw new Error("Error: element must not be null or undefined");
            }
            if (!element.shadowRoot) {
                throw new Error("Error: element must have a shadowroot");
            }
            var shadowRoot = element.shadowRoot;
            if (shadowRoot.adoptedStyleSheets) {
                return this.createStyleSheet().then(function (styleSheet) {
                    shadowRoot.adoptedStyleSheets = __spreadArray(__spreadArray([], shadowRoot.adoptedStyleSheets), [
                        styleSheet
                    ]);
                });
            }
            throw new Error("Could not apply stylesheet");
        };
        StyleElementDefinition.prototype.createStyleSheet = function () {
            var properties = this.properties, mode = properties.mode;
            if (mode !== 'constructable') {
                throw new Error("Error: cannot construct a stylesheet in 'page' mode");
            }
            return constructStyleSheetFrom(properties);
        };
        return StyleElementDefinition;
    }());
    StyleElementDefinition.initialize();
    /**
     * internal registration
     */
    var LinkStyleRegistration = /** @class */ (function () {
        function LinkStyleRegistration(element) {
            this.element = element;
        }
        LinkStyleRegistration.prototype.remove = function () {
            this.element.remove();
        };
        return LinkStyleRegistration;
    }());
    var ErrorStyleRegistration = /** @class */ (function () {
        function ErrorStyleRegistration(message, element) {
            this.message = message;
            this.element = element;
        }
        ErrorStyleRegistration.prototype.remove = function () {
            if (this.element) {
                this.element.remove();
            }
        };
        return ErrorStyleRegistration;
    }());

    var LocalInlineScriptInstaller = /** @class */ (function () {
        function LocalInlineScriptInstaller(manager) {
            this.manager = manager;
        }
        LocalInlineScriptInstaller.prototype.install = function (definition) {
            var script = createElement(definition);
            return new Promise(function (resolve, reject) {
                script.textContent = definition.content;
                document.head.append(script);
                resolve(new DefaultScriptRegistration(script));
            });
        };
        return LocalInlineScriptInstaller;
    }());
    var RemoteInlineScriptInstaller = /** @class */ (function () {
        function RemoteInlineScriptInstaller(manager) {
            this.manager = manager;
        }
        RemoteInlineScriptInstaller.prototype.install = function (definition) {
            var script = createElement(definition);
            script.src = definition.content;
            return new Promise(function (resolve, reject) {
                script.onload = function (e) {
                    resolve(new DefaultScriptRegistration(script));
                };
                script.onerror = function (e) {
                    reject(new Error("Error loading script.  Reason: " + e));
                };
                document.head.append(script);
            });
        };
        return RemoteInlineScriptInstaller;
    }());
    function createElement(definition) {
        var element = document.createElement('script');
        element.type = 'text/javascript';
        if (definition.defer) {
            element.defer = definition.defer;
        }
        if (definition.asynchronous) {
            element.async = definition.asynchronous;
        }
        if (definition.integrity) {
            element.integrity = definition.integrity;
        }
        if (definition.type) {
            element.type = definition.type;
        }
        return element;
    }
    var DefaultScriptRegistration = /** @class */ (function () {
        function DefaultScriptRegistration(element) {
            this.element = element;
        }
        DefaultScriptRegistration.prototype.remove = function () {
            this.element.remove();
        };
        return DefaultScriptRegistration;
    }());

    var ScriptElementDefinition = /** @class */ (function () {
        /**
         * create a new script element definition
         * @param properties the properties to use
         */
        function ScriptElementDefinition(properties) {
            this.properties = properties;
        }
        ScriptElementDefinition.initialize = function () {
            var bySource = new Map();
            bySource.set('inline', LocalInlineScriptInstaller);
            bySource.set('remote', RemoteInlineScriptInstaller);
            ScriptElementDefinition.installers = bySource;
        };
        ScriptElementDefinition.prototype.install = function (themeManager) {
            return __awaiter(this, void 0, void 0, function () {
                var props, source, installer;
                return __generator(this, function (_a) {
                    props = this.properties, source = props.source, installer = ScriptElementDefinition.installers.get(source);
                    if (installer) {
                        return [2 /*return*/, new installer(themeManager).install(props)];
                    }
                    return [2 /*return*/, Promise.reject("No script installer for source: " + source)];
                });
            });
        };
        return ScriptElementDefinition;
    }());
    ScriptElementDefinition.initialize();

    /**
     * AireThemeManager
     *
     * this component is responsible for client-side
     * theme-management
     */
    var AireThemeManager = /** @class */ (function () {
        function AireThemeManager() {
            this.styleDefinitions = [];
            this.styleDefinitionsToRemove = [];
        }
        Object.defineProperty(AireThemeManager.prototype, "currentTheme", {
            get: function () {
                if (!this.theme) {
                    throw new Error("Error: no current theme set");
                }
                return this.theme;
            },
            /**
             * @param definition the theme definition to use
             */
            set: function (definition) {
            },
            enumerable: false,
            configurable: true
        });
        AireThemeManager.prototype.installTheme = function (definition) {
            var _this = this;
            if (this.theme) {
                this.uninstall(this.theme);
            }
            this.theme = definition;
            return this.install(definition).then(function (registrations) {
                _this.registrations = registrations;
                return _this.theme;
            });
        };
        AireThemeManager.prototype.removeTheme = function () {
            if (this.theme && this.registrations) {
                this.uninstall(this.theme);
            }
        };
        AireThemeManager.prototype.enqueueConstructableStyleSheet = function (definition) {
            this.styleDefinitions.push(definition);
        };
        AireThemeManager.prototype.enqueueConstructableStyleSheetForRemoval = function (styleSheet) {
            this.styleDefinitionsToRemove.push(styleSheet);
        };
        AireThemeManager.prototype.uninstall = function (theme) {
            if (this.registrations) {
                for (var _i = 0, _a = this.registrations; _i < _a.length; _i++) {
                    var registration = _a[_i];
                    if (registration) {
                        registration.remove();
                    }
                }
                this.registrations.length = 0;
            }
            this.removeInstalledStyles();
        };
        AireThemeManager.prototype.install = function (definition) {
            return __awaiter(this, void 0, void 0, function () {
                var promises;
                var _this = this;
                return __generator(this, function (_a) {
                    promises = [];
                    if (definition.scripts) {
                        this.enqueueScriptDefinitions(promises, definition.scripts);
                    }
                    if (definition.styles) {
                        this.enqueueStyleInstallations(promises, definition.styles);
                    }
                    return [2 /*return*/, Promise.all(promises).then(function (regs) {
                            _this.applyStyles();
                            return regs;
                        })];
                });
            });
        };
        /**
         *
         * @param styleDefinition
         */
        AireThemeManager.prototype.addStyleDefinition = function (styleDefinition) {
            return __awaiter(this, void 0, void 0, function () {
                return __generator(this, function (_a) {
                    return [2 /*return*/, styleDefinition.install(this)];
                });
            });
        };
        /**
         *
         * @param promises the set of promises to add this request to
         * @param scripts the scripts to include
         * @private
         */
        AireThemeManager.prototype.enqueueScriptDefinitions = function (promises, scripts) {
            for (var _i = 0, scripts_1 = scripts; _i < scripts_1.length; _i++) {
                var script = scripts_1[_i];
                promises.push(new ScriptElementDefinition(script).install(this));
            }
        };
        /**
         *
         * @param promises the set of promises to include this registration in
         * @param scripts the scripts to add
         * @private
         */
        AireThemeManager.prototype.enqueueStyleInstallations = function (promises, scripts) {
            for (var _i = 0, scripts_2 = scripts; _i < scripts_2.length; _i++) {
                var script = scripts_2[_i];
                promises.push(new StyleElementDefinition(script).install(this));
            }
        };
        /**
         * walk the set of theme-included elements and adopt the included
         * stylesheets
         * @private
         */
        AireThemeManager.prototype.applyStyles = function () {
            this.applyToIncludedElements(function (e, sr, style) {
                if (sr.adoptedStyleSheets) {
                    sr.adoptedStyleSheets = __spreadArray(__spreadArray([], sr.adoptedStyleSheets), [style]);
                }
            });
        };
        /**
         * remove all the adopted stylesheets that we added
         * @private
         */
        AireThemeManager.prototype.removeInstalledStyles = function () {
            this.applyToIncludedElements(function (e, sr, style) {
                if (sr.adoptedStyleSheets) {
                    sr.adoptedStyleSheets = sr.adoptedStyleSheets.filter(function (s) { return s !== style; });
                }
            });
        };
        /**
         * walk the included elements and apply the function to any with a shadow dom
         * @param f the function to apply
         * @private
         */
        AireThemeManager.prototype.applyToIncludedElements = function (f) {
            if (this.theme && this.theme.installationInstructions) {
                var elements = collectElements(this.theme.installationInstructions), styleDefinitions = this.styleDefinitions;
                if (elements &&
                    elements.length &&
                    styleDefinitions &&
                    styleDefinitions.length) {
                    for (var _i = 0, elements_1 = elements; _i < elements_1.length; _i++) {
                        var element = elements_1[_i];
                        if (element.shadowRoot) {
                            var sr = element.shadowRoot;
                            if (sr.adoptedStyleSheets) {
                                for (var _a = 0, styleDefinitions_1 = styleDefinitions; _a < styleDefinitions_1.length; _a++) {
                                    var style = styleDefinitions_1[_a];
                                    f(element, sr, style);
                                }
                            }
                        }
                    }
                }
            }
        };
        return AireThemeManager;
    }());

    (function () {
        window.Aire = window.Aire || {};
        window.Aire.Utilities = {
            walkDom: walkDom
        };
        if (!window.Aire.ThemeManager) {
            window.Aire.ThemeManager = new AireThemeManager();
        }
    })();

    exports.AireThemeManager = AireThemeManager;
    exports.ErrorStyleRegistration = ErrorStyleRegistration;
    exports.LinkStyleRegistration = LinkStyleRegistration;
    exports.ScriptElementDefinition = ScriptElementDefinition;
    exports.StyleElementDefinition = StyleElementDefinition;
    exports.collectElements = collectElements;
    exports.constructStyleSheetFrom = constructStyleSheetFrom;
    exports.loadText = loadText;
    exports.walkDom = walkDom;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}));
