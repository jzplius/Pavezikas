Downaload .apk at http://goo.gl/NVr6Mi

The aim of an app:
=========

* Allow user to register and login using Facebook login;
* Fill-in their information about travel from point A to point B (suppose from Kaunas to Vilnius);
* Post this information on an app and on selected Facebook groups;
* Allow see posts list, post details information in an app.
* Allow to see Facebook groups in an app.

NOTE THAT:
=========

* This is app is still being developed, so it is not fully-functional (part of buttons are not-resposive to clicks and so on...)
* I worked on the middleware & server-side of the project. Huge part of UI-side was sketched and implemented by other colleague.

Functionalities based on screens:
=========

* Add a post. Provides a handled 3 step post add mechanism.
* Posts. Displays a list of all added posts.
* Post information. When clicked on post in list, provides a detail information about post.

Technical features:
=========

* DB is designed on server-side, it is based on MySQL. The DB is normalized.
* App-server communication:
     * Downloads are committed in background thread using AsyncTaskLoader.
     * App->PHP script. In app there is performed a HTTP POST action to specific URL on a server, each URL points to PHP script.
     *PHP script->MySQL. For specific situations there are prepared PHP scripts on server side. They can require data to be passed on them, in order to commit actions. If they receive required data they commit a query on MySQL DB. Query result are handled via PHP and printed out as JSON Objects.
     *PHP script->App.  In app there is retrieved a HTTP response (string containing JSON Object) and it is converted to JSONObject.
* Adding a post is divided into 3 steps:
     * most of pickers were presented in DialogFragment;
     * combined a DatePicker, TimePicker and SeekBar to select leaving date and time;
     * used Spinners and DialogFragments to provide address, Facebook groups selection.
* Created a 1-2 fragments layout for tablets and phones. It is a 3 clicks depth layout which is designed to work in desired way:
     * Allows screen rotations on any state;
     * Allows back pressed events;
     * Remembers previous selected data (post selections are pre-saved on singleton);
* Implemented Facebook login, which retrieves basic user information and list of user joined Facebook groups.
* Implemented custom design with selected color palette.
* Communication between fragments is made through Activities by various solutions: 
     * by implementing interfaces, 
     * by setting Target Fragment and retrieving it in a child (such as DialogFragment)
* Connection state is being checked while attaching all fragments;

Reusable components:
=========

* Created network state utilities static class for access of these methods:
    * Determine if no network is available. 
    * Determine if available network is active or not.   
    * If active network is not present then new activity starts, which waits till network connection is active;
    * Alternatively, if active network is not present and action is not so trivial, toasts are shown.
* Focused on high reusability of code:
     * implemented abstract single-pane FragmentActivity for use in phones;
     * implemented abstract double-pane FragmentActivity for use in tablets;
     * implemented abstract generic AsyncTaskLoader which delivers results if they are downloaded;
     * implemented connection change receiver. Network state changes are received by BroadcastReceiver and app knows if connection is active at run-time. Receiver has only to be registered on manifest.
     * implemented URL response downloader;
     * created Sliding menu utility, to provide simple method for menu creation on fragment


Libraries used:
=========

* Otto event bus: mostly for informing about downloads being finished, so that could prevent simultaneous use of methods that are dependent of each other.
* GSON: for Java objects conversion from ant to their String representations.
* Sliding menu (this one also requires ActionBarSherlock): for the creation of right-side sliding menu. 
* Facebook SDk: for retrieving user data and posting information to selected Facebook groups.


Fully tested on:
=========
* The app is tested on "add post" activities and fragments.
