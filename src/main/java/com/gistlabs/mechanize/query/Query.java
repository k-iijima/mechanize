package com.gistlabs.mechanize.query;


/**
 * @author Martin Kersten <Martin.Kersten.mk@gmail.com>
 */
public class Query extends AbstractQuery<Query> {

	public final Query or;
	public final Query and;

	Query() {
		this.or = this;
		this.and = new MyAndQuery(this);
	}
	
	Query(Query parent) {
		this.or = parent.or;
		this.and = this;
	}
	
	private static class MyAndQuery extends Query implements AndQuery<Query> {
		private final Query parent; 
		public MyAndQuery(Query parentQuery) {
			super(parentQuery);
			this.parent = parentQuery;
		}

		public Query getParent() {
			return parent;
		}
		
		@Override
		public String toString() {
			return parent.toString();
		}
	}
}