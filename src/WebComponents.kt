import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode
import kotlin.browser.window
import kotlin.reflect.KClass

abstract class CustomTag(val tag: String, val observedAttributes: Array<String>? = emptyArray()) {
    @JsName("init") open fun init(el: HTMLElement) {}
    @JsName("mounted") open fun mounted() {}
    @JsName("unmounted") open fun unmounted() {}
    @JsName("attributeChanged") open fun attributeChanged(name: String, oldVal: String, newVal: String) {}

    companion object {
        private val wrapImpl = jsFunction("impl", "def", jsCode = ES6_CLASS_ADAPTER) as (JsClass<out CustomTag>, CustomTag) -> () -> dynamic

        fun define(vararg tags: KClass<out CustomTag>) {
            tags.forEach { tagClass ->
                val impl = tagClass.js
                val def = js("new impl()") as CustomTag
                window.customElements.define(def.tag, wrapImpl(impl, def))
            }
        }
    }
}

abstract class RenderableCustomTag(tag: String, observedAttributes: Array<String>? = emptyArray()) : CustomTag(tag, observedAttributes) {
    protected lateinit var root: HTMLElement;
    private lateinit var shadow: HTMLElement

    // language=html
    abstract fun render(): String

    override fun init(el: HTMLElement) {
        root = el
        shadow = el.attachShadow(ShadowRootInit(ShadowRootMode.OPEN)).unsafeCast<HTMLElement>()
    }

    override fun mounted() = doRender()
    override fun attributeChanged(name: String, oldVal: String, newVal: String) = doRender()

    private fun doRender() {
        shadow.innerHTML = render()
    }
}

@JsName("Function")
private external fun <T> jsFunction(vararg params: String, jsCode: String): T

// language=es6
private const val ES6_CLASS_ADAPTER = """return class extends HTMLElement {
    static get observedAttributes() {return def.observedAttributes}
    constructor() {super(); this.inst = new impl(); this.inst.init(this)}
    connectedCallback() {this.inst.mounted()}
    disconnectedCallback() {this.inst.unmounted()}
    attributeChangedCallback(attrName, oldVal, newVal) {this.inst.attributeChanged(attrName, oldVal, newVal)}
}"""
