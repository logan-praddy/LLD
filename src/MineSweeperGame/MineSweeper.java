package MineSweeperGame;

class MineSweeperBoard {
    private char[][] board;
    private int m;
    private int n;

    MineSweeperBoard(char[][]board, int m , int n){
        this.board = board;
        this.m = m;
        this.n = n;
    }
    int[][] dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{1,-1},{-1,1}};

    public char[][] updateBoard(char[][] board, int[] click) {
        int i = click[0], j = click[1];

        if(board[i][j] == 'M'){
            board[i][j] = 'X';
            return board;
        }

        DFS(board, i, j);

        return board;
    }

    private void DFS(char[][] board, int i, int j){
        int count = 0;

        for(int[] dir : dirs){
            int x = i + dir[0];
            int y = j + dir[1];

            if(x < 0 || x >= board.length || y < 0 || y >= board[0].length) continue;

            if(board[x][y] == 'M') count++;
        }

        board[i][j] = (count > 0) ? (char)(count + '0') : 'B';

        if(board[i][j] == 'B'){
            for(int[] dir : dirs){
                int x = i + dir[0];
                int y = j + dir[1];

                if(x < 0 || x >= board.length || y < 0 || y >= board[0].length || board[x][y] != 'E') continue;

                DFS(board, x, y);
            }
        }
    }
}
public class MineSweeper {
    public static void main(String[] args){

        char[][] board = {{'E','E','E','E','E'},{'E','E','M','E','E'},{'E','E','E','E','E'},{'E','E','E','E','E'}};
        MineSweeperBoard mineSweeperBoard = new MineSweeperBoard(board, 4,5);
        int[] click = {3,0};
        char[][] finalBoard = mineSweeperBoard.updateBoard(board, click);
        for(int i=0;i<4;i++){
            for(int j=0;j<5;j++){
                System.out.print(finalBoard[i][j] + " ");
            }
            System.out.println();
        }


    }
}
