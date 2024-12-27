package com.stockfetcher.constants;

public class CacheConstant {
	
	public static final String STOCK_META_ALL="stock_meta_all" ;
	public static final String STOCK_META_BYSYMBOL="stock_meta_symbol_" ;
	public static final String STOCK_META_BYCOUNTRY="stock_meta_country_" ;
	public static final String STOCK_META_BYCOUNTRY_BYEXCHANGE= "stock_meta_country_{0}_exchange_{1}";
	public static final String STOCK_META_BYCOUNTRY_BYEXCHANGE_BYSYMBOL= "stock_meta_country_{0}_exchange_{1}_symbol_{2}";
	public static final String STOCK_META_BYCOUNTRY_BYEXCHANGE_BYPREFIX="stock_meta_country_{0}_exchange_{1}_prefix_" ;
	
	public static final String COUNTRY_DATA="countries_";
	public static final String COUNTRY_DATA_BYISO3="countries_data_iso3_{0}";
	
	public static final String INDICES_DATA="indices_";
	public static final String INDICES_DATA_BYCOUNTRY="indices_bycountry_{0}";
	public static final String INDICES_DATA_BYSYMBOL="indices_bySymbol_{0}";
	
	public static final String HISTORICAL_DATA_BYSYMBOL_BYINTERVAL="historical_data__bySymbol_{0}_byInterval_{1}";
	
	public static final String INCOME_DATA_BYSYMBOL_BYEXCHANGE="income_statement_{0}_{1}";
	
	public static final String BALANCE_SHEET_BYSYMBOL_BYEXCHANGE_BYMICCODE="balance_sheet_{0}_{1}_{2}";
	
	
}
