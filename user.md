# Introduction #

Some information about the users in the system.

# General #
The metadatabase knows currently two modes:
  * User Mode
  * No-User Mode

## User Mode ##
In the User Mode you can login to have different advantages like watchlists and user-defined FileItem-Tags.

## No-User Mode ##
In the No-User Mode the system runs with a single (invisible) user, called "default user" with database id = 0.
In this mode, all "users" use the same base of information and share all information with all other.

# Security / Login #
If a user will be created, some informations will be used to create an account:
  * Name
  * E-Mail
  * Password

These are the min informations about a user. The metadatabase create a (pseudo-random) token (salt) for every user. metadatabase itself has an own token (pepper). salt and pepper will be used to hash the whole user-password. THEN it will be saved to database.