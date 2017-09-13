package cn.rieon.vod;

import cn.rieon.vod.VideoItem.Video;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VOD html page parser.
 *
 * @author <a href="mailto:rieon@rieon.cn">Rieon Ke</a>
 * @version 1.0.0
 * @since 1.0.0
 */
public class Parser {

  public static final List<VideoItem> items = new ArrayList<>();

  private static final String tagPtn =
      "<td width=\"922\" height=\"25\" align=\"right\" class=\"search\">&nbsp;&nbsp;您现在的位置是： "
          + "(.*)</td>";
  private static final Pattern tagPattern = Pattern.compile(tagPtn);

  private static final String metaPtn =
      "<td width=\"202\"><img src=\"(.*?)\" width=\"158\" height=\"198\"></td>.*?.<td width=\"630\""
          + " align=\"left\" class=\"flimnamen\">(.*?)</td>";
  private static final Pattern metaPattern = Pattern.compile(metaPtn, Pattern.DOTALL);

  private static final String contentPtn =
      "<div id=\"moiveContent\" style=\"width: 906px; height: 600px; overflow: scroll; overflow-x:"
          + " hidden; overflow-y: scroll; \">(.*)</div></td>";
  private static final Pattern contentPattern = Pattern.compile(contentPtn, Pattern.DOTALL);

  private static final String linkPtn =
      "\\('(.*)','(.*)','(http.*)'\\);.*.<font\\ size=\"1\">(.*)</font></td>";
  private static final Pattern linkPattern = Pattern.compile(linkPtn, Pattern.DOTALL);

  /**
   * parse html result.
   * @param result html result
   * @param originalUrl page original url
   */
  public void parse(String result, String originalUrl) {
    String[] results = result.split("javascript:openMovie");

    if (results.length <= 1) {
      return;
    }

    List<String> tags = new ArrayList<>();
    List<Video> videos = new ArrayList<>();
    VideoItem item = new VideoItem();

    item.setOriginalUrl(originalUrl);

    for (int i = 0; i < results.length; i++) {

      if (i == 0) {

        Matcher tagMatcher = tagPattern.matcher(results[i]);
        if (tagMatcher.find()) {
          final String tag = tagMatcher.group(1);
          String divider = "-&gt;";
          String[] ts = tag.split(divider);
          for (String t : ts) {
            if (!t.equals("首页")) {
              tags.add(t);
            }
          }

          //meta data
          Matcher metaMatcher = metaPattern.matcher(results[i]);
          if (metaMatcher.find()) {
            item.setFeatureImageUrl(metaMatcher.group(1));
            String metadata = metaMatcher.group(2);
            String[] metas = metadata.split("<br>");
            item.setName(metas[0]);
            item.setOtherNames(metas[1]);
            item.setActors(metas[2]);
            item.setLanguages(metas[3]);
            item.setSubtitles(metas[4]);
            item.setRegion(metas[5]);
            item.setLength(metas[7]);
            item.setDate(metas[8]);
            item.setPublishedAt(metas[9]);
          }
        }
      } else {
        Matcher m = linkPattern.matcher(results[i]);
        if (m.find()) {
          Video video = new Video();
          video.setName(m.group(1));
          video.setServer(m.group(3));
          video.setUrl(m.group(2));
          video.setButton(m.group(4));

          videos.add(video);
          if (item.getName() == null) {
            item.setName(m.group(1));
          }
        }
      }
      if (i == results.length - 1) {
        Matcher contentMatcher = contentPattern.matcher(results[i]);
        if (contentMatcher.find()) {
          item.setContent(contentMatcher.group(1).trim());
        }
        item.setVideos(videos);
        item.setTags(tags);
      }
    }
    items.add(item);
    System.out.println("Current Progress: " + items.size());
  }

}
