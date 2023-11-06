import java.util.Scanner;

public class Main {
    static final Scanner scanner = new Scanner(System.in);

    // Variables para almacenar el tamaño del tablero y el número de bombas.
    static int boardSize;
    static int numberOfBombs;

    // Constantes para representar caracteres en el juego.
    static final String BOMB_CHAR = "%";
    static final String UNKNOWN_CHAR = "?";
    static final String OPEN_CHAR = "=";

    // Matrices para representar el tablero del jugador y el tablero de bombas.
    static String[][] board = new String[8][8];
    static String[][] bombField = new String[8][8];

    // Array en el que se almacenan las coordenadas atacadas.
    static String[] attackedCoords;

    // Variable para controlar si el jugador ha perdido.
    static boolean hasLost = false;

    // El método principal donde comienza el juego.
    public static void main(String[] args) {
        // Se inicia el juego llamando al método startGame().
        startGame();

        // Bucle principal del juego que continúa hasta que el jugador gane o pierda.
        while (true) {
            // Se piden coordenadas al jugador.
            System.out.println("¿Qué coordenadas quieres atacar? (Ej: 2,2)");
            attackedCoords = scanner.nextLine().split(",");

            // Se llama al método countAdjacentMines para procesar el ataque del jugador.
            countAdjacentMines(board, bombField, Integer.parseInt(attackedCoords[0]) - 1, Integer.parseInt(attackedCoords[1]) - 1);

            // Se verifica si el jugador ha ganado y en ese caso termina la partida.
            if (checkForWin()) {
                System.out.println("¡Enhorabuena, has ganado la partida!");
                displayFinishedBoard(board);
                break;
            }

            // Se verifica si el jugador ha perdido y en ese caso termina la partida.
            if (hasLost) {
                System.out.println("¡En esa casilla había una bomba, has perdido la partida!");
                displayFinishedBoard(board);
                break;
            }

            // Se actualiza el tablero y se muestra al jugador.
            displayBoard(board);
        }
    }

    // Método para iniciar el juego y configurar el tablero.
    public static void startGame() {
        // Se pide al jugador que especifique el tamaño del tablero.
        System.out.println("¿De qué tamaño quieres que sea el tablero? (Ej: 10 -> 10x10):");
        boardSize = Integer.parseInt(scanner.nextLine());

        // Se pide al jugador que especifique el número de bombas en el tablero.
        System.out.println("¿Cuántas bombas quieres que haya en la partida? (Ej: 8):");
        numberOfBombs = Integer.parseInt(scanner.nextLine());

        // Se inicializan las matrices del tablero del jugador y del tablero de bombas.
        board = new String[boardSize][boardSize];
        bombField = new String[boardSize][boardSize];

        // Se llenan ambas matrices con celdas desconocidas.
        fillBoard(board);
        displayBoard(board);

        // Se llena la matriz del tablero de bombas y se generan las bombas aleatoriamente.
        fillBoard(bombField);
        generateBombs(bombField);
    }

    // Método para llenar una matriz con celdas desconocidas.
    public static void fillBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = UNKNOWN_CHAR;
            }
        }
    }

    // Método para generar bombas en el tablero de bombas de manera aleatoria.
    public static void generateBombs(String[][] board) {
        int row;
        int column;

        for (int i = 0; i < numberOfBombs; ) {
            row = (int) Math.floor((Math.random() * boardSize));
            column = (int) Math.floor((Math.random() * boardSize));

            // Se verifica si la celda no contiene ya una bomba antes de colocar una.
            if (!board[row][column].equals(BOMB_CHAR)) {
                board[row][column] = BOMB_CHAR;
                i++;
            }
        }
    }

    // Método para mostrar el tablero del jugador en la consola.
    public static void displayBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(" " + board[i][j] + " ");
            }

            System.out.println();
        }
    }

    // Método para mostrar el tablero final revelando todas las bombas.
    public static void displayFinishedBoard(String[][] board) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (bombField[i][j].equals(BOMB_CHAR)) {
                    board[i][j] = BOMB_CHAR;
                }
            }
        }

        displayBoard(board);
    }

    // Método recursivo para contar las bombas adyacentes y realizar la expansión del área en caso de celdas vacías.
    public static void countAdjacentMines(String[][] playerBoard, String[][] bombField, int x, int y) {
        if (x < 0 || x >= playerBoard.length || y < 0 || y >= playerBoard.length) return;

        if (bombField[x][y].equals(BOMB_CHAR)) {
            hasLost = true;
        }

        if (playerBoard[x][y].equals(UNKNOWN_CHAR)) {
            int count = checkForBombs(bombField, x, y);

            if (count == 0) {
                playerBoard[x][y] = OPEN_CHAR;
            } else {
                playerBoard[x][y] = String.valueOf(count);
            }

            if (count == 0) {
                countAdjacentMines(playerBoard, bombField, x - 1, y - 1);
                countAdjacentMines(playerBoard, bombField, x - 1, y);
                countAdjacentMines(playerBoard, bombField, x - 1, y + 1);
                countAdjacentMines(playerBoard, bombField, x, y - 1);
                countAdjacentMines(playerBoard, bombField, x, y);
                countAdjacentMines(playerBoard, bombField, x, y + 1);
                countAdjacentMines(playerBoard, bombField, x + 1, y - 1);
                countAdjacentMines(playerBoard, bombField, x + 1, y);
                countAdjacentMines(playerBoard, bombField, x + 1, y + 1);
            }
        }
    }

    // Método para contar el número de bombas en las celdas adyacentes.
    public static int checkForBombs(String[][] board, int x, int y) {
        int nearBombs = 0;

        for (int i = (x - 1); i <= (x + 1); i++) {
            if (i < 0 || i >= board.length) continue;

            for (int j = (y - 1); j <= (y + 1); j++) {
                if (j < 0 || j >= board.length) continue;

                if ((x != i || y != j) && board[i][j].equals(BOMB_CHAR)) {
                    nearBombs++;
                }
            }
        }

        return nearBombs;
    }

    // Método para verificar si el jugador ha ganado.
    public static boolean checkForWin() {
        int totalSpaces = (int) Math.pow(boardSize, 2) - numberOfBombs;
        int openSpaces = 0;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!board[i][j].equals(UNKNOWN_CHAR) && !bombField[i][j].equals(BOMB_CHAR)) {
                    openSpaces++;
                }
            }
        }

        // El jugador gana si todas las celdas seguras se han abierto.
        return openSpaces == totalSpaces;
    }
}
