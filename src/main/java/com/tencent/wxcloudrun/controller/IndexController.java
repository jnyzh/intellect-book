package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.tools.ImageListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * index控制器
 */
@Controller

public class IndexController {

  /**
   * 主页页面
   * @return API response html
   */
  @GetMapping
  public String index() {
    return "index";
  }

  @ResponseBody
  @RequestMapping(value = "/success")
  public String success(){return "success";}

  //上传小程序快照
  @ResponseBody
  @RequestMapping(value = "/uploadShot")
  public Map<String,Object> findIndex(@RequestParam(value="before") String before_path,
                                      @RequestParam(value="after") String after_path){
    ImageListener imageListener = new ImageListener(before_path, after_path);
    double similarity = imageListener.actionPerformed();

    Map<String,Object> result = new HashMap<>();
    result.put("result",true);
    result.put("similarity",similarity);
    return result;
  }
}
