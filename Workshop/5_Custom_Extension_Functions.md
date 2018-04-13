# 5. Custom Extension Functions

### EditText validation extension function
    
The `afterTextChanged()` method, which validates the values when you add a new contact, is still longer than it needs to be. It has repeated calls to set the validation drawables, and for each call you have to access the EditText instance twice: once to set the drawable and once to check if the input is valid. You also have to check the validation lambda expressions again to set the `mEntryValid` boolean.
    
In this task, you will create an extension function on EditText that performs validation (checks the input and sets the drawable).
    
1. Create a new Kotlin file by selecting your package directory in the project browser and clicking on __File > New > Kotlin File/Class__. Call it Extensions and make sure the __Kind__ field says __File__.
    
   To create an Extensions function, you use the same syntax as for a regular function, except you preface the function name with the class you wish to extend and a period.
    
1. In the Extensions.kt file, create an Extension function on `EditText` called `EditText.validateWith()`. It takes three arguments: a drawable for the case where the input is valid, a drawable for when the input is invalid, and a validator function that takes a `TextView` as a parameter and returns a `Boolean` (like the notEmpty and isEmail validators you created). This extensions function also returns a `Boolean`:

   ```kotlin
    internal fun EditText.validateWith
            (passIcon: Drawable?, failIcon: Drawable?, 
             validator: (TextView) -> Boolean): Boolean {
    }
   ```
    
1. Define the method to return the result of the validator function. You can pass this as a parameter to the validator, since the function is an extension on `EditText`, so this is the instance that the method is called on:
    ```kotlin
    internal fun EditText.validateWith
            (passIcon: Drawable?, failIcon: Drawable?, 
             validator: (TextView) -> Boolean): Boolean {
        return validator(this)
    }
   ```
    
1. In the `validateWith()` method, call `setCompoundDrawablesWithIntrinsicBounds()`, passing in null for the top, left, and bottom drawables, and using the if/else syntax with the passed in validator function to select either the pass or fail drawable:
    
    ```kotlin
    setCompoundDrawablesWithIntrinsicBounds(null, null,
                if (validator(this)) passIcon else failIcon, null)
   ```
    
1. Back in the `ContactsActivity`, in the `afterTextChanged()` method, remove all three calls to `setCompoundDrawablesWithIntrinsicBounds()`.

1. Change the assignment of the `mEntryValid` variable to call `validateWith()` on all three EditTexts, passing in the pass and fail icons, as well as the appropriate validator:

    ```kotlin
    mEntryValid = mFirstNameEdit.validateWith(passIcon, failIcon, notEmpty) and
            mLastNameEdit.validateWith(passIcon, failIcon, notEmpty) and
            mEmailEdit.validateWith(passIcon, failIcon, isEmail)
   ```
    
    You can take this one step further by changing the type of `notEmpty` and `isEmail` lambda expression to be extensions of the TextView class, rather than passing in an instance of TextView. This way, inside the lambda expression, you are a member the of TextView instance and can therefore call TextView methods and properties without referencing the instance at all.
    
1. Change the `notEmpty` and `isEmail` type declaration to be an extension of `TextView` and remove the parameter. Remove the `it` parameter reference, and use the text property directly:
    
    ```kotlin
    val notEmpty: TextView.() -> Boolean = { text.isNotEmpty() }
    val isEmail: TextView.() -> Boolean = { Patterns.EMAIL_ADDRESS.matcher(text).matches() }
    ```
    
1. In the `validateWith()` method in the Extensions.kt file, make the third parameter (the validator function) extend the TextView type rather than have it passed in, and remove the `this` parameter inside the validator method call:
    
    ```kotlin
    internal fun EditText.validateWith(passIcon: Drawable?, 
                                              failIcon: Drawable?,
                                              validator: TextView.() -> Boolean): Boolean {
        setCompoundDrawablesWithIntrinsicBounds(null, null,
                if (validator()) passIcon else failIcon, null)
        return validator()
    }
   ```
    
    When you use a higher-order function, the generated Java bytecode creates an instance of an anonymous class, and calls the passed in function as a member of the anonymous class. This creates performance overhead, as the class needs to be loaded into memory. In the above example, every call to `validateWith()` in the activity will create an anonymous inner class that wraps the validator function, and calls it when it is needed.
    
    Most of the time, the main reason for using a higher-order function is to specify a call order or location, as in the above example, where the passed in function must be called to determine which drawable to load and again to determine the return Boolean. To prevent these anonymous class instances from being created, you can use the `inline` keyword when defining a higher-order function. In this case, the body of the inlined function gets copied to the location where it is called and no instance is created.
    
1. Add the `inline` keyword to the `validateWith()` method declaration.
    
> __Kotlin Tips__:
>   
> 1. Define an Extension function on a class (called the receiver class) by prefacing the function name with the receiver class name and a period such as: `EditText.validateWith()`. Inside the function, you can call members of the class (methods and properties) as if the function was a member of the class itself.
> 1. Higher-order functions have performance overhead as the compiler creates anonymous inner classes to call the passed function. Use the `inline` keyword when declaring the function to eliminate the overhead by copying the contents of the passed in function to each callsite instead of generating an anonymous class.
> 
> See the [Extensions documentation](https://kotlinlang.org/docs/reference/extensions.html).

### Default Arguments
    
Kotlin provides the ability to declare default values for parameters of a function. Then, when calling the function, you can omit the parameters that use their default values, and only pass in the values that you choose using the `<variableName> = <value>` syntax.
    
1. In the `validateWith()` method in the Extensions.kt file, set the pass and fail drawables as defaults for the first two parameters:
    
    ```kotlin
    internal inline fun EditText.validateWith
            (passIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_pass),
             failIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_fail),
             validator: TextView.() -> Boolean)
   ```
    
1. In the `afterTextChanged()` method, remove the drawable variables and the first two arguments of each call to `validateWith()`. You must preface remaining argument with validator = to let the compiler know which argument you are passing in:
    ```kotlin
     mEntryValid = mFirstNameEdit.validateWith(validator = notEmpty) and
                    mLastNameEdit.validateWith(validator = notEmpty) and
                    mEmailEdit.validateWith(validator = isEmail)
   ```
    
    The `afterTextChanged()` method is now much easier to read: it declares two lambda expressions for validation, and passes them in the `validateWith()` extension function using the default pass and fail drawables.
    
> __Kotlin Tips__:
>
> 1. You can specify default arguments in a function, so that it can be called without certain parameters passed in.
> 1. When calling a function with default arguments, you can omit any of the arguments with default values. You must however, specify the name of the arguments you do choose to pass in using the `<variableName> = <value>` syntax in the arguments field.
    
