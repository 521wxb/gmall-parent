package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2021000122609658";

	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKBT3jUwcV7Q7jSIaAROj9C6ebTKjQqLiBuqh7yZq9XPVtsIyMcY92Kbe5NZIzKUaWgkO0Aovyf4QMvdg2llkXjXJn/s+yASOyiP7eqDnOjKpErb7T2ip0KC9YerrZ1GLtYFoipJzGn5IN3F1I1EYbnxNBaAo4YnFj19bV6teDIiPZ8KxuOz19z9KBx1q0Kkd/4aHxIYcXob8JPEkR/f8/q1x1JFbU7/NvNSHVXkjBO6sfwybqaVTjBIKJNm/LDqjIdPvJcB+2r4LEcoR6zsA8Y3nFfQTvlumax9DVIGl/q1JDQ0Gg4J+e4VHn9V0WekqTnihrYqB0aPl4GckLOfQfAgMBAAECggEAUKNzXyeZUjQoqJ4tBzA7t+xhRpjpYM/SzOjTm09UuyJeunGWDFAk83K4RtuHyaBp/3GoQLnkLhBWhGM+udXpw5r+psf3bRbBVoQ39Lrw2z7QFEQ24+vpNehskRNTrAUsr2fGAcuPVXTRjoubbPuo4mZ0h5hTsFHD0gO/H0iQFaq2iLAJ3bj8yyFMXNI1x0A7tUsTOOm7l3EyU5OToKfuGncQ1VeHTF5RNtauBTaa/6C45kCVYWXXxgmn7IjJN3rJaCYEsNttt8cnOFllZx6CThJXP2smG6+nq7YdsgMyNxP4/b23y7Wni+L+HJRhFy0OhUT8Kr8/7K6TUNhSUEx0kQKBgQDI7kTocOIfbmZketcxZfsCZNUs66tjNGakkd42hvwIRvRHEMxIZBA1/rVjq2/AONa+7cDd0l24PEaknhcVhOvrwMpfhG7iwHOCmiOt0qbJLfdUoiyPP72uEA9t+pfREF+wvx5Kl8FMtuoB7PMIXXYO/HFFbYHZu1i3jmzx1xzq8wKBgQCv2Q3GpvYtXrR8+c2olIvPArGUfCDdqlDfu53w0XPRs7/0IkyNcgbJuGK4e0fXUSGQ8NbKQC3IMK/yfwIOmiLklyJ0bfmIP928v7EYTuM9G0FA31EeOxbJWjIt0iTC40sKx5YQvWAuZ5TUkCqbyFbl9NVmW0GOQUN/YtaGkYXFJQKBgQC6nOisxOKOdJdubNRd0aw9ve5rCPolae6Gm98430dGXLfVJNnFxgF0VHqc9gUVDvULYH9GxXE7gefOTfF8j/YY//Y0nifqOvlM6+GFN+alZCaYmQIvjPCar9nUt++2QLHO0sHMX4Msv5k4bpwNQdg69OQyqhD2alRsLkAqoDIAVwKBgBG4QZcfA/eWb4GYeozeSyhVESfxSi6MFxI0ibnWoWEkR9eGDUjDkMOK/QeyBI9B8av1WO+QgAyIO3KoMcDe710xjZtF6bs9FE8M0f+tE2D4+h5bYoU1Vxp7KEtNvb6VwN4ZNpoBO0vxzCGPVXvVOEx723UxBO4czXxsrUlqKJyxAoGANBMquDq96RA2Br+rrmKyLYhqFM83DX68gom4u/1P8KHsia/ghC02riFXvmnXS2zremsRuq8WCRg8jmccBaqA6mKJdEcsmC32RE12kh5oHe9vsW4zNeqHJb/LeLrhnb9txBTCBNc/eFzFzGZ8Nkp4rGLdczOBH2hScu/iJr/0x08=";

	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk41ooyX7utKM9B7jNcc4EzmyVU0Qfs18KFVFwyhl7YQMw/PB2YVQreVSvvb1rS/2YVxcjLK/9ehD//79b8aoxhFlEGqA7fGu0C2UR6pl+PhmRLcHeyN+DOG87Fhqb1t4JXmXQc1LXUfelJoh+r5XnMPWDAlY5JJtH3GZIU+AoBt9PuEtfhh03LL6WtnJMwOnqH94T8qHymLDftEGOWme1iAlenB692cUId20BmLJal621EAN+xpmkeJZEpx1wQ2fGhyTo7pm4v8LVLuqzOXkraffITvfbPl5IU0kjjs/QECwItAI0IBbNsDutezw/a0JobijjoF28uo4gtwmncBoQwIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://www.baidu.com";

	// 签名方式
	public static String sign_type = "RSA2";

	// 字符编码格式
	public static String charset = "utf-8";

	// 支付宝网关 线上环境的地址：https://openapi.alipay.com/gateway.do
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

	// 支付宝网关
	public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

