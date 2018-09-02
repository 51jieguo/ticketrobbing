package cn.yangtengfei.trainticket.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TicketSearchResult<T> {

    T data;

    int httpstatus;

    String message;

    boolean status;

}
