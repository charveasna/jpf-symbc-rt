/**
 * 
 */
package gov.nasa.jpf.symbc.realtime;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.CACHE_POLICY;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.JOP_TIMING_MODEL;
import gov.nasa.jpf.symbc.realtime.rtsymexectree.jop.cache.JOP_CACHE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 */
public class RTConfig {
	private static final String PLATFORM_CONF_STR = "symbolic.realtime.platform";
	private static final String TARGET_SYMRT_CONF_STR = "symbolic.realtime.targetsymrt";
	private static final String OUTPUT_BASE_PATH_CONF_STR = "symbolic.realtime.outputbasepath";
	private static final String OPTIMIZE_CONF_STR = "symbolic.realtime.optimize";
	private static final String PROGRESS_MEASURE_CONF_STR = "symbolic.realtime.progressmeasure";
	private static final String GENERATE_QUERIES_CONF_STR = "symbolic.realtime.generatequeries";
	private static final String TIMING_DOC_PATH_CONF_STR = "symbolic.realtime.timingdoc.path";
	private static final String JOP_CACHE_POLICY_CONF_STR = "symbolic.realtime.jop.cachepolicy";
	private static final String JOP_TIMINGMODEL_CONF_STR = "symbolic.realtime.jop.timingmodel";
	private static final String JOP_CACHE_TYPE_CONF_STR = "symbolic.realtime.jop.cachetype";
	private static final String JOP_CACHE_BLOCKS_CONF_STR = "symbolic.realtime.jop.cache.blocks";
	private static final String JOP_CACHE_SIZE_CONF_STR = "symbolic.realtime.jop.cache.size";
	private static final String JOP_RAM_CNT_CONF_STR = "symbolic.realtime.jop.ram_cnt";
	private static final String JOP_RWS_CONF_STR = "symbolic.realtime.jop.rws";
	private static final String JOP_WWS_CONF_STR = "symbolic.realtime.jop.wws";
	private static final String SYM_TARGET_METHOD_CONF_STR = "symbolic.method";
	
	public static final Setting<RTPLATFORM> PLATFORM = new Setting<>(PLATFORM_CONF_STR, RTPLATFORM.JOP);
	public static final Setting<Boolean> TARGET_SYMRT = new Setting<>(TARGET_SYMRT_CONF_STR, false);
	public static final Setting<String> OUTPUT_BASE_PATH = new Setting<>(OUTPUT_BASE_PATH_CONF_STR, "./");
	public static final Setting<Boolean> OPTIMIZE = new Setting<>(OPTIMIZE_CONF_STR, true);
	public static final Setting<Boolean> PROGRESS_MEASURE = new Setting<>(PROGRESS_MEASURE_CONF_STR, true);
	public static final Setting<Boolean> GENERATE_QUERIES = new Setting<>(GENERATE_QUERIES_CONF_STR, true);
	public static final Setting<String> TIMING_DOC_PATH = new Setting<>(TIMING_DOC_PATH_CONF_STR, "./");
	public static final Setting<CACHE_POLICY> JOP_CACHE_POLICY = new Setting<>(JOP_CACHE_POLICY_CONF_STR, CACHE_POLICY.MISS);
	public static final Setting<JOP_TIMING_MODEL> JOP_TIMINGMODEL = new Setting<>(JOP_TIMINGMODEL_CONF_STR, JOP_TIMING_MODEL.HANDBOOK);
	public static final Setting<JOP_CACHE> JOP_CACHE_TYPE = new Setting<>(JOP_CACHE_TYPE_CONF_STR, JOP_CACHE.FIFOVARBLOCK);
	public static final Setting<Integer> JOP_CACHE_BLOCKS = new Setting<>(JOP_CACHE_BLOCKS_CONF_STR, 16);
	public static final Setting<Integer> JOP_CACHE_SIZE = new Setting<>(JOP_CACHE_SIZE_CONF_STR, 1024);
	public static final Setting<Integer> JOP_RAM_CNT = new Setting<>(JOP_RAM_CNT_CONF_STR, 2);
	public static final Setting<Integer> JOP_RWS = new Setting<>(JOP_RWS_CONF_STR, 1);
	public static final Setting<Integer> JOP_WWS = new Setting<>(JOP_WWS_CONF_STR, 1);
	public static final Setting<String> SYM_TARGET_METHOD = new Setting<>(SYM_TARGET_METHOD_CONF_STR, "");
	
