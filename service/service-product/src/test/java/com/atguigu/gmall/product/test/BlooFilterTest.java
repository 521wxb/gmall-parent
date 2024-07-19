package com.atguigu.gmall.product.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BlooFilterTest {

    public static void main(String[] args) {

        /**
         * Funnel用来指定布隆过滤器中所存储的数据的类型
         * expectedInsertions： 用来指定布隆过滤器中可以存储的数据的个数
         * fpp：指定误判率
         */
        // 创建一个布隆过滤器
        BloomFilter<Long> bloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000000, 0.000001);

        // 往bloomFilter中添加数据
        bloomFilter.put(48L) ;
        bloomFilter.put(49L) ;
        bloomFilter.put(50L) ;

        // 判断数据是否存在
        boolean b1 = bloomFilter.mightContain(48L) ;
        boolean b2 = bloomFilter.mightContain(49L) ;
        boolean b3 = bloomFilter.mightContain(50L) ;
        boolean b4 = bloomFilter.mightContain(51L) ;

        // 输出
        System.out.println(b1);
        System.out.println(b2);
        System.out.println(b3);
        System.out.println(b4);

    }

}
