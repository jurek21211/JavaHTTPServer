import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.NoSuchFileException;


public class ServerHTTP {
    public static void main(String args[]) throws IOException {
        ServerSocket serv = new ServerSocket(80);

        while (true) {
            System.out.println("Waiting for connection...");
            Socket sock = serv.accept();

            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();
            BufferedReader dataInput = new BufferedReader(new InputStreamReader(is));
            DataOutputStream dataOutput = new DataOutputStream(os);

            //get full browser request
            String request = dataInput.readLine();
            String request2 = request;
            String message = "";
            while (request2.length() != 0) {
                message += request2 + "\n";
                request2 = dataInput.readLine();
            }
            System.out.println(message);

            String[] file;

            if (request.startsWith("GET")) {
                try {
                    file = request.split(" ");
                    FileInputStream fis = new FileInputStream(file[1].substring(1));
                    String fileName = null;

                    if (file[1].substring(1).length() == 0)
                        System.out.println("No file requested");
                    else {
                        System.out.println("requested file name: " + file[1].substring(1) + "\n");
                        fileName = file[1].substring(1);
                        String fileExtension = "";

                        int fileNameLength = fileName.length();
                        for (int i = fileNameLength - 4; i < fileNameLength; i++) {
                            fileExtension += fileName.charAt(i);
                        }
                        System.out.println(fileExtension);



                        //response
                        dataOutput.writeBytes("HTTP/1.0 200 OK\r\n");
                        if (fileExtension.equalsIgnoreCase("html"))
                            dataOutput.writeBytes("Content-Type: text/html\r\n");
                        else
                            dataOutput.writeBytes("Content-Type: text\r\n");
                        dataOutput.writeBytes("Content-Length: \r\n");
                        dataOutput.writeBytes("\r\n");

                        //response body
                        dataOutput.writeBytes("<html>\r\n");
                        dataOutput.writeBytes("<H1>Strona testowa</H1></br>\r\n ");

                        byte[] buffer;
                        buffer = new byte[1024];
                        int n = 0;

                        while ((n = fis.read(buffer)) != -1) {
                            dataOutput.write(buffer, 0, n);
                            dataOutput.writeBytes("</br>");
                        }

                    }
                    dataOutput.writeBytes("</html>\r\n");

                } catch (FileNotFoundException e) {
                    dataOutput.writeBytes("HTTP/1.0 404 Not Found");
                }

            } else {
                dataOutput.writeBytes("HTTP/1.1 501 Not supported.\r\n");
            }
            dataInput.close();
            dataOutput.close();
            sock.close();
        }
    }
}
