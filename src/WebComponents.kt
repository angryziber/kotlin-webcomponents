import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode
import kotlin.browser.window

abstract class CustomTag(val tag: String, val observedAttributes: Array<String>? = emptyArray()) {
    @JsName("init") open fun init(el: HTMLElement) {}
    @JsName("mounted") open fun mounted() {}
    @JsName("unmounted") open fun unmounted() {}
    @JsName("attributeChanged") open fun attributeChanged(name: String, oldVal: String, newVal: String) {}

    companion object {
        private val wrapImpl = jsFunction("spec", block = ES6_CLASS_ADAPTER) as (spec: CustomTag) -> () -> dynamic

        fun define(vararg tags: CustomTag) {
            tags.forEach { tag ->
                window.customElements.define(tag.tag, wrapImpl(tag))
            }
        }
    }
}

abstract class RenderableCustomTag(name: String) : CustomTag(name) {
    lateinit var shadow: HTMLElement

    // language=html
    abstract fun render(): String

    override fun init(el: HTMLElement) {
        shadow = el.attachShadow(ShadowRootInit(ShadowRootMode.OPEN)).unsafeCast<HTMLElement>()
        doRender()
    }

    override fun attributeChanged(name: String, oldVal: String, newVal: String) {
        doRender()
    }

    private fun doRender() {
        shadow.innerHTML = render()
    }
}

@JsName("Function")
private external fun <T> jsFunction(vararg params: String, block: String): T

// language=es6
private const val ES6_CLASS_ADAPTER = """return class extends HTMLElement {
    static get observedAttributes() {return spec.observedAttributes}
    constructor() {super(); spec.init(this)}
    connectedCallback() {spec.mounted()}
    disconnectedCallback() {spec.unmounted()}
    attributeChangedCallback(attrName, oldVal, newVal) {spec.attributeChanged(attrName, oldVal, newVal)}
}"""
