# 4. Lambdas and Library Extensions

### Add lambdas for validation

Kotlin provides support for lambdas, which are functions that are not declared, but passed immediately as an expression. They have the following syntax:

* A lambda expression is always surrounded by curly braces `{ }`.
* An arrow (`->`) separates the function parameters from the function definition.
* The function's parameters (if any) are declared before the `->`. Parameter types may be omitted if they can be inferred.
    The body of the function is defined after `->`.
    
```kotlin
{ x: Int, y: Int -> x + y }
```

You can then store these expressions in a variable and reuse them.

In this step, you will modify the `afterTextChanged()` method to use lambda expressions to validate the user input when adding or modifying a contact. This method uses the `setCompoundDrawablesWithIntrinsicBounds()` method to set the pass or fail drawable on the right side of the EditText.

You will create two lambda expressions for validating the user input and store them as variables.

1. In the `afterTextChanged()` method, remove the three booleans that check the validity of the three fields.
1. Declare an immutable variable that stores a lambda expression called `notEmpty`. The lambda expression takes a `TextView` as a parameter (`EditText` inherits from `TextView`) and returns a `Boolean`:

    ```kotlin
    val notEmpty: (TextView) -> Boolean
    ```
    
1. Assign a lambda expression to `notEmpty` that returns `true` if the `TextView`'s text property is not empty using the Kotlin `isNotEmpty()` method:

    ```kotlin
    val notEmpty: (TextView) -> Boolean = { textView -> textView.text.isNotEmpty() }
    ```
    
1. If a lambda expression only has a single parameter, it can be omitted and replaced with the `it` keyword. Remove the `textView` parameter and replace its reference with `it` in the lambda body:

    ```kotlin
    val notEmpty: (TextView) -> Boolean = { it.text.isNotEmpty() }
    ```
    
1. Create another lambda with the same signature, and assign it to an `isEmail` variable. This function checks if the `TextView`'s text property matches the email pattern:

    ```kotlin
    val isEmail: (TextView) -> Boolean = { Patterns.EMAIL_ADDRESS.matcher(it.text).matches() }
    ```
    
1. In each call to `EditText.setCompoundDrawablesWithIntrinsicBounds()`, remove the deleted boolean inside the if/else statement:

    ```kotlin
     mFirstNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    if () passIcon else failIcon, null)
    ```
    
1. Replace it with the appropriate validation lambda expression and pass in the `EditText` that needs to be validated:

    ```kotlin
    mFirstNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    if (notEmpty(mFirstNameEdit)) passIcon else failIcon, null)
    ```
    
1. Change the `mEntryValid` boolean to call `notEmpty()` on the first and last name `EditTexts`, and call `isEmail()` on the email EditText:

    ```kotlin
    mEntryValid = notEmpty(mFirstNameEdit) and notEmpty(mLastNameEdit) and isEmail(mEmailEdit)
    ```

Although these changes have not reduced the code much, it is possible to see how these validators can be reused. Using lambda expressions becomes even more useful in combination with higher-order functions, which are functions that take other functions as arguments, which will be discussed in the next section.

> __Kotlin Tips__:
>
>    When a lambda expression has a single parameter, it can be referred to using the it keyword.
    Kotlin has no ternary operator, and uses if/else statements in its place.

### Add sorting
    
In this step, you'll use standard library extension functions to add a sort option to the options menu, allowing the contacts to be sorted by first and last name.

The Kotlin standard library includes the `sortBy()` extension function for mutable lists, including `ArrayLists`, that takes a "selector" function as a parameter. This is an example of a higher-order function, a function that takes another function as parameter. The role of this passed in function is to define a natural sort order for the list of arbitrary objects. The `sortBy()` method iterates through each item of the list it is called on, and performs the selector function on the list item to obtain a value it knows how to sort. Usually, the selector function returns one of the fields of the object that implements the `Comparable` interface, such as a `String` or `Int`.

1. Create two new menu items, to sort the contacts by first name and last name:
   ```xml
   <item
       android:id="@+id/action_sort_first"
       android:orderInCategory="100"
       android:title="Sort by First Name"
       app:showAsAction="never" />
   <item
       android:id="@+id/action_sort_last"
       android:orderInCategory="100"
       android:title="Sort by Last Name"
       app:showAsAction="never" />
   ```

1. In the `onOptionsItemSelected()` method, add two more cases to the when statement (similar to the switch in Java), using the IDs for the cases:

   ```kotlin
   R.id.action_sort_first -> {}
   R.id.action_sort_last -> {}
   ```

   For `R.id.action_sort_first` call the `sortBy()` method on the `mContacts` list. Pass in a lambda expression that takes a `Contact` object as a parameter and returns its first name property. Because the contact is the only parameter, the contact can be referred to as `it`:

   ```kotlin
   mContacts.sortBy { it.firstName }
   ```

