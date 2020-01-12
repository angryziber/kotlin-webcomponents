fun main() {
  CustomTag.define(MyHello::class, MyBinding::class)
}

class MyHello: RenderableCustomTag("my-hello", arrayOf("name")) {
  private val name by Attribute()
  // language=html
  override fun render() = """<h1><slot></slot> $name</h1>"""
}

class MyBinding: BindableCustomTag("my-binding", arrayOf("name")) {
  private val name by Attribute()

  override fun render() = """
    <input type="text" value="$name" bind="name">
  """.trimIndent()
}
