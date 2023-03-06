package JointProjects.BattleshipGameClient;

import java.io.*;
import java.net.Socket;

public class MainClient {
    static final int port = 7777;
    private static Socket clientSocket; // Сокет для общения
    private static BufferedReader in; // Поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private static String line; // Содержит строку, которую вводит игрок в консоли
    private static BufferedReader reader; // Нам нужен ридер читающий с консоли, иначе как мы узнаем что хочет сказать клиент?
    static int step = 1; // Переменная с этапом, на котором, в данный момент, находится игра
    static Commands command; // Строка с одной из команд Enum Commands. Нужна для проверки на правильность введенной команды и на доступность команды в процессе игры

    public static void main(String[] args) {
        System.out.println("Добрый День! Введите одну из следующих команд:");
        help();
        printCommand();

        //  Подключение игрока к серверу, если игрок введёт в терминале  /connect
        if (line.equals("/connect")) {
            try {
                try {
                    // адрес - локальный хост, порт - 4004, такой же как у сервера
                    clientSocket = new Socket("localhost", port); // этой строкой мы запрашиваем
                    //  у сервера доступ на соединение
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    // читать соообщения с сервера
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // писать туда же
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                    // если соединение произошло и потоки успешно созданы - мы можем
                    //  работать дальше и предложить клиенту что то ввести
                    // если нет - вылетит исключение
                    String word = reader.readLine(); // ждём пока клиент что-нибудь
                    // не напишет в консоль
                    out.write(word + "\n"); // отправляем сообщение на сервер
                    out.flush();
                    String serverWord = in.readLine(); // ждём, что скажет сервер
                    System.out.println(serverWord); // получив - выводим на экран
                } finally { // в любом случае необходимо закрыть сокет и потоки
                    System.out.println("Закрываем клиент...");
                    try {
                        clientSocket.close();
                        in.close();
                        out.close();
                    } catch (NullPointerException e) {
                        System.out.println("Ошибка. Невозможно закрыть неоткрытый поток ввода-вывода");
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }

        }
        // ДЛЯ ПЕТИ! Во втором блоке Try будет цикл while(true). Там и будем считывать что передаёт клиент, и что отправляет сервер.

        printCommand();

        while (command != Commands.END) {
            printCommand();
        }
        System.out.println("Игра завершена!");
    }

    private static void help() {
        for (Commands c : Commands.values()) {
            if (c.isAvailable(step)) {
                System.out.println(c.getName() + " - " + c.getDescription());
            }
        }
    }

    private static void printCommand() {
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            boolean isCommand;
            do {
                line = reader.readLine();
                command = Commands.getCommand(line);
                isCommand = command != Commands.UNKNOWN;
                if (!isCommand) {
                    System.out.println("Неизвестная команда!");
                    System.out.println("Введите одну из следующих команд:");
                    help();
                }
            } while (!isCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setUserMessageFromTerminal() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}