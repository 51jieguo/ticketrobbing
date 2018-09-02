package cn.yangtengfei.trainticket.bean;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TicketData {

    int flg;

    Map<String,String> map;

    List<String> result;
}
