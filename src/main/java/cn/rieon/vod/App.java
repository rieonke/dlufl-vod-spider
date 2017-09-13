package cn.rieon.vod;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * main class.
 * @author <a href="mailto:rieon@rieon.cn">Rieon Ke</a>
 * @version 1.0.0
 * @since 1.0.0
 */
public class App implements PageProcessor {

  private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
  private static final Parser parser = new Parser();

  @Override
  public void process(Page page) {
    final Page p = page;
    p.addTargetRequests(p.getHtml().links().regex("http://vod.dlufl.edu.cn/vod/.*").all());
    p.putField("link", p.getUrl().regex("vod").toString());
    if (p.getResultItems().get("link") == null) {
      p.setSkip(true);
    } else {
      new Thread() {
        @Override
        public void run() {
          parser.parse(p.getHtml().get(), p.getUrl().toString());
        }
      }.run();
    }
  }


  @Override
  public Site getSite() {
    return site;
  }

  /**
   * main entry.
   *
   * @param args launch args
   * @throws Exception exception
   */
  public static void main(String[] args) throws Exception {

    Spider.create(new App())
        .addUrl("http://vod.dlufl.edu.cn/vod/index.htm").thread(5)
        .run();
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
    try {
      //change to your own location
      writer.writeValue(new File("/Users/rieon/Desktop/vod.json"), Parser.items);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
