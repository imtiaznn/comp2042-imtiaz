# Tetris JavaFX -- COMP2042

## GitHub Repository Link
https://github.com/imtiaznn/comp2042-imtiaz

## Compilation Instructions

### Prerequisites
- Install JDK 25 and ensure it is added as an environment variable

### Running the Application
- Run the following command to run the application:
    - **MacOS/Linux:**
        ```./mvnw clean javafx:run```
    - **Windows:**
        ```mvn javafx:run```

## Features (Implemented, working properly)

**Timed Mode -** Player is given 2 minutes to gain as much score as possible. The game will end if either of the following is true:
    - The player has reached the maximum allowed block height
    - The 2-minute timer has run out

**7-Bag Brick Randomization -** 7 bricks (a brick of each type) is placed inside an array, known as the bag. The array is then shuffled and set as the next bricks to be given to the player. This approach aims to allow the following:
- Create a fair distribution of bricks, preventing long repetitions and absences of a brick
- On game start, a bag of 7 shuffled bricks is created then added to the currentBrick and nextBricks.When the nextBricks array is empty, a new array of 7 shuffled bricks is initialised. 

**Next Brick -** The next 3 bricks from the currentBrick is displayed to the player. If the needed number of next peeked bricks is less than the amount of needed bricks, the bag will be reshuffled. The method is created to be able to implement future changes in the number of next peeked bricks.

**Hold Brick -** The player is able to hold the current falling brick. if there is currently no brick being held, the current falling brick will be stored as the held brick. If there is another brick being held, a swap will occur which will cause the current falling brick to be stored, and the previously held brick to start falling from the brick spawn position.

**Ghost Brick -** The current falling brick will have a translucent duplicate of itself be placed at the bottom-most position possible before any intersection. This feature aims to improve player prediction of brick falling location.

**Hard Drop -** Pressing spacebar will allow the user to bring the current falling brick to the lcoation of the ghost brick, providing a faster alternative for players to bring the brick down.

**Minimal User Interface -** A simple user interface is added to allow the player to replay the game and navigate between game modes.

**Smooth Input Handling -** A separate method is made to implement ARR (auto repeat rate) and DAS (delayed auto shift), which will override the system's input handling and allow better control on the milliseconds of delay when a key is held.
 
## Features (Implemented, not working properly)

**Score Notification System -** The score notification that pops up when the player clears a row is no longer functioning properly. This is probably due to overlaps in how the hardDrop feature is implemented. The feature will occasionally function if a brick falls into a cleared row by itself.

## New Java Classes
### Scene Controllers
**LevelSelectController.java, MainMenuController.java -** New controller classes separate from the GuiController, meant to be able to handle interactions in each scene separate from each other. It also allow a more scalable approach for any future additions, as well as allowing separate initialisation of elements and handling of events.

### MessageOverlay
A new class made from combining the GameOverPanel.java class and NotificationPanel.java class, since both of these classes have similar purposes that can be implemented into one class, they are combined to prevent redundancy. This change might allow future problems if it is required to change only one of the panels, however it aims to maintain a standard panel look inot the game.

## Modified Java Classes

### GuiController.java
- Packaged under com.comp2042.views
- No longer handles the logic for falling bricks and game ticks
- No longer takes input handling for keyEvents that require precise user movements
- Ensures only minimal interaction with other Models to imitate an MVC architecture

### GameController.java
- Packaged under com.comp2042.controller
- Is now responsible for handling game ticks and updates
- Is now responsible for handling inputs that requires precise user movements
- Combined the separate movement events into one method relying on a switch statement

### SimpleBoard.java
- Packaged under com.comp2042.models
- Added ghost brick and its calculator helper function
- Added hold brick interaction

### RandomBrickGenerator.java
- Packaged under com.comp2042.logic.bricks
- Implemented the 7-bag brick randomisation system
- Changed the peeking next brick system to be able to peek more than 1 next brick

## Unexpected Problems
- **Issue:** Bricks are offset by a few pixels from the grid 
    - **Fix:** Implemented offset based on the UI calculated dimensions
- **Issue:** Score notification does not appear when clearing a row
    - **Fix:** Displayed a separate score UI at the side of the gameboard
- **Issue:** Implementing multiple scenes inside GuiController causes it to be bloated
    - **Fix:** Separated the GuiController to be separate for each scene