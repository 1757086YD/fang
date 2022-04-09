package com.tencent.wxcloudrun.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * counter控制器
 */
@RestController

public class CounterController {

  final CounterService counterService;
  final Logger logger;

  public CounterController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.logger = LoggerFactory.getLogger(CounterController.class);
  }


  /**
   * 获取当前计数
   * @return API response json
   */
  @GetMapping(value = "/api/count")
  ApiResponse get() {
    logger.info("/api/count get request");
    logger.error("/api/count ", "33333");
    Optional<Counter> counter = counterService.getCounter(1);
    Integer count = 0;
    if (counter.isPresent()) {
      count = counter.get().getCount();
    }

    return ApiResponse.ok(count);
  }


  /**
   * 更新计数，自增或者清零
   * @param request {@link CounterRequest}
   * @return API response json
   */
  @PostMapping(value = "/api/count")
  ApiResponse create(@RequestBody CounterRequest request,HttpServletRequest request1, HttpServletResponse response) {
	  handleEvent(request1, response);
	  logger.info("/api/count post request, action: {}", request.getAction());
    logger.error("/api/count ", "222222");
    Optional<Counter> curCounter = counterService.getCounter(1);
    if (request.getAction().equals("inc")) {
      Integer count = 1;
      if (curCounter.isPresent()) {
        count += curCounter.get().getCount();
      }
      Counter counter = new Counter();
      counter.setId(1);
      counter.setCount(count);
      counterService.upsertCount(counter);
      return ApiResponse.ok(count);
    } else if (request.getAction().equals("clear")) {
      if (!curCounter.isPresent()) {
        return ApiResponse.ok(0);
      }
      counterService.clearCount(1);
      return ApiResponse.ok(0);
    } else {
      return ApiResponse.error("参数action错误");
    }
  }

  
  public void handleEvent(HttpServletRequest request, HttpServletResponse response) {
      InputStream inputStream = null;
      try {
          inputStream = request.getInputStream();
          
          int length = 0;
          byte buffer[] = new byte[2048];
          StringBuilder sb = new StringBuilder();
          if((length = inputStream.read(buffer)) !=-1){
              sb.append(new String(buffer,0,length));
          }
          Gson gson = new Gson();
          //将存放入数组的数据转为map格式
          HashMap map = gson.fromJson(sb.toString(), HashMap.class);
          System.err.println("map    "+map);
          
          //Map<String, Object> map = XmlUtil.parseXML(inputStream);
          // openId
          String userOpenId = (String) map.get("FromUserName");
          // 微信账号
          String userName = (String) map.get("ToUserName");
          // 事件
          String event = (String) map.get("Event");
          // 区分消息类型
          String msgType = (String) map.get("MsgType");
          // 普通消息
          if ("text".equals(msgType)) {
             System.out.println("userOpenId:" + userOpenId);
          }
          else if ("event".equals(msgType)) {
              if ("subscribe".equals(event)) {
                 
              } else if ("SCAN".equals(event)) {
                 
              } else if ("unsubscribe".equals(event)) {
                
              }
          }
          logger.info("接收参数:{}", map);
      } catch (IOException e) {
          logger.error("处理微信公众号请求异常：", e);
      } finally {
          if (inputStream != null) {
              try {
                  inputStream.close();
              } catch (IOException ioe) {
                  logger.error("关闭inputStream异常：", ioe);
              }
          }
      }
  }
}
