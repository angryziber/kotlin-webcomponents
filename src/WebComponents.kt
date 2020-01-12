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
        private val wrapImpl = jsFunction("impl", jsCode = ES6_CLASS_ADAPTER) as (CustomTag) -> () -> dynamic

        fun define(vararg tags: CustomTag) {
            tags.forEach { tag ->
                window.customElements.define(tag.tag, wrapImpl(tag))
            }
        }
    }
}

abstract class RenderableCustomTag(tag: String) : CustomTag(tag) {
    protected lateinit var element: HTMLElement
    protected lateinit var shadow: HTMLElement

    // language=html
    abstract fun render(): String

    override fun init(el: HTMLElement) {
        element = el
        shadow = el.attachShadow(ShadowRootInit(ShadowRootMode.OPEN)).unsafeCast<HTMLElement>()
    }

    override fun mounted() = doRender()
    override fun attributeChanged(name: String, oldVal: String, newVal: String) = doRender()

    protected open fun doRender() {
        shadow.innerHTML = render()
    }

    protected inner class Attribute(private val name: String) {
        init {
            observedAttributes.asDynamic().push(name)
        }

        operator fun getValue(thisRef: RenderableCustomTag?, prop: KProperty<*>) =
          thisRef?.element?.getAttribute(name)

        operator fun setValue(thisRef: RenderableCustomTag?, prop: KProperty<*>, value: String) =
          thisRef?.element?.setAttribute(name, value)
    }
}

abstract class BindableCustomTag(tag: String): RenderableCustomTag(tag) {
    override fun init(el: HTMLElement) {
        super.init(el)
        shadow.on("change") { e ->
            val input = e.target as? HTMLInputElement
            val bindProp = input?.getAttribute("bind")
            if (bindProp != null) element.setAttribute(bindProp, input.value)
        }
    }

    override fun doRender() {
        super.doRender()
        shadow.findAll<HTMLInputElement>("[bind]").forEach { el ->
            el.value = element.getAttribute(el.getAttribute("bind")!!)!!
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
