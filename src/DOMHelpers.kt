import org.w3c.dom.Element
import org.w3c.dom.events.Event

inline fun Element.on(event: String, noinline handler: (Event) -> Unit) = addEventListener(event, handler)
