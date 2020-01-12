fun main() {
  CustomTag.define(MyHello::class)
}

class MyHello: RenderableCustomTag("my-hello", arrayOf("name")) {
  private val name get() = root.getAttribute("name")
  // language=html
  override fun render() = "<h1>Hello $name</h1>"
}
