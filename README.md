# Kotlin WebComponents

Due to lack of examples on how to use W3C standard web components in Kotlin/JS, I am trying to create one.

For simplicity, this is an IDEA project without build scripts.

Open the project in IDEA, build it and open `index.html` using IDEA built-in web server.

## Gotchas

WebComponents are built using native ES6 classes, but Kotlin doesn't support ES6 target yet.
Therefore an ES6 adapter is needed, as you can see from the code.
