import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server{
    private static final int PortNumber=1234;
    private static  Set<ClientHandler> clientHandle =new HashSet<>();

    public static void main(String[] args){
        try(ServerSocket serversocket= new ServerSocket(PortNumber);) {
            while(true){
                    Socket socket=serversocket.accept();
                    System.out.println("New user connected:"+socket.getInetAddress());
                    ClientHandler clientHandler=new ClientHandler(socket);
                    clientHandle.add(clientHandler);
                    new Thread(clientHandler).start();
            }
        } catch (IOException ex) {
             System.out.println(ex.getMessage());
        }
    }

    public static void broadcast(String message, ClientHandler excludeuser ){
        for(ClientHandler x: clientHandle){
            if(x!=excludeuser){
                x.sendmessage(message);
            }
        }

    }

    public static void removeClient(ClientHandler client){
                    clientHandle.remove(client);
                    System.out.println("Client disconnected");
    }

    

}

class ClientHandler implements Runnable{
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String ClientName;

    public ClientHandler(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run(){
        try {
            out= new PrintWriter(socket.getOutputStream(),true);
            out.println("Enter Your Name:");
            in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientName=in.readLine();
            System.out.println(ClientName + " joined the chat.");
            Server.broadcast("[SERVER] " + ClientName + " has joined chat", this);
            String message;
            while((message=in.readLine())!=null){
                System.out.println(ClientName + ": " + message);
                Server.broadcast(ClientName + ": " + message, this);
            }

        } catch (IOException ex) {
            System.out.println("Error with client: " + ex.getMessage());
        }
        finally{
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        Server.removeClient(this);
        Server.broadcast("[SERVER] "+ClientName + " left the chat.", this);
    }

    public void sendmessage(String message){
            out.println(message);
    }
}