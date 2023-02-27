package xyz.riun.seckilltest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.riun.seckilltest.constants.RedisConstant;
import xyz.riun.seckilltest.mapper.VoucherOrderMapper;
import xyz.riun.seckilltest.model.SeckillVoucher;
import xyz.riun.seckilltest.model.VoucherOrder;
import xyz.riun.seckilltest.service.ISeckillVoucherService;
import xyz.riun.seckilltest.service.IVoucherOrderService;
import xyz.riun.seckilltest.utils.RedisIdWorker;

import javax.annotation.Resource;

/**
 * @Author：Hanxu
 * @url：https://riun.xyz/
 * @Date：2023/2/23 18:52
 * 优惠券订单相关
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private IVoucherOrderService voucherOrderService;

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 购买一张秒杀券
     * @param voucherId
     * @return
     */
    @Override
    public long seckillVoucher(Long voucherId, Long userId) {
        //检测秒杀券是否可以正常购买
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
        checkAvailable(seckillVoucher);

        //检测用户是否购买过
        //每个用户维度加锁 lock:order:userId
        RLock rLock = redissonClient.getLock(RedisConstant.LOCK_PRE_KEY + RedisConstant.BIZ_ORDER + ":" + userId);
        boolean isLock = rLock.tryLock();
        if (!isLock) {
            log.error("可能存在刷单行为：userId:{} voucherId:{}", userId, voucherId);
            throw new RuntimeException("正在购买中，请勿重复提交!");
        }
        try {
            //使用代理执行对应方法，确保事务生效
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            long orderId = proxy.createVoucherOrder(voucherId, userId);
            return orderId;
        } finally {
            //一定要在事务提交后再解锁。
            // 若事务未提交时解锁，则可能voucherOrder还未写入，那么其他线程进入createVoucherOrder方法判断count=0，可继续向下执行，就不再是一人一单了。
            rLock.unlock();
        }
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public long createVoucherOrder(Long voucherId, Long userId) {
        //查询用户是否已经购买 如果不加分布式锁，这里可能有多个线程同时满足条件，同时向下执行，那么一个用户就有可能通过抢单软件抢到多个优惠券
        // select count(*) from voucher_order where user_id = #{userId} and voucher_id = #{voucherId}
        int count = voucherOrderService.query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            throw new RuntimeException("已经购买过!");
        }

        //减库存 stock > 0 控制不会超卖
        // update seckill_vouscher set stock = stock - 1 where voucher_id = #{voucherId} and stock > 0
        boolean success = seckillVoucherService.update().setSql("stock = stock - 1").eq("voucher_id", voucherId).gt("stock", 0).update();
        if (!success) {
            throw new RuntimeException("库存不足!");
        }

        //添加订单信息
        long nextId = redisIdWorker.nextId(RedisConstant.BIZ_ORDER);
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUserId(userId);
        voucherOrder.setId(nextId);
        voucherOrderService.save(voucherOrder);
        return nextId;
    }

    private void checkAvailable(SeckillVoucher seckillVoucher) {
        /*if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("秒杀尚未开始！");
        }
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("秒杀已经结束！");
        }*/
        if (seckillVoucher.getStock() < 1) {
            throw new RuntimeException("库存不足！");
        }
    }
}
