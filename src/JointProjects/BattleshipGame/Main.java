package JointProjects.BattleshipGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    static int step = 1;

    public static void main(String[] args) {
        Commands command;

        System.out.println("Добрый день! Введите одну из следующих команд:");
        help();
        //host or client
        command = printCommand();
        /*
            Здесь будет подключение двух игроков.
            Для хоста запускается метод becomeHost и ожидает подключение игрока.
            Для клиента запускается метод becomePlayer и ожидается ответ от хоста: сгенерированные доски двух игроков.
        */
        if (command.equals(Commands.CONNECTION_HOST)) {
            Game.Server server;
            server = new Game.Server();
            server.becomeServer();
        } else if (command.equals(Commands.CONNECTION_CLIENT)) {
            Game.Client client = new Game.Client();
            client.becomeClient();
        }
        //connect
        command = printCommand();
        //play
        while (command != Commands.END) {
            command = printCommand();
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

    private static Commands printCommand() {
        Commands command = Commands.UNKNOWN;
        String line;
        BufferedReader reader = null;
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
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return command;
    }
}