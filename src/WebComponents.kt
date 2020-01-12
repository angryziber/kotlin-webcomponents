import org.w3c.dom.HTMLElement
import kotlin.browser.window

open class CustomTag(val name: String, val observedAttributes: Array<String>? = emptyArray()) {
    @JsName("init") open fun init(el: HTMLElement) {}
    @JsName("mounted") open fun mounted(el: HTMLElement) {}
    @JsName("unmounted") open fun unmounted(el: HTMLElement) {}
    @JsName("attributeChanged") open fun attributeChanged(el: HTMLElement, attrName: String, oldVal: String, newVal: String) {}
    @JsName("adopted") open fun adopted(el: HTMLElement) {}

    companion object {
        private val wrapImpl = jsFunction("spec", block = ES6_CLASS_ADAPTER) as (spec: CustomTag) -> () -> dynamic

        fun define(vararg tags: CustomTag) {
            tags.forEach { tag ->
                window.customElements.define(tag.name, wrapImpl(tag))
            }
        }
    }
}

@JsName("Function")
private external fun <T> jsFunction(vararg params: String, block: String): T

private const val ES6_CLASS_ADAPTER = """return class extends HTMLElement {
    static get observedAttributes() {
        return spec.observedAttributes
    }

    constructor() {
        super();
        spec.init(this)
    }

    connectedCallback() {
        spec.mounted(this)
    }

    disconnectedCallback() {
        spec.unmounted(this)
    }

    attributeChangedCallback(attrName, oldVal, newVal) {
        spec.attributeChanged(this, attrName, oldVal, newVal)
    }

    adoptedCallback() {
        spec.adopted(this)
    }
}"""