	public static boolean isConfSet(Setting<?> setting, Config jpfConf) {
		return jpfConf.hasValue(setting.getConfString());
	}
	
	public static int getIntValueFor(Setting<Integer> setting, Config jpfConf) {
		return jpfConf.getInt(setting.getConfString(), setting.getDefaultValue());
	}
	
	public static boolean getBooleanValueFor(Setting<Boolean> setting, Config jpfConf) {
		return jpfConf.getBoolean(setting.getConfString(), setting.getDefaultValue());
	}
	
	public static String getStringValueFor(Setting<String> setting, Config jpfConf) {
		return jpfConf.getString(setting.getConfString(), setting.getDefaultValue());
	}
	
	public static <T extends Enum<T>> T getEnumValueFor(Setting<T> setting, Class<T> enumType, Config jpfConf) {
		return T.valueOf(enumType, jpfConf.getString(setting.getConfString(), setting.getDefaultValue().toString().toUpperCase()).toUpperCase());
	}
	
	private SettingsMap values;
	
	public <T> T getValue(Setting<T> setting, Class<T> clType){
		return clType.cast(this.values.get(setting));
	}
	
	public SettingsMap getAllSettings() {
		return this.values;
	}
	
	public RTConfig(Config jpfConf) {
		this.values = new SettingsMap();
		this.values.put(PLATFORM, getEnumValueFor(PLATFORM, RTPLATFORM.class, jpfConf));
		this.values.put(TARGET_SYMRT, getBooleanValueFor(TARGET_SYMRT, jpfConf));
		this.values.put(OUTPUT_BASE_PATH, getStringValueFor(OUTPUT_BASE_PATH, jpfConf));
		this.values.put(OPTIMIZE, getBooleanValueFor(OPTIMIZE, jpfConf));
		this.values.put(PROGRESS_MEASURE, getBooleanValueFor(PROGRESS_MEASURE, jpfConf));
		this.values.put(GENERATE_QUERIES, getBooleanValueFor(GENERATE_QUERIES, jpfConf));
		this.values.put(TIMING_DOC_PATH, getStringValueFor(TIMING_DOC_PATH, jpfConf));
		this.values.put(JOP_CACHE_POLICY, getEnumValueFor(JOP_CACHE_POLICY, CACHE_POLICY.class, jpfConf));
		this.values.put(JOP_TIMINGMODEL, getEnumValueFor(JOP_TIMINGMODEL, JOP_TIMING_MODEL.class, jpfConf));
		this.values.put(JOP_CACHE_TYPE, getEnumValueFor(JOP_CACHE_TYPE, JOP_CACHE.class, jpfConf));
		this.values.put(JOP_CACHE_BLOCKS, getIntValueFor(JOP_CACHE_BLOCKS, jpfConf));
		this.values.put(JOP_CACHE_SIZE, getIntValueFor(JOP_CACHE_SIZE, jpfConf));
		this.values.put(JOP_RAM_CNT, getIntValueFor(JOP_RAM_CNT, jpfConf));
		this.values.put(JOP_RWS, getIntValueFor(JOP_RWS, jpfConf));
		this.values.put(JOP_WWS, getIntValueFor(JOP_WWS, jpfConf));
		this.values.put(SYM_TARGET_METHOD, getStringValueFor(SYM_TARGET_METHOD, jpfConf));
	}
	
	public static class SettingsMap extends HashMap<Setting<?>, Object> {
		private static final long serialVersionUID = 54L;

		public String toStringIfConfigSet(Config jpfConf) {
			StringBuilder sb = new StringBuilder();
			Iterator<Setting<?>> iter = this.keySet().iterator();
			while(iter.hasNext()) {
				Setting<?> setting = iter.next();
				if(isConfSet(setting, jpfConf)) {
					sb.append(setting.getConfString()).append(" = ").append(this.get(setting).toString());
					if(iter.hasNext())
						sb.append("\n");
				}
			}
			return sb.toString();
		}
	}
	
	public static class Setting<T> {
		private String confStr;
		private T defVal;
		
		public Setting(String confStr, T defVal) {
			this.confStr = confStr;
			this.defVal = defVal;
		}
		
		public String getConfString() {
			return this.confStr;
		}
		
		public T getDefaultValue() {
			return this.defVal;
		}
	}
}
