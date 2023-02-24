package xyz.riun.seckilltest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.riun.seckilltest.model.Voucher;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 17:40
 */
public interface IVoucherService extends IService<Voucher> {
    void addSeckillVoucher(Voucher voucher);

}
