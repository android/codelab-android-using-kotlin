# 2. Getting set up

## Download the Code
   
Clone the repo from following repo to download all the code for this codelab:
   
[git clone https://github.com/rcgroot/android-using-kotlin.git](https://github.com/rcgroot/android-using-kotlin.git)

You can also find it in this github [project](https://github.com/rcgroot/android-using-kotlin).

The repo contains a tag for each step of this codelab.

* step-1 contains the starter app
* step-N folders contain the app in the finished state after step N.
* final contains the final app.

## App Overview
   
MyAddressBook is a simplified address book app that displays a list of contacts in a RecyclerView. You'll be converting all of the Java code into Kotlin, so take a moment to familiarize yourself with the existing code in the MyAddressBook-starter app.

1. Open MyAddressBook-starter in Android Studio.
1. Run it.

### Contact.java

The Contact class is a simple Java class that contains data. It is used to model a single contact entry and contains three fields: first name, last name and email.

### ContactsActivity.java

The ContactsActivity shows a RecyclerView that displays an ArrayList of Contact objects. You can add data in two ways:

1. Pressing the Floating Action Button will open up a __New Contact__ dialog, allowing you to enter contact information.
1. You can generate a list of 20 mock contacts quickly by selecting the __Generate__ option in the options menu, which will use an included JSON file to generate Contact objects.

Once you have some contacts, you can swipe on a row to delete that contact, or else clear the whole list by selecting __Clear__ in the options menu.

You can also tap on a row to edit that contact entry. In the editing dialog the first and last name fields are disabled, since for this tutorial app, these fields are immutable (only getters are provided in the Contact class), but the email field is modifiable.

The New Contact dialog performs validation in the following ways:

* The first and last name fields must not be empty.
* The email field must contain an email address using a valid format.
* Attempting to save an invalid contact will show an error in a Toast message.

### Configure Kotlin
    
Before you can use the Kotlin tools and methods, you must configure Kotlin in your project.
    
1. In Android Studio, select __Tools > Kotlin > Configure Kotlin__ in Project. If a window titled __Choose Configurator__ appears, select __Android with Gradle__, make sure __All modules__ is selected, and click __OK__.
  Android Studio will add the required dependencies in both the app level and the project level build.gradle files.
 
2. You will be prompted to sync the project with Gradle as the build.gradle files have changed. Once the sync is complete, move to the next step and write some Kotlin code.
    
> Note: When using Android Studio 3.0 or higher and creating a new project, you can have Kotlin configured by default by selecting the Include Kotlin support checkbox on the Create Android Project dialog.

