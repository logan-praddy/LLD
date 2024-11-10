
import java.util.*;

// Represents a tile on the board with a specific number and closed/open state
class Tile {
    private final int number;
    private boolean isClosed;

    public Tile(int number) {
        this.number = number;
        this.isClosed = false;
    }

    public int getNumber() {
        return number;
    }

    public boolean isClosed() {
        return isClosed;
    }

    // Close the tile (indicate that it is covered)
    public void close() {
        this.isClosed = true;
    }

    // Open the tile (reset to uncovered)
    public void open() {
        this.isClosed = false;
    }
}

// Represents a pair of dice used in the game
class Dice {
    private final Random random;

    public Dice() {
        this.random = new Random();
    }

    // Roll two dice and return the sum
    public int roll() {
        return (random.nextInt(6) + 1) + (random.nextInt(6) + 1); // Roll two dice
    }
}

// Manages the board state with nine tiles
class Board {
    private final List<Tile> tiles;

    public Board() {
        // Initialize tiles from 1 to 9
        tiles = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            tiles.add(new Tile(i));
        }
    }

    // Retrieve all open (unclosed) tiles
    public List<Tile> getOpenTiles() {
        List<Tile> openTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isClosed()) {
                openTiles.add(tile);
            }
        }
        return openTiles;
    }

    // Close specific tiles based on given numbers
    public boolean closeTiles(List<Integer> tileNumbers) {
        for (Integer number : tileNumbers) {
            for (Tile tile : tiles) {
                if (tile.getNumber() == number && !tile.isClosed()) {
                    tile.close();
                    break;
                }
            }
        }
        return true;
    }

    // Check if all tiles on the board are closed
    public boolean allTilesClosed() {
        for (Tile tile : tiles) {
            if (!tile.isClosed()) {
                return false;
            }
        }
        return true;
    }

    // Find all possible combinations of open tiles that sum up to the target roll
    public List<List<Integer>> findTileCombinations(int target) {
        List<Integer> openTileNumbers = new ArrayList<>();
        for (Tile tile : getOpenTiles()) {
            openTileNumbers.add(tile.getNumber());
        }
        List<List<Integer>> combinations = new ArrayList<>();
        findCombinationsHelper(openTileNumbers, target, new ArrayList<>(), 0, combinations);
        return combinations;
    }

    // Recursive helper to find combinations of numbers that sum up to target
    private void findCombinationsHelper(List<Integer> numbers, int target, List<Integer> current, int start, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < numbers.size(); i++) {
            if (numbers.get(i) <= target) {
                current.add(numbers.get(i));
                findCombinationsHelper(numbers, target - numbers.get(i), current, i + 1, result);
                current.remove(current.size() - 1);
            }
        }
    }
}

// The main game logic for Shut the Box
class Game {
    private final Board board;
    private final Dice dice;

    public Game() {
        this.board = new Board();
        this.dice = new Dice();
    }

    // Main game loop; returns true if the player wins, otherwise false
    public boolean play() {
        while (true) {
            int roll = dice.roll();
            List<List<Integer>> validCombinations = board.findTileCombinations(roll);

            // If no valid combinations, the player loses
            if (validCombinations.isEmpty()) {
                return false;
            }

            // Randomly select a valid combination of tiles to close
            List<Integer> selectedCombination = validCombinations.get(new Random().nextInt(validCombinations.size()));
            board.closeTiles(selectedCombination);

            // If all tiles are closed, the player wins
            if (board.allTilesClosed()) {
                return true;
            }
        }
    }
}

// Simulation class to run multiple games and calculate win rate
class ShutTheBoxSimulation {
    private final int numSimulations;

    public ShutTheBoxSimulation(int numSimulations) {
        this.numSimulations = numSimulations;
    }

    public void run() {
        int wins = 0;

        // Run each game simulation and count wins
        for (int i = 0; i < numSimulations; i++) {
            Game game = new Game();
            if (game.play()) {
                wins++;
            }
        }

        // Calculate win percentage and display
        double winPercentage = ((double) wins / numSimulations) * 100;
        System.out.printf("Win percentage after %d simulations: %.2f%%\n", numSimulations, winPercentage);
    }
}

// Entry point for the Shut the Box simulation
public class ShutTheBox {
    public static void main(String[] args) {
        int numSimulations = 100000; // Number of simulations to run
        ShutTheBoxSimulation simulation = new ShutTheBoxSimulation(numSimulations);
        simulation.run();
    }
}

Approach

1.Initialize Game Components: The game consists of tiles numbered 1–9, each
represented by a Tile object with methods to open or close it.
A Dice object simulates the rolling of two dice, returning the sum.

2. Board Setup: The Board class manages the tiles and provides methods to get open tiles,
close specific tiles, check if all tiles are closed, and find combinations of open tiles that sum to a target value.

3. Combination Finder: findTileCombinations() finds all sets of open tiles that add up to a
target roll value. This uses a recursive helper to gather all possible combinations.

Game Play:

Roll the dice to get a sum.
Find all valid combinations of tiles that match the roll sum. If there are no valid combinations, the player loses.
Select a random valid combination to close tiles.
If all tiles are closed, the player wins.
Simulating Multiple Games: The ShutTheBoxSimulation class runs the game multiple times to calculate the probability of winning. It keeps track of the wins, calculates the win percentage, and displays it.

Time Complexity

Tile Combination Finding: Finding combinations is O(2^n) in the worst case, where
n is the number of open tiles, due to the recursive exploration of all subsets.

Overall Complexity: The main complexity arises from finding combinations in each game,
making it exponential in nature. The rest of the game operations are
O(n), as they involve simple list operations.


