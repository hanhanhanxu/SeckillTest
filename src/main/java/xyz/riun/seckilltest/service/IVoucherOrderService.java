package xyz.riun.seckilltest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.riun.seckilltest.model.VoucherOrder;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 18:51
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {
    long seckillVoucher(Long voucherId, Long userId);
    long createVoucherOrder(Long voucherId, Long userId);
}
