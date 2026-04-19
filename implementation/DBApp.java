package DBMS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DBApp
{
	static int dataPageSize = -100;
	
	public static void createTable(String tableName, String[] columnsNames)
	{
		Table t = new Table(tableName, columnsNames);
		t.trace.add("Table created name:" + tableName + ", columnsNames:" + Arrays.toString(columnsNames));
		FileManager.storeTable(tableName, t);
	}
	
	public static void insert(String tableName, String[] record)
	{
		long start = System.currentTimeMillis();
		Table t = FileManager.loadTable(tableName);
		if(t == null)
			return;
		
		int pageNumber = t.recordsCount / dataPageSize;
		Page p;
		if(pageNumber >= t.pagesCount)
		{
			p = new Page();
			t.pagesCount++;
		}
		else
		{
			p = FileManager.loadTablePage(tableName, pageNumber);
			if(p == null)
				p = new Page();
		}
		p.records.add(Arrays.copyOf(record, record.length));
		t.recordsCount++;
		FileManager.storeTablePage(tableName, pageNumber, p);
		
		long end = System.currentTimeMillis();
		t.trace.add("Inserted:" + Arrays.toString(record) + ", at page number:" + pageNumber + ", execution time (mil):" + (end - start));
		FileManager.storeTable(tableName, t);
	}
	
	public static ArrayList<String []> select(String tableName)
	{
		long start = System.currentTimeMillis();
		ArrayList<String[]> result = new ArrayList<String[]>();
		Table t = FileManager.loadTable(tableName);
		if(t == null)
			return result;
		
		for(int i = 0; i < t.pagesCount; i++)
		{
			Page p = FileManager.loadTablePage(tableName, i);
			if(p == null)
				continue;
			for(int j = 0; j < p.records.size(); j++)
			{
				String[] rec = p.records.get(j);
				result.add(Arrays.copyOf(rec, rec.length));
			}
		}
		
		long end = System.currentTimeMillis();
		t.trace.add("Select all pages:" + t.pagesCount + ", records:" + result.size() + ", execution time (mil):" + (end - start));
		FileManager.storeTable(tableName, t);
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		long start = System.currentTimeMillis();
		ArrayList<String[]> result = new ArrayList<String[]>();
		Table t = FileManager.loadTable(tableName);
		if(t == null)
			return result;
		
		Page p = FileManager.loadTablePage(tableName, pageNumber);
		if(p != null && recordNumber >= 0 && recordNumber < p.records.size())
		{
			String[] rec = p.records.get(recordNumber);
			result.add(Arrays.copyOf(rec, rec.length));
		}
		
		long end = System.currentTimeMillis();
		t.trace.add("Select pointer page:" + pageNumber + ", record:" + recordNumber + ", total output count:" + result.size() + ", execution time (mil):" + (end - start));
		FileManager.storeTable(tableName, t);
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, String[] cols, String[] vals)
	{
		long start = System.currentTimeMillis();
		ArrayList<String[]> result = new ArrayList<String[]>();
		Table t = FileManager.loadTable(tableName);
		if(t == null)
			return result;
		
		int[] colIndexes = new int[cols.length];
		for(int i = 0; i < cols.length; i++)
		{
			colIndexes[i] = -1;
			for(int j = 0; j < t.columnsNames.length; j++)
			{
				if(t.columnsNames[j].equals(cols[i]))
				{
					colIndexes[i] = j;
					break;
				}
			}
		}
		
		ArrayList<String> recordsPerPage = new ArrayList<String>();
		for(int i = 0; i < t.pagesCount; i++)
		{
			Page p = FileManager.loadTablePage(tableName, i);
			if(p == null)
				continue;
			
			int pageMatches = 0;
			for(int r = 0; r < p.records.size(); r++)
			{
				String[] rec = p.records.get(r);
				boolean match = true;
				for(int c = 0; c < colIndexes.length; c++)
				{
					if(colIndexes[c] == -1 || colIndexes[c] >= rec.length || !rec[colIndexes[c]].equals(vals[c]))
					{
						match = false;
						break;
					}
				}
				if(match)
				{
					pageMatches++;
					result.add(Arrays.copyOf(rec, rec.length));
				}
			}
			if(pageMatches > 0)
				recordsPerPage.add("[" + i + ", " + pageMatches + "]");
		}
		
		long end = System.currentTimeMillis();
		t.trace.add("Select condition:" + Arrays.toString(cols) + "->" + Arrays.toString(vals) + ", Records per page:" + recordsPerPage.toString() + ", records:" + result.size() + ", execution time (mil):" + (end - start));
		FileManager.storeTable(tableName, t);
		
		return result;
	}
	
	public static String getFullTrace(String tableName)
	{
		Table t = FileManager.loadTable(tableName);
		if(t == null)
			return "";
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < t.trace.size(); i++)
		{
			sb.append(t.trace.get(i));
			sb.append("\n");
		}
		sb.append("Pages Count: ").append(t.pagesCount).append(", Records Count: ").append(t.recordsCount);
		return sb.toString();
	}
	
	public static String getLastTrace(String tableName)
	{
		Table t = FileManager.loadTable(tableName);
		if(t == null || t.trace.size() == 0)
			return "";
		
		return t.trace.get(t.trace.size() - 1);
	}
	
	
	public static void main(String []args) throws IOException
	{
		
		
	}
	
	
	
}
