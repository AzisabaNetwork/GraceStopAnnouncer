package net.azisaba.gracestopannouncer.bukkit;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import net.azisaba.gracestopannouncer.core.HoldConnectionHandler;
import net.azisaba.gracestopannouncer.core.PreStopHookReceiver;
import net.azisaba.gracestopannouncer.core.util.MessageFormatter;
import net.azisaba.gracestopannouncer.core.util.PodSelfInformation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GraceStopAnnouncerBukkit extends JavaPlugin {

  @Getter private KubernetesClient client;

  private static final String POD_NAMESPACE_ENV = "POD_NAMESPACE";
  private static final String POD_NAME_ENV = "POD_NAME";

  @Getter private PodSelfInformation podSelfInformation;

  private PreStopHookReceiver receiver;

  @Override
  public void onEnable() {
    client = new DefaultKubernetesClient();

    String namespace = System.getenv(POD_NAMESPACE_ENV);
    String podName = System.getenv(POD_NAME_ENV);

    boolean workable = true;
    if (namespace == null) {
      getLogger().warning("Environment variable \"" + POD_NAMESPACE_ENV + "\" is not set.");
      workable = false;
    }
    if (podName == null) {
      getLogger().warning("Environment variable \"" + POD_NAME_ENV + "\" is not set.");
      workable = false;
    }

    if (!workable) {
      getLogger().warning("Shutdown announcing feature will not work.");
    } else {
      podSelfInformation = new PodSelfInformation(namespace, podName);
    }

    saveDefaultConfig();
    int port = getConfig().getInt("port");
    String baseMessage = getConfig().getString("message.base", "&cこのサーバーは &e%time%後 &cに停止します！");
    String hourStr = getConfig().getString("message.hour", "%h時間");
    String minuteStr = getConfig().getString("message.minute", "%m分");
    String secondStr = getConfig().getString("message.second", "%s秒");

    MessageFormatter messageFormatter =
        new MessageFormatter(baseMessage, hourStr, minuteStr, secondStr);

    receiver =
        new PreStopHookReceiver(
            port,
            new HoldConnectionHandler(
                client, podSelfInformation, new BukkitServerAdapter(this, messageFormatter)));
    receiver.start();

    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().info(getName() + " disabled.");
  }
}
