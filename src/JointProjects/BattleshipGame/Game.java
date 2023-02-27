package JointProjects.BattleshipGame;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        HashMap<String, Square> desk;
        boolean isVisable;

        public void drawDesk(){
            int counter = 0, rowCounter = 1;
            char c;
            System.out.print(" | А |Б |В | Г |Д |Е |Ж |З |И |Й |");
            for (Map.Entry<String, Square> e : desk.entrySet()) {
                if (counter == 0) {
                    System.out.println();
                    System.out.print(rowCounter + "| ");
                }
                if (isVisable) {
                    System.out.print(e.getKey() + ""); // temp
                    //System.out.print(e.getValue().getSymbol());
                }
                counter++;
                if (counter == 9) {
                    rowCounter++;
                    counter = 0;
                }
            }
        }

        private Desk(boolean isVisable) {
            this.isVisable = isVisable;

            this.desk = new HashMap<>();
            int count1x = 4, count2x = 3, count3x = 2, count4x = 1;
            for (int i = 1; i <= 10; i++){
                for (char c = 'А'; c < 1050; c++){
                    this.desk.put("" + c + i, Square.EMPTY);
                }
            }
            int x,y;
            char c;
            while ( count1x + count2x + count3x + count4x != 0 ) {
                 x = (int)(Math.random() * 10 + 1);
                 y = (int)(Math.random() * 10 + 1);

                 c = (char)(1040 + y);
                 if (desk.get("" + c + x) == Square.EMPTY) {

                 }
            }
        }
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