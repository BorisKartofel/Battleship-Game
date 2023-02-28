package JointProjects.BattleshipGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    static String line;
    static BufferedReader reader;
    static int step = 1;
    static Commands command;
    public static void main(String[] args) {
        System.out.println("Добрый День! Введите одну из следующих команд:");
        help();
        //host or client
        printCommand();

        //  Здесь будет подключение двух игроков.
        //  Для хоста запускается метод startServerConnection() и ожидает подключение игрока.
        //  Для клиента запускается метод startClientConnection() и ожидается ответ от хоста.

        //  Узнаем, хочет ли клиент быть сервером (/host) или клиентом (/connect)

        if (line.equals("/host")) {
            Game.Server server = new Game.Server();
            server.startServerHosting();
        } else {
            Game.Client client = new Game.Client();
            client.startClientConnection();
        }

        //connect
        printCommand();
        //play
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
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void setUserMessageFromTerminal() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}