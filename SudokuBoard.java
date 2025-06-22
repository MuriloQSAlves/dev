import java.util.Random;

public class SudokuBoard {

    private int[][] board;
    private int[][] initialBoard; // Para manter o estado original do puzzle
    private final int N = 9; // Tamanho do tabuleiro 9x9
    private final int SRN = (int) Math.sqrt(N); // Tamanho da submatriz (3x3)
    private Random random = new Random();

    public SudokuBoard() {
        board = new int[N][N];
        initialBoard = new int[N][N];
    }

    // Gera um novo tabuleiro de Sudoku completo e, em seguida, remove dígitos para criar o puzzle.
    public void generateNewPuzzle(int difficulty) {
        // Limpa o tabuleiro antes de gerar um novo
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = 0;
            }
        }

        // Passo 1: Preenche as diagonais das submatrizes 3x3
        fillDiagonal();

        // Passo 2: Preenche o restante do tabuleiro usando backtracking
        fillRemaining(0, SRN);

        // Copia o tabuleiro resolvido para ser a base do puzzle
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                initialBoard[i][j] = board[i][j];
            }
        }

        // Passo 3: Remove alguns números para criar o puzzle
        removeKDigits(difficulty); // 'difficulty' será o número de dígitos a serem removidos
    }

    // Preenche as submatrizes diagonais para garantir um ponto de partida válido
    private void fillDiagonal() {
        for (int i = 0; i < N; i = i + SRN) {
            fillBox(i, i);
        }
    }

    // Preenche uma submatriz 3x3 específica com números únicos
    private void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < SRN; i++) {
            for (int j = 0; j < SRN; j++) {
                do {
                    num = random.nextInt(N) + 1; // Gera um número de 1 a 9
                } while (!unUsedInBox(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    // Verifica se o número é seguro para ser colocado na célula (row, col) de acordo com as regras do Sudoku
    private boolean isSafe(int row, int col, int num) {
        return (unUsedInRow(row, num) &&
                unUsedInCol(col, num) &&
                unUsedInBox(row - row % SRN, col - col % SRN, num));
    }

    // Verifica se o número não está na linha
    private boolean unUsedInRow(int row, int num) {
        for (int col = 0; col < N; col++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    // Verifica se o número não está na coluna
    private boolean unUsedInCol(int col, int num) {
        for (int row = 0; row < N; row++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    // Verifica se o número não está na submatriz 3x3
    private boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < SRN; i++) {
            for (int j = 0; j < SRN; j++) {
                if (board[rowStart + i][colStart + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // Método principal para preencher o restante do tabuleiro usando backtracking
    private boolean fillRemaining(int i, int j) {
        if (j >= N && i < N - 1) {
            i = i + 1;
            j = 0;
        }
        if (i >= N && j >= N) {
            return true; // Tabuleiro preenchido com sucesso
        }

        // Pula células já preenchidas nas diagonais
        if (i < SRN) {
            if (j < SRN) {
                j = SRN;
            }
        } else if (i < N - SRN) {
            if (j == (i / SRN) * SRN) {
                j = j + SRN;
            }
        } else {
            if (j == N - SRN) {
                i = i + 1;
                j = 0;
                if (i >= N) {
                    return true;
                }
            }
        }

        for (int num = 1; num <= N; num++) {
            if (isSafe(i, j, num)) {
                board[i][j] = num;
                if (fillRemaining(i, j + 1)) {
                    return true;
                }
                board[i][j] = 0; // Backtrack
            }
        }
        return false;
    }

    // Remove K dígitos para criar o puzzle jogável
    private void removeKDigits(int K) {
        int count = K;
        while (count != 0) {
            int cellId = random.nextInt(N * N);
            int i = (cellId / N);
            int j = cellId % N;
            if (board[i][j] != 0) {
                int temp = board[i][j]; // Guarda o número para verificar se a remoção mantém solução única
                board[i][j] = 0;

                // Uma verificação de solução única seria complexa para um Sudoku simples.
                // Para este exemplo, apenas removemos sem garantir unicidade da solução.
                count--;
            }
        }
    }

    // Imprime o tabuleiro formatado no console
    public void printBoard() {
        System.out.println("-------------------------");
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    System.out.print(" . "); // Representa células vazias
                } else {
                    // Se o número foi parte do puzzle original, imprime-o normalmente.
                    // Se foi um número que o jogador preencheu, podemos diferenciá-lo (ex: com cor na GUI)
                    // No console, simplesmente imprimimos.
                    System.out.print(" " + board[i][j] + " ");
                }
                if ((j + 1) % SRN == 0) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if ((i + 1) % SRN == 0) {
                System.out.println("-------------------------");
            }
        }
    }

    // Tenta definir um número em uma célula.
    // Retorna true se a operação for bem-sucedida, false caso contrário.
    public boolean setNumber(int row, int col, int num) {
        // Ajusta para índices baseados em 0
        row--;
        col--;

        if (row < 0 || row >= N || col < 0 || col >= N) {
            System.out.println("Coordenadas inválidas. Use linhas e colunas de 1 a 9.");
            return false;
        }

        if (initialBoard[row][col] != 0) {
            System.out.println("Não é possível alterar um número pré-definido do puzzle.");
            return false;
        }

        // Se o número for 0, significa que o usuário quer limpar a célula
        if (num == 0) {
            board[row][col] = 0;
            return true;
        }

        // Verifica se o movimento é válido
        int originalValue = board[row][col]; // Guarda o valor original para o isSafe
        board[row][col] = 0; // Temporariamente limpa para a verificação do isSafe
        boolean safe = isSafe(row, col, num);
        board[row][col] = originalValue; // Restaura o valor original

        if (safe) {
            board[row][col] = num;
            return true;
        } else {
            System.out.println("Movimento inválido! O número " + num + " não pode ser colocado em (" + (row + 1) + ", " + (col + 1) + ").");
            return false;
        }
    }

    // Verifica se o tabuleiro atual está completamente preenchido e é uma solução válida
    public boolean isSolved() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    return false; // Ainda há células vazias
                }
                // Verifica a validade de cada número (se não fosse 0)
                int num = board[i][j];
                board[i][j] = 0; // Temporariamente remove para verificar a segurança
                if (!isSafe(i, j, num)) {
                    board[i][j] = num; // Restaura
                    return false;
                }
                board[i][j] = num; // Restaura
            }
        }
        return true; // Todos os números estão preenchidos e válidos
    }

    // Reseta o tabuleiro para o estado inicial do puzzle
    public void resetBoard() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = initialBoard[i][j];
            }
        }
        System.out.println("Tabuleiro resetado para o estado inicial.");
    }

    // Resolve o tabuleiro atual usando backtracking e imprime a solução
    public void solveAndShowSolution() {
        // Cria uma cópia do tabuleiro atual para resolver, para não alterar o puzzle original
        int[][] tempBoard = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tempBoard[i][j] = initialBoard[i][j]; // Começa da base do puzzle
            }
        }

        // Temporariamente usa o tabuleiro da classe para resolver, depois restaura
        int[][] currentBoardState = new int[N][N];
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                currentBoardState[i][j] = board[i][j]; // Salva o estado atual do jogador
            }
        }
        
        this.board = tempBoard; // Define o tabuleiro da classe para a cópia do puzzle para resolver

        if (solveSudokuBacktracking(0, 0)) {
            System.out.println("\n--- Solução do Sudoku ---");
            printBoard();
        } else {
            System.out.println("Não foi possível encontrar uma solução para o Sudoku (o que é inesperado para puzzles gerados).");
        }
        this.board = currentBoardState; // Restaura o tabuleiro do jogador
    }

    // Lógica de backtracking para resolver o Sudoku
    private boolean solveSudokuBacktracking(int row, int col) {
        if (row == N - 1 && col == N) {
            return true; // Tabuleiro resolvido
        }

        if (col == N) {
            row++;
            col = 0;
        }

        if (board[row][col] != 0) { // Se a célula já estiver preenchida (parte do puzzle ou preenchida anteriormente)
            return solveSudokuBacktracking(row, col + 1);
        }

        for (int num = 1; num <= N; num++) {
            if (isSafe(row, col, num)) {
                board[row][col] = num;
                if (solveSudokuBacktracking(row, col + 1)) {
                    return true;
                }
                board[row][col] = 0; // Backtrack
            }
        }
        return false;
    }
}