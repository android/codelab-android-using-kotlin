# 3. Kotlin Conversion Basics

## Convert the Contact POJO to a Kotlin Data Class

### Problems with the Contact class file

> __Kotlin Tips__: Look for these boxes throughout this codelab to find the new Kotlin syntax used in each step.

The Contact.java class should look pretty familiar, as it contains standard POJO code:

* Private fields, in this case three strings.
* A constructor to initially set those fields.
* Getters for the fields you want to obtain externally, in this case all of the fields.
* Setters for the fields you want to set externally, in this case just the `email` field.

This kind of object can cause some problems to the unwary developer, because it leaves a few questions open:

1. __Nullability__: which of these fields can be `null`? The fact that the first name and last name can only be set through the constructor and don't have setter methods implies that they are meant to be non nullable, but this is not a guarantee: one could still pass `null` into the constructor for one of the fields. The email does have a setter, so for this one there is no way to know whether it should be nullable or not.
1. __Mutability__: which of these fields might change? Because only the email field has a setter defined, the implication is that only that field is mutable.

The fact that the Java language does not force you to consider the potential `null` cases, as well as the setting of fields meant to be read-only, can lead to runtime errors such as the dreaded `NullPointerException`.

Kotlin helps to solve these problems by forcing the developer to think about them while the class is being written, and not at runtime.

### Use the converter
    
1. Select Contact.java in the Project pane.
1. Select __Code > Convert Java File to Kotlin File__.
1. A dialog appears that warns you that there is code in the rest of the project that may require some corrections to work with the conversion (the Contact class is used in the activity). Click __OK__ to make those changes.
1. Android Studio may still claim Kotlin is not configured, regardless of step 1. A rsync with Gradle will remove the warning. 

The converter runs and changes the Contact.java file extension to .kt. All of the code for the Contact class reduces to the following single line:

```kotlin
internal class Contact(val firstName: String, val lastName: String, var email: String?)
```

In summary, this class declaration does the following:

1. Declares a class called Contact.
1. Declares three properties for the class: two read-only fields for the first and last name, and one mutable variable for the email address.
1. The firstName and lastName properties are guaranteed never to be `null`, since they are of type `String`. The converter can guess this because the Java code did not have setters or secondary constructors that don't set all three properties.
1. The email field may be `null`, since it is of type `String?`.

