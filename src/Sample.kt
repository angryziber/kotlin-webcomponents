fun main() {
  CustomTag.define(MyHello::class)
}

class MyHello: RenderableCustomTag("my-hello") {
  private val name = "World"
  // language=html
  override fun render() = "<h1>Hello $name</h1>"
}
