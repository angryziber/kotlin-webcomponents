import org.w3c.dom.*
import kotlin.browser.window
import kotlin.reflect.KProperty

abstract class CustomTag(val tag: String) {
    protected val observedAttributes: Array<String> = emptyArray()
    @JsName("init") open fun init(el: HTMLElement) {}
    @JsName("mounted") open fun mounted() {}
    @JsName("unmounted") open fun unmounted() {}
    @JsName("attributeChanged") open fun attributeChanged(name: String, oldVal: String, newVal: String) {}

    companion object {
        private val wrapImpl = jsFunction("impl", jsCode = ES6_CLASS_ADAPTER) as (impl: CustomTag) -> () -> dynamic

        fun define(vararg tags: CustomTag) {
            tags.forEach { tag ->
                window.customElements.define(tag.tag, wrapImpl(tag))
            }
        }
    }
}

abstract class RenderableCustomTag(tag: String) : CustomTag(tag) {
    protected lateinit var outer: HTMLElement
    protected lateinit var inner: HTMLElement

    // language=html
    abstract fun render(): String

    override fun init(element: HTMLElement) {
        outer = element
        inner = element.attachShadow(ShadowRootInit(ShadowRootMode.OPEN)).unsafeCast<HTMLElement>()
    }

    override fun mounted() = rerender()
    override fun attributeChanged(name: String, oldVal: String, newVal: String) = rerender()

    protected open fun rerender() {
        inner.innerHTML = render()
    }

    protected inner class attr(private val name: String) {
        init {
            observedAttributes.asDynamic().push(name)
        }

        operator fun getValue(thisRef: RenderableCustomTag?, prop: KProperty<*>) =
          thisRef?.outer?.attr(name)

        operator fun setValue(thisRef: RenderableCustomTag?, prop: KProperty<*>, value: String) =
          thisRef?.outer?.attr(name, value)
    }
}

abstract class BindableCustomTag(tag: String): RenderableCustomTag(tag) {
    override fun init(element: HTMLElement) {
        super.init(element)
        inner.on("change") { e ->
            val input = e.target as? HTMLInputElement
            val bindProp = input?.attr("bind")
            if (bindProp != null) outer.setAttribute(bindProp, input.value)
        }
    }

    override fun rerender() {
        super.rerender()
        inner.findAll<HTMLInputElement>("[bind]").forEach { el ->
            el.value = outer.attr(el.attr("bind")!!)!!
        }
    }
}

@JsName("Function")
private external fun <T> jsFunction(vararg params: String, jsCode: String): T

// language=es6
private const val ES6_CLASS_ADAPTER = """return class extends HTMLElement {
    static get observedAttributes() {return impl.observedAttributes}
    constructor() {super(); this.inst = new impl.constructor(); this.inst.init(this)}
    connectedCallback() {this.inst.mounted()}
    disconnectedCallback() {this.inst.unmounted()}
    attributeChangedCallback(attrName, oldVal, newVal) {this.inst.attributeChanged(attrName, oldVal, newVal)}
}"""
