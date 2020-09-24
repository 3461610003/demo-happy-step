package com.example.demo.happy.step.task;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * StepTask
 * </p>
 *
 * @author zhenghao
 * @date 2020/9/18 15:56
 */
@Configuration
@EnableScheduling
@Slf4j
public class StepTask {
    private static int reCount = 2;

    @Value("${step.login.account}")
    private String account;
    @Value("${step.login.password}")
    private String password;
    @Value("${token.file.name}")
    private String tokenFilePath;
    @Value("${step.login.minStep}")
    private Integer minStep;

    //    @Scheduled(cron = "0 0 18 * * ?")
    @Scheduled(cron = "0 0/30 8-20 * * ?")
//    @Scheduled(cron = "0 * 13 * * ?")
    public void task() throws InterruptedException {
        int nextInt = new Random().nextInt(10);
        log.info("【准备同步步数】等待{}分钟------------------------------------", nextInt);
        Thread.sleep(1000 * 60 * nextInt);
        int step = getStep();
        Map<String, String> tokenMap = readToken();
        if (tokenMap == null || tokenMap.size() < 2 || isBlank(tokenMap.get("accessToken")) || isBlank(tokenMap.get("userId"))) {
            reUpdate(step);
            return;
        }
        JSONObject updateJson = JSONObject.parseObject(update(tokenMap.get("accessToken"), tokenMap.get("userId"), step));
        if (isSuccess(updateJson)) {
            log.info("==============【同步步数成功】");
        } else {
            log.info("==============【同步步数失败】\n尝试重新登录并同步。。。。。");
            reUpdate(step);
        }
    }

    private Integer getStep() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY) - 7;
        int minute = cal.get(Calendar.MINUTE) / 30;
        int addStep = hour * 1000 + minute * 500;
        return new Random().nextInt(500) + addStep + (minStep == null ? 20000 : minStep);
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    private void reUpdate(int step) {
        JSONObject loginJson = JSONObject.parseObject(login());
        if (isSuccess(loginJson)) {
            JSONObject dataJson = loginJson.getJSONObject("data");
            String accessToken = dataJson.getString("accessToken");
            String userId = dataJson.getString("userId");
            log.info("==============【登录成功】====================");
            writeToken(accessToken, userId);
            log.info("【登录成功】写入：accessToken={}, userId={}", accessToken, userId);
            JSONObject updateJson = JSONObject.parseObject(update(accessToken, userId, step));
            if (isSuccess(updateJson)) {
                log.info("==============【同步步数成功】result={}", updateJson);
                return;
            } else {
                log.info("==============【同步步数失败】result={}", updateJson);
            }
        } else {
            log.info("==============【重新登录失败】---------");
        }
        if (--reCount > 0) {
            log.info("==============【同步步数失败】准备重新尝试.................");
            reUpdate(step);
        } else {
            reCount = 2;
        }
    }

    /**
     * 状态码是否为200
     *
     * @param jsonObject json结果
     * @return 是否
     */
    private boolean isSuccess(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.getInteger("code").equals(200);
    }

    /**
     * 更新步数
     *
     * @param accessToken token
     * @param userId      用户id
     * @param step        步数
     * @return 更新结果 如：{"msg":"成功","code":200,"data":{"pedometerRecordHourlyList":[{"distance":"0,0,0,0,0,0,0,17925.00,0,0,18723.00,19206.00,10673.00,11704.00,0,11737.00,0,0,0,0,0,0,0,0","created":"2020-09-18 07:44:23","measurementTime":"2020-09-18 00:00:00","active":0,"step":"0,0,0,0,0,0,0,29876,0,0,31205,32010,32020,35112,0,35212,0,0,0,0,0,0,0,0","id":"1822cd8ababb4174be5a89c68bfab39f","calories":"0,0,0,0,0,0,0,746.00,0,0,780.00,800.00,8005.00,8778.00,0,8803.00,0,0,0,0,0,0,0,0","userId":27231098,"deviceId":"M_NULL","dataSource":2,"updated":1600412486817}]}}
     */
    private String update(String accessToken, String userId, int step) {
        String updateUrl = "https://sports.lifesense.com/sport_service/sport/sport/uploadMobileStepV2?version=4.5&systemType=2";
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json; charset=utf-8");
        header.put("Cookie", "accessToken=" + accessToken);
        String params = "{\"list\":[{\"DataSource\":2,\"active\":1,\"calories\":\"" + step / 4 + "\"," +
                "\"dataSource\":2,\"deviceId\":\"M_NULL\",\"distance\":" + step / 3 + ",\"exerciseTime\":0,\"isUpload\":0," +
                "\"measurementTime\":\"" + sdf.format(now) + "\",\"priority\":0,\"step\":" + step + "," +
                "\"type\":2,\"updated\":" + now.getTime() + ",\"userId\":" + userId + "}]};";
        log.info("============== 【更新步数参数】 updateUrl={}, params={}, header={}", updateUrl, params, header);
        String result = doPost(updateUrl, params, header);
        log.info("============== 【更新步数结果】result={}", result);
        return result;
    }

    /**
     * 登录
     *
     * @return 结果
     */
    private String login() {
        String loginUrl = "https://sports.lifesense.com/sessions_service/login?systemType=2&version=4.6.7";
        String md5Pass = DigestUtils.md5DigestAsHex(password.getBytes());
        String params = "{\"appType\":6,\"clientId\":\"88888\",\"loginName\":\"" + account + "\",\"password\":\"" + md5Pass + "\",\"roleType\":0}";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json; charset=utf-8");
        log.info("==============【登录接口参数】loginUrl={}, params={}, header={}", loginUrl, params, header);
        String result = doPost(loginUrl, params, header);
        log.info("==============【登录接口结果】result={}", result);
        return result;
    }

    public void writeToken(String accessToken, String userId) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(new File(tokenFilePath));
            //写入中文字符时会出现乱码
            bw = new BufferedWriter(fw);
            //BufferedWriter  bw=new BufferedWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("temp.txt")), "UTF-8")));
            bw.write(accessToken + System.getProperty("line.separator"));
            bw.write(userId);
        } catch (Exception e) {
            log.info("==============【写入token失败】=========,{}", e.toString());
            e.printStackTrace();
        } finally {
            try {
                assert bw != null;
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> readToken() {
        FileReader fr = null;
        BufferedReader br = null;
        Map<String, String> resultMap = new HashMap<>();
        try {
            fr = new FileReader(tokenFilePath);
            br = new BufferedReader(fr);
            resultMap.put("accessToken", br.readLine());
            resultMap.put("userId", br.readLine());
        } catch (Exception e) {
            log.info("==============【读取token失败】=========,{}", e.toString());
            e.printStackTrace();
        } finally {
            closeResource(br);
            closeResource(fr);
        }
        return resultMap;
    }

    /**
     * post请求
     *
     * @param httpUrl httpUrl
     * @param param   param
     * @param header  header
     * @return 结果
     */
    private String doPost(String httpUrl, String param, Map<String, String> header) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);

            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置鉴权信息：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
//            connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            if (header != null && header.size() > 0) {
                for (String key : header.keySet()) {
                    connection.setRequestProperty(key, header.get(key));
                }
            }
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sbf = new StringBuilder();
                String temp = null;
                // 循环遍历一行一行读取数据
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            closeResource(br);
            closeResource(os);
            closeResource(is);
            // 断开与远程地址url的连接
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private void closeResource(Reader r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void closeResource(OutputStream r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void closeResource(InputStream r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
