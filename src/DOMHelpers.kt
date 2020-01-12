import org.w3c.dom.*
import org.w3c.dom.events.Event

inline fun Element.on(event: String, noinline handler: (Event) -> Unit) = addEventListener(event, handler)
inline fun <T: Element> Element.find(selector: String) = querySelector(selector) as T?
inline fun <T: Element> Element.findAll(selector: String) = querySelectorAll(selector).asList() as List<T>
