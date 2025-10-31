
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class client{
    private static final String server_address = "localhost";
    private static final int port=1234;

    public static void main(String[] args) throws InterruptedException {
        Socket socket;
        try {
            socket=new Socket(server_address,port);
            System.out.println("Connected to the chat server.");
            Thread r=new ReadThread(socket);
            Thread w= new WriteThread(socket);
            r.start();
            w.start();
            r.join();
            w.join();
            socket.close();
        } 
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

            
    }
}

class ReadThread extends Thread{

    private BufferedReader br;

    public ReadThread(Socket socket) {
        try {
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run(){ 
            String message;
        try {
            while((message=br.readLine())!=null){
                System.out.println("\n"+message);
            }   } 
        catch (IOException ex) {    
                System.out.println(ex);
        }
        finally{
            try {
                if (br != null) br.close();
            } 
            catch (IOException ex) {
                
            }
        }
    }
}

class WriteThread extends  Thread{ 

            private  PrintWriter pw;
            private BufferedReader bf;

            WriteThread(Socket socket){
                try {
                    pw = new PrintWriter(socket.getOutputStream(),true);
                    bf = new BufferedReader(new InputStreamReader(System.in));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            @Override
            public void run(){
                try {
                    while (true) {
                    String message = bf.readLine();
                    if (message.equalsIgnoreCase("exit")) {
                    System.out.println("You left the chat.");
                    break;
                    }
                    pw.println(message);
                    }
                } catch (IOException ex) {
                        ex.getMessage();
                }
                finally{
                    try {
                        bf.close();
                        pw.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }

}
