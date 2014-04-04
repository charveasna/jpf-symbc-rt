/**
 * 
 */
package gov.nasa.jpf.symbc.realtime.util;

import gov.nasa.jpf.vm.MethodInfo;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Kasper S. Luckow <luckow@cs.aau.dk>
 *
 */
public class EnteredMethodsSet {

	//TODO: This seems a bit messy... Clean up!
	private HashMap<String, MethodInfo> enteredMethods;
	private HashMap<String, Integer> methodIds;
	private HashMap<Integer, String> idToMethod;
	private int methodId;
	
	public EnteredMethodsSet() {
		this.enteredMethods = new HashMap<>();
		this.methodIds = new HashMap<>();
		this.idToMethod = new HashMap<>();
		this.methodId = 0;
	}
	
	public void add(MethodInfo enteredMethod) {
		String methodName = enteredMethod.getFullName();
		if(!enteredMethods.containsKey(methodName)) {
			this.enteredMethods.put(methodName, enteredMethod);
			int id = methodId++;
			this.methodIds.put(methodName, id);
			this.idToMethod.put(id, methodName);
		}
	}
	
	public HashMap<Integer, String> getIds() {
		return this.idToMethod;
	}
	
	public HashMap<String, MethodInfo> getEnteredMethods() {
		return this.enteredMethods;
	}
	public String getMethodString(int id) {
		return this.idToMethod.get(id);
	}
	
	public MethodInfo getMethod(int id) {
		return this.enteredMethods.get(this.idToMethod.get(id));
	}
	
	public int getIdForMethod(MethodInfo method) {
		return this.methodIds.get(method.getFullName());
	}
	
	public int getIdForMethod(String fullyQualifiedMethodName) {
		return this.methodIds.get(fullyQualifiedMethodName);
	}
}
