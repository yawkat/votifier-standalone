package at.yawk.votifier.votifier;

import at.yawk.votifier.VotifierKeyPair;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yawkat
 */
class Generator {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java -cp <filename> " + Generator.class.getName() +
                               " generate [output file] [size]");
            System.exit(-1);
        }
        if (args[0].equalsIgnoreCase("generate")) {
            String target = "-";
            int size = -1;
            if (args.length > 1) {
                target = args[1];
            }
            if (args.length > 2) {
                size = Integer.parseInt(args[2]);
            }

            Path targetPath = null;
            if (!target.equals("-")) {
                targetPath = Paths.get(target).toAbsolutePath();
                if (Files.exists(targetPath)) {
                    System.err.println("Target file already exists.");
                    System.exit(-1);
                }
                if (!Files.isDirectory(targetPath.getParent())) {
                    System.err.println("Parent is not a directory.");
                    System.exit(-1);
                }
            }

            System.err.println("Generating key pair...");
            VotifierKeyPair keyPair = size < 0 ? VotifierKeyPair.generate() : VotifierKeyPair.generate(size);
            if (targetPath == null) {
                System.err.println("Printing...");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
                keyPair.write(writer);
                writer.newLine();
                writer.flush();
            } else {
                System.err.println("Saving...");
                keyPair.write(targetPath);
            }
        } else {
            System.err.println("Unknown operation");
            System.exit(-1);
        }
    }
}
