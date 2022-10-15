package net.azisaba.gracestopannouncer.velocity.config;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.azisaba.gracestopannouncer.velocity.GraceStopAnnouncerVelocity;

@Getter
@RequiredArgsConstructor
public class GraceStopAnnouncerConfig {

  private final GraceStopAnnouncerVelocity plugin;

  private static final String CONFIG_FILE_PATH = "./plugins/GraceStopAnnouncer/config.yml";

  private int port;
  private String baseMessage;

  private String hourMessage;
  private String minuteMessage;
  private String secondMessage;

  public void load() throws IOException {
    VelocityConfigLoader conf =
        VelocityConfigLoader.load(new File(CONFIG_FILE_PATH), "velocity-config.yml");
    conf.saveDefaultConfig();

    port = conf.getInt("port", 50000);

    baseMessage =
        conf.getString(
            "message.base",
            "&d【お知らせ】&eこのプロキシサーバーは &6%time%後 &eに再起動されます。 一度ログアウトして再接続することで、再起動による切断を回避することができます。");
    hourMessage = conf.getString("message.hour", "%h時間");
    minuteMessage = conf.getString("message.minute", "%m分");
    secondMessage = conf.getString("message.second", "%s秒");
  }
}