> __Kotlin Tips__:
> 
> * Classes in Kotlin can have properties, which replace fields in the Java language. These can be declared as mutable, using the `var` keyword or immutable using the `val` keyword.
> * Mutable properties (declared with `var`) can be assigned any number of times, while the immutable variables (with `val`) can only be assigned once.
> * Kotlin property types come after the property name and a colon `val firstName: String`.
> * The primary constructor is part of the class declaration: it follows the class name in parentheses. You can provide additional constructors using the `constructor` keyword in the body of the class.
> * The properties are declared in the primary constructor.
> * By default, property types in Kotlin cannot be null. If a property can hold a null value, declare it with a nullable type, using the `?` syntax. For example a `String?` can be null but a `String` cannot.
> 
> See the [Properties and Fields](https://kotlinlang.org/docs/reference/properties.html) documentation.

### Kotlin Data Classes
    
1. Run the app, and use the __Generate__ menu option in the app to create some contacts. If you want to reset the app and clear your contacts, choose __Clear__ from the Options menu.
1. In your code, the `generateContacts()` method uses the `toString()` method to log each contact being created. Check the logs to see the output of this statement. The default `toString()` method uses the object's hashcode to identify the object, but it doesn't give you any useful information about the object's contents:

   ```
   generateContacts: com.example.android.myaddressbook.Contact@d293353
   ```
1. In your Contact.kt file, add the `data` keyword after the `internal` visibility modifier.
   ```kotlin
   internal data class Contact 
   (val firstName: String, val lastName: String, var email: String?)
   ``` 

1. Run the app again and generate contacts. Check your logs for the much more informative output.
    
> __Kotlin Tips__:
>    
> The `data` keyword tells the Kotlin compiler that the class is used for storing data, which generates a number of useful methods, such as a `toString()` implementation that reveals the object's contents, a `copy()` method, and component functions used for [destructuring](https://kotlinlang.org/docs/reference/multi-declarations.html) the object into its fields. See the [Data class](https://kotlinlang.org/docs/reference/data-classes.html) documentation.

## Convert ContactsActivity to Kotlin

The next step is to convert the ContactsActivity to Kotlin. In this process you will learn about some of the limitations of the converter, and some new Kotlin keywords and syntax.

1. Run the converter on ContactsActivity, using the same process as you did for the Contact.java file (__Code > Convert Java File to Kotlin File__).

### The lateinit keyword

1. Note that the conversion process changed the Java member variables such as `mContacts` into Kotlin properties.
   ```kotlin
   private var mContacts: ArrayList<Contact>? = null
   ```
   
   All of these properties are marked as nullable except the boolean, since they are not initialized with any values until `onCreate()` is executed and are therefore null when the activity is created.
   This is not ideal, since anytime you want to use a method or set a property, you will have to check if the reference is null first (otherwise the compiler will throw an error to avoid a possible `NullPointerException`).
   In Android apps, you usually initialize member variables in an activity lifecycle method, such as `onCreate()`, rather than when the instance is instantiated.
   
   Fortunately, Kotlin has a keyword for precisely this situation: `lateinit`. Use this keyword when you know the value of a property will not be `null` once it is initialized.
   
1. Add the `lateinit` keyword after the `private` visibility modifier to all of the member variables except the initialized boolean. Remove the `null` assignment, and change the type to be not nullable by removing the `?`.

   ```kotlin
   private lateinit var mContacts: ArrayList<Contact>
   ```

1. Remove the `Boolean` property type from the `mEntryValid` member variable (and the preceding colon), because Kotlin can infer the type from the assignment:
    ```kotlin
    private var mEntryValid = false
    ```

1. Remove all of the occurrences of the `!!` operator. You can select all occurrences of an existing selection by pressing selecting __Edit > Find> Select all occurrences__ and pressing the backspace key.

> __Important__: Depending on the version of the Kotlin plugin you are using, you may have an error in the readContactJsonFile() method. If the return type is String, there will be an error as the contactsString variable is of type String?. Change the return type of the method to String? to fix the error.

> __Kotlin Tips__:
>
> * Semicolons at the end of statements are optional in Kotlin.
> * Functions are declared with the fun keyword.
> * The `lateinit` keyword allows you to defer initializing a non-null variable until a later time.
> * The property type can be omitted if it is unambiguous, such as in `val isActive = false`.
> * You can access the fields and methods of a nullable type using the !! operator. This indicates to the compiler that you know the reference will not be null at the time of the call. Use caution with this operator as it could result in a `NullPointerException`. See the [Null Safety](https://kotlinlang.org/docs/reference/null-safety.html) documentation for other ways to accomplish to reference nullable values.

### Clean up ContactsActivity
    
Your activity should now work as expected. There are a few changes left to finish cleaning up the converted activity:

1. In the ViewHolder inner class, the nameLabel variable is underlined. Select the variable and press on the orange lightbulb and select Join declaration and assignment.
1. Repeat step 1 for the emailLabel variable.
    In the generateContacts() method, note the range syntax in the for loop:
   
   ```kotlin
   for (i in 0 until contactsJson.length())
   ```
   
   The `until` notation indicates a half open ranges range, meaning that the final value is excluded in the range. Using this notation can avoid [off by one](https://en.wikipedia.org/wiki/Off-by-one_error) errors. It does not need an additional `-1`. Forgetting a `-1` on a closed or inclusive range will get an `IndexOutofBoundsException` since the first index of the list is 0 (the final index is therefore the length of the list - 1).
   
1. Compare the syntax of the half open or exclusive `until` range with that the closed or inclusive range indicated with the `..` notation.

```kotlin
for (i in 0..contactsJson.length() - 1)
```

> __Kotlin Tips__:
> 
> * Perform any constructor initialization code for a class inside an `init {}` block.
> * Define ranges in Kotlin using the `start..end` notation for closed ranges and `start until end` notation for half open ranges.
    
### String interpolation
    
Kotlin includes support for string interpolation: the process of evaluating a string literal containing one or more placeholders, yielding a result in which the placeholders are replaced with their corresponding values.
    
1. In onBindViewHolder() compare the assignment of the fullName variable with the following line:

```kotlin
val fullName = firstName + " " + lastName
```

    
> __Kotlin Tips__:
>
> The Kotlin converter makes a lot of changes that make that code a lot more readable. Visit the Reference section at the end of the codelab to learn about some of these changes.
