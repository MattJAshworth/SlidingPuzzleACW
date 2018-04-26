# Sliding ACW - University Coursework
University Coursework for Mobile Devices and Applications - [2017/2018] [Semester 2]
Extended deadline by 1 month - Additional improvements have been made

Miniumum SDK Version: 16
Target SDK Version: 25
No use of Google APIs or other 3rd party APIS for Coursework, Google Play Leaderboards and Achievements planned for public release.

Required network connection initially. The Coursework spec required that the puzzles and puzzle data be downloaded seperate to the app. This data is fetched from a server and thus requires a connection. Once downloaded a connection is not neccessary but recommened for Google Play Leaderboard integration and downloading of new puzzles (should there be any).

Note that the JSON data required to download in order to play the game may not be available at a later date than publishing. In this event I will push an update for a working build.

# Sliding Puzzle Game

An app rendition of a popular childs game where a player must arrange a jumbled up image by sliding pieces of the image into the correct places.

- At the time of publication there are 160 puzzles to play, this could change and no recode is neccessary.
- Puzzles can be played in 4 configurations; 3x3, 3x4, 4x3 and 4x4
- User is prompted for a username for use on the local high scores board.
- Ability to stay logged in, achieved through shared preferences.
- Available in 2 languages, English and Dutch as per the Coursework spec.
- Can only tap to move the tiles, slide coming soon



