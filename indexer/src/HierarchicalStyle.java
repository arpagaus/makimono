import java.util.Collection;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class HierarchicalStyle extends ToStringStyle {
	private static final long serialVersionUID = 1L;

	private final static ToStringStyle instance = new HierarchicalStyle();

	public HierarchicalStyle() {
		setArrayContentDetail(true);
		setUseShortClassName(true);
		setUseClassName(false);
		setUseIdentityHashCode(false);
		
		setFieldSeparatorAtStart(false);
		setFieldSeparatorAtEnd(false);
		setFieldSeparator(";  ");
		setArraySeparator(", ");
		setFieldNameValueSeparator(": ");
	}

	public static ToStringStyle getInstance() {
		return instance;
	}

	@Override
	public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
		if (value == null) {
			return;
		} else if (!value.getClass().getName().startsWith("java")) {
			buffer.append(ReflectionToStringBuilder.toString(value, instance));
		} else {
			super.appendDetail(buffer, fieldName, value);
		}
	}

	@Override
	public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
		if (value == null) {
			return;
		}
		super.append(buffer, fieldName, value, fullDetail);
	}

	@Override
	public void appendDetail(StringBuffer buffer, String fieldName, Collection<?> value) {
		if (value.size() == 1) {
			appendDetail(buffer, fieldName, value.iterator().next());
		} else {
			appendDetail(buffer, fieldName, value.toArray());
		}
	}
}