package JointProjects.main.BattleshipGameClient;

import java.io.*;
import java.net.Socket;

public class MainClient {
    static final int port = 7777;
    private static Socket clientSocket; // Сокет для общения
    private static BufferedReader in; // Поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private static String line; // Содержит строку, которую вводит игрок в консоли
    private static BufferedReader reader;
    static int step = 1; // Переменная с этапом, на котором, в данный момент, находится игра
    static Commands command; // Строка с одной из команд Enum Commands. Нужна для проверки на правильность введенной команды и на доступность команды в процессе игры

    public static void main(String[] args) throws IOException {
        System.out.println("Добрый День! Введите одну из следующих команд:");
        help();
        printCommand();

        //  Подключение игрока к серверу, если игрок введёт в терминале /play
        if (line.equals("/play")) {
            try {
                clientSocket = new Socket("26.214.188.116", 7777);
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                System.out.println("Игра начинается... Ждём подключение соперника");

                // Клиент первым делом получает от сервера строку, в которой необходимо заменить все символы "#" на символ переноса строки "\n"
                System.out.println('\n' + in.readLine().replace('#','\n')); // Выводим на экран игровую доску с расставленными кораблями
                String text;
                //Цикл, в котором будет происходить общение сервера с клиентом
                while (true) {

                    // TO DO    1. Сервер должен отправлять "Ваш ход:" и "Ход противника:"

                    System.out.println("Ожидание хода:");
                    text = reader.readLine();
                    if(text.equals("/end")) break; // Сюда будем вписывать все возможные команды

                    while (!text.matches("[А-ИК]\\d")) {
                        System.out.println("Попробуйте еще раз:");
                        text = reader.readLine();
                    }
                    out.write(text + '\n');
                    out.flush();

                    System.out.println('\n' + in.readLine().replace('#','\n'));
                }
            } finally { // В конце необходимо закрыть сокет и потоки
                System.out.println("Выключаемся...");
                System.out.println("Спасибо за игру!");
                try {
                    clientSocket.close();
                    reader.close();
                    in.close();
                    out.close();
                } catch (IOException ignored) {}
            }
        }

        while (command != Commands.END) {
            printCommand();
        }
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