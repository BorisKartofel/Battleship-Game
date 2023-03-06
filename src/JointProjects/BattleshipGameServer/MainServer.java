package JointProjects.BattleshipGameServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    static final int port = 7777;
    private static Socket clientSocket; //сокет для общения
    private static ServerSocket server; // серверсокет
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) {
        try {
            try  {
                server = new ServerSocket(port); // серверсокет прослушивает порт 4004
                System.out.println("Сервер запущен!"); // хорошо бы серверу объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока кто-нибудь не захочет подключиться
                try { // установив связь и воссоздав сокет для общения с клиентом можно перейти к созданию потоков ввода/вывода.

                    // теперь мы можем принимать сообщения
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // и отправлять сообщения
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                    System.out.println(word);
                    // не долго думая отвечает клиенту
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                    out.flush(); // выталкиваем все из буфера

                } finally { // в любом случае сокет будет закрыт
                    try {
                        clientSocket.close();
                        // потоки тоже хорошо бы закрыть
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        System.err.println("Ошибка. Невозможно закрыть неоткрытый поток ввода-вывода");
                    }
                }
            } finally {
                System.out.println("Сервер закрыт!");
                try {
                    server.close();
                } catch (IOException e) {
                    System.err.println("Ошибка. Невозможно закрыть незапущенный сервер");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка. Не удалось запустить сервер");
        }
    }
}
