package com.atguigu.gmall.item.test;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelExpressionParserTest {

    /**
     * 使用预设的表达式的值
     */
    @Test
    public void test03() {

        String str = "skuInfo:#{#params[2]}" ;
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;
        Expression expression = spelExpressionParser.parseExpression(str, ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext evaluationContext = new StandardEvaluationContext() ;     // 预设一些常用的变量以及值
        evaluationContext.setVariable("params" , new String[]{"哈哈" , "呵呵" , "嘿嘿"});
        evaluationContext.setVariable("name" , "尚硅谷");
        String value = expression.getValue(evaluationContext, String.class);
        System.out.println(value);
    }


    /**
     * SpelExpressionParser定义解析边界
     */
    @Test
    public void test02() {
        String str = "skuInfo:#{1 + 1}:#{'hello'.toUpperCase()}:#{T(java.util.UUID).randomUUID().toString().replace('-', '')}" ;
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;
        Expression expression = spelExpressionParser.parseExpression(str, ParserContext.TEMPLATE_EXPRESSION);
        String value = expression.getValue(String.class);
        System.out.println(value);
    }

    /**
     * SpelExpressionParser入门 Spring EL表达式的解析器
     */
    @Test
    public void test01() {

        // 定义一个表达式
        String str1 = "'hello'" ;               // 字符串的表达式
        String str2 = "1 + 1" ;                 // 表达式支持数学运算
        String str3 = "'hello'.toUpperCase()" ; // 表达式支持方法调用
        String str4 = "T(java.util.UUID).randomUUID().toString().replace('-', '')" ;                      // 支持静态方法调用

        // 创建一个解析器对象
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;

        // 解析表达式，得到一个表达式的对象
        Expression expression = spelExpressionParser.parseExpression(str4);

        // 获取表达式对应的实际值
        String value = expression.getValue(String.class);

        // 输出
        System.out.println(value);

    }

}
