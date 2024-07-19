package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.GoodsFeignClient;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class SearchController {

    @Autowired
    private GoodsFeignClient goodsFeignClient ;

    @GetMapping(value = "/list.html")
    public String search(SearchParamDTO searchParamDTO , Model model) {
        log.info("SearchController....search方法执行了....");

        // TODO 远程调用搜索微服务，进行数据的搜索，搜索完毕以后，把数据存储到Model
        Result<SearchResponseVo> responseVoResult = goodsFeignClient.search(searchParamDTO);
        SearchResponseVo searchResponseVo = responseVoResult.getData();

        // 把相关数据存储到Model对象中
        model.addAttribute("searchParam" , searchResponseVo.getSearchParam()) ;
        model.addAttribute("trademarkParam" , searchResponseVo.getTrademarkParam()) ;
        model.addAttribute("urlParam" , searchResponseVo.getUrlParam()) ;
        model.addAttribute("propsParamList" , searchResponseVo.getPropsParamList()) ;
        model.addAttribute("trademarkList" , searchResponseVo.getTrademarkList()) ;
        model.addAttribute("attrsList" , searchResponseVo.getAttrsList()) ;
        model.addAttribute("orderMap" , searchResponseVo.getOrderMap()) ;
        model.addAttribute("goodsList" , searchResponseVo.getGoodsList()) ;
        model.addAttribute("pageNo" , searchResponseVo.getPageNo()) ;
        model.addAttribute("totalPages" , searchResponseVo.getTotalPages()) ;

        return "list/index" ;
    }

}
