package com.tencent.wxcloudrun.tools;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageUtil {
    //将图片长宽分别八等分，即整张图片64份
    int[] average1 = new int[65];
    int[] average2 = new int[65];
    boolean b = false;

    //获得图片像素点的sRGB值
    public int[][] readImagePix(String path){
        // 文件对象
        File file = new File (path);
        BufferedImage readimg = null;
        try {
            readimg = ImageIO.read (file);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        int width = readimg.getWidth ();
        int height = readimg.getHeight ();
        int[][] imgArray = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                //BufferedImage.getRGB()返回的值是负整数，eg:-12834262
                imgArray[i][j] = readimg.getRGB (i, j);
            }
        }
        return imgArray;
    }

    //三原色与灰度,原色：红（R）绿（G）蓝（B
    //灰度就是没有色彩，RGB色彩分量全部相等
    public int getRgbGray(int numPixels){
        // byte -128 127
        //>>按位右移 &按位与
        int red = (numPixels>>16)&0xFF;
        int green = (numPixels>>8)&255;
        int blue = (numPixels>>0)&255;
        // 灰度 -- 减少计算量 以及 更方便计算
        int gray = (red + green + blue) / 3;
        return gray;
    }

    //将像素点的sRGB值依次转为灰度值，比较形成指纹
    public void zhiwen(int[][] imgArray){
        int width = 8;
        int height = 8;
        //a1：八等分后宽 b1:八等分后高
        int a1 = imgArray.length/width;
        int b1 = imgArray[0].length/height;
        //没有被完全整除的部分的宽高
        int a2 = imgArray.length - a1 * (width-1);
        int b2 = imgArray[0].length - b1 * (height-1);
        int[][] average = new int[width][height];

        int sum0 = 0;
        //64等分块
        for(int m = 0; m < width; m++){
            for(int n = 0; n < height; n++){
                int sum = 0;
                //既不是最后一行也不是最后一列
                if(m != (width-1) && n != (height-1)) {
                    for(int i = 0 + m*a1; i < a1 + m*a1; i++){
                        for(int j = 0 + n*b1; j < b1 + n*b1; j++){
                            int num1 = imgArray[i][j];
                            int gray1 = getRgbGray (num1);
                            sum += gray1;
                        }
                    }
                    //当前块的平均灰度
                    average[m][n] = sum/(a1 * b1);

                }
                //最后一列,但不是最后一行
                if(m == (width-1) && n != (height-1)) {
                    for(int i = 0 + m*a1; i < a2 + m*a1; i++){
                        for(int j = 0 + n*b1; j < b1 + n*b1; j++){
                            int num1 = imgArray[i][j];
                            int gray1 = getRgbGray (num1);
                            sum += gray1;
                        }
                    }
                    average[m][n] = sum/(a2 * b1);

                }
                //最后一行，但不是最后一列
                if(m != (width-1) && n == (height-1)) {
                    for(int i = 0 + m*a1; i < a1 + m*a1; i++){
                        for(int j = 0 + n*b1; j < b2 + n*b1; j++){
                            int num1 = imgArray[i][j];
                            int gray1 = getRgbGray (num1);
                            sum += gray1;
                        }
                    }
                    average[m][n] = sum/(a1 * b2);

                }
                //最后一个块，执行
                if(m == (width-1) && n == (height-1)) {
                    //行开始值m*a1，偏置a2
                    for(int i = 0 + m*a1; i < a2 + m*a1; i++){
                        //列开始值n*b1，偏置b2
                        for(int j = 0 + n*b1; j < b2 + n*b1; j++){
                            //获取当前像素点的SRGB
                            int num1 = imgArray[i][j];
                            //转换成灰度值
                            int gray1 = getRgbGray (num1);
                            sum += gray1;
                        }
                    }
                    average[m][n] = sum/(a2 * b2);

                }
                //整个图片的灰度值为64块灰度平均之和
                sum0 += average[m][n];
            }
        }
        //每块的灰度平均值
        int average0 = sum0/(width*height);
        //若当前块灰度值大于average0则1，否则0
        for(int n = 0; n < height; n++){
            for(int m = 0; m < width; m++){
                if(average[m][n] > average0)average[m][n] = 1;
                else average[m][n] = 0;

                //用作对比的一维数组
                if(b) {
                    average1[m+width*n] = average[m][n];
                }else {
                    average2[m+width*n] = average[m][n];
                }

                System.out.print(average[m][n]);

            }
        }
        System.out.println(" ");
    }

    public double xiangsidu(){
        double a=0;
        for(int i=0;i<64;i++) {
            //100/64==1.5625
            if(average1[i] == average2[i])a=a+1.5625;
        }

        System.out.println(" ");
        System.out.print("两张图像的相似度为："+a);
        return a;
    }
}
