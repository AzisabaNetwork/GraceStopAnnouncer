package net.azisaba.gracestopannouncer.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.logging.Logger;
import lombok.Getter;
import net.azisaba.gracestopannouncer.core.HoldConnectionHandler;
import net.azisaba.gracestopannouncer.core.PreStopHookReceiver;
import net.azisaba.gracestopannouncer.core.util.MessageFormatter;
import net.azisaba.gracestopannouncer.core.util.PodSelfInformation;
import net.azisaba.gracestopannouncer.velocity.config.GraceStopAnnouncerConfig;

@Plugin(
    id = "gracestopannouncer",
    name = "GraceStopAnnouncer",
    version = "1.0.1",
    url = "https://github.com/AzisabaNetwork/GraceStopAnnouncer",
    description = "Announce server shutdown to players.",
    authors = {"Azisaba Network"})
@Getter
public class GraceStopAnnouncerVelocity {

  @Getter private KubernetesClient client;

  private final ProxyServer proxy;
  private final Logger logger;

  private static final String POD_NAMESPACE_ENV = "POD_NAMESPACE";
  private static final String POD_NAME_ENV = "POD_NAME";

  private GraceStopAnnouncerConfig graceStopAnnouncerConfig;
  private PodSelfInformation podSelfInformation;
  private PreStopHookReceiver receiver;

  @Inject
  public GraceStopAnnouncerVelocity(ProxyServer server, Logger logger) {
    this.proxy = server;
    this.logger = logger;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    client = new DefaultKubernetesClient();

    String namespace = System.getenv(POD_NAMESPACE_ENV);
    String podName = System.getenv(POD_NAME_ENV);

    boolean workable = true;
    if (namespace == null) {
      logger.warning("Environment variable \"" + POD_NAMESPACE_ENV + "\" is not set.");
      workable = false;
    }
    if (podName == null) {
      logger.warning("Environment variable \"" + POD_NAME_ENV + "\" is not set.");
      workable = false;
    }

    if (!workable) {
      logger.warning("Shutdown announcing feature will not work.");
    } else {
      podSelfInformation = new PodSelfInformation(namespace, podName);
    }

    graceStopAnnouncerConfig = new GraceStopAnnouncerConfig(this);
    try {
      graceStopAnnouncerConfig.load();
    } catch (Exception e) {
      logger.severe("Failed to load config file.");
      e.printStackTrace();
      return;
    }

    MessageFormatter messageFormatter =
        new MessageFormatter(
            graceStopAnnouncerConfig.getBaseMessage(),
            getGraceStopAnnouncerConfig().getHourMessage(),
            getGraceStopAnnouncerConfig().getMinuteMessage(),
            getGraceStopAnnouncerConfig().getSecondMessage());

    receiver =
        new PreStopHookReceiver(
            getGraceStopAnnouncerConfig().getPort(),
            new HoldConnectionHandler(
                client, podSelfInformation, new VelocityServerAdapter(this, messageFormatter)));
    receiver.start();

    logger.info("GraceStopAnnouncer has been enabled.");
  }

  @Subscribe
  public void onProxyShutdown(ProxyShutdownEvent event) {
    logger.info("GraceStopAnnouncer is disabled.");
  }
}
