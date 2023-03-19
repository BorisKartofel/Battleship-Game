package JointProjects.BattleshipGameServer;

import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MainServer {
    static final int port = 7777;
    private static InetAddress bindAddr; // Адрес сервера
    private final static String radminLocalAddressIP = "26.214.188.116"; // Строковое представление адреса сервера
    private static Socket clientSocket; // Сокет для общения
    private static ServerSocket server; // серверсокет
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private static HashMap<String, Desk> playersMap = new HashMap<>(); // Здесь будут храниться пары ключ-значение игроков

    public static void main(String[] args) {
        // Получаем адрес сервера из строки (Получаем объект InetAddress из объекта String)
        // Он будет нужен, чтобы создавать сокет сервера каждый раз по одному и тому же адресу
        try {
            bindAddr = InetAddress.getByName(radminLocalAddressIP);
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        try {
            try  {
                server = new ServerSocket(port, 4, bindAddr); // backlog = 4, из-за ограничения одновременных подключений в RadminVPN.
                // Если был бы выкуплен постоянный IP адрес на стороннем сервере - такого ограничения не было бы
                System.out.println("Сервер запущен!"); // хорошо бы серверу объявить о своем запуске
                clientSocket = server.accept(); // accept() будет ждать пока кто-нибудь не захочет подключиться
                try {

                    // Получаем IP-адрес клиента, с которым установили соединение
                    String clientIP = ((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress().toString().replace("/","");
                    // Получаем порт, по которому клиент установил соединение с сервером
                    String clientPort = String.valueOf(((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getPort());
                    String clientIdentification = (clientIP + ":" + clientPort);
                    System.out.println("Подключен клиент " + clientIdentification);

                    // Создаём строку, которая будет служить ключом в мапе
                    String clientSocketAddress = clientSocket.getRemoteSocketAddress().toString();
                    // Создаём новую игровую доску
                    Desk playerDesk = new Desk(true);
                    if(!playersMap.containsKey(clientSocketAddress)) playersMap.put(clientSocketAddress, playerDesk);

                    // Установив связь и воссоздав сокет для общения с клиентом можно перейти к созданию потоков ввода/вывода.
                    // Теперь мы можем принимать сообщения
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // и отправлять сообщения
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String word = in.readLine(); // ждём пока клиент что-нибудь нам напишет
                    System.out.println(word);
                    // Отвечаем клиенту
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                    out.flush(); // выталкиваем все из буфера
                    //Цикл, в котором будет происходить общение сервера с клиентом
                    while (true){

                    }
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
                } catch (NullPointerException e) { // Нужно обработать исключение, когда сервер не смог создаться, соответственно переменная server ссылается на null
                    System.err.println("Ошибка. Невозможно закрыть незапущенный сервер");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка. Не удалось запустить сервер");
        }
    }



}
