package JointProjects.BattleshipGameServer;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class MainServer {

    private static LinkedList<ClientHandling> connectedClients = new LinkedList<>(); // Список всех потоков.
    public static void main(String[] args) throws IOException {
        // backlog = 4, из-за ограничения одновременных подключений в RadminVPN.
        ServerSocket server = new ServerSocket(7777, 4, InetAddress.getByName("26.214.188.116"));
        System.out.println("Сервер запущен!");

        while (true){
            Socket player1 = server.accept();
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()))) {
                out.write("Ожидаем второго игрока...");
                out.flush();
            }
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
        private Socket player1;
        private Socket player2;
        private BufferedReader in1; // поток чтения из сокета игрока 1
        private BufferedWriter out1; // поток записи в сокет игрока 1
        private BufferedReader in2; // поток чтения из сокета 2
        private BufferedWriter out2; // поток записи в сокет 2
        private Desk clientVisibleGameDesk1; // Игровая доска игрока 1
        private Desk clientNotVisibleGameDesk1; // Игровая доска противника 1
        private Desk clientVisibleGameDesk2; // Игровая доска игрока 2
        private Desk clientNotVisibleGameDesk2; // Игровая доска противника 2
        StringBuilder message;

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
            } catch (IOException ignored) {}
            return text;
        }

        private boolean areClientsConnected(){
            try {
                int state1 = player1.getInputStream().read();
                int state2 = player2.getInputStream().read();
                return (state1 != -1 && state2 != -1);
            } catch (IOException e) {
                System.err.println("Один из клиентов отключился");
            }
            return false;
        }

        @Override
        public void run() {
            clientVisibleGameDesk1 = new Desk(true); // Инициализируем доску рандомно расставленными кораблями
            clientVisibleGameDesk2 = new Desk(true);
            clientNotVisibleGameDesk1 = new Desk(false); // Инициализируем пустую доску
            clientNotVisibleGameDesk2 = new Desk(false);
            message = new StringBuilder();
            // Вводим дополнительный символ "#", чтобы отправлять двумерный массив одной строкой
            // Клиент при получении строки, будет заменять "#" на символ переноса строки "\n"

            message.append("Ваша доска:#").append(clientVisibleGameDesk1.getPrintedDesk());
            message.append("#Доска противника:#").append(clientNotVisibleGameDesk1.getPrintedDesk()).append('\n');
            sendMessageToClient(out1, message.toString());
            message.setLength(0); // Очищаем строку

            message.append("Ваша доска:#").append(clientVisibleGameDesk2.getPrintedDesk());
            message.append("#Доска противника:#").append(clientNotVisibleGameDesk2.getPrintedDesk()).append('\n');
            sendMessageToClient(out2, message.toString());
            message.setLength(0);

            while (true) {
                if (!areClientsConnected()) {
                    connectedClients.remove(this);
                    break;
                }

                String text = getMessageFromClient(in1);
                System.out.println(text);
            }
        }
    }
}
