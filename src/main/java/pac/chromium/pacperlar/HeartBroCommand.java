package pac.chromium.pacperlar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeartBroCommand implements CommandExecutor, TabCompleter {
    private final burger plugin;

    public HeartBroCommand(burger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version":
                sender.sendMessage("§6HeartBro версия: §e" + plugin.getDescription().getVersion());
                return true;

            case "cleardatabase":
                if (!sender.hasPermission("heartbro.admin")) {
                    sender.sendMessage("§cУ вас нет прав!");
                    return true;
                }
                plugin.getDbManager().clearDatabase();
                // Update all online players to default hearts
                for (Player p : Bukkit.getOnlinePlayers()) {
                    plugin.getHeartManager().setHearts(p, HeartManager.DEFAULT_HEARTS);
                }
                sender.sendMessage("§aБаза данных очищена. У всех онлайн игроков теперь 10 сердец.");
                return true;

            case "heart":
                if (!sender.hasPermission("heartbro.admin")) {
                    sender.sendMessage("§cУ вас нет прав!");
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage("§cИспользование: /heartbro heart {player} {add/set/remove} {amount}");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден!");
                    return true;
                }

                String action = args[2].toLowerCase();
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cКоличество должно быть числом!");
                    return true;
                }

                switch (action) {
                    case "add":
                        plugin.getHeartManager().addHearts(target, amount);
                        sender.sendMessage("§aДобавлено " + amount + " сердец игроку " + target.getName());
                        break;
                    case "set":
                        plugin.getHeartManager().setHearts(target, amount);
                        sender.sendMessage("§aУстановлено " + amount + " сердец игроку " + target.getName());
                        break;
                    case "remove":
                        plugin.getHeartManager().removeHearts(target, amount);
                        sender.sendMessage("§aУдалено " + amount + " сердец у игрока " + target.getName());
                        break;
                    default:
                        sender.sendMessage("§cНеизвестное действие: " + action);
                        break;
                }
                return true;

            default:
                sender.sendMessage("§cНеизвестная подкоманда. Используйте /heartbro help");
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6--- HeartBro Помощь ---");
        sender.sendMessage("§e/heartbro help §7- узнать все команды");
        sender.sendMessage("§e/heartbro heart {player} {add/set/remove} {amount} §7- изменить значение сердец");
        sender.sendMessage("§e/heartbro cleardatabase §7- удаление значение всех игроков из дб");
        sender.sendMessage("§e/heartbro version §7- узнать версию плагина");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("help", "heart", "cleardatabase", "version").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("heart")) {
            return null; // Returns online players
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("heart")) {
            return Arrays.asList("add", "set", "remove").stream()
                    .filter(s -> s.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
