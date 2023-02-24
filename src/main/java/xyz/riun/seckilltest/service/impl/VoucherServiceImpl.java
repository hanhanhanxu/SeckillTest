package xyz.riun.seckilltest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.riun.seckilltest.mapper.VoucherMapper;
import xyz.riun.seckilltest.model.SeckillVoucher;
import xyz.riun.seckilltest.model.Voucher;
import xyz.riun.seckilltest.service.ISeckillVoucherService;
import xyz.riun.seckilltest.service.IVoucherService;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 17:50
 * 优惠券相关
 */
@Slf4j
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService;


    /**
     * 新增一张秒杀券
     * @param voucher
     */
    @Override
    public void addSeckillVoucher(Voucher voucher) {
        //保存优惠券
        save(voucher);
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        //保存秒杀券
        seckillVoucherService.save(seckillVoucher);
    }

}
