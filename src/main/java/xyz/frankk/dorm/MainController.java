package xyz.frankk.dorm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MainController {

    @Autowired
    HttpUtils httpUtils;

    @Scheduled(fixedRate = 3600000)
    public String ListDorm() throws IOException {
        List<String> dormList = new ArrayList<>();

        for (int i=1; i<=10; i++){
            Document doc = Jsoup.connect("https://www.renthub.in.th/search/condo_filter?condo_monthly_price[price_range_1]=1&locale=th&page="+String.valueOf(i)+"&search_price=monthly&temp[zone_id]=3006&pagination=true").header("Accept","text/javascript").get();
            String tmp = doc.outerHtml();
            tmp = tmp.replace("\\&quot;","");
            tmp = tmp.replace("\\n","");
            tmp = tmp.replace("\\t","");
            tmp = tmp.replace("\n","");
            tmp = tmp.replace("\t","");
            doc = Jsoup.parse(tmp);
            doc.getElementsByTag("li").forEach(a->{
                a.getElementsByClass("condo_listing");
                dormList.add(a.toString());
            });
        }


        Pattern info = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");
        Pattern price = Pattern.compile("<span class=\"price\">(.*)</span>");

        Map<DormBean,Integer> map = new HashMap<>();
        for(String data:dormList){
            Matcher matcherInfo = info.matcher(data);
            Matcher matcherPrice = price.matcher(data);
            if(matcherInfo.find()&&matcherPrice.find()){
                Integer priceInt = Integer.parseInt(matcherPrice.group(1).replace(",",""));
                String infoStr = "https://www.renthub.in.th"+matcherInfo.group(2);
                map.put(new DormBean(priceInt,infoStr),priceInt);
            }
        }

        Map<DormBean,Integer> result = MapUtil.sortByValue(map);
        String res = "";
        int i = 0;
        httpUtils.post("https://notify-api.line.me/api/notify","message=" + URLEncoder.encode("เริ่ม >>> " + new Date() , "UTF-8"));
        for (DormBean beanTmp : result.keySet()){
            i++;
            if(i==21)break;
            httpUtils.post("https://notify-api.line.me/api/notify","message=" + String.valueOf(beanTmp.getPrice())+"  "+ URLEncoder.encode(beanTmp.getInfo(), "UTF-8"));
        }





    return res;
    }

}
