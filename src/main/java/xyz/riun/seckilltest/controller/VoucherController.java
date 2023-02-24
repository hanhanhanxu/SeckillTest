package xyz.riun.seckilltest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.riun.seckilltest.model.Voucher;
import xyz.riun.seckilltest.service.IVoucherOrderService;
import xyz.riun.seckilltest.service.IVoucherService;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 17:39
 * 优惠券相关
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    private IVoucherService voucherService;
    @Autowired
    private IVoucherOrderService voucherOrderService;

    /**
     * 新增秒杀券
     * @param voucher
     * @return
     */
    @PostMapping("seckill/add")
    public Long addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return voucher.getId();
    }

    /**
     * 购买秒杀券，没有登录部分，所以直接传入userId模拟某个用户购买
     * @param voucherId
     * @return
     */
    @PostMapping("seckill")
    public Long seckillVoucher(Long voucherId, Long userId) {
        long orderId = voucherOrderService.seckillVoucher(voucherId, userId);
        return orderId;
    }
}
