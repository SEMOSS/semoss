//package prerna.ds;
//
//import java.util.Iterator;
//import java.util.NoSuchElementException;
//
//import prerna.engine.api.IHeadersDataRow;
//import prerna.om.HeadersDataRow;
//
//public class TinkerHeadersDataRowIterator implements Iterator<IHeadersDataRow> {
//	
//	private TinkerIterator baseIterator;
//	private TinkerMetaData metadata;
//	private String[] headers;
//	private String[] types;
//
//	public TinkerHeadersDataRowIterator(Iterator composeIterator, TinkerMetaData metaData) {
//		this.baseIterator = (TinkerIterator) composeIterator;
//		this.metadata = metaData;
//	}
//
//	@Override
//	public boolean hasNext() {
//		return baseIterator.hasNext();
//	}
//
//	@Override
//	public IHeadersDataRow next() {
//		if (hasNext()) {
//			Object[] values = baseIterator.next();
//			String[] headers = baseIterator.getHeaders();
//			HeadersDataRow nextData = new HeadersDataRow(headers, values);
//			return nextData;
//		}
//		throw new NoSuchElementException("No more elements in Array");
//
//	}
//
//	public String[] getHeaders() {
//		if (this.headers == null) {
//			setHeaders();
//		}
//		return this.headers;
//	}
//
//	private void setHeaders() {
//		this.headers = baseIterator.getHeaders();
//	}
//
//	public String[] getTypes() {
//		if (types == null) {
//			types = new String[getHeaders().length];
//			for (int i = 0; i < types.length; i++) {
//				types[i] = (metadata.getDataType(headers[i])).toString();
//			}
//		}
//
//		return types;
//	}
//
//}
