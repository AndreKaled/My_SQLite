import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

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
          System.err.println("Error reading file: " + e.getMessage());
        }
      }
      //parte 3
      case ".tables" -> {
        try {
          byte[] bytes = Files.readAllBytes(Path.of(databaseFilePath));
          ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
          System.out.println("Nomes das tabelas:");
          for(String s: getTablesName(buf)){
            System.out.println("| " +s +" |");
          }
        } catch (IOException e) {
          // TODO: handle exception
          System.err.println("Error reading file: " + e.getMessage());
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

  public static String[] getTablesName(ByteBuffer buf) throws IOException {
    int numberTables = countTable(buf);
    String[] tables = new String[numberTables];
    int pointerCells = 100 + 8;
    buf.position(pointerCells);
    int offset_inicio = readVarInt(buf); // Usando readVarInt para o offset de início

    for (int i = 0; i < numberTables; i++) {
        // Ler os dados considerando o deslocamento
        buf.position(offset_inicio + 2 * i);
        int offset_cell = readVarInt(buf); // Usando readVarInt para o offset da célula
        
        // Pula ao registro
        buf.position(offset_cell);
        tables[i] = decodeNameTable(buf);
    }
    return tables;
  }

  public static int readVarInt(ByteBuffer buf) {
    int result = 0;
    int shift = 0;
    while (true) {
        byte b = buf.get();
        result |= (b & 0x7F) << shift;
        shift += 7;
        if ((b & 0x80) == 0) {
            break;
        }
    }
    return result;
}


  public static String decodeNameTable(ByteBuffer buf){
    int cabecalho = buf.get() & 0xFF;
    //pula o cabecalho
    buf.position(buf.position() + cabecalho);

    //le a string
    int length = buf.get();
    byte[] strBytes = new byte[length];
    buf.get(strBytes);
    return new String(strBytes);
  }
}