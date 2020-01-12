import org.w3c.dom.HTMLElement
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode

fun main() {
  CustomTag.define(MyHello())
}

class MyHello: CustomTag("my-hello") {
  override fun init(el: HTMLElement) {
    val shadow = el.attachShadow(ShadowRootInit(ShadowRootMode.OPEN)).unsafeCast<HTMLElement>()
    shadow.innerHTML = "<h1>Hello</h1>"
  }

  override fun attributeChanged(el: HTMLElement, attrName: String, oldVal: String, newVal: String) {
    println("$attrName: $oldVal -> $newVal")
  }
}
