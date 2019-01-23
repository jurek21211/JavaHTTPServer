import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.charset.*;

public class SerwerHTTP
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serv=new ServerSocket(80);

        while(true)
        {
            //przyjecie polaczenia
            System.out.println("Oczekiwanie na polaczenie...");
            Socket sock=serv.accept();

            //strumienie danych
            InputStream is=sock.getInputStream();
            OutputStream os=sock.getOutputStream();
            BufferedReader inp=new BufferedReader(new InputStreamReader(is));
            DataOutputStream outp=new DataOutputStream(os);

            //przyjecie zadania (request)
            String request=inp.readLine();
            String[] plik;

            //wyslanie odpowiedzi (response)
            if(request.startsWith("GET"))
            {
                try{
                    plik = request.split(" ");
                    FileInputStream fis = new FileInputStream(plik[1].substring(1));
                    int iloscBitow = 0;
                    iloscBitow = fis.available();
                    String nazwaPliku = plik[1].substring(1);
                    int dlugoscNazwy = nazwaPliku.length() - 1;
                    String rozszerzenie = "";
                    for(int i=0 ; i<4; i++){
                        rozszerzenie = nazwaPliku.charAt(dlugoscNazwy) + rozszerzenie;
                        dlugoscNazwy = dlugoscNazwy - 1;
                    }

                    //response header

                    outp.writeBytes("HTTP/1.0 200 OK\r\n");
                    if(rozszerzenie.equals("html")){
                        outp.writeBytes("Content-Type: text/html\r\n");
                    }
                    else{
                        outp.writeBytes("Content-Type: text\r\n");
                    }
                    outp.writeBytes("Content-Length: "+iloscBitow*8+"\r\n");
                    outp.writeBytes("\r\n");

                    //response body
                    outp.writeBytes("<html>\r\n");
                    outp.writeBytes("<H1>Strona testowa</H1></br>\r\n");
                    outp.writeBytes(request+"</br>");
                    plik = request.split(" ");
                    outp.writeBytes("Rzadany plik: "+plik[1]+"</br>");

                    while((request = inp.readLine())!=null){
                        outp.writeBytes(request+"</br>");
                        if(request.isEmpty()){
                            break;
                        }
                    }

                    byte[] bufor;
                    bufor=new byte[1024];
                    int n=0;

                    while ((n = fis.read(bufor)) != -1 )
                    {
                        outp.write(bufor, 0, n);
                        outp.writeBytes("</br>");
                    }
                    outp.writeBytes("</html>\r\n");

                }
                catch (NoSuchFileException e){
                    outp.writeBytes("Zly plik");
                }
                catch (FileNotFoundException e){
                    outp.writeBytes("Zly plik");
                }
            }
            else
            {
                outp.writeBytes("HTTP/1.1 501 Not supported.\r\n");
            }

            //zamykanie strumieni
            inp.close();
            outp.close();
            sock.close();
        }
    }
}