package com.atguigu.gmall.search;

import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SearchApplication.class)
public class SearchTest {

    @Autowired
    private GoodsBizService goodsBizService ;

    @Test
    public void searchTest() {

        SearchParamDTO searchParamDTO = new SearchParamDTO() ;

        searchParamDTO.setCategory1Id(2L);
//        searchParamDTO.setCategory2Id(0L);
//        searchParamDTO.setCategory3Id(0L);
        searchParamDTO.setKeyword("手机");
        searchParamDTO.setTrademark("1:小米");
        searchParamDTO.setProps(new String[]{"23:8G:运行内存"});
        searchParamDTO.setOrder("2:desc");
        searchParamDTO.setPageNo(1);
        searchParamDTO.setPageSize(10);

        SearchResponseVo searchResponseVo = goodsBizService.search(searchParamDTO);
        System.out.println(searchResponseVo);

    }

}
