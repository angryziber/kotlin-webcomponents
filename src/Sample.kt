fun main() {
  CustomTag.define(MyHello(), MyBinding())
}

class MyHello: RenderableCustomTag("my-hello") {
  private val name by attr("name")
  // language=html
  override fun render() = """<h1><slot></slot> $name</h1>"""
}

class MyBinding: BindableCustomTag("my-binding") {
  private val name by attr("name")

  override fun render() = """
    <input type="text" bind="name">
  """.trimIndent()
}
