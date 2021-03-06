package org.bergefall.base.strategy;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

import org.bergefall.base.commondata.CommonStrategyData;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MetaTraderMessage;

public class StrategyToken {

	private LocalDateTime ldt;	
	private CommonStrategyData csd;
	private List<Long> timeStamps;
	private MetaTraderMessage mtMsg;
	
	public StrategyToken(LocalDateTime ldt, CommonStrategyData cds) {
		this.ldt = ldt;
		this.csd = cds;
		timeStamps = new LinkedList<>();
	}
	
	public void setTriggeringMsg(MetaTraderMessage msg) {
		mtMsg = msg;
	}
	
	public LocalDateTime getLdt() {
		return ldt;
	}
	
	public CommonStrategyData getCsd() {
		return csd;
	}
	
	public void addTimestamp(long time) {
		timeStamps.add(Long.valueOf(time));
	}
		
	public long getEpochTime() {
		return ldt.toEpochSecond(ZoneOffset.UTC);
	}
	
	public MetaTraderMessage getTriggeringMsg() {
		return mtMsg;
	}
}
