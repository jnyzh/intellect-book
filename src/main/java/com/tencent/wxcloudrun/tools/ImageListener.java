package com.tencent.wxcloudrun.tools;

public class ImageListener {
    ImageUtil imageUtils;
    //图像1和图像2的二进制
    int[][] readImagePixArray1;
    int[][] readImagePixArray2;

    public ImageListener(String path1, String path2){
        imageUtils = new ImageUtil();
        readImagePixArray1 = imageUtils.readImagePix (path1);
        readImagePixArray2 = imageUtils.readImagePix (path2);
    }

    public double actionPerformed(){
        //开始匹配
        imageUtils.zhiwen (readImagePixArray1);
        imageUtils.b=true;
        imageUtils.zhiwen (readImagePixArray2);
        return imageUtils.xiangsidu();
    }
}
