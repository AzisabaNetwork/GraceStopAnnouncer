package net.azisaba.gracestopannouncer.velocity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.azisaba.gracestopannouncer.core.ServerAdapter;
import net.azisaba.gracestopannouncer.core.util.MessageFormatter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@RequiredArgsConstructor
public class VelocityServerAdapter implements ServerAdapter {

  private final GraceStopAnnouncerVelocity plugin;
  private final MessageFormatter messageFormatter;

  @Override
  public void executeAsyncAfter(Runnable runnable, long delay) {
    plugin
        .getProxy()
        .getScheduler()
        .buildTask(plugin, runnable)
        .delay(delay, TimeUnit.SECONDS)
        .schedule();
  }

  @Override
  public void notifyShutdown(int remainingTime) {
    if (remainingTime <= 0) {
      return;
    }

    plugin.getLogger().info("This proxy server will shutdown in " + remainingTime + " seconds.");

    List<Integer> notifySeconds =
        new ArrayList<>(
            Arrays.asList(1, 2, 3, 4, 5, 10, 30, 60, 120, 180, 300, 600, 900, 1800, 2700, 3600));
    notifySeconds.removeIf(i -> i > remainingTime);

    for (int sec : notifySeconds) {
      executeAsyncAfter(
          () -> {
            String formattedMessage = messageFormatter.format(sec);
            TextComponent component =
                LegacyComponentSerializer.legacyAmpersand().deserialize(formattedMessage);
            plugin.getProxy().getAllPlayers().forEach(p -> p.sendMessage(component));
            plugin.getLogger().info(component.toString());
          },
          remainingTime - sec);
    }
  }
}
