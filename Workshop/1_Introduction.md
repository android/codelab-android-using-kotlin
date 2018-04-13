# 1. Introduction
  
At I/O 2017, Google [announced official support for Kotlin](https://www.youtube.com/watch?v=X1RVYt2QKQE) 
for developing Android apps. [Kotlin](https://kotlinlang.org/) is a language developed by Jetbrains, with a 
quickly growing developer base because of its concise, elegant syntax, and 100% interoperability with Java.

## What you will build

In this codelab, you will convert an address book application written in the Java language to Kotlin. In doing so, you will see how Kotlin can:
  
* Help reduce boilerplate code.
* Provide concise, readable code that is easy to maintain.
* Avoid common Android development pitfalls.
* Enforce best practices as you write the code, preventing runtime errors.

## What you'll learn

* How to use the Android Studio's Java to Kotlin converter.
* How to write code using Kotlin syntax.
* How to use Kotlin lambda expressions and extension functions to reduce boilerplate code and avoid common errors.
* How to use built-in standard library functions to extend existing Java class functions.
* How Kotlin can help avoid the Billion Dollar Mistake, the NullPointerException.

## What you'll need
   
* Android Studio 3.1
* An Android device or emulator to run the app on
* The sample code (download link below)
* Basic knowledge of developing Android apps in the Java language

## Attributions

These markdown files are a adaptation of the Google codelab [Taking Advantage of Kotlin](https://codelabs.developers.google.com/codelabs/taking-advantage-of-kotlin/index.html) Containing these changes:
1. Based on Android Studio 3.1.1 with Kotlin 1.2.21. That includes the newer code converting.
1. Remove the use of [Kotlin View Binding](https://kotlinlang.org/docs/tutorials/android-plugin.html) This early in the lab make this extension a distraction from the Kotlin language features.
1. Use git tags instead of directories to store intermediate code steps. A repo with multiple projects has proven to be a bit unexpected for many developers.
1. Add a step that introduces Android KTX during dealing with SharedPreferences