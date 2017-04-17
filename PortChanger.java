
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PortChanger {

    public static void main(String[] args) throws Exception {
        if ((args.length < 2) || (args.length > 3)) {
            System.out.println("Usage: PortChanger <conf_location> <optional_port_src> <port_dest>");
            System.exit(1);
        }

        String tempPrefix = ".delete_me";
        String confFile = args[0];

        boolean firstLine = true;
        int srcPort = 80;
        int dstPort = 0;

        if (args.length == 3) {
            srcPort = checkPort(args[1]);
            dstPort = checkPort(args[2]);
        } else {
            dstPort = checkPort(args[1]);
        }

        BufferedReader rdr = new BufferedReader(new FileReader(confFile));
        FileWriter dst = new FileWriter(new File(confFile + tempPrefix), false);

        while (rdr.ready()) {

            String lineRead = rdr.readLine();
            if (lineRead.matches("^Listen .*:?" + srcPort + "$")) {
                lineRead = lineRead.replaceAll("\\d+$", String.valueOf(dstPort));
            }

            if (firstLine) { //Prevent unnecessary carriage return at the end of new file
                dst.write(lineRead);
                firstLine = false;
                continue;
            }
            dst.write(System.lineSeparator() + lineRead);
        }
        dst.close();
        rdr.close();

        Files.delete(Paths.get(confFile));
        Path source = Paths.get(confFile + tempPrefix);
        Files.move(source, source.resolveSibling(confFile));
    }

    private static int checkPort(String port) {
        int portnum = 0;

        portnum = Integer.parseInt(port);
        if ((portnum < 0) || (portnum > 65535)) {
            throw new IllegalArgumentException();
        }

        return portnum;
    }
}
