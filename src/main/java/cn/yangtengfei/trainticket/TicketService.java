package cn.yangtengfei.trainticket;

import cn.yangtengfei.http.HttpUtil;
import cn.yangtengfei.trainticket.bean.TicketData;
import cn.yangtengfei.trainticket.bean.TicketSearchResult;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;

import java.util.List;
import java.util.Map;

public class TicketService {


    //https://kyfw.12306.cn/otn/leftTicket/queryA?
    // leftTicketDTO.train_date=2018-09-02
    // &leftTicketDTO.from_station=BJP
    // &leftTicketDTO.to_station=XUN&purpose_codes=ADULT
    public static String showTicket(String date,String from,String aim){

        StringBuffer stringBuffer = new StringBuffer("https://kyfw.12306.cn/otn/leftTicket/queryA");
        stringBuffer.append("?");
        stringBuffer.append("leftTicketDTO.train_date="+date);
        stringBuffer.append("&");
        stringBuffer.append("leftTicketDTO.from_station="+from);
        stringBuffer.append("&");
        stringBuffer.append("leftTicketDTO.to_station="+aim);
        stringBuffer.append("&");
        stringBuffer.append("purpose_codes=ADULT");

        System.out.println(stringBuffer.toString());

        String url="https://kyfw.12306.cn/otn/leftTicket/queryA?leftTicketDTO.train_date=2018-09-08&leftTicketDTO.from_station=BJP&leftTicketDTO.to_station=XUN&purpose_codes=ADULT";
        String result = HttpUtil.doGet(url,null);
        //System.out.println(result);
        return result;
    }

    public static void main(String[] args) {
        String result = showTicket("2018-09-28","BJP","XUN");
        //String jsonStr = result.substring(result.indexOf("{"),result.length());
        //System.out.println(jsonStr);
        Map mapResult = JSON.parseObject(result);
        System.out.println(mapResult);
        TicketSearchResult<TicketData> ticketDataTicketSearchResult = JSON.parseObject(result,TicketSearchResult.class);
        /*List<String> ticketList = ticketDataTicketSearchResult.getData().getResult();
        for(String ticketStr:ticketList){
            System.out.println(ticketStr);.
        }*/
        TicketData ticketData = JSON.parseObject(JSON.toJSONString(ticketDataTicketSearchResult.getData()),TicketData.class);

        //TicketData ticketData =(TicketData)ticketDataTicketSearchResult.getData();

        //System.out.println(ticketData.getResult());

        for(String ticket:ticketData.getResult()){
            System.out.println(ticket);
        }
    }
}
