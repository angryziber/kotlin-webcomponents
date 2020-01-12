fun main() {
  CustomTag.define(MyHello::class)
}

class MyHello: RenderableCustomTag("my-hello", arrayOf("name")) {
  private val name by Attribute()
  // language=html
  override fun render() = """<h1><slot></slot> $name</h1>"""
}
