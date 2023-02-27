package JointProjects.BattleshipGame;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    /*
        class Main - класс на котором мы будем запускать приложение
        class Desk - описана логика прорисовки доски
        class Ship - описана логика взаимодействия с кораблем: узнать состояние, изменить состояние
        class Game - описана логика самой игры, правила
        class Multiplayer - описана логика подключения двух игроков к игре.
        class DBConnection - описано подключение к БД
    */
    private class Desk {

    }

    private class Ship {

    }


    public static class Server {
        final int port = 7777;
        private static Socket clientSocket; //сокет для общения
        private static ServerSocket server; // серверсокет
        private static BufferedReader in; // поток чтения из сокета
        private static BufferedWriter out; // поток записи в сокет

        public void becomeServer() {
            try {
                try {
                    server = new ServerSocket(port); // серверсокет прослушивает порт 4004
                    System.out.println("Сервер запущен по адресу: " + Inet4Address.getLocalHost().getHostAddress()
                                        + ":" + port);
                    //   хорошо бы серверу
                    //   объявить о своем запуске
                    clientSocket = server.accept(); // accept() будет ждать пока
                    //кто-нибудь не захочет подключиться
                    try { // установив связь и воссоздав сокет для общения с клиентом можно перейти
                        // к созданию потоков ввода/вывода.
                        // теперь мы можем принимать сообщения
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        // и отправлять
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                        String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                        System.out.println(word);
                        // не долго думая отвечает клиенту
                        out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                        out.flush(); // выталкиваем все из буфера

                    } finally { // в любом случае сокет будет закрыт
                        clientSocket.close();
                        // потоки тоже хорошо бы закрыть
                        in.close();
                        out.close();
                    }
                } finally {
                    System.out.println("Сервер закрыт!");
                    server.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static class Client {
        final int port = 7777;
        private static Socket clientSocket; //сокет для общения
        private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
        // мы узнаем что хочет сказать клиент?
        private static BufferedReader in; // поток чтения из сокета
        private static BufferedWriter out; // поток записи в сокет

        public void becomeClient() {
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
                    System.out.println("Клиент был закрыт...");
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }

        }
    }


    private class DBConnection {

    }
}