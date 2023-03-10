package JointProjects.BattleshipGame;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    public void start(boolean isMyTurn) {
        Desk MyDesk = new Desk(true);
        Desk EnemyDesk = new Desk(false);
        boolean isCanShoot;
        while (true) {
            isCanShoot = isMyTurn;
            while (isCanShoot) {
                //getCoordinates();
                //isCanShoot = EnemyDesk.isAbleToShootAgain();
                //EnemyDesk.makeShoot();
                EnemyDesk.drawDesk();
            }
        }
    }


    /*
        class Main - класс на котором мы будем запускать приложение
        class Desk - описана логика прорисовки доски
        class Ship - описана логика взаимодействия с кораблем: узнать состояние, изменить состояние
        class Game - описана логика самой игры, правила
        class Multiplayer - описана логика подключения двух игроков к игре.
        class DBConnection - описано подключение к БД
    */
    public class Desk {
        private Square[][] desk;
        private boolean isVisible;

        public void drawDesk(){

            int counter = 0, rowCounter = 1;
            char c;
            System.out.print(" |А |Б |В |Г |Д |Е |Ж |З |И |Й |");
            for (int y = 0; y < 10; y++) {
                System.out.println();
                System.out.print(y + "|");
                for (int x = 0; x < 10; x++){
                    if (isVisible) {
                        System.out.print(desk[y][x].getSymbol() + "");
                    }
                    else {
                        if (desk[y][x] != Square.BATTLESHIP) {
                            System.out.print(desk[y][x].getSymbol() + "");
                        }
                        else {
                            System.out.print(Square.EMPTY.getSymbol() + "");
                        }
                    }
                }
            }
            System.out.println();
        }

        private Desk(boolean isVisible) {
            this.isVisible = isVisible;
            int ships[] = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
            desk = new Square[10][10];
            for (int y = 0; y < 10; y++){
                //desk[y] = new Square[10];
                for (int x = 0; x < 10; x++){
                    desk[y][x] = Square.EMPTY;
                }
            }
            if (isVisible) {
                int x, y, x1, y1;
                boolean isHorisontal;
                boolean isFree = true;
                for (int i = 0; i < ships.length; ) {
                    x = (int) (Math.random() * 10);
                    x1 = x;
                    y = (int) (Math.random() * 10);
                    y1 = y;
                    isHorisontal = ((int) (Math.random() * 10) > 4 ? true : false);

                    if ((isHorisontal ? x : y) + ships[i] > 9) {
                        continue;
                    }

                    for (int l = 0; ((l < ships[i]) && isFree); l++) {
                        if (desk[y1][x1] != Square.EMPTY) {
                            isFree = false;
                        }
                        if (isHorisontal)
                            x1++;
                        else
                            y1++;
                    }
                    if (!isFree) {
                        isFree = true;
                        continue;
                    }

                    int vectorY = (isHorisontal ? 0 : ships[i] - 1),
                            vectorX = (isHorisontal ? ships[i] - 1 : 0),
                            minY = (y > 0 ? (y - 1) : 0),
                            maxY = (y + vectorY >= 9 ? (9 - ships[i]) : (y + vectorY + 1)),
                            minX = (x > 0 ? (x - 1) : 0),
                            maxX = (x + vectorX >= 9 ? (9 - ships[i]) : (x + vectorX + 1));
                    ///*
                    for (y1 = minY; y1 <= maxY; y1++) {//вместо 3 блоков закрывает 5, по середине не закрывает
                        for (x1 = minX; x1 <= maxX; x1++) {
                            desk[y1][x1] = Square.OCCUPIED;
                        }
                    }
                    for (y1 = y; y1 <= y + vectorY; y1++) {
                        for (x1 = x; x1 <= x + vectorX; x1++) {
                            desk[y1][x1] = Square.BATTLESHIP;
                        }
                    }
                    i++;
                }
            }
        }
        public void makeShoot(int x, int y) {

            if (desk[y][x] == Square.EMPTY || desk[y][x] == Square.OCCUPIED) {
                desk[y][x] = Square.EXPLORED;
            } else if (desk[y][x] == Square.BATTLESHIP) {
                desk[y][x] = Square.DAMAGED;
            } /* else if (desk[y][x] == Square.DAMAGED || desk[y][x] == Square.EXPLORED) {
                return;
            } */
        }
        public boolean isAbleToShootAgain(int x, int y) {
            return desk[y][x] == Square.BATTLESHIP || desk[y][x] == Square.DAMAGED || desk[y][x] == Square.EXPLORED;
        }
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