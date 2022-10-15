package net.azisaba.gracestopannouncer.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.azisaba.gracestopannouncer.core.ServerAdapter;
import net.azisaba.gracestopannouncer.core.util.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class BukkitServerAdapter implements ServerAdapter {

  private final JavaPlugin plugin;
  private final MessageFormatter messageFormatter;

  @Override
  public void executeAsyncAfter(Runnable runnable, long delay) {
    long executeOn = System.currentTimeMillis() + (delay * 1000L);
    new BukkitRunnable() {
      @Override
      public void run() {
        if (System.currentTimeMillis() >= executeOn) {
          runnable.run();
          cancel();
        }
      }
    }.runTaskTimerAsynchronously(plugin, 0L, 20L);
  }

  @Override
  public void notifyShutdown(int remainingTime) {
    if (remainingTime <= 0) {
      return;
    }

    plugin.getLogger().info("This server will shutdown in " + remainingTime + " seconds.");

    List<Integer> notifySeconds =
        new ArrayList<>(
            Arrays.asList(1, 2, 3, 4, 5, 10, 30, 60, 120, 180, 300, 600, 900, 1800, 2700, 3600));
    notifySeconds.removeIf(i -> i > remainingTime);
    new BukkitRunnable() {

      private final long shutdownMillisecond = System.currentTimeMillis() + (remainingTime * 1000L);

      @Override
      public void run() {
        if (System.currentTimeMillis() - shutdownMillisecond - 1000L > 0) {
          cancel();
          return;
        }

        int remainingSecond = (int) ((shutdownMillisecond - System.currentTimeMillis()) / 1000L);

        if (!notifySeconds.contains(remainingSecond)) {
          return;
        }

        notifySeconds.remove((Integer) remainingSecond);
        String uncoloredMessage = messageFormatter.format(remainingSecond);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', uncoloredMessage));
      }
    }.runTaskTimerAsynchronously(plugin, 0, 5);
  }
}
