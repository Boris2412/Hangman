import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
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
        startGame();
    }

    public static void startGame() {
        System.out.println("\nВыберите действие:");
        System.out.println("Y - начать игру");
        System.out.println("N - выйти");
        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "Y":
                System.out.println("\nНачинаем играть");
                prepareGameWord();
                preparePlayerTry();
                break;
            case "N":
                System.out.println("Game over");
                break;
            default:
                System.out.println("Не удалось определить действие");
                startGame();
        }
    }

    public static void preparePlayerTry() {
        System.out.println("\nВведите букву:");
        char inputChar = scanner.nextLine().toUpperCase().charAt(0);

        usedChars.add(String.valueOf(inputChar));

        boolean hasCharInWord = false;
        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == inputChar) {
                maskedWord.setCharAt(i, inputChar);
                hasCharInWord = true;
            }
        }
        if (hasCharInWord) {
            System.out.println("\nЕсть такая буква");
        } else {
            System.out.println("\nТакой буквы нет.");
            userErrorCount++;
        }

        presentCurrentStateInfo();
        handleCurrentGameState();
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

    public static void handleCurrentGameState() {
        boolean isWordGuessed = guessedWord.equals(maskedWord.toString());

        if (isWordGuessed) {
            System.out.println("\nПоздравляем! Вы выиграли!");
            System.out.println("Предлагаем сыграть еще раз");
            resetAll();
            startGame();
        } else if (userErrorCount > 5) {
            System.out.println("\nИгра закончена. Вы проиграли!");
            System.out.println("Было загадано слово - " + guessedWord);
            System.out.println("Предлагаем сыграть еще раз");
            resetAll();
            startGame();
        } else {
            preparePlayerTry();
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
            System.out.println("Файл пуст.");
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