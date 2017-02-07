package org.bergefall.base.commondata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bergefall.common.data.AccountCtx;
import org.bergefall.common.data.InstrumentCtx;
import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.protocol.metatrader.MetaTraderMessageCreator;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Account;
import org.bergefall.protocol.metatrader.MetaTraderProtos.Instrument;
import org.bergefall.protocol.metatrader.MetaTraderProtos.MarketData;

public class CommonStrategyData {
	
	private final Map<String, SortedSet<MarketDataCtx>> marketDataPerSymbol;
	private final Map<Integer, AccountCtx> accounts;
	private final Map<String, Integer> accountNameToId;
	private final Map<String, InstrumentCtx> instruments;
	
	public CommonStrategyData() {
		marketDataPerSymbol = new HashMap<>();
		accounts = new HashMap<>();
		instruments = new HashMap<>();
		accountNameToId = new HashMap<>();
	}
	
	public SortedSet<MarketDataCtx> getMarketDataForSymbol(String symbol) {
		SortedSet<MarketDataCtx> marketData = marketDataPerSymbol.get(symbol);
		return null == marketData ? new TreeSet<MarketDataCtx>() : marketData;
	}

	public void addMarketData(MarketDataCtx md) {
		if (null == md) {
			return;
		}
		String symbol = md.getSymbol();
		SortedSet<MarketDataCtx> marketData = marketDataPerSymbol.get(symbol);
		if (null == marketData) {
			marketData = new TreeSet<>();
			marketDataPerSymbol.put(symbol, marketData);
		}
		marketData.add(md);
	}
	
	public void addOrUpdateAccount(Account acc) {
		if (null == acc) {
			return;
		}
		
		AccountCtx accCtx = new AccountCtx(acc.getName(), acc.getId(), acc.getBroker(), acc.getUser());
		addOrUpdateAccount(accCtx);
	}
	public Collection<AccountCtx> getAllAccounts() {
		return accounts.values();
	}
	public void addOrUpdateInstrument(Instrument instrument) {
		InstrumentCtx ctx = new InstrumentCtx(instrument.getName(), instrument.getId());
		addOrUpdateInstrument(ctx);
	}
	
	public void addOrUpdateInstrument(InstrumentCtx ctx) {
		instruments.put(ctx.getSymbol(), ctx);
	}
	
	public void addOrUpdateAccount(AccountCtx acc)  {
		accounts.put(Integer.valueOf(acc.getId()), acc);
		accountNameToId.put(acc.getName(), acc.getId());
	}
	
	public AccountCtx getAccount(int id) {
		return accounts.get(Integer.valueOf(id));
	}
	
	public Integer getAccountId(String name) {
		return accountNameToId.get(name);
	}
	
	public void addMarketData(MarketData md) {
		if (null == md) {
			return;
		}
		MarketDataCtx mdCtx = MetaTraderMessageCreator.convertToContext(md);
		addMarketData(mdCtx);
	}
	
	public void addMarketData(Collection<? extends MarketDataCtx> mds) {
		if (null == mds || mds.isEmpty()) {
			return;
		}
		for (MarketDataCtx mdItem : mds ) {
			addMarketData(mdItem);
		}
	}
}
