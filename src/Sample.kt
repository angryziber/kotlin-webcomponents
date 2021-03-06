fun main() {
  CustomTag.define(MyHello(), MyBinding(), MyCombined())
}

class MyHello: RenderableCustomTag("my-hello") {
  private val name by attr("name")
  // language=html
  override fun render() = """<h1><slot></slot> $name</h1>"""
}

class MyBinding: BindableCustomTag("my-binding") {
  private val name by attr("name")

  // language=html
  override fun render() = """
    <div>
      <input type="text" bind="name">
      <div>You entered: <i>$name</i></div>
    </div>
  """.trimIndent()
}

class MyCombined: RenderableCustomTag("my-combined") {
  private val name by attr("name")

  // language=html
  override fun render() = """
    <div>
      <my-hello name="$name"><i>Hello</i></my-hello>
      <my-binding name="$name"></my-bidinging>
    </div>
  """.trimIndent()
}
