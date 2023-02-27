package JointProjects.BattleshipGame;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.*;

public class Game {
    /*
        class Main - класс на котором мы будем запускать приложение
        class Desk - описана логика прорисовки доски
        class Ship - описана логика взаимодействия с кораблем: узнать состояние, изменить состояние
        class Game - описана логика самой игры, правила
        class Multiplayer - описана логика подключения двух игроков к игре.
        class DBConnection - описано подключение к БД
    */
    private static class Desk {

    }

    private static class Ship {

    }

    public static class Server {
        final int port = 7777;
        private static Socket clientSocket; //сокет для общения
        private static ServerSocket server; // серверсокет
        private static BufferedReader in; // поток чтения из сокета
        private static BufferedWriter out; // поток записи в сокет

        public void startServerHosting() {
            try {
                try {
                    server = new ServerSocket(port); // серверсокет прослушивает порт 7777
                    String address = Inet4Address.getLocalHost().getHostAddress();
                    System.out.println("Сервер запущен по IP: " + address);
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

                        while (!word.equals("Конец игры")) {
                            System.out.println("Клиент написал: " + word);
                            // не долго думая отвечает клиенту
                            out.write("Это Сервер! Подтверждаю, вы написали : " + word + "\n");
                            out.flush(); // выталкиваем все из буфера
                        }

                    } finally { // в любом случае сокет будет закрыт
                        System.out.println("Сервер выключается... Спасибо за игру ♥");
                        out.write("Конец игры" + "\n"); // пишем Клиенту, что игра заканчивается и надо отключиться
                        out.flush();
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
                throw new RuntimeException(e);
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

        public void startClientConnection() {
            try {
                try {
                    System.out.println("Пожалуйста, введите IP-адрес сервера (по типу 26.196.182.189) (порт не вводите, умоляю)");
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    String address = reader.readLine();
                    reader.close();
                    // адрес передаётся от сервера при подключении, а порт - 7777 по умолчанию
                    clientSocket = new Socket(address, port); // этой строкой мы запрашиваем
                    //  у сервера доступ на соединение

                    // читать соообщения с сервера
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // писать туда же
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    // если соединение произошло и потоки успешно созданы - мы можем
                    // работать дальше и предложить клиенту что то ввести
                    // если нет - вылетит исключение

                    // Создадим переменную String, чтобы зациклить чтение с консоли, пока не наступит Конец игры
                    String word;
                    do {
                        System.out.println("Вы что-то хотели сказать серверу? Введите это здесь:");
                        reader = new BufferedReader(new InputStreamReader(System.in));
                        word = reader.readLine(); // ждём пока клиент что-нибудь не напишет в консоль
                        reader.close();
                        out.write(word + "\n"); // отправляем сообщение на сервер
                        out.flush();
                        String serverWord = in.readLine(); // ждём, что скажет сервер
                        System.out.println(serverWord); // получив - выводим на экран
                    } while (!word.equals("Конец игры"));
                } finally { // в любом случае необходимо закрыть сокет и потоки
                    System.out.println("Вы отключились от сервера... Поиграли так поиграли!");
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    private static class DBConnection {

    }
}