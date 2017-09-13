package cn.rieon.vod;

import java.util.List;
import lombok.Data;

/**
 * VideoItem Model.
 * @author <a href="mailto:rieon@rieon.cn">Rieon Ke</a>
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class VideoItem {

  String name;
  String otherNames;
  String actors;
  String languages;
  String subtitles;
  String region;
  String length;
  String date;
  String featureImageUrl;
  String publishedAt;
  String content;
  String originalUrl;
  List<Video> videos;
  List<String> tags;

  @Data
  static class Video {

    String name;
    String server;
    String url;
    String button;

  }

}
