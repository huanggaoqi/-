package com.qhit.tests;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by tp on 2019/3/6.
 */
@RestController
@RequestMapping("/Mzfcontroller")
public class Mzfcontroller {


   /* @RequestMapping("/sss")
    public Object ss(HttpSession session){
        List<Object> baseUser = (List<Object>) session.getAttribute("sessionAttr:baseUser");
        System.out.println(baseUser);
        return baseUser;
    }*/

    /**
     * 接收参数 创建订单
     */
    @RequestMapping("/found")
    //表单提交的价格
    //支付类型  1：支付宝

    public  void   demo1(HttpServletRequest request,HttpServletResponse response,String price,String pay_id) throws IOException {
        //支付人的唯一标识用户ID
        String type=request.getParameter("type");

        String token = "xlVRRbsh6nTcXStfIVSX7tjQMSvxltyZ"; //记得更改 http://codepay.fateqq.com 后台可设置
        String codepay_id ="184533" ;//记得更改 http://codepay.fateqq.com 后台可获得
        String notify_url="http://172.25.20.133:9100/Mzfcontroller/execute";//通知地址
        String return_url="http://172.25.20.133:9100/Mzfcontroller/execute";//支付后同步跳转地址

        //参数有中文则需要URL编码
        String url="http://codepay.fateqq.com:52888/creat_order?id="+codepay_id+"&pay_id="+pay_id+"&price="+price+"&type="+type+"&token="+token+"&notify_url="+notify_url+"&return_url="+return_url;

            response.sendRedirect(url);

    }
    @RequestMapping("/execute")
    public  void  demo2(HttpServletRequest request,HttpServletResponse response) throws NoSuchAlgorithmException, IOException {
        /**
         *验证通知 处理自己的业务
         */
        String key = "ALRYBB9aU24ZQpZKmOYUncZepC2uTr01"; //记得更改 http://codepay.fateqq.com 后台可设置
        Map<String,String> params = new HashMap<String,String>(); //申明hashMap变量储存接收到的参数名用于排序
        Map requestParams = request.getParameterMap(); //获取请求的全部参数
        String valueStr = ""; //申明字符变量 保存接收到的变量
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            valueStr = values[0];
            //乱码解决，这段代码在出现乱码时使用。如果sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);//增加到params保存
        }
        List<String> keys = new ArrayList<String>(params.keySet()); //转为数组
        Collections.sort(keys); //重新排序
        String prestr = "";
        String sign= params.get("sign"); //获取接收到的sign 参数

        for (int i = 0; i < keys.size(); i++) { //遍历拼接url 拼接成a=1&b=2 进行MD5签名
            String key_name = keys.get(i);
            String value = params.get(key_name);
            if(value== null || value.equals("") ||key_name.equals("sign")){ //跳过这些 不签名
                continue;
            }
            if (prestr.equals("")){
                prestr =  key_name + "=" + value;
            }else{
                prestr =  prestr +"&" + key_name + "=" + value;
            }
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((prestr+key).getBytes());
        String  mySign = new BigInteger(1, md.digest()).toString(16).toLowerCase();
        if(mySign.length()!=32)mySign="0"+mySign;
        if(mySign.equals(sign)){
//            编码要匹配 编码不一致中文会导致加密结果不一致
//            参数合法处理业务
            String pay_no = request.getParameter("pay_no");//流水号
            String pay_id = request.getParameter("pay_id");//用户唯一标识
            String money = request.getParameter("money");//付款金额
            String price = request.getParameter("price");//提交的金额
            System.out.print("ok");
            response.sendRedirect("http://172.25.20.133:8080/success.html");
        }else{
            //参数不合法
            System.out.print("fail");
            response.sendRedirect("http://172.25.20.133:8080/errenr.html");
        }
    }

}
