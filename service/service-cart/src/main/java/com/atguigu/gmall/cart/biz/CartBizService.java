package com.atguigu.gmall.cart.biz;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;

import java.util.List;

public interface CartBizService {

    /**
     * 添加商品到购物车中
     * @param skuId
     * @param skuNum
     * @return
     */
    public abstract AddCartSuccessVo addCart(Long skuId, Integer skuNum);

    /**
     * 查询用户所有的购物车数据
     * @return
     */
    public abstract List<CartItem> findCartList();

    /**
     * 进行购物车中购物项数量的+1 ， -1操作
     * @param skuId
     * @param skuNum
     */
    public abstract void addToCart(Long skuId, Integer skuNum);

    /**
     * 更新选中或者不选中的状态
     * @param skuId
     * @param isChecked
     */
    public abstract void checkCart(Long skuId, Integer isChecked);

    /**
     * 删除指定的购物项
     * @param skuId
     */
    public abstract void deleteCart(Long skuId);

    /**
     * 删除选中的购物车数据
     */
    public abstract void deleteCheckedCart();

    /**
     * 获取选中的购物车的数据
     * @return
     */
    public abstract List<CartItem> getCheckedCartItem();

}
