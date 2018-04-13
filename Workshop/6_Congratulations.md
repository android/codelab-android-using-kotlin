# 6. Congratulations!

## What we've covered

* How to use the Android Studio's Java to Kotlin converter.
* How to write code using Kotlin syntax.
* How to use Kotlin lambda expressions and extension functions to reduce boilerplate code and avoid common errors.
* How to use built-in standard library functions to extend existing Java class functions.

## Reference

### Notable Kotlin syntax changes
  
The Kotlin converter changes a lot of the code to use Kotlin specific syntax. The following section will point out some of these changes that the converter made to the MyAddressBook app.
  
### Kotlin Properties
  
In Kotlin, you can access properties of objects directly, using the object.property syntax, without the need for an access method (getters and setters in Java). You can see this in action in many places throughout the ContactsActivity class:
  
* In the `setupRecyclerView()` method, the `recyclerView.setAdapter(mAdapter)` method is replaced with `recyclerView.adapter = mAdapter` and `viewHolder.getPosition()` with `viewHolder.position`.
* The `EditText` `setText()` and `getText()` methods are replaced throughout with the text property. The `setEnabled()` method is replaced with the `isEnabled` property.
* The size of the `mContacts` list is accessed with `mContacts.size` throughout.
* You can access one of the items in the `mContacts` list using `mContacts[index]` instead of `mContacts.get(index)`.
  
### Lambda Expressions
  
Kotlin supports lambda expressions, which is a function that is not declared before it is used, but can be passed immediately as an expression.
  
  ```kotlin
  { a, b -> a.length < b.length }
  ```
  
This is especially useful wherever you would use an anonymous inner class that implements a single abstract method, such as inside a `setOnClickListener()` method on a view. In this case, you can pass a lambda expression directly instead of the anonymous object. The converter does this automatically for every `OnClickListener` in the ContactsActivity, for example:
  
  ```java
  fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          showAddContactDialog(-1);
      }
  });
  ```
  turns into
  ```kotlin
  fab.setOnClickListener { showAddContactDialog(-1) }
  ```
  
### Destructured declarations
  
Sometimes it is convenient to destructure an object into a number of variables. For example, it is common in the `onBindViewHolder()` method of adapter classes to use the fields of an object to populate the ViewHolder with data.
  
This is made easier in Kotlin with by using destructured declarations, such as the ones the converter creates automatically in the `onBindViewHolder()` declaration. The order of the deconstructed elements the same as in the original class declaration:
  
   ```kotlin
   val (firstName, lastName, email) = mContacts[position]
   ```
  
  You can then use each property to populate the view:
  
  ```kotlin
  val fullName = "$firstName $lastName"
  holder.nameLabel.text = fullName
  holder.emailLabel.text = email
  ```
> __Kotlin Tips__:
>   
> 1. You can access Kotlin properties directly using the `object.property` syntax notation to get and set their values (you won't be able to set a value if it is in an immutable `val`).
> 1. Kotlin supports the use of [lambda](https://kotlinlang.org/docs/reference/lambdas.html) expressions which can be used to simplify anonymous inner classes with a single abstract method. See the Lambda documentation for more information.
> 1. You can destructure an instance into its component properties. See the [Destructuring Declarations](https://kotlinlang.org/docs/reference/multi-declarations.html) documentation for more information.
  
### Where to learn more
    
* [Kotlin Koans](https://try.kotlinlang.org/#/Kotlin%20Koans/Introduction/Hello,%20world!/Task.kt) interactive tutorials
* [Kotlin website](https://kotlinlang.org/docs/tutorials/) tutorial section
* [Kotlin reference](https://kotlinlang.org/docs/reference/)
