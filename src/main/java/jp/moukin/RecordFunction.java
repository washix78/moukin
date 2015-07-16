package jp.moukin;

import java.util.List;

@FunctionalInterface
public interface RecordFunction {
	  public abstract void func(List<String> record);
}
