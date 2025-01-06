import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
          FileInputStream databaseFile = new FileInputStream(new File(databaseFilePath));
          //parte 1 do desafio
          System.out.println("database page size: " +getPageSize(databaseFile));
          //parte 2 do desafio
          System.out.println("number of tables: " +countTable(databaseFile));
          
        } catch (IOException e) {
          System.out.println("Error reading file: " + e.getMessage());
        }
      }
      default -> System.out.println("Missing or invalid command passed: " + command);
    }
  }

  public static int getPageSize(FileInputStream databaseFile) throws IOException{
    databaseFile.skip(16); // Pula os primeiros 16 bytes do cabecalho
    byte[] pageSizeBytes = new byte[2]; // armazena o tamanho em bytes
    databaseFile.read(pageSizeBytes);
    short pageSizeSigned = ByteBuffer.wrap(pageSizeBytes).getShort();
    int pageSize = Short.toUnsignedInt(pageSizeSigned);
    return pageSize;
  }

  public static int countTable(FileInputStream databasefile) throws IOException{
    int cont;
    byte[] data = new byte[2];
    databasefile.skip(100+3);
    databasefile.read(data);
    cont = (int) ByteBuffer.wrap(data).getShort();
    return cont;
  }
}
