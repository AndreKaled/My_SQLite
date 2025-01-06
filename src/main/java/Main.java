import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args){
    if (args.length < 2) {
      System.out.println("Missing <database path> and <command>");
      return;
    }

    String databaseFilePath = args[0];
    String command = args[1];

    switch (command) {
      case ".dbinfo" -> {
        try {
          byte[] bytes = Files.readAllBytes(Path.of(databaseFilePath));
          ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
          //parte 1 do desafio
          System.out.println("database page size: " +getPageSize(buf));
          //parte 2 do desafio
          System.out.println("number of tables: " +countTable(buf));
          
        } catch (IOException e) {
          System.out.println("Error reading file: " + e.getMessage());
        }
      }
      default -> System.out.println("Missing or invalid command passed: " + command);
    }
  }

  public static int getPageSize(ByteBuffer buf) throws IOException{
    buf.position(16);//pula o header
    int pageSize = buf.getShort(); //pega 2 bytes (referentes ao tamanho)
    return pageSize;
  }

  public static int countTable(ByteBuffer buf) throws IOException {
    buf.position(100+3);
    return Short.toUnsignedInt(buf.getShort());
  }
}
