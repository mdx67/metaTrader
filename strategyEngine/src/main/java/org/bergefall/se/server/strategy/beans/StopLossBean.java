package org.bergefall.se.server.strategy.beans;

import org.bergefall.base.strategy.AbstractStrategyBean;
import org.bergefall.base.strategy.IntraStrategyBeanMsg;
import org.bergefall.base.strategy.Status;
import org.bergefall.base.strategy.StrategyToken;
import org.bergefall.common.MetaTraderConstants;
import org.bergefall.common.config.MetaTraderConfig;
import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.data.OrderCtx;
import org.bergefall.common.data.PositionCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage.Type;


public class StopLossBean extends AbstractStrategyBean<IntraStrategyBeanMsg, Status> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1578965216255593549L;
	private long stopLoss;
	IntraStrategyBeanMsg intraMsg;
	
	@Override
	public Status execute(StrategyToken token, IntraStrategyBeanMsg intraMsg) {
		if (null != msg && Type.MarketData == msg.getMsgType()) {
			this.intraMsg = null == intraMsg ? new IntraStrategyBeanMsg() : intraMsg;
			handleMarketData(MetaTraderMessageCreator.convertToContext(msg.getMarketData()));
		}
		return status;
	}

	protected void handleMarketData(MarketDataCtx md) {
		for (AccountCtx acc : csd.getAllAccounts()){
			PositionCtx pos = acc.getPosition(md.getSymbol());
			if (isStopLossHit(pos, md)) {
				intraMsg.addOrder(createExitOrder(acc.getId(), pos));
			}
		}
	}
	
	protected OrderCtx createExitOrder(int accountId, PositionCtx pos) {
		OrderCtx ctx = new OrderCtx(pos.getSymbol(), pos.getLongQty(), true);
		ctx.setAccountId(accountId);
		return ctx;
	}

	protected boolean isStopLossHit(PositionCtx pos, MarketDataCtx mdCtx) {
		if (mdCtx.getClosePrice() >= pos.getAvgLongPrice()) {
			return false;
		}
		long ratio = (mdCtx.getClosePrice() * MetaTraderConstants.DIVISOR) / pos.getAvgLongPrice();
		long downPercentage = 1 * MetaTraderConstants.DIVISOR - ratio;
		if (downPercentage >= stopLoss) {
			return true;
		}
		return false;
	}
	
	protected OrderCtx generateStopOrder() {
		return null;
	}
	
	@Override
	public void initBean(MetaTraderConfig config) {
		stopLoss = null == config ? 0L : config.getLongProperty(this.getClass().getName(), "stopLoss");
	}
	
}
