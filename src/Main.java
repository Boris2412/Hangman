import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private enum MenuState {
        WAITING,
        PLAYING,
        EXITING
    }

    private enum GameState {
        PLAYING,
        FINISH_WITH_ERROR,
        FINISH_WITH_WINNING;
    }

    private static String WORDS_FILE_PATH = "src/resources/words";
    private static int MAX_ERROR_COUNT = 6;
    private static List<String> words = new ArrayList<>();
    private static String[] hangmanStateItems = configureHangmanStateItems();
    private static String guessedWord = "";
    private static StringBuilder maskedWord = new StringBuilder();
    private static List<String> usedChars = new ArrayList<>();
    private static int userErrorCount = 0;

    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();

    public static void main(String[] args) {
        configureWordsList();
        startMenu();
    }

    public static void startMenu() {
        MenuState menuState = MenuState.WAITING;

        do {
            System.out.println("\nВыберите действие:");
            System.out.println("Y - начать игру");
            System.out.println("N - выйти");
            String playerInput = scanner.nextLine().toUpperCase();
            menuState = configureMenuState(playerInput);
            handleMenuState(menuState);
        } while (menuState != MenuState.EXITING);
    }

    public static MenuState configureMenuState(String playerInput) {
        switch (playerInput) {
            case "Y":
                return MenuState.PLAYING;
            case "N":
                return MenuState.EXITING;
            default:
                return MenuState.WAITING;
        }
    }

    public static void handleMenuState(MenuState state) {
        switch (state) {
            case WAITING:
                System.out.println("\nНе удалось определить действие. Попробуйте снова");
                break;
            case PLAYING:
                System.out.println("\nНачинаем играть");
                prepareGameWord();
                startGameLoop();
                break;
            case EXITING:
                System.out.println("\nВыходим из игры");
        }
    }

    public static void startGameLoop() {
        do {
            char playerChar = chooseLetter();
            boolean isLetter = Character.isLetter(playerChar);
            boolean isUsedChar = usedChars.contains(String.valueOf(playerChar));

            if (isLetter && !isUsedChar) {
                usedChars.add(String.valueOf(playerChar));
                int[] indices = getCharIndices(playerChar);
                openHiddenLettersInWord(indices, playerChar);

                if (indices.length > 0) {
                    System.out.println("\nЕсть такая буква");
                } else {
                    System.out.println("\nТакой буквы нет.");
                    userErrorCount++;
                }

                GameState gameState = configureGameState();
                presentCurrentStateInfo();
                handleCurrentGameState(gameState);

                if (!gameState.equals(GameState.PLAYING)) {
                    resetAll();
                    return;
                }
            } else if (!isLetter) {
                System.out.println("\nНеверный символ. Можно вводить только буквы.");
            } else if (isUsedChar) {
                System.out.println("\nБуква уже была введена ранее.");
            }
        } while (true);
    }

    public static GameState configureGameState() {
        boolean isWordGuessed = guessedWord.equals(maskedWord.toString());

        if (isWordGuessed) {
            return GameState.FINISH_WITH_WINNING;
        } else if (userErrorCount > 5) {
            return GameState.FINISH_WITH_ERROR;
        } else {
            return GameState.PLAYING;
        }
    }

    public static char chooseLetter() {
        System.out.println("\nВведите букву:");
        char inputChar = scanner.nextLine().toUpperCase().charAt(0);
        return inputChar;
    }

    public static int[] getCharIndices(char playerChar) {
        List<Integer> charInWordIndices = new ArrayList<>();
        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == playerChar) {
                charInWordIndices.add(i);
            }
        }

        int[] charIndices = new int[charInWordIndices.size()];
        for (int i = 0; i < charInWordIndices.size(); i++) {
            charIndices[i] = charInWordIndices.get(i);
        }

        return charIndices;
    }

    public static void openHiddenLettersInWord(int[] indices, char playerChar) {
        for (int i = 0; i < indices.length; i++) {
            maskedWord.setCharAt(indices[i], playerChar);
        }
    }

    public static void presentCurrentStateInfo() {
        System.out.println("\n==============================");
        System.out.println("\nЗагаданное слово: " + maskedWord);
        System.out.println("Использованные буквы: " + String.join(", ", usedChars));
        System.out.println("Ошибок: " + userErrorCount + " из " + MAX_ERROR_COUNT);
        System.out.println("Текущее состояние: ");
        System.out.println("\n" + hangmanStateItems[userErrorCount]);
        System.out.println("==============================");
    }

    public static void handleCurrentGameState(GameState gameState) {
        switch (gameState) {
            case PLAYING:
                break;
            case FINISH_WITH_WINNING:
                System.out.println("\nПоздравляем! Вы выиграли!");
                System.out.println("Предлагаем сыграть еще раз");
                break;
            case FINISH_WITH_ERROR:
                System.out.println("\nИгра закончена. Вы проиграли!");
                System.out.println("Было загадано слово - " + guessedWord);
                System.out.println("Предлагаем сыграть еще раз");
        }
    }

    public static void configureWordsList() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(WORDS_FILE_PATH))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void prepareGameWord() {
        if (!words.isEmpty()) {
            String randomWord = words.get(random.nextInt(words.size()));
            guessedWord = randomWord.toUpperCase();

            maskedWord.setLength(0);
            for (int i = 0; i < randomWord.length(); i++) {
                maskedWord.append("*");
            }

            System.out.println("Загаданное слово: " + maskedWord);
        } else {
            System.out.println("Ошибка. Файл пуст.");
        }
    }

    public static void resetAll() {
        guessedWord = "";
        maskedWord.setLength(0);
        usedChars.clear();
        userErrorCount = 0;
    }

    public static String[] configureHangmanStateItems() {
        String[] hangmanStates = {
                "  -----\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " /    |\n" +
                        "      |\n" +
                        "=========",

                "  -----\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " / \\  |\n" +
                        "      |\n" +
                        "========="
        };

        return hangmanStates;
    }
}