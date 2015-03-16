# Features #

## Current Features ##
  * Find files (controlled with a file filter)
  * Type files
  * Collect metadatas (using selfwritten collectors)
  * Tags
  * Different interfaces
    * Web (currently the most supported interface)
    * Telnet (currently only a simple search and view)
    * Taskbar Icon (contains links to other interfaces, not really to control the application)
  * Filter output in Interfaces (currently only for the WebInterface)
  * Simple control over a config file
  * Exporter
    * PDF (a simple output of files, but is currently unused in the interfaces)
  * SQLite to store information
  * User login
    * User tagged files
  * Possibility to display average page creation

## Upcoming Features ##
  * Add ability to enable/disable user logins
  * Add ability to add a profile picture to user
  * Better integration of users
  * Add ability to add lists for users and global
  * Add video plugin feature
    * Add a video as textlink to file details page
    * Add a video as embed video to file details page
    * Add option in MDBConfig to add a Class
      * setVideoPlugin(VideoTextLink.class)
  * Filter output for all interfaces with a specified interfaceclass
  * Integration of the Typer to the Collectors (collectors specify the types)
  * Better solution of configs and property-files for the internal features (this library) and the external features (own application)
  * Better integration of exporters over a e.g. specified interfaceclass
  * Better integration of other databases (e.g. with hibernate?) like H2 or MySQL
  * Better control for interfaces like the webinterface
  * Better managing of additional metafiles like images or videos
  * Default/Better "API"s for getting information from database and put informations to database
  * Better implementation of functions like searching, tagging and exporting to use this in own projects
  * Possibility to change filenames
  * Possibility to remove collector-information
  * Possibility to make stats