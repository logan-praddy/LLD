package SnakeAndLadderStandard;


import java.util.*;

class Snake {
    // Each snake will have its head at some number and its tail at a smaller number.
    private int start;
    private int end;

    public Snake(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}

class Ladder {
    // Each ladder will have its start position at some number and end position at a larger number.
    private int start;
    private int end;

    public Ladder(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
class Player {
    private String name;
    private String id;

    public Player(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

class SnakeAndLadderBoard {
    private int size;
    private List<Snake> snakes; // The board also contains some snakes and ladders.
    private List<Ladder> ladders;
    private Map<String, Integer> playerPieces;

    public SnakeAndLadderBoard(int size) {
        this.size = size;
        this.snakes = new ArrayList<Snake>();
        this.ladders = new ArrayList<Ladder>();
        this.playerPieces = new HashMap<String, Integer>();
    }

    public int getSize() {
        return size;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<Snake> snakes) {
        this.snakes = snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public void setLadders(List<Ladder> ladders) {
        this.ladders = ladders;
    }

    public Map<String, Integer> getPlayerPieces() {
        return playerPieces;
    }

    public void setPlayerPieces(Map<String, Integer> playerPieces) {
        this.playerPieces = playerPieces;
    }
}
class DiceService {
    public static int roll() {
        return new Random().nextInt(6) + 1; // The game will have a six sided dice numbered from 1 to 6 and will always give a random number on rolling it.
    }
}
class SnakeAndLadderService {
    private SnakeAndLadderBoard snakeAndLadderBoard;
    private int initialNumberOfPlayers;
    private Queue<Player> players; // Comment: Keeping players in game service as they are specific to this game and not the board. Keeping pieces in the board instead.
    private boolean isGameCompleted;

    private int noOfDices; //Optional Rule 1
    private boolean shouldGameContinueTillLastPlayer; //Optional Rule 3
    private boolean shouldAllowMultipleDiceRollOnSix; //Optional Rule 4

    private static final int DEFAULT_BOARD_SIZE = 100; //The board will have 100 cells numbered from 1 to 100.
    private static final int DEFAULT_NO_OF_DICES = 1;

    public SnakeAndLadderService(int boardSize) {
        this.snakeAndLadderBoard = new SnakeAndLadderBoard(boardSize);  //Optional Rule 2
        this.players = new LinkedList<Player>();
        this.noOfDices = SnakeAndLadderService.DEFAULT_NO_OF_DICES;
    }

    public SnakeAndLadderService() {
        this(SnakeAndLadderService.DEFAULT_BOARD_SIZE);
    }

    /**
     * ====Setters for making the game more extensible====
     */

    public void setNoOfDices(int noOfDices) {
        this.noOfDices = noOfDices;
    }

    public void setShouldGameContinueTillLastPlayer(boolean shouldGameContinueTillLastPlayer) {
        this.shouldGameContinueTillLastPlayer = shouldGameContinueTillLastPlayer;
    }

    public void setShouldAllowMultipleDiceRollOnSix(boolean shouldAllowMultipleDiceRollOnSix) {
        this.shouldAllowMultipleDiceRollOnSix = shouldAllowMultipleDiceRollOnSix;
    }

    /**
     * ==================Initialize board==================
     */

    public void setPlayers(List<Player> playersList) {
        this.players = new LinkedList<Player>();
        this.initialNumberOfPlayers = playersList.size();
        Map<String, Integer> playerPieces = new HashMap<String, Integer>();
        for (Player player : playersList) {
            this.players.add(player);
            playerPieces.put(player.getId(), 0); //Each player has a piece which is initially kept outside the board (i.e., at position 0).
        }
        snakeAndLadderBoard.setPlayerPieces(playerPieces); //  Add pieces to board
    }

    public void setSnakes(List<Snake> snakes) {
        snakeAndLadderBoard.setSnakes(snakes); // Add snakes to board
    }

    public void setLadders(List<Ladder> ladders) {
        snakeAndLadderBoard.setLadders(ladders); // Add ladders to board
    }

    /**
     * ==========Core business logic for the game==========
     */

    private int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition) {
        int previousPosition;

        do {
            previousPosition = newPosition;
            for (Snake snake : snakeAndLadderBoard.getSnakes()) {
                if (snake.getStart() == newPosition) {
                    newPosition = snake.getEnd(); // Whenever a piece ends up at a position with the head of the snake, the piece should go down to the position of the tail of that snake.
                }
            }

            for (Ladder ladder : snakeAndLadderBoard.getLadders()) {
                if (ladder.getStart() == newPosition) {
                    newPosition = ladder.getEnd(); // Whenever a piece ends up at a position with the start of the ladder, the piece should go up to the position of the end of that ladder.
                }
            }
        } while (newPosition != previousPosition); // There could be another snake/ladder at the tail of the snake or the end position of the ladder and the piece should go up/down accordingly.
        return newPosition;
    }

    private void movePlayer(Player player, int positions) {
        int oldPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int newPosition = oldPosition + positions; // Based on the dice value, the player moves their piece forward that number of cells.

        int boardSize = snakeAndLadderBoard.getSize();

        // Can modify this logic to handle side case when there are multiple dices (Optional requirements)
        if (newPosition > boardSize) {
            newPosition = oldPosition; // After the dice roll, if a piece is supposed to move outside position 100, it does not move.
        } else {
            newPosition = getNewPositionAfterGoingThroughSnakesAndLadders(newPosition);
        }

        snakeAndLadderBoard.getPlayerPieces().put(player.getId(), newPosition);

        System.out.println(player.getName() + " rolled a " + positions + " and moved from " + oldPosition +" to " + newPosition);
    }

    private int getTotalValueAfterDiceRolls() {
        // Can use noOfDices and setShouldAllowMultipleDiceRollOnSix here to get total value (Optional requirements)
        return DiceService.roll();
    }

    private boolean hasPlayerWon(Player player) {
        // Can change the logic a bit to handle special cases when there are more than one dice (Optional requirements)
        int playerPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int winningPosition = snakeAndLadderBoard.getSize();
        return playerPosition == winningPosition; // A player wins if it exactly reaches the position 100 and the game ends there.
    }

    private boolean isGameCompleted() {
        // Can use shouldGameContinueTillLastPlayer to change the logic of determining if game is completed (Optional requirements)
        int currentNumberOfPlayers = players.size();
        return currentNumberOfPlayers < initialNumberOfPlayers;
    }

    public void startGame() {
        while (!isGameCompleted()) {
            int totalDiceValue = getTotalValueAfterDiceRolls(); // Each player rolls the dice when their turn comes.
            Player currentPlayer = players.poll();
            movePlayer(currentPlayer, totalDiceValue);
            if (hasPlayerWon(currentPlayer)) {
                System.out.println(currentPlayer.getName() + " wins the game");
                snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
            } else {
                players.add(currentPlayer);
            }
        }
    }
// On getting roll dice again and three 6's comes then value will be 0.
//    public void startGame() {
//        while (!isGameCompleted()) {
//            Player currentPlayer = players.poll();
//            int totalDiceValue = 0;
//            int consecutiveSixes = 0;
//            boolean getAnotherTurn;
//
//            do {
//                int diceValue = DiceService.roll();
//                System.out.println(currentPlayer.getName() + " rolled a " + diceValue);
//
//                if (diceValue == 6) {
//                    consecutiveSixes++;
//                    if (consecutiveSixes == 3) {
//                        System.out.println(currentPlayer.getName() + " rolled three consecutive 6s. All rolls get canceled.");
//                        totalDiceValue = 0;
//                        break;
//                    }
//                    getAnotherTurn = true;
//                } else {
//                    getAnotherTurn = false;
//                    consecutiveSixes = 0;
//                }
//
//                totalDiceValue += diceValue;
//
//            } while (getAnotherTurn);
//
//            if (totalDiceValue > 0) {
//                movePlayer(currentPlayer, totalDiceValue);
//                if (hasPlayerWon(currentPlayer)) {
//                    System.out.println(currentPlayer.getName() + " wins the game");
//                    snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
//                } else {
//                    players.add(currentPlayer);
//                }
//            } else {
//                players.add(currentPlayer);
//            }
//        }
//    }
//}

    /**
     * =======================================================
     */
}
public class Driver {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        int noOfSnakes = scanner.nextInt();
//        List<Snake> snakes = new ArrayList<Snake>();
//        for (int i = 0; i < noOfSnakes; i++) {
//            snakes.add(new Snake(scanner.nextInt(), scanner.nextInt()));
//        }
//
//        int noOfLadders = scanner.nextInt();
//        List<Ladder> ladders = new ArrayList<Ladder>();
//        for (int i = 0; i < noOfLadders; i++) {
//            ladders.add(new Ladder(scanner.nextInt(), scanner.nextInt()));
//        }
//
//        int noOfPlayers = scanner.nextInt();
//        List<Player> players = new ArrayList<Player>();
//        for (int i = 0; i < noOfPlayers; i++) {
//            players.add(new Player(scanner.next()));
//        }
//
//        SnakeAndLadderService snakeAndLadderService = new SnakeAndLadderService();
//        snakeAndLadderService.setPlayers(players);
//        snakeAndLadderService.setSnakes(snakes);
//        snakeAndLadderService.setLadders(ladders);
//
//        snakeAndLadderService.startGame();
//    }


    //hardcoded


        public static void main(String[] args) {
            int noOfSnakes = 2;
            List<Snake> snakes = new ArrayList<Snake>();
            snakes.add(new Snake(14, 7));
            snakes.add(new Snake(31, 26));

            int noOfLadders = 2;
            List<Ladder> ladders = new ArrayList<Ladder>();
            ladders.add(new Ladder(3, 22));
            ladders.add(new Ladder(20, 29));

            int noOfPlayers = 2;
            List<Player> players = new ArrayList<Player>();
            players.add(new Player("Alice"));
            players.add(new Player("Bob"));

            SnakeAndLadderService snakeAndLadderService = new SnakeAndLadderService();
            snakeAndLadderService.setPlayers(players);
            snakeAndLadderService.setSnakes(snakes);
            snakeAndLadderService.setLadders(ladders);

            snakeAndLadderService.startGame();
        }


}
