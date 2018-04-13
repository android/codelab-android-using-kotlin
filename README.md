MyAddressBook
=============

This is the repository that contains both the starter, each step and a finished app for the workshop assignments in the Workshop/ directory. 
This is an updated version of the original 
[Taking Advantage of Kotlin codelab](https://codelabs.developers.google.com/codelabs/taking-advantage-of-kotlin/index.html).

The starter app is written in Java, which is then converted to Kotlin and extended.

Tags:
* `step-1` begin of 1_Introduction.md, the starter app in Java
* `step-2` completed 2_Getting_set_up.md, the starter app with Kotlin configured
* `step-3` completed 3_Kotlin_Conversion_Basics.md, the app converted to Kotlin
* `step-4` completed 4_Lambdas_and_Standard_Library_Extensions.md, applied lambdas 
* `step-5` completed 5_Custom_Extension_Functions.md, created extension functions


Introduction
------------
MyAddressBook is an address book android application, that lists contacts
containing a first name, last name, and email address. The contacts can be
generated quickly from an included JSON file, or created manually in the app.
It contains validation for the input fields, stores the data in
SharedPreferences, and supports swipe to delete in the RecyclerView.

Pre-requisites
--------------
A basic knowledge of developing Android apps in Java, specifically:
- Displaying data in a RecyclerView.
- Using SharedPreferences to persist data.
- How to create JavaBean objects.
- Creating layouts using ConstraintLayout.
- Displaying an AlertDialog.
- Validating user input in an EditText.

Getting Started
---------------
1. Clone the repo. `git clone https://github.com/rcgroot/android-using-kotlin.git`
2. Switch to the beginning tap `git checkout step-1` and open `MyAddressBook` in Android Studio 3.1
3. Run the app.
4. Open the markdown file 1_Introduction.md in the directory `Workshop/` and begin


License
-------

Copyright 2017 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
