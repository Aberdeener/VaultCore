package net.vaultmc.vaultcore.misc.commands.staff.logs;

import net.vaultmc.vaultcore.VaultCore;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class LogsHandler implements Runnable {

    private static HashMap<Integer, String> lineMatches = new HashMap<>();
    private static HashMap<Integer, String> lineFiles = new HashMap<>();
    private VLCommandSender sender;
    private String search;

    public LogsHandler(VLCommandSender sender, String search) {
        this.sender = sender;
        this.search = search;
    }

    public void run() {
        LogsCommand.setSearching(true);
        File logsDir = new File("/srv/" + VaultCore.getInstance().getConfig().getString("server") + "/logs/");
        for (File file : logsDir.listFiles()) {
            try {
                Scanner scanner = new Scanner(file);
                int lineID = 0;
                Pattern regex = Pattern.compile(search);
                Matcher matcher = null;
                String line = "";

                if (file.getName().endsWith(".gz")) {
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
                    while (inputReader.readLine() != null) {
                        line = inputReader.readLine();
                        lineID++;
                        matcher = regex.matcher(line);
                        if (matcher.find()) {
                            if (line.endsWith(sender.getName() + " issued server command: /logs " + search)) continue;
                            lineMatches.put(lineID, line);
                            lineFiles.put(lineID, file.getName());
                        }
                    }
                    inputReader.close();
                } else {
                    while (scanner.hasNextLine()) {
                        line = scanner.nextLine();
                        lineID++;
                        matcher = regex.matcher(line);
                        if (matcher.find()) {
                            if (line.endsWith(sender.getName() + " issued server command: /logs " + search)) continue;
                            lineMatches.put(lineID, line);
                            lineFiles.put(lineID, file.getName());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (!lineMatches.isEmpty()) {
            sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.logs.header"));
            for (int lineNumber : lineMatches.keySet()) {
                String fileName = lineFiles.get(lineNumber);
                String line = lineMatches.get(lineNumber);

                String substring = line.substring(line.indexOf("/INFO]:") + 7);

                sender.sendMessage(fileName + " -- " + substring);
            }
            lineFiles.clear();
            lineMatches.clear();
            LogsCommand.setSearching(false);
        } else {
            sender.sendMessage(VaultLoader.getMessage("vaultcore.commands.logs.no_matches"));
            LogsCommand.setSearching(false);
        }
    }
}