1. The list will be reordered, so notify the adapter that the dataset has changed, and return true inside the first case of the `onOptionsItemSelected()` method:

   ```kotlin
   {
      mContacts.sortBy { it.firstName }
      mAdapter.notifyDataSetChanged()
      return true
   }
   ```

1. Copy the code to the "Sort by Last Name" case, changing the lambda expression to return the last name of the passed in contact:

   ```kotlin
   {
       mContacts.sortBy { it.lastName }
       mAdapter.notifyDataSetChanged()
       return true
   }
   ```

    Run the app. You can now sort the contacts by first and last name from the options menu!

### Replace for loops with standard library extensions

The Kotlin standard library adds many extension functions to collections such as `List`s, `Set`s, and `Map`s, to allow conversion between them. In this step, you'll simplify the save and load contacts methods to use the conversion extensions.

1. In the `loadContacts()` method, set the cursor on the `for` keyword.
1. Press the orange light bulb to show the quick fix menu and choose __Replace with mapTo(){}__.

   Android Studio will change the for loop into the `mapTo(){}` function, another higher-order function that takes two arguments: the collection to turn the receiver parameter (The class you are extending) into, and a lambda expression that specifies how to convert the items of the set into items of the list. Note the `it` notation in the lambda, referring to the single passed in parameter (the item in the list).

1. Inline this call into the return statement, as the contacts variable is not useful anymore:

   ```kotlin
   private fun loadContacts(): ArrayList<Contact> {
       val contactSet = mPrefs.getStringSet(CONTACT_KEY, HashSet())
       return contactSet.mapTo<String?, Contact?, ArrayList<Contact>>(ArrayList()) { 
                   Gson().fromJson(it, Contact::class.java) 
               }
   }
   ```
   
1. Remove the mapTo type parameterization, as it can be inferred by the compiler from the lambda in the second argument:

   ```kotlin
   return contactSet.mapTo(ArrayList()) {
       Gson().fromJson(it, Contact::class.java)
   }
   ```

1. In the `saveContacts()` method, set the cursor on the underlined for loop definition.

1. Press Alt + Enter to show the quick fix menu and choose __Replace with map{}.toSet()__.

   Again, Android Studio replaces the loop with an extension function: this time `map{}`, which performs the passed in function on each item in the list (Uses GSON to convert it to a string) and then converts the result to a `Set` using the `toSet()` extension method.

The Kotlin standard library is full of extension functions, particularly higher-order ones that add functionality to existing data structures by allowing you to pass in lambda expressions as parameters. You can also create your own higher-order extension functions, as you'll see in the next section.

> __Kotlin Tips__:
>
> 1. The Kotlin standard library comes with a number of extension methods that add functionality to existing Java data structures.
> 1. These extensions are often higher-order functions that take other functions as arguments (usually passed in as lambda expressions).
> 1. The `sortBy()` extension method extends many of the Java list classes to allow them to be sorted according to the passed in lambda expressions which take an item of the list as an argument and return an object implementing the Comparable interface, defining the natural sort order of the list.
> 1. The `mapTo()` and `map()` methods are also higher-order functions that extend Java lists. The passed in lambda expressions operate on each item in the list.

### Android KTX

A set of [Kotlin extensions for Android](https://github.com/android/android-ktx) app development. The goal of Android KTX is to make Android development with Kotlin more concise, pleasant, and idiomatic by leveraging the features of the language such as extension functions/properties, lambdas, named parameters, and parameter defaults. It is an explicit goal of this project to not add any new features to the existing Android APIs.

1. Add the Android KTX dependency to the app build.gradle and resynchronize with Gradle.

   ```groovy
   dependencies {
       implementation 'androidx.core:core-ktx:0.3'
   }
   ```
   
1. Replace the `edit()` and `apply()` in the method `saveContacts()` with the `edit()` from Android KTX that can take a single lambda as parameter.

   ```kotlin
    mPrefs.edit {
        clear()
        val contactSet = mContacts
                .asSequence()
                .map { Gson().toJson(it) }
                .toSet()
        putStringSet(CONTACT_KEY, contactSet)
    }
   ```
   
   The `edit()` from Android KTX always calls `apply()` to help you avoid bugs.  
   
> __Kotlin Tips__:
> 
> 1. There are many places where the Android API's are enhanced for use with Kotlin. Browse the [documentation](https://android.github.io/android-ktx/core-ktx/).
> 1. Android KTX is in preview to try out so you can [suggest ideas and report issues](https://github.com/android/android-ktx#how-to-contribute).
