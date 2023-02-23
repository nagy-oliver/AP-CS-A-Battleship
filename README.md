# AP-CS-A-Battleship
The battleship game using Java, allowing for multiplayer by running on local network

## How to play

### Set up
- Set up config.json so that it matches on both computers
- Set up the myships.game file based on where you want your ships to be, make sure it matches config
- In myships.game, 0 annotates water, 1, 2, 3... are numbers of ships
- Ships neeed to be set up in line

### Run
- In terminal, run the battleship-1.0-SNAPSHOT-jar-with-dependencies.jar file
- Choose one computer to host the game and the other one to join the game
- After you type in the IP and get success message, type start on host to begin the game

### Play
- First player is chosen randomly
- They need to make a shot by typing 'move x y' where x and y are coordinates starting from 0
- Based on that, the turn either switches to other player or remains
- The other player makes moves same way
- Game ends once all ships of one player are sunk


## Technical details

### Socket command codes

- `INV_CONF` - config mismatch or invalid
- `REQ_CONF` - request config object from client
- `INV_BOARD` - board invalid
- `REQ_BOARD` - request board object from client
- `MOVE {X} {Y}` - player palyed a move on tile [X, Y]
- `INV_BOARD` - Player has an invalid board loaded
- `TERM {X}` - Game terminated with exit code X
