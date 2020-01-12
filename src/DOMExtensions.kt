import org.w3c.dom.*
import org.w3c.dom.events.Event

inline fun Element.on(event: String, noinline handler: (Event) -> Unit) = addEventListener(event, handler)
fun <T: Element> Element.find(selector: String) = querySelector(selector)?.unsafeCast<T>()
fun <T: Element> Element.findAll(selector: String) = querySelectorAll(selector).asList().unsafeCast<List<T>>()
inline fun Element.attr(name: String) = getAttribute(name)
inline fun Element.attr(name: String, value: String) = setAttribute(name, value)
