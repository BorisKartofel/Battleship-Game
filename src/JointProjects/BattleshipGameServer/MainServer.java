package JointProjects.BattleshipGameServer;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;

public class MainServer {
    private static final LinkedList<ClientHandling> connectedClients = new LinkedList<>(); // Список всех потоков.
    public static void main(String[] args) throws IOException {
        // backlog = 4, из-за ограничения одновременных подключений в RadminVPN.
        ServerSocket server = new ServerSocket(7777, 4, InetAddress.getByName("26.214.188.116"));
        System.out.println("Сервер запущен!");

        while (true){
            Socket player1 = server.accept();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()));
            out.write("Ожидаем второго игрока...");
            out.flush();
            Socket player2 = server.accept();
            // С каждым новым подключением клиента, для него будет создаваться отдельный поток.
            try {
                connectedClients.add(new ClientHandling(player1, player2));
                System.out.println("Новая пара игроков подключена!");
            } catch (IOException e) {
                System.err.println(e);
                player1.close();
                player2.close();
            }
        }
    }
    private static class ClientHandling extends Thread {
        private final Socket player1;
        private final Socket player2;
        private final BufferedReader in1; // поток чтения из сокета игрока 1
        private final BufferedWriter out1; // поток записи в сокет игрока 1
        private final BufferedReader in2; // поток чтения из сокета 2
        private final BufferedWriter out2; // поток записи в сокет 2
        private Desk shipFieldPlayer1; // Игровая доска игрока 1
        private Desk enemyShipFieldForPlayer1; // Игровая доска противника 1
        private Desk shipFieldPlayer2; // Игровая доска игрока 2
        private Desk enemyShipFieldForPlayer2; // Игровая доска противника 2
        StringBuilder message;
        HashMap<Character, Integer> map = new HashMap<>();
        {
            map.put('А', 0); map.put('Б', 1); map.put('В', 2); map.put('Г', 3); map.put('Д', 4);
            map.put('Е', 5); map.put('Ж', 6); map.put('З', 7); map.put('И', 8); map.put('К', 9);
        }
        

        private ClientHandling(Socket player1, Socket player2) throws IOException {
            this.player1 = player1;
            in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            out1 = new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()));
            this.player2 = player2;
            in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            out2 = new BufferedWriter(new OutputStreamWriter(player2.getOutputStream()));
            start();
        }

        private void sendMessageToClient(BufferedWriter out, String message){
            try {
                out.write(message);
                out.flush();
            } catch (IOException ignored) {}
        }

        private String getMessageFromClient(BufferedReader in){
            String text = null;
                try {
                    text = in.readLine();
                } catch (IOException e) {
                    System.err.println(e);
                }
            return text;
        }

        private boolean areClientsConnected(){
            int state1 = 0;
            int state2 = 0;
            try {
                state1 = player1.getInputStream().read();
                state2 = player2.getInputStream().read();
            } catch (IOException e) {
                System.err.println("Один из клиентов отключился");
            }
            return (state1 != -1 && state2 != -1);
        }

        @Override
        public void run() {

            shipFieldPlayer1 = new Desk(true); // Инициализируем доску рандомно расставленными кораблями
            shipFieldPlayer2 = new Desk(true);
            enemyShipFieldForPlayer1 = new Desk(false); // Инициализируем пустую доску. В неё будет записываться состояние после выстрела (попал/мимо)
            enemyShipFieldForPlayer2 = new Desk(false);
            message = new StringBuilder();

            // Вводим дополнительный символ "#", чтобы отправлять двумерный массив одной строкой
            // Клиент при получении строки, будет заменять "#" на символ переноса строки "\n"

            message.append("Ваша доска:#").append(shipFieldPlayer1.getPrintedDesk());
            message.append("#Доска противника:#").append(enemyShipFieldForPlayer1.getPrintedDesk()).append('\n');
            sendMessageToClient(out1, message.toString());
            message.setLength(0); // Очищаем строку

            message.append("Ваша доска:#").append(shipFieldPlayer2.getPrintedDesk());
            message.append("#Доска противника:#").append(enemyShipFieldForPlayer2.getPrintedDesk()).append('\n');
            sendMessageToClient(out2, message.toString());
            message.setLength(0);

            while (true) {

                // Вводим внутреннюю переменную для проверки, попал ли игрок в прошлом ходу
                boolean isAbleToShoot;
                // Строка, в которую будут записываться сообщения клиентов
                String text = getMessageFromClient(in1);
                // Строка с состоянием клетки, в которую произвели выстрел
                String cellStatus;

                    try {
                        while (!text.matches("[А-ИК]\\d")) {  // Проверяем, чтобы сообщение клиента обязательно было в принятом формате, например "Г1" или "И9"
                            sendMessageToClient(out1, "Неверный формат. Введите еще раз:");
                            text = getMessageFromClient(in1);
                        }
                        do {
                            // Игрок пишет серверу куда он стреляет, а сервер проверяет, попал ли игрок и отправляет ответ
                            isAbleToShoot = shipFieldPlayer2.isAbleToShootAgain(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10));
                            shipFieldPlayer2.shoot(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10));
                            cellStatus = shipFieldPlayer2.getRespond(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10));
                            message.append(cellStatus);

                            // Не забываем изменять состояние и второго игрового поля выстрелившего игрока
                            enemyShipFieldForPlayer1.setSquareStateAccordingToRespond(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10), cellStatus);

                            // Сервер отправляет игроку состояние доски, по которой он стреляет
                            message.append('#').append("#Доска противника:#").append(enemyShipFieldForPlayer1.getPrintedDesk()).append('\n');

                            sendMessageToClient(out1, message.toString());
                            message.setLength(0);

                            // TO DO
                            // Второе поле игрока 1 после выстрела по оппоненту не меняется.
                            // Клиент принимает некорректное отображение поля игрока

                            if (isAbleToShoot) {
                                text = getMessageFromClient(in1);
                                while (!text.matches("[А-ИК]\\d")) {  // Проверяем, чтобы сообщение клиента обязательно было в принятом формате, например "Г1" или "И9"
                                    sendMessageToClient(out1, "Неверный формат. Введите еще раз:");
                                    text = getMessageFromClient(in1);
                                }
                                System.err.println("Игрок 1 написал: " + text);
                            }
                        } while (isAbleToShoot);
                    } catch (NullPointerException e) {
                        System.err.println(e);
                        System.err.println("Игрок 1 отключился. Удаляем комнату");
                        sendMessageToClient(out2, "Ваш противник отключился");
                        connectedClients.remove(this);
                    } finally {
                        // TO DO
                        // Здесь прописать логику завершения игры, подсчёта очков и нахождения победителя
                    }

                // То же самое проделываем и со вторым игроком

                text = getMessageFromClient(in2);

                try {
                    while (!text.matches("[А-ИК]\\d")) {  // Проверяем, чтобы сообщение клиента обязательно было в принятом формате, например "Г1" или "И9"
                        sendMessageToClient(out2, "Неверный формат. Введите еще раз:");
                        text = getMessageFromClient(in2);
                    }
                    // Обнуляем вспомогательную переменную
                    isAbleToShoot = false;

                    do {
                        // Игрок пишет серверу куда он стреляет, а сервер проверяет, попал ли игрок и отправляет ответ
                        isAbleToShoot = shipFieldPlayer1.isAbleToShootAgain(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10));
                        message.append(shipFieldPlayer1.shootAndGetRespond(map.get(text.charAt(0)), Character.digit(text.charAt(1), 10))).append('#');

                        // Сервер отправляет игроку состояние доски, по которой он стреляет
                        message.append("#Доска противника:#").append(enemyShipFieldForPlayer2.getPrintedDesk()).append('\n');

                        sendMessageToClient(out2, message.toString());
                        message.setLength(0);

                        if (isAbleToShoot) {
                            text = getMessageFromClient(in2);
                            while (!text.matches("[А-ИК]\\d")) {  // Проверяем, чтобы сообщение клиента обязательно было в принятом формате, например "Г1" или "И9"
                                sendMessageToClient(out2, "Неверный формат. Введите еще раз:");
                                text = getMessageFromClient(in2);
                            }
                            System.err.println("Игрок 2 написал: " + text);
                        }
                    } while (isAbleToShoot);
                } catch (NullPointerException e) {
                    System.err.println(e);
                    System.err.println("Игрок 2 отключился. Удаляем комнату");
                    sendMessageToClient(out1, "Ваш противник отключился");
                    connectedClients.remove(this);
                } finally {
                }
            }
        }
    }
}
