package DBMS;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable
{
	private static final long serialVersionUID = 1L;

	String tableName;
	String[] columnsNames;
	int recordsCount;
	int pagesCount;
	ArrayList<String> trace;

	public Table(String tableName, String[] columnsNames)
	{
		this.tableName = tableName;
		this.columnsNames = columnsNames;
		this.recordsCount = 0;
		this.pagesCount = 0;
		this.trace = new ArrayList<String>();
	}
}